package com.yupfeg.base.tools.databinding.proxy

import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.yupfeg.base.tools.databinding.ext.getInflateMethod
import com.yupfeg.base.tools.lifecycle.AutoLifecycleEventObserver
import com.yupfeg.base.tools.lifecycle.AutoLifecycleStateObserver
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * [Fragment]拓展函数，通过属性委托的by关键字，获取dataBinding的对象
 * @param T layout对应的DataBinding自动生成类
 * @param doOnRelease 视图销毁时执行的DataBinding内部变量回收操作，可选，默认为null
 */
@Suppress("unused")
inline fun <reified T : ViewDataBinding> Fragment.bindingFragment(
    noinline doOnRelease : ((T)->Unit)? = null
) = FragmentDataBindingProxy(T::class.java,this,doOnRelease)

/**
 * [Fragment]获取`DataBinding`实例的属性委托类
 * * 使用by关键字委托获取`DataBinding`实例
 * @param clazz DataBinding类class
 * @param fragment 绑定的[Fragment]的[Lifecycle]，仅用于订阅生命周期，注意使用[Fragment.getViewLifecycleOwner]
 * @author yuPFeG
 * @date 2021/03/09
 */
class FragmentDataBindingProxy<out T : ViewDataBinding>(
    clazz: Class<T>,
    fragment: Fragment,
    private val doOnRelease : ((T)->Unit)? = null
) : ReadOnlyProperty<Fragment,T>{

    private var mViewBinding: T? = null

    /**
     * 通过反射获取自动生成DataBinding类内部的inflate方法
     * */
    private val mBindInflateMethod = clazz.getInflateMethod()

    init {
        fragment.lifecycle.addObserver(AutoLifecycleEventObserver(Lifecycle.Event.ON_CREATE){
            //Fragment与其中的View生命周期可能存在不一致情况，需要分开进行订阅，否则可能导致DataBinding为空
            //在onCreate时添加订阅ViewLifecycleOwner的生命周期，
            fragment.viewLifecycleOwnerLiveData.observe(fragment){ viewLifecycleOwner->
                //在viewLifecycleOwner有值时添加订阅View的生命周期
                viewLifecycleOwner?.lifecycle?.addObserver(
                    AutoLifecycleStateObserver(Lifecycle.State.DESTROYED){
                        //在View生命周期结束时,解绑并销毁binding数据，防止内存泄漏
                        mViewBinding?.also {
                            doOnRelease?.invoke(it)
                            it.unbind()
                            mViewBinding = null
                        }
                })
            }

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
            mBindInflateMethod.invoke(null,fragment.layoutInflater) as? T
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