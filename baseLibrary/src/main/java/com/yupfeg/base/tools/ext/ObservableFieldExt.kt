package com.yupfeg.base.tools.ext

import androidx.databinding.ObservableField

/**
 * [ObservableField]的拓展函数，复制原有的ObservableField值
 * @param action 获取复制数据的函数
 */
@Suppress("unused")
inline fun <reified T> ObservableField<T>.copyValue(action : ((T)->T)){
    val oldValue : T?= get()
    oldValue?.also {
        this.set(action(oldValue))
    }?:run{
        throw NullPointerException("ObservableField<${T::class.java}> not contain value.")
    }
}