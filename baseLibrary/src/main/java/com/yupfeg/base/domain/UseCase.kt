package com.yupfeg.base.domain

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

/**
 * 业务用例的接口声明
 * * 需要手动管理内部
 * @author yuPFeG
 * @date 2021/10/08
 */
abstract class UseCase{
    private var mScope : CoroutineScope? = null
    /**
     * 协程作用域是否已取消
     * */
    private var isScopeCanceled : Boolean = false

    /**
     * 业务用例内的协程作用域
     * * 该作用域默认在视图Destroy时会取消
     * * 注意在协程作用域结束后，无法继续创建新协程
     * */
    protected val useCaseCoroutineScope : CoroutineScope
        get() = if (isScopeCanceled) createCoroutineScope()
        else mScope?: createCoroutineScope()

    /**
     * 创建用例类的协程作用域
     * */
    protected open fun createCoroutineScope() : CoroutineScope{
        //使用SupervisorJob，不会让作用域内的协程出现异常后，影响整个作用域内所有协程的运行
        return CoroutineScope(SupervisorJob()+Dispatchers.Main.immediate)
            .also {
                isScopeCanceled = false
                mScope = it
            }
    }

    /**
     * 尝试结束用例类中正在运行的任务
     * * 注意在协程作用域结束后，无法继续创建新协程
     * */
    open fun cancel(){
        mScope?.cancel()
    }
}