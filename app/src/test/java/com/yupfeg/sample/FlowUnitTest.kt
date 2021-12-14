package com.yupfeg.sample

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.selects.select
import org.junit.Test
import java.lang.Exception
import java.util.concurrent.Executors
import kotlin.time.measureTimedValue

/**
 *
 * @author yuPFeG
 * @date
 */
class FlowUnitTest {

    @Test
    fun testFlow() = runBlocking {
        val scope = CoroutineScope(SupervisorJob())
        val startTime = System.currentTimeMillis()
        flow{
            (0..20).forEach {
                println("emit $it")
                if (it > 10) throw NullPointerException("test Exception")
                emit(it)
            }
        }
            .flatMapConcat {index->
                flow<String> {
                    (0..5).forEach {innerIndex->
                        emit("flatMapConcat $index + ${innerIndex},1")
                        delay(200)
//                    println("running flatMapConcat first $index + $innerIndex")
                        emit("flatMapConcat $index + ${innerIndex},2")
                    }
//                println("running flatMapConcat second $index")
//                emit("flatMapConcat second $index")
//                println("running flatMapConcat end $index")
                }
            }
            .collect {
                println("last collect result ：$it")
            }
//        delay(2000)
        println("after flow")
    }

    @FlowPreview
    @Test
    fun testFlowOn() = runBlocking {
        val flow1 = flowOf("first","second","third")
        val myDispatcher = Executors.newSingleThreadExecutor()
            .asCoroutineDispatcher()
        flow {
            println("emit on ${Thread.currentThread().name}")
            emit("data")
        }
            .flowOn(myDispatcher)
            .flatMapConcat {data->
                flow<String> {
                    val array = arrayOf("first","second","third")
                    array.forEach {
                        println("run flatMapConcat on ${Thread.currentThread().name}")
                        emit("$data $it")
                    }
                }
            }
            .flowOn(Dispatchers.IO)
            .map {
                println("run second map on ${Thread.currentThread().name}")
                "${it},${it.length}"
            }
            .flowOn(myDispatcher)
            .collect {
                println("result $it on ${Thread.currentThread().name}")
            }
    }

    @Test
    fun testFlowEvent() = runBlocking {
//        val flow1 = flowOf("first","second","third")

        flow<String> {
//            println("wait emit data")
            val array = arrayOf("first","second","third")
            array.forEach {
                println("wait emit $it")
                if (it == "second") throw NullPointerException("test")
                else emit(it)
            }
        }
            .onStart {
                println("on Start")
                emit("start before1")
                emit("start before2")
            }
            .onEach {
                println("on each $it")
            }
            .onEmpty {
                print("on Empty")
                emit("on empty data")
            }
            .onCompletion {error->
                println("on onCompletion $error")
                emit("completion after")
            }
            .retry (3){
                println("wait retry")
                true
            }
            .catch { error->
                println("on catch $error")
                emit("catch error data")
            }
            .collect {
                println("result : $it")
            }
    }

    @Test
    fun testFlowMerge() = runBlocking {
        val flow1 = flowOf("first","second","third").onEach { delay(50) }
        val flow2 = flowOf(1,2,3,4).onEach { delay(100) }


        flow1.zip(flow2){first,second->
            "$first $second"
        }
            .conflate()
            .collect { println("result $it") }

    }

    @Test
    fun testFlowBuffer() = runBlocking {
        val startTime = System.currentTimeMillis()
        flow{
            (0..100).forEach {
                delay(50)
                val currTime = System.currentTimeMillis()
                println("emit $it on ${Thread.currentThread().name} (${currTime - startTime} ms)")
                emit(it)
            }
        }
//            .conflate()
            .buffer()
            .flowOn(Dispatchers.IO)
            .onEach { delay(300) }
            .collectLatest {

                val endTime = System.currentTimeMillis()
                println("result : $it on ${Thread.currentThread().name} (${endTime - startTime} ms)")
            }
    }

    @Test
    fun testSelect() = runBlocking {
        val d1 = async {
            delay(60)
            1
        }
        val d2 = async {
            delay(50)
            2
        }
        val d3 = async {
            delay(55)
            3
        }

        val data = select<Int> {
            d3.onAwait{data->
                println("d3 first result $data")
                data
            }
            d1.onAwait{i->
                println("d1 first result :$i")
                i
            }
            d2.onAwait{i->
                println("d2 first result : $i")
                i
            }
        }
        println("result : $data")
    }

    @Test
    fun testChannel() = runBlocking {
//        val channel = Channel<Int>()
        val scope = CoroutineScope(SupervisorJob()+Dispatchers.IO)
        val receiveChannel = scope.produce<Int>(capacity = Channel.BUFFERED) {
            for (i in 0..20){
                println("wait send $i $isClosedForSend")
                send(i)
                delay(50)
            }
            awaitClose {
                println("awaitClose")
            }
        }

        launch {
            while (isActive){
                if (receiveChannel.isClosedForReceive) break
                val channelResult = receiveChannel.receiveCatching()
                if (channelResult.isSuccess) {
                    println("receive result : ${channelResult.getOrNull()}")
                }
                delay(100)
            }
//            println("receive is cancel try get ${receiveChannel.receiveCatching().getOrNull()}")
        }

        for (i in receiveChannel) {

        }

        delay(1000)
        receiveChannel.cancel()
        println("task over")
    }

    @ExperimentalCoroutinesApi
    @Test
    fun testSelectChannel() = runBlocking {
        val slowChannel = Channel<Int>(
            capacity = 1,onBufferOverflow = BufferOverflow.SUSPEND
        )
        val fastChannel = Channel<Int>(
            capacity = 3,onBufferOverflow = BufferOverflow.SUSPEND
        )
        //生产者协程
        val produceJob = launch(Dispatchers.IO){
            for (i in 1..20){
                if (!isActive) break
                println("send state slow ${slowChannel.isClosedForSend}" +
                        " ; fast : ${fastChannel.isClosedForSend}")

                select<Unit> {
                    slowChannel.onSend(i){channel->
                        //回调内会回调当前选中的channel
                        println("slow channel selected $channel send $i")
                        delay(50)
                    }
                    fastChannel.onSend(i){channel->
                        println("fast channel selected $channel send $i")
                    }
                }
            }
//            delay(500)
//            slowChannel.close()
//            fastChannel.close()
        }
        //消费者协程
        val consumeJob = launch {
            while (isActive){
                println("receive state slow : ${slowChannel.isClosedForReceive} ; " +
                        "fast : ${fastChannel.isClosedForReceive}")
                if (slowChannel.isClosedForReceive || fastChannel.isClosedForReceive) break

                val result = select<Int> {
                    slowChannel.onReceiveCatching{
                        println("slowChannel receive ${it.getOrNull()}")
                        delay(100)
                        it.getOrNull()?:-1
                    }
                    fastChannel.onReceiveCatching{
                        println("fastChannel receive ${it.getOrNull()}")
                        it.getOrNull()?:-1
                    }
                }
                println("receive result : $result")
            }
        }
        delay(1000)
        slowChannel.cancel()
        fastChannel.cancel()
//        produceJob.cancel()
//        consumeJob.cancel()
    }


    @Test
    fun testChannelFlow() = runBlocking {
        val flow = channelFlow<String> {
            send("11")
            println("send first on ${Thread.currentThread()}")
            withContext(Dispatchers.IO){
                send("22")
                println("send second on ${Thread.currentThread()}")
            }
            send("33")
            println("send third on ${Thread.currentThread()}")
            awaitClose {
                println("awaitClose")
            }
        }
        val job = launch {
            flow.collect {
                println("result : $it")
            }
        }
        delay(200)
        job.cancel()
    }

//    fun registerCallBack(callBack : (String)->Unit){
//        for (i in 0..5){
//            callBack("data $i")
//        }
//    }
//
//    private suspend fun createCallBackFlow() = callbackFlow<String>{
//        registerCallBack{result->
//            send(result)    //这里回调是普通函数，无法调用send
//        }
//        close()
//        awaitClose{
//            unRegisterCallBack()
//        }
//    }


    @Test
    fun testShardFlow() = runBlocking(SupervisorJob()){
        val sharedFlow = MutableSharedFlow<String>(
        )

        //假设处于另一个类，异步发送数据
        val produce = launch(Dispatchers.IO) {
            for (i in 0..50) {
//                println("emit $i")
                sharedFlow.emit("data$i")
                delay(50)
            }
        }

        //模拟在外部调用
        val readOnlySharedFlow = sharedFlow.asSharedFlow()
        val scope = CoroutineScope(SupervisorJob())

        delay(100)
        val job1 = scope.launch {
            readOnlySharedFlow.map {
                delay(100)
                "$it receive 1"
            }
                .collect {
                    println("collect1 result : $it")
                }
        }
//
        delay(200)
        val handler = CoroutineExceptionHandler { _, throwable -> println("catch $throwable") }
        val job2 = scope.launch(handler) {
            readOnlySharedFlow.map {
                if (it == "data6") throw Exception("test Exception")
                "$it receive 2"
            }
//                .catch { println("catch2 error $it") }
                .collect{
                    println("collect2 result : $it")
                }
        }
//
        delay(2000)
        job1.cancel()
        job2.cancel()
//        delay(1000)
//        produce.cancel()
//
//        launch {
//            sendMessage(sharedFlow)
//        }
//
//        val newReadShardFlow = sharedFlow.asSharedFlow()
//        newReadShardFlow.collect {
//            println("new collect3 result : $it on ${Thread.currentThread()}")
//        }
    }

    @Test
    fun testStateFlow() = runBlocking{
        val stateFlow = MutableStateFlow(1)
        val readOnlyStateFlow = stateFlow.asStateFlow()

        val job0 = launch {
            stateFlow.collect { println("collect0 : $it") }
        }
        delay(50)

        launch(Dispatchers.IO) {
            for (i in 1..3){
                println("wait emit $i")
                stateFlow.emit(i)
                delay(50)
            }
        }

        delay(200)
        val job1 = launch {
            stateFlow.collect{ println("collect1 : $it") }
        }
        val job2 = launch {
            stateFlow.collect{ println("collect2 : $it") }
        }

        println("get value : ${readOnlyStateFlow.value}")

        delay(200)
        job0.cancel()
        job1.cancel()
        job2.cancel()
    }


}