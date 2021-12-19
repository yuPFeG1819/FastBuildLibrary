package com.yupfeg.base.viewmodel

import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yupfeg.base.application.BaseApplication
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

/**
 * 作用域覆盖Application的ViewModel代理类
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
        return mViewModel?:getViewModelInstance(thisRef)
    }

    /**
     * [Fragment]属性委托方法，获取Application作用域的ViewModel
     * @param thisRef 进行委托的类的对象
     * @param property 进行委托的属性的对象
     * */
    operator fun getValue(thisRef: Fragment,property: KProperty<*>) : T{
        return mViewModel?:getViewModelInstance(thisRef)
    }

    private fun getViewModelInstance(lifecycleOwner : LifecycleOwner) : T{
        val application = obtainApplication(lifecycleOwner)
        val factory = factoryProducer()
        //从Application作用域范围的ViewModel实例（相当于单例）
        return mViewModel?:ViewModelProvider(application.viewModelStore,factory)[clazz.java]
            .also { mViewModel = it }
    }

    private fun obtainApplication(lifecycleOwner: LifecycleOwner) : BaseApplication{
        return when(lifecycleOwner){
            is ComponentActivity -> {
                lifecycleOwner.applicationContext as? BaseApplication
                    ?:throw IllegalArgumentException("check your application extends BaseApplication")
            }
            is Fragment -> {
                lifecycleOwner.requireActivity().applicationContext as? BaseApplication
                    ?:throw IllegalArgumentException("check your application extends BaseApplication")
            }
            else -> {
                //ViewModel仅在视图层使用
                throw IllegalStateException("Activity or Fragment is null! cant get ViewModel")
            }
        }
    }


}