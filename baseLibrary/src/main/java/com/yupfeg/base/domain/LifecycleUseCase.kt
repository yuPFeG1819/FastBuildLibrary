package com.yupfeg.base.domain

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.yupfeg.logger.ext.logd

/**
 * 允许绑定视图生命周期的业务逻辑用例基类
 * - 其实现类仅能在`ViewModel`内使用，需要手动绑定到视图的[Lifecycle]，不允许使用在后台场景的用例继承该类
 * - 谨慎继承该类，避免子类直接使用LifecycleOwner
 * - 特定场景可能需要视图生命周期，可以抽离部分页面的功能
 * ```
 *    //在Activity或Fragment内，订阅生命周期相关的调用
 *    this.lifecycle.addObserver(useCase)
 * ```
 *
 * - 多业务用例绑定生命的执行顺序：
 * 视图启动（onCreate -> onResume）的生命周期顺序是按照添加订阅顺序依次调用，
 * 视图结束（onPause -> onDestroy）的生命周期顺序是按照添加订阅顺序倒序调用，
 * 详情参见[DefaultLifecycleObserver]的调用顺序
 * @author yuPFeG
 * @date 2020/10/24
 */
@Suppress("unused")
abstract class LifecycleUseCase : UseCase(), LifecycleEventObserver,DefaultLifecycleObserver {

    /**初始化标识，避免在UI重建时，重复获取数据*/
    var isInitial : Boolean = true
        protected set

    /**是否输出调试用的生命周期日志*/
    @Suppress("MemberVisibilityCanBePrivate")
    open var isPrintDebugLifecycleLog : Boolean = false
        protected set

    //<editor-fold desc="Lifecycle监听">

    /**
     * 视图状态变化时调用
     * * 会先调用生命周期函数，然后才调用`onStateChanged`
     * */
    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (source.lifecycle.currentState <= Lifecycle.State.DESTROYED) {
            //在视图生命周期结束时，结束移除生命周期订阅
            source.lifecycle.removeObserver(this)
        }
    }

    //禁止子类重写
    final override fun onCreate(owner: LifecycleOwner) {
        printLifecycleLog(Lifecycle.Event.ON_CREATE)
        onCreate()
    }

    final override fun onStart(owner: LifecycleOwner) {
        printLifecycleLog(Lifecycle.Event.ON_START)
        onStart()
    }

    final override fun onResume(owner: LifecycleOwner) {
        printLifecycleLog(Lifecycle.Event.ON_RESUME)
        onResume()
    }

    final override fun onPause(owner: LifecycleOwner) {
        printLifecycleLog(Lifecycle.Event.ON_PAUSE)
        onPause()
    }

    final override fun onStop(owner: LifecycleOwner) {
        printLifecycleLog(Lifecycle.Event.ON_STOP)
        onStop()
    }

    final override fun onDestroy(owner: LifecycleOwner) {
        printLifecycleLog(Lifecycle.Event.ON_DESTROY)
        onDestroy()
    }

    protected open fun printLifecycleLog(event : Lifecycle.Event){
        if (!isPrintDebugLifecycleLog) return

        val lifecycleEventName = when(event){
            Lifecycle.Event.ON_CREATE -> "onCreate"
            Lifecycle.Event.ON_START -> "onStart"
            Lifecycle.Event.ON_RESUME -> "onResume"
            Lifecycle.Event.ON_PAUSE -> "onPause"
            Lifecycle.Event.ON_STOP -> "onStop"
            Lifecycle.Event.ON_DESTROY -> "onDestroy"
            else -> "other event"
        }
        logd("${this.javaClass.name} use case lifecycle : $lifecycleEventName")
    }

    // </editor-fold>

    /**
     * 外部绑定的视图生命周期onCreate
     * * 避免子类直接使用LifecycleOwner
     * * 视图启动的生命周期顺序是按照`viewModel.addUseCase`的添加顺序依次调用
     * */
    protected open fun onCreate() = Unit

    /**
     * 外部绑定的生命周期onStart
     * * 避免子类直接使用LifecycleOwner
     * * 视图启动的生命周期顺序是按照`viewModel.addUseCase`的添加顺序依次调用
     * */
    protected open fun onStart() = Unit

    /**
     * 外部绑定的视图生命周期onResume
     * * 避免子类直接使用LifecycleOwner
     * * 视图启动的生命周期顺序是按照`viewModel.addUseCase`的添加顺序依次调用
     * */
    protected open fun onResume() = Unit

    /**
     * 外部绑定的视图生命周期onPause
     * * 避免子类直接使用LifecycleOwner
     * * 视图结束生命周期顺序是按照`viewModel.addUseCase`的添加顺序倒序调用
     * */
    protected open fun onPause() = Unit

    /**
     * 外部绑定的视图生命周期onStop
     * * 避免子类直接使用LifecycleOwner
     * * 视图结束生命周期顺序是按照`viewModel.addUseCase`的添加顺序倒序调用
     * */
    protected open fun onStop() = Unit

    /**
     * 外部绑定的生命周期onDestroy
     * * 避免子类直接使用LifecycleOwner
     * * 视图结束生命周期顺序是按照`viewModel.addUseCase`的添加顺序倒序调用
     * */
    protected open fun onDestroy() = Unit

}