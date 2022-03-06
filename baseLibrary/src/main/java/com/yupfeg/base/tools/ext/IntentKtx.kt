package com.yupfeg.base.tools.ext

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.os.bundleOf

/**
 * [Activity]的拓展函数，延迟获取`Extras`内指定key的非空值
 * - 使用by关键字进行代理
 * @param key bundle的key值
 * @param default 默认值，如果无法获取对应值，则使用默认值
 * */
inline fun <reified T> Activity.intentExtrasLazy(
    key : String,default : T
) = lazy(LazyThreadSafetyMode.NONE){
    intent.extras[key] ?: default
}

/**
 * [Activity]的拓展函数，延迟获取`Extras`内指定key的可空值
 * - 使用by关键字代理
 * @param key bundle的key值
 * @return 如果无法获取对应值，则返回null
 * */
inline fun <reified T> Activity.intentExtrasLazyOrNull(key: String)
    = lazy<T?>(LazyThreadSafetyMode.NONE){
        intent.extras[key]
    }

/**
 * [Bundle]的运算符重载函数，简化对可空类型的[Bundle]，可以直接调用get方法
 * 如：
 * ```
 * intent.extras[key]
 * ```
 * @param
 * */
inline operator fun <reified T> Bundle?.get(key : String) : T? = this?.get(key) as? T

/**
 * [Context]的拓展函数，使用键值对快速创建[Intent]对象
 * @param pairs 保存在[Bundle]的键值对
 * */
@Suppress("unused")
inline fun <reified T> Context.intentOf(vararg pairs: Pair<String,*>) : Intent
    = intentOf<T>(bundleOf(*pairs))

/**
 * [Context]的拓展函数，快速创建[Intent]对象
 * @param bundle [Bundle]
 * */
inline fun <reified T> Context.intentOf(bundle: Bundle) : Intent
    = Intent(this,T::class.java).putExtras(bundle)