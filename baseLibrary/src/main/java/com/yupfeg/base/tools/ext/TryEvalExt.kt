package com.yupfeg.base.tools.ext

import java.lang.Exception
import java.util.concurrent.CancellationException

/**
 * tryEval尝试捕获异常并保存值的结果类
 */
@JvmInline
value class TryEvalResult(
    val value : Any? = null
)

/**
 * 尝试捕获代码块抛出的异常
 * - 通常仅用于协程内部使用，避免捕获到表示协程取消的[CancellationException]
 * @param block 执行代码块，同时赋值返回值
 * @return 代码块的结果类，如果没有异常则结果类包装对应的代码块返回值，如果捕获到异常，结果类包装对应的异常
 * */
inline fun <reified V> tryEval(block : ()->V) : TryEvalResult{
    return try {
        TryEvalResult(block())
    }catch (e : Exception){
        TryEvalResult(e)
    }
}

/**
 * [TryEvalResult]的拓展中缀函数，捕获指定类型的异常
 * - 通常仅用于协程内部使用，避免捕获协程被取消的[CancellationException]异常
 * - 相比[catch]函数，能捕获多个异常形成异常处理链
 *
 * @param block 处理捕获指定类型异常的代码块，高阶函数返回捕获异常时的值
 * @return 如果成功捕获指定类型的异常，则返回新的[TryEvalResult]对象，包装指定类型的值
 * 如果无法捕获到异常则返回原本的[TryEvalResult]对象
 * */
@Suppress("unused")
inline infix fun <reified T : Throwable,R> TryEvalResult.catching(block: (t : T) -> R) : TryEvalResult{
    if (this.value is CancellationException) throw value

    return if (this.value is T){
        TryEvalResult(block(value))
    }else{
        this
    }
}

/**
 * [TryEvalResult]的拓展中缀函数，捕获指定类型的异常，结束异常捕获处理链
 * - 通常仅用于协程内部使用，避免捕获协程被取消的[CancellationException]异常
 * - 属于异常处理链的结束符
 *
 * @param block 处理捕获指定类型异常的代码块，高阶函数返回捕获异常时的值
 * @return 如果成功捕获返回[block]代码块的值，反之返回异常处理链的上游结果携带的值
 */
inline infix fun <reified T : Throwable,reified R> TryEvalResult.catch(
    block: (t: T) -> R
) : R{
    if (value is CancellationException) throw value
    return if (value is T) block(value)
    else value as R
}

/**
 * [TryEvalResult]的拓展中缀函数，处理捕获的所有异常，并返回对应的值，结束异常捕获处理链
 * - 通常仅用于协程内部使用，避免捕获协程被取消的[CancellationException]异常
 * - 属于异常处理链的结束符
 * @param block 处理捕获异常的代码块，函数类型返回捕获异常时的需要提供的值
 * @return 返回指定泛型类型的值
 * */
@Suppress("unused")
inline infix fun <reified R> TryEvalResult.catchAll(block: (t : Throwable) -> R) : R{
    if (this.value is CancellationException) throw value

    return if (value is Throwable){
        block(value)
    }else{
        value as R
    }
}

/**
 * [TryEvalResult]的拓展中缀函数，在捕获异常后执行的结尾逻辑
 * - 属于异常处理链的结束符
 *
 * @param block 结束代码块
 * @return 返回异常捕获处理流的上游携带的值，如果上游处理流还有未捕获的异常
 * */
@Suppress("unused")
inline infix fun <reified R> TryEvalResult.finally(block: () -> Unit) : R{
    block()
    return if (value is Throwable) throw value
    else value as R
}