package com.yupfeg.base.tools.bridge

import androidx.annotation.MainThread
import androidx.lifecycle.*

/**
 * LiveData的拓展函数
 * @author yuPFeG
 * @date 2020/02/28
 */

/**
 * [MutableLiveData]的拓展函数，获取LiveData原有的值，通过[copyAction]函数体，将新数据并赋值给LiveData
 * * 内部使用[MutableLiveData.postValue]，能够兼容子线程调用赋值
 * * PS：使用[MutableLiveData.postValue]存在一定延迟，短时间内大量数据同时变化，会有被覆盖的风险
 * * PS：LiveData内必须包含值，否则会抛出异常
 *
 * @param copyAction 获取新数据的函数，以旧数据作为参数输入
 */
@Suppress("unused")
inline fun <reified T> MutableLiveData<T>.copyValue(crossinline copyAction : ((T)->T)){
    val oldValue : T?= value
    oldValue?.also {
        this.postValue(copyAction(oldValue))
    }?:run {
        throw NullPointerException("MutableLiveData<${T::class.java}> not contain value.")
    }
}

/**
 * [MutableLiveData]的拓展函数，获取LiveData原有的值，通过[copyAction]函数体，将新数据并赋值给LiveData
 * * 内部使用[MutableLiveData.setValue]，只能在主线程调用赋值
 * * PS：LiveData内必须包含值，否则会抛出异常
 *
 * @param copyAction 获取新数据的函数，以旧数据作为参数输入
 */
@Suppress("unused")
@MainThread
inline fun <reified T> MutableLiveData<T>.copyValueNow(crossinline copyAction: (T) -> T){
    val oldValue : T?= value
    oldValue?.also {
        this.value = copyAction(oldValue)
    }?:run {
        throw NullPointerException("MutableLiveData<${T::class.java}> not contain value.")
    }
}

/**
 * [LiveData]的拓展函数，Value or else
 *
 * @param T
 * @param default
 * @receiver
 * @return
 */
@Suppress("unused")
inline fun <reified T> LiveData<T>.valueOrElse(default: ()->T) : T
    = this.value?:run(default)