package com.yupfeg.base.viewmodel

import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import com.yupfeg.base.domain.UseCase
import java.util.*


/**
 * [ViewModel]基类，
 * - 内部提供一个保存所有用例的队列结构。
 * 确保在`ViewModel`销毁时，会自动移除并结束`UseCase`内部任务
 * @author yuPFeG
 * @date 2020/02/15
 */
abstract class BaseViewModel : ViewModel(){
    private val mUseCases : Queue<UseCase> = LinkedList()

    /**
     * 添加业务用例
     * - 推荐在ViewModel初始化时调用，将`UseCase`绑定`ViewModel`所在作用域，
     * 在`ViewModel`销毁时，会自动移除并结束`UseCase`内部任务
     * @param useCase 支持[LifecycleObserver]的UseCase
     * */
    @Suppress("unused")
    @MainThread
    protected open fun addUseCase(useCase : UseCase){
        mUseCases.offer(useCase)
    }

    /**
     * 移除业务用例，并结束内部任务
     * @param useCase
     * */
    @Suppress("unused")
    @MainThread
    protected open fun removeUseCase(useCase: UseCase){
        useCase.cancel()
        mUseCases.remove(useCase)
    }

    override fun onCleared() {
        cancelAllUseCase()
    }

    /**
     * 结束所有绑定的`UseCase`内部任务
     * */
    @MainThread
    protected open fun cancelAllUseCase(){
        //取消并移除所有业务用例
        while (mUseCases.isNotEmpty()){
            val useCase = mUseCases.poll()
            useCase?:continue
            //取消业务用例内部任务
            useCase.cancel()
        }
    }

}