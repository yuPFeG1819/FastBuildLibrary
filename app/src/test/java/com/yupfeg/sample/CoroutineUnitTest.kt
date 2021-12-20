package com.yupfeg.sample

import kotlinx.coroutines.*
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import kotlin.coroutines.*

/**
 * Kotlin协程的单元测试
 * @author yuPFeG
 * @date
 */
class CoroutineUnitTest {

//    @get:Rule
//    val mainCoroutineRule = MainCoroutineRule()

    @Test
    fun testCreateCoroutine(){
        val coroutine = suspend {
            println("create coroutine")
            "result"
        }.createCoroutine(object : Continuation<String>{
            override val context: CoroutineContext = Job()

            override fun resumeWith(result: Result<String>) {
                println("Coroutine Result ${result.getOrNull()}")
            }
        })
        coroutine.resumeWith(Result.success(Unit))

    }

    private fun lazyPrint(vararg data : Lazy<String>) : Boolean{
        return data.all { data.isNullOrEmpty() }
    }

    @Test
    fun testCoroutineAsync() = runBlocking {
        val startTime = System.currentTimeMillis()
        val task1 = async(CoroutineName("async_coroutine_1")) {
            delay(100)
            println("task1 running print")
            yield()
            1
        }
        val task2 = async(CoroutineName("async_coroutine_2")) {
            delay(150)
            println("task2 running print")
            2
        }
        awaitAll(task1,task2)
        val endTime = System.currentTimeMillis()
        println("async task over ${endTime - startTime}")
    }

    @Test
    fun testThrowCoroutine() {
        println("${Thread.currentThread().name} testThrowCoroutine start")
        runBlocking {
            val job = launch (CoroutineName("coroutine_1") + SupervisorJob()) {
                println("${Thread.currentThread().name} job1 running print")
                try {
                    val innerJob = async(CoroutineName("inner_coroutine_1")){
                        println("${Thread.currentThread().name} 测试抛出异常")
                        throw NullPointerException("${Thread.currentThread().name} 测试抛出异常")
                    }
//                    println("${Thread.currentThread().name} 测试抛出异常")
//                    throw NullPointerException("${Thread.currentThread().name} 测试抛出异常")
//                    innerJob.await()
                }catch (e : Exception){
                    println("coroutine_1 ${Thread.currentThread().name} catch $e")
                }

                println("${Thread.currentThread().name} job1 running print2")
            }
//            val job2 = async (CoroutineName("coroutine_2")) {
//                println("${Thread.currentThread().name} 测试抛出异常")
//                throw NullPointerException("${Thread.currentThread().name} 测试抛出异常")
//            }
//            try {
//                job2.await()
//            }catch (e : Exception){
//                println("${Thread.currentThread().name} catch ${e.message} Exception")
//            }
            delay(2000)
            println("job over")
        }
        println("${Thread.currentThread().name} testThrowCoroutine end")
    }

    @Test
    fun testCoroutineExceptionHandler() = runBlocking {
        val scope = CoroutineScope(Job())

        val handler = createThrowableHandler()
//        scope.launch(CoroutineName("coroutine_scope_1")+handler) {
//            println("CoroutineScope content start 1")
//            launch (CoroutineName("coroutine_scope_1")+handler){
//                println("${Thread.currentThread().name} job1 running print")
//
//                val innerJob = launch(CoroutineName("inner_coroutine_1")+ SupervisorJob()+handler){
//                    println("${Thread.currentThread().name} 测试抛出异常")
//                    throw NullPointerException("${Thread.currentThread().name} 测试抛出异常")
//                }
//            }
//        }

        supervisorScope{
            delay(200)
            println("coroutineScope content start 2")
            launch (CoroutineName("coroutineScope内部协程")+handler){
                println("${Thread.currentThread().name} coroutineScope内部协程 running print")
                throw NullPointerException("coroutineScope内部协程 测试抛出异常")
            }
        }

        val content = withContext(Dispatchers.Default){
            delay(100)
            "with context over"
        }
        println("with context result : $content")
        delay(2000)
        println("block task over")
    }

    private fun createThrowableHandler() = CoroutineExceptionHandler { coroutineContext, throwable ->
        println("${coroutineContext[CoroutineName]} handler cast $throwable")
    }

    private suspend fun testSupervisorScope() = supervisorScope{
        val job2 = async (CoroutineName("coroutine_2")) {
            repeat(5){i->
                println("${Thread.currentThread().name} running print $i")
                //开启子协程
                launch(CoroutineName("child_coroutine_2_$i")) {
                    repeat(10){y-> println("${Thread.currentThread().name} inner_running print $y")}
                }
                if (i == 2){
//                    try {
                    println("${Thread.currentThread().name} 测试抛出异常")
                    throw NullPointerException("${Thread.currentThread().name} 测试抛出异常")
//                    }catch (e : NullPointerException){
//                        println("${Thread.currentThread().name} catch ${e.message} Exception")
//                    }
                }


            }
        }
        try {
            job2.await()
        }catch (e : Exception){
            println("${Thread.currentThread().name} catch ${e.message} Exception")
        }
    }

    @Test
    fun testCancelCoroutine() = runBlocking() {
        val startTime = System.currentTimeMillis()
        val scope = CoroutineScope(SupervisorJob()+Dispatchers.Default)
        val job = scope.launch {
            try {
                var nextPrintTime = startTime
                var i = 0
                while (i <= 5){
                    this.ensureActive()
                    if (System.currentTimeMillis() >= nextPrintTime){
                        println("job running print ${i++}")
                        testSuspend(i)
                        nextPrintTime += 500
                    }
                }
            }finally {
                println("job NonCancellable suspend finally")
            }
        }
        delay(2000)
        println("coroutine scope delay done,wait cancel scope")
        scope.cancel()
        println("scope canceled")
        delay(1000)
        println("after scope canceled")
        scope.launch {
            delay(200)
            println("after scope canceled new task running")
        }
        delay(1000)
        println("test over")
    }

    private suspend fun testSuspend(count : Int){
        withTimeout(1000){
            println("print test suspend $count")
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun testCallBackToSuspend() = runBlocking{
        val scope = CoroutineScope(Dispatchers.Default)
        scope.launch {
            try {
                val result = queryData1()
                val result2 = queryData2(result)
                println("测试CallBack result : $result2")
            }catch (e : Exception){
                println("catch $e")
            }
        }
        delay(1000)
    }

    private suspend  fun queryData1() = suspendCancellableCoroutine<String> {continuation->
        println("queryData1 ${Thread.currentThread().name}")
//        continuation.resume("result 1")
        continuation.resumeWithException(NullPointerException("111"))
    }

    private suspend fun queryData2(lastData : String) = suspendCancellableCoroutine<String>{continuation->
        println("queryData2 ${Thread.currentThread().name}")
        continuation.resume(lastData + "new query")
    }

    @ExperimentalCoroutinesApi
    @Test
    fun testChannel() = runBlockingTest {
//        val channel = Channel<String>()
//        launch (Dispatchers.Default){
//            channel.send("1")
//        }
    }



}