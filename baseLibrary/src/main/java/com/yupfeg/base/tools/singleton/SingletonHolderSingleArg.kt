package com.yupfeg.base.tools.singleton

/**
 * 只有一个参数构造函数的单例模式提取父类
 * Used to allow Singleton with arguments in Kotlin while keeping the code efficient and safe.
 *
 * See https://medium.com/@BladeCoder/kotlin-singletons-with-argument-194ef06edd9e
 */
@Suppress("unused")
open class SingletonHolderSingleArg<out T, in A>(private val creator: (A) -> T) {

    @Volatile
    private var mInstance: T? = null

    fun getInstance(arg: A): T =
            mInstance ?: synchronized(this) {
                mInstance ?: creator(arg).apply { mInstance = this }
            }
}