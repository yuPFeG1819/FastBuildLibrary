package com.yupfeg.base.tools.lifecycle

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

/**
 * 对指定视图生命周期状态的监听
 * @author yuPFeG
 * @date 2021/03/15
 */
class AutoLifecycleStateObserver(
    private val targetState : Lifecycle.State = Lifecycle.State.DESTROYED,
    private val doOnStateAction : ()->Unit
) : LifecycleEventObserver {

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        val currentState = source.lifecycle.currentState
        if (currentState == Lifecycle.State.DESTROYED) {
            //视图已销毁
            source.lifecycle.removeObserver(this)
            if (targetState == Lifecycle.State.DESTROYED){
                doOnStateAction.invoke()
            }
            return
        }
        //在指定生命周期执行操作
        if (currentState == targetState) {
            doOnStateAction.invoke()
        }
    }

}