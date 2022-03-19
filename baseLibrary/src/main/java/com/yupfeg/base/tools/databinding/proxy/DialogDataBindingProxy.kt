package com.yupfeg.base.tools.databinding.proxy

import android.app.Dialog
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Lifecycle
import com.yupfeg.base.tools.lifecycle.AutoLifecycleStateObserver
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * [Dialog]拓展函数，通过属性委托的by关键字，获取dataBinding的对象
 * @param T layout对应的DataBinding自动生成类
 * @param layoutId 布局id
 * @param lifecycle 当前Dialog使用lifecycle
 * @param doOnRelease 视图销毁时执行的DataBinding内部变量回收操作，可选，默认为null
 */
@Suppress("unused")
inline fun <reified T : ViewDataBinding> Dialog.bindingDialog(
    @LayoutRes layoutId: Int,lifecycle: Lifecycle,noinline doOnRelease : ((T)->Unit)? = null
) = DialogDataBindingProxy(layoutId,lifecycle,doOnRelease)

/**
 * [Dialog]获取`DataBinding`实例的属性委托类
 * @author yuPFeG
 * @date 2022/03/10
 */
class DialogDataBindingProxy<T : ViewDataBinding>(
    @LayoutRes private val layoutId : Int,
    lifecycle: Lifecycle,
    private val doOnRelease : ((T)->Unit)? = null
) : ReadOnlyProperty<Dialog, T> {

    private var mDataBinding : T? = null

    init {
        lifecycle.addObserver(AutoLifecycleStateObserver(Lifecycle.State.DESTROYED){
            //在生命周期结束时，回收binding对象，防止内存泄漏
            mDataBinding?.also {
                doOnRelease?.invoke(it)
                it.unbind()
                mDataBinding = null
            }
        })
    }

    override fun getValue(thisRef: Dialog, property: KProperty<*>): T {
        return mDataBinding ?: createNewInstance(thisRef)

    }

    private fun createNewInstance(dialog: Dialog) : T{
        val dataBinding = DataBindingUtil.inflate(
            dialog.layoutInflater,layoutId,null,false
        ) as? T
        dataBinding?:throw throw NullPointerException(
            "can not inflate DataBinding instance , check DataBinding Class on this Dialog"
        )
        return dataBinding.apply {
            dialog.setContentView(dataBinding.root)
            mDataBinding = this
        }
    }


}