package com.yupfeg.base.viewmodel

import androidx.annotation.MainThread
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import com.yupfeg.base.domain.UseCase
import com.yupfeg.base.tools.lifecycle.LifecycleEndObserver
import com.yupfeg.logger.ext.logd


/**
 * [ViewModel]基类
 * @author yuPFeG
 * @date 2020/02/15
 */
abstract class BaseViewModel : ViewModel(){
    private val mUseCaseLifecycleObservers : MutableList<LifecycleObserver> = mutableListOf()

    /**是否已订阅视图生命周期*/
    var isSubscribedLifecycle : Boolean = false
        private set

    /**
     * 订阅视图生命周期，绑定管理`UseCase`生命周期
     * * 禁止子类重写，避免在子类操作[Lifecycle]
     * * 最好在视图启动时，绑定视图生命周期，时机尽可能早，否则可能导致`UseCase`生命周期会延迟
     * @param lifecycle Activity/Fragment的lifecycle
     * */
    @MainThread
    open fun bindUseCaseLifecycle(lifecycle: Lifecycle){
        logd("${this.javaClass.name} registerLifecycle : $isSubscribedLifecycle")
        if (isSubscribedLifecycle) return
        isSubscribedLifecycle = true
        for (lifecycleObserver in mUseCaseLifecycleObservers) {
            lifecycle.addObserver(lifecycleObserver)
        }
        lifecycle.addObserver(LifecycleEndObserver(Lifecycle.State.DESTROYED) {
            // 在视图销毁后，重置useCase订阅状态，
            // 防止屏幕旋转（等视图配置修改情况）后无法重新绑定
            clearLifecycleObserver(lifecycle)
        })
    }

    /**
     * 清空绑定生命周期的useCase订阅
     * @param lifecycle
     * */
    @MainThread
    private fun clearLifecycleObserver(lifecycle: Lifecycle){
        if (!isSubscribedLifecycle) return
        isSubscribedLifecycle = false
        for (lifecycleObserver in mUseCaseLifecycleObservers) {
            lifecycle.removeObserver(lifecycleObserver)
        }
    }

    /**
     * 添加需要绑定生命周期的业务用例
     * * 推荐在ViewModel初始化时调用，将`UseCase`绑定`ViewModel`所在作用域的生命周期，
     * 相当于在Domain层使用`lifecycleScope`，默认在视图销毁时，结束`UseCase`任务
     * 并且`ViewModel`要在视图层调用[bindUseCaseLifecycle]函数，绑定视图生命周期才会生效.
     *
     * * 如果不需要绑定视图生命周期，则直接使用[UseCase]，手动管理内部任务
     * @param useCase
     * */
    @Suppress("unused")
    protected fun addUseCase(useCase : UseCase){
        val lifecycleObserver = useCase as? LifecycleObserver
        lifecycleObserver?:return
        mUseCaseLifecycleObservers.add(lifecycleObserver)
    }

    /**
     * 移除业务用例
     * @param useCase
     * */
    @Suppress("unused")
    protected open fun removeUseCase(useCase: UseCase){
        val lifecycleObserver = useCase as? LifecycleObserver
        lifecycleObserver?:return
        mUseCaseLifecycleObservers.remove(lifecycleObserver)
    }

    override fun onCleared() {
        clearUseCase()
        isSubscribedLifecycle = false
    }

    /**
     * 清空业务用例，并结束用例的协程作用域
     * */
    @Suppress("MemberVisibilityCanBePrivate")
    protected fun clearUseCase(){
        if (mUseCaseLifecycleObservers.isNullOrEmpty()) return

//        mUseCaseLifecycleObservers.forEach {
//            (it as? UseCase)?.cancel()
//        }
        mUseCaseLifecycleObservers.clear()
    }

}