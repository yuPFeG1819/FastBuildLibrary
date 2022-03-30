package com.yupfeg.base.viewmodel

import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yupfeg.base.tools.process.ProcessLifecycleOwner
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

/**
 * Application作用域的ViewModel代理类
 * - 内部创建的ViewModel相当于进程内单例
 * @author yuPFeG
 * @date 2021/09/20
 */
class AppViewModelDelegate<out T : ViewModel>(
    private val clazz: KClass<T>,
    private val factoryProducer : ()->ViewModelProvider.Factory
){
    private var mViewModel : T? = null

    /**
     * [ComponentActivity]属性委托方法，获取Application作用域的ViewModel
     * @param thisRef 进行委托的类的对象
     * @param property 进行委托的属性的对象
     * */
    operator fun getValue(thisRef : ComponentActivity,property: KProperty<*>) : T{
        return mViewModel?:getViewModelInstance()
    }

    /**
     * [Fragment]属性委托方法，获取Application作用域的ViewModel
     * @param thisRef 进行委托的类的对象
     * @param property 进行委托的属性的对象
     * */
    operator fun getValue(thisRef: Fragment,property: KProperty<*>) : T{
        return mViewModel?:getViewModelInstance()
    }

    private fun getViewModelInstance() : T{
        val viewModelStore = ProcessLifecycleOwner.viewModelStore
        val factory = factoryProducer()
        //Application作用域范围的ViewModel实例（相当于单例）
        return mViewModel?:ViewModelProvider(viewModelStore,factory)[clazz.java]
            .also { mViewModel = it }
    }



}