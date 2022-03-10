package com.yupfeg.base.tools.databinding.proxy

import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.yupfeg.base.tools.databinding.ext.getInflateMethod
import com.yupfeg.base.tools.lifecycle.LifecycleEndObserver
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * [Fragment]拓展函数，通过属性委托的by关键字，获取dataBinding的对象
 * @param T layout对应的DataBinding自动生成类
 */
@Suppress("unused")
inline fun <reified T : ViewDataBinding> Fragment.bindingFragment()
        = FragmentDataBindingProxy(T::class.java,this.viewLifecycleOwner.lifecycle)

/**
 * [Fragment]获取`DataBinding`实例的属性委托类
 * * 使用by关键字委托获取`DataBinding`实例
 * @param clazz DataBinding类class
 * @param lifecycle 绑定[Fragment]的[Lifecycle]，仅用于订阅生命周期，注意使用[Fragment.getViewLifecycleOwner]
 * @author yuPFeG
 * @date 2021/03/09
 */
class FragmentDataBindingProxy<out T : ViewDataBinding>(
    clazz: Class<T>,
    lifecycle : Lifecycle,
) : ReadOnlyProperty<Fragment,T>{

    private var mViewBinding: T? = null

    /**
     * 通过反射获取自动生成DataBinding类内部的inflate方法
     * */
    private val mBindInflateMethod = clazz.getInflateMethod()

    init {
        lifecycle.addObserver(LifecycleEndObserver(Lifecycle.State.DESTROYED){
            //在生命周期结束时,解绑并销毁binding数据，防止内存泄漏
            mViewBinding?.unbind()
            mViewBinding = null
        })
    }


    override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        return mViewBinding ?: createBindingInstance(thisRef)
    }

    private fun createBindingInstance(fragment: Fragment) : T{
        @Suppress("UNCHECKED_CAST")
        val dataBinding = if (fragment.view == null){
            //在onCreateView内调用，此时view还没有赋值，
            //反射方法进行构建DataBinding类实例
            mBindInflateMethod.invoke(fragment.layoutInflater) as? T
        }else{
            //在onViewCreated内或之后调用，view已赋值
//            (mBindViewMethod.invoke(null,fragment.requireView()) as T)
            DataBindingUtil.bind(fragment.requireView())
        }
        dataBinding?: throw NullPointerException(
            "can not create DataBinding instance , check DataBinding Class on this Fragment"
        )

        return dataBinding.apply {
            mViewBinding = this
            //由于Fragment与View的生命周期不一致，所有要使用viewLifecycleOwner
            lifecycleOwner = fragment.viewLifecycleOwner
        }
    }

}