package com.yupfeg.sample

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.junit.Test
import java.util.concurrent.locks.ReentrantLock

/**
 * 并发测试
 * @author yuPFeG
 * @date
 */
class ConcurrentUnitTest {
    @Test
    fun testReentrantLock() {
        val lock = ReentrantLock()
        var count = 0
        val totalCount = 100000
        val scope = CoroutineScope(Job())
        val startTime = System.currentTimeMillis()
        repeat(totalCount) {
            // 循环开启协程
            scope.launch(Dispatchers.Default) {
                lock.lock()
                try {
                    count++
                    println(
                        "${Thread.currentThread().name} \n " +
                            "try lock count change : $count"
                    )
                } finally {
                    lock.unlock()
                }
            }
        }
        // 防止线程结束
        while (count < totalCount) { print("") }
        val endTime = System.currentTimeMillis()
        println("ReentrantLock task over : $count , totalTime : ${endTime - startTime} ms")
    }

    @Test
    fun testMutex() {
        val mutex = Mutex()
        var count = 0
        val totalSize = 100000
        val scope = CoroutineScope(Job())
        val startTime = System.currentTimeMillis()
        repeat(totalSize) {
            // 循环开启协程
            scope.launch(Dispatchers.Default) {
                mutex.withLock {
                    count++
                    println(
                        "${Thread.currentThread().name} \n " +
                            "try lock count change : $count"
                    )
                }
            }
        }
        // 防止线程结束
        while (count < totalSize) { print("") }
        val endTime = System.currentTimeMillis()
        println("mutex task over : $count , totalTime : ${endTime - startTime} ms")
    }

    @Test
    fun testBlockQueue() {
    }
}
