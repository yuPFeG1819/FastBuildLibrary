package com.yupfeg.base.domain

import androidx.annotation.MainThread
import java.util.*

/**
 * `UseCase`的业务用例队列
 * - 从基类`ViewModel`抽离的`UseCase`维护逻辑，将视图场景与非视图场景进行整合。
 * 统一由外部手动创建管理，并在合适时机结束业务用例。避免影响比如`AndroidViewModel`的使用
 * @param initCapacity 队列的初始容量，确定需要添加多少`UseCase`，避免触发扩容影响效率，默认初始数组长度为8
 *
 * @author yuPFeG
 * @date 2021/08/09
 */
@Suppress("unused")
class UseCaseQueue(initCapacity : Int = 8) {
    private val mUseCases : Queue<UseCase> = ArrayDeque(initCapacity)

    /**
     * 添加业务用例
     * - 推荐在ViewModel初始化时调用，将`UseCase`绑定`ViewModel`所在作用域，
     * - 在`ViewModel`销毁时，调用`cancelAndRemoveAll`来结束`UseCase`内部作用域
     * @param useCase
     * */
    @MainThread
    fun add(useCase: UseCase){
        mUseCases.add(useCase)
    }

    /**
     * 移除业务用例，并结束内部任务
     * @param useCase
     * */
    @MainThread
    fun cancel(useCase: UseCase){
        useCase.cancel()
        mUseCases.remove(useCase)
    }

    /**
     * 终止当前所有运行中的用例
     * */
    fun cancelAll(){
        for (useCase in mUseCases) {
            useCase.cancel()
        }
    }

    /**
     * 取消并移除所有业务用例
     * */
    fun cancelAndRemoveAll(){
        while (mUseCases.isNotEmpty()){
            val useCase = mUseCases.poll()
            useCase?:continue
            //取消业务用例内部任务
            useCase.cancel()
        }
    }

}