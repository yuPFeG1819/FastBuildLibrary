package com.yupfeg.base.tools.lifecycle

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

/**
 * 在视图结束时自动销毁的视图生命周期监听
 * @param targetEvent 目标生命周期，默认为onCreate
 * @param doOnAction 具体执行逻辑
 * @author yuPFeG
 * @date 2022/03/10
 */
class AutoLifecycleEventObserver(
    private val targetEvent : Lifecycle.Event = Lifecycle.Event.ON_CREATE,
    private val doOnAction : ()->Unit
) : LifecycleEventObserver {
    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        val currentState = source.lifecycle.currentState
        if (currentState == Lifecycle.State.DESTROYED) {
            //视图已销毁
            source.lifecycle.removeObserver(this)
            if (targetEvent.targetState == currentState){
                doOnAction()
            }
            return
        }

        if (event == Lifecycle.Event.ON_CREATE){
            //在onCreate时调用
            doOnAction()
        }
    }


}