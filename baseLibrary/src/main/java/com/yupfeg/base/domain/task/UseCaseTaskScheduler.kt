package com.yupfeg.base.domain.task

import com.yupfeg.base.domain.UseCase

/**
 * `UseCase`在后台运行的任务调度类
 * * 允许`UseCase`脱离`ViewModel`，单独运行在Application进程的后台
 * * 仅提供给后台Service或者其他非UI场景
 * @author yuPFeG
 * @date 2021/08/09
 */
@Suppress("unused")
class UseCaseTaskScheduler {
    private val mUseCaseList : MutableList<UseCase> = mutableListOf()

    @Suppress("unused")
    fun addUseCase(useCase: UseCase){
        mUseCaseList.add(useCase)
    }

    @Suppress("unused")
    fun removeUseCase(useCase: UseCase){
        mUseCaseList.remove(useCase)
    }

    /**
     * 尝试终止当前所有运行中的用例
     * */
    fun onTerminate(){
        for (useCase in mUseCaseList) {
            useCase.cancel()
        }
        mUseCaseList.clear()
    }

}