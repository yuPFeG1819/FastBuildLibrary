package com.yupfeg.sample

import kotlinx.coroutines.*
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

import org.junit.Assert.*
import kotlin.coroutines.suspendCoroutine
import kotlin.system.measureTimeMillis

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }


    fun testFlow() {
        val sequence = sequence {
            for (i in 0..10){
                println("before yield $i : ${Thread.currentThread().name}")
                yield(i)
                println("after yield $i : ${Thread.currentThread().name}")
            }
        }
        println("sequence build after : ${Thread.currentThread().name}")
        sequence.take(2).forEach {i->
            println("sequence build take $i")
        }
    }

}