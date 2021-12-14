package com.yupfeg.base.view.fragment

import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.yupfeg.base.tools.lifecycle.LifecycleEndObserver
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * [Fragment]拓展函数，通过属性委托的by关键字，获取dataBinding的对象
 * @param T layout对应的DataBinding自动生成类
 */
@Suppress("unused")
inline fun <reified T : ViewDataBinding> Fragment.bindingFragment()
        = FragmentDataBindingProxy(T::class.java,this.lifecycle)

/**
 * [Fragment]的`DataBinding`实例的属性委托类
 * * 使用by关键字委托获取`DataBinding`实例
 * @param clazz DataBinding类class
 * @param lifecycle 绑定[Fragment]的[Lifecycle]，仅用于订阅生命周期
 * @param endLifecycle 销毁binding对象的生命周期
 * @author yuPFeG
 * @date 2021/03/09
 */
class FragmentDataBindingProxy<out T : ViewDataBinding>(
    clazz: Class<T>,
    lifecycle : Lifecycle,
    endLifecycle : Lifecycle.State = Lifecycle.State.DESTROYED
) : ReadOnlyProperty<Fragment,T>{
    @Volatile
    private var mViewBinding: T? = null
    //通过反射获取自动生成DataBinding类的bind方法
    private var mBindViewMethod = clazz.getMethod("bind", View::class.java)

    init {
        lifecycle.addObserver(LifecycleEndObserver(endLifecycle){
            //在生命周期结束时,解绑并销毁binding数据，防止内存泄漏
            mViewBinding?.unbind()
            mViewBinding = null
        })
    }


    override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        return mViewBinding ?: prepareBindingInstance(thisRef)
    }

    @Synchronized
    private fun prepareBindingInstance(fragment: Fragment) : T{
        //反射调用bind方法
        @Suppress("UNCHECKED_CAST")
        return (mBindViewMethod.invoke(null,fragment.view) as T).apply {
            mViewBinding = this
            lifecycleOwner = fragment
        }
    }

}