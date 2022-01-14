package com.yupfeg.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.yupfeg.dispatcher.ExecutorProvider
import com.yupfeg.logger.ext.logd
import kotlinx.coroutines.*

/**
 *
 * @author yuPFeG
 * @date
 */
class TestCoroutineActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun testCoroutine(){
//        val context = Dispatchers.Default
//        context.plus(object : CoroutineExceptionHandler {
//            /**
//             * A key of this coroutine context element.
//             */
//            override val key: CoroutineContext.Key<*>
//                get() = TODO("Not yet implemented")
//
//            /**
//             * Handles uncaught [exception] in the given [context]. It is invoked
//             * if coroutine has an uncaught exception.
//             */
//            override fun handleException(context: CoroutineContext, exception: Throwable) {
//                TODO("Not yet implemented")
//            }
//
//        })
        val dispatcher = ExecutorProvider.cpuExecutor.asCoroutineDispatcher()
        lifecycleScope.launch {
            val job = launch(dispatcher + CoroutineName("coroutine_1")){
                logd("${Thread.currentThread().name} job1 running print")
                val innerJob = async(CoroutineName("inner_coroutine_1")){
                    println("${Thread.currentThread().name} running")
//                    throw NullPointerException("${Thread.currentThread().name} 测试抛出异常")
                }
            }

            val job2 = async (CoroutineName("coroutine_2")) {
                logd("${Thread.currentThread().name} 测试抛出异常")
                throw NullPointerException("${Thread.currentThread().name} 测试抛出异常")
            }
//            try {
//                job2.await()
//            }catch (e : Exception){
//                logd("${Thread.currentThread().name} catch ${e.message} Exception")
//            }
        }

    }

    private suspend fun testSuspend1() : String{
        withContext(Dispatchers.IO){
            delay(1000)
        }
        return "testSuspend1"
    }
}
