package com.yupfeg.sample

import org.junit.Test

/**
 *
 * @author yuPFeG
 * @date
 */
class SequenceUnitTest {

    @Test
    fun testSequence(){
        val list = mutableListOf<Int>()
        for (i in 0..10000){ list.add(i) }
//        val sequence = list.asSequence()
        val startTime = System.currentTimeMillis()
        val count = list.map {
            println("running map $it")
            it+1
        }
            .filter {
                println("running filter $it")
                it % 2 == 0
            }
            .first {
                println("running first $it")
                it > 500
            }
        val endTime = System.currentTimeMillis()

        println("task result : $count , time : ${endTime-startTime} ms")
    }

    @Test
    fun testSuspendSequence(){
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