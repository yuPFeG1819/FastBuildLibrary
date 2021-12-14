package com.yupfeg.base.tools.navigation

import android.app.Activity
import android.view.View
import androidx.annotation.IdRes
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.yupfeg.base.tools.lifecycle.LifecycleEndObserver
import java.lang.IllegalArgumentException
import kotlin.reflect.KProperty

/**
 * [NavController]的委托类
 * * 使用by关键字委托获取[NavController]实例
 * @author yuPFeG
 * @date 2021/03/15
 */
class NavControllerMediator(lifecycle: Lifecycle) {
    private var viewInstance : View ?= null
    private var viewResId : Int = 0

    private var mInstance : NavController ?= null

    constructor(@IdRes viewResId : Int, lifecycle: Lifecycle) : this(lifecycle){
        viewResId.takeIf { it != 0 }?.also {viewId -> this.viewResId = viewId }
    }

    constructor(view : View,lifecycle: Lifecycle) : this(lifecycle){
        this.viewInstance = view
    }

    init {
        lifecycle.addObserver(LifecycleEndObserver(Lifecycle.State.DESTROYED){
            //在Lifecycle.State.DESTROYED 将会销毁Nav引用的View对象，防止内存泄漏
            viewInstance = null
            mInstance = null
        })
    }

    operator fun getValue(thisRef: Activity, property: KProperty<*>): NavController {
        return mInstance ?: findNavController(thisRef)

    }

    operator fun getValue(thisRef: Fragment, property: KProperty<*>): NavController {
        return mInstance ?: findNavController(thisRef)
    }

    private fun findNavController(thisRef : Activity) : NavController{
        viewInstance?.also {
            mInstance = Navigation.findNavController(it)
        }?: takeIf { viewResId != 0 }?.also {
            viewInstance = ActivityCompat.requireViewById(thisRef,viewResId)
            mInstance = Navigation.findNavController(viewInstance!!)
        }
        return mInstance
            ?:throw IllegalArgumentException("You must set view or viewId before get NavController")
    }

    private fun findNavController(thisRef : Fragment) : NavController{
        return thisRef.findNavController().apply {
            mInstance = this
        }
    }

}