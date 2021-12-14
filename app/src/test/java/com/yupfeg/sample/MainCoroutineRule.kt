package com.yupfeg.sample

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before

/**
 *
 * @author yuPFeG
 * @date
 */
class MainCoroutineRule {

    @ExperimentalCoroutinesApi
    val testDispatcher = TestCoroutineDispatcher()

    @ExperimentalCoroutinesApi
    @Before
    fun setupDispatcher(){
        //在单元测试启动前，重新设置主线程的调度器
        Dispatchers.setMain(testDispatcher)
        println("setupDispatcher")
    }

    @ExperimentalCoroutinesApi
    @After
    fun resetDispatcher(){
        //重置主线程调度器
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
        println("resetDispatcher")
    }
}