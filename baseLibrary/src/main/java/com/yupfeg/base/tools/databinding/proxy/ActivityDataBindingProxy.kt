package com.yupfeg.base.tools.databinding.proxy

import androidx.activity.ComponentActivity
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Lifecycle
import com.yupfeg.base.tools.lifecycle.AutoLifecycleStateObserver
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * [ComponentActivity]拓展函数，通过属性委托的by关键字，获取dataBinding的对象
 * @param T layout对应的DataBinding自动生成类
 * @param layoutId 布局id
 * @param doOnDestroy 执行需要回收DataBinding内部变量的操作，可选，默认为null
 */
@Suppress("unused")
inline fun <reified T : ViewDataBinding> ComponentActivity.bindingActivity(
    @LayoutRes layoutId : Int, noinline doOnDestroy : ((T?)->Unit)? = null
) = ActivityDataBindingDelegate(layoutId,this.lifecycle,doOnDestroy)

/**
 * [ComponentActivity]获取`DataBinding`实例的属性委托类
 * @param layoutId 布局Id
 * @param lifecycle [ComponentActivity]的lifecycle，仅用于订阅生命周期
 * @author yuPFeG
 * @date 2021/03/09
 */
class ActivityDataBindingDelegate<T : ViewDataBinding>(
    @LayoutRes val layoutId : Int,
    lifecycle : Lifecycle,
    private val doOnDestroy: ((T?) -> Unit)? = null,
) : ReadOnlyProperty<ComponentActivity,T>{

    private var mViewBinding : T? = null

    init {
        lifecycle.addObserver(AutoLifecycleStateObserver(Lifecycle.State.DESTROYED){
            //在Lifecycle.State.DESTROYED 将会解绑并销毁binding，防止内存泄漏
            mViewBinding?.also {
                doOnDestroy?.invoke(it)
                it.unbind()
            }
            mViewBinding = null
        })
    }

    override fun getValue(thisRef: ComponentActivity, property: KProperty<*>): T {
        return mViewBinding ?: prepareViewBindingInstance(thisRef)
    }

    private fun prepareViewBindingInstance(thisRef: ComponentActivity) : T{
        return DataBindingUtil.setContentView<T>(thisRef, layoutId).apply{
            mViewBinding = this
            lifecycleOwner = thisRef
        }
    }

}