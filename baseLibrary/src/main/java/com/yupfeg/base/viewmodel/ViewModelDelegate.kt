package com.yupfeg.base.viewmodel

import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

/**
 * ViewModel的委托类
 *
 * * ViewModel在外部调用时，通常是val修饰的不可重复赋值对象，所以这里只重载了getValue()方法
 * @author yuPFeG
 * @date 2020/02/15
 */
class ViewModelDelegate<out T : ViewModel>(
    private val clazz: KClass<T>,
    private val storeProducer : ()->ViewModelStore,
    private val factoryProducer : ()->ViewModelProvider.Factory,
) {
    private var mViewModel : T? = null

    /**
     * [ComponentActivity]属性委托方法
     * @param thisRef 进行委托的类的对象
     * @param property 进行委托的属性的对象
     * */
    operator fun getValue(thisRef: ComponentActivity, property: KProperty<*>)
            = mViewModel?:getViewModelInstance(thisRef)

    /**
     * [Fragment]属性委托方法
     * @param thisRef 进行委托的类的对象
     * @param property 进行委托的属性的对象
     * */
    operator fun getValue(thisRef: Fragment, property: KProperty<*>)
            = mViewModel?:getViewModelInstance(thisRef)


    private fun getViewModelInstance(lifecycleOwner: LifecycleOwner) : T {
        val store = storeProducer()
        val factory = factoryProducer()
        return ViewModelProvider(store,factory)[clazz.java].apply {
            mViewModel = this
            //订阅视图生命周期，useCase绑定视图生命周期
            (this as? BaseViewModel)?.bindUseCaseLifecycle(lifecycleOwner.lifecycle)
        }
    }

}



