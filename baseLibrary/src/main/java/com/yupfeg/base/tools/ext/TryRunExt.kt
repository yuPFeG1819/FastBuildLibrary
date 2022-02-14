package com.yupfeg.base.tools.ext

import java.util.concurrent.CancellationException

/**
 * tryRun/catch捕获的异常结果类
 */
@JvmInline
value class TryResult(
    val throwable: Throwable? = null
)

/**
 * 尝试运行协程逻辑
 * - 通常仅用于在捕获协程内抛出异常，避免捕获到表示协程取消的[CancellationException]
 * @param block 协程运行代码块
 * @return 捕获异常的结果类，如果没有异常则结果类内部的异常为null
 * */
@Suppress("unused")
inline fun tryRun(block : ()->Unit) : TryResult{
    return try {
        block()
        TryResult(null)
    }catch (e : Throwable){
        TryResult(e)
    }
}

/**
 * [TryResult]的拓展中缀函数，捕获执行类型的异常
 * - 捕获的异常会忽略代表协程取消的CancellationException
 * @param block 捕获指定类型异常的代码块
 * */
@Suppress("unused")
@Throws(CancellationException::class)
inline infix fun <reified T : Throwable> TryResult.catch(block: (t : T) -> Unit) {
    if (throwable is CancellationException) throw throwable
    if (throwable is T) block(throwable)
}

/**
 * [TryResult]的拓展中缀函数，捕获除CancellationException外的所有类型的异常
 * @param block 异常捕获逻辑代码块，无法捕获CancellationException
 * */
@Suppress("unused")
@Throws(CancellationException::class)
inline infix fun TryResult.catchAll(block: (t: Throwable) -> Unit){
    if (throwable is CancellationException) throw throwable
    throwable?.also{
        block(it)
    }
}

/**
 * [TryResult]的拓展中缀函数,
 * @param block 捕获到指定异常的逻辑代码块
 * @return 如果成功捕获到指定异常，则消费掉异常，返回新的无异常的[TryResult]结果类。
 * 如果无法捕获到异常，则直接返回当前[TryResult]结果类对象
 * */
@Suppress("unused")
@Throws(CancellationException::class)
inline infix fun <reified T : Throwable> TryResult.catching(
    block : (t : T) -> Unit
) : TryResult{
    if (throwable is CancellationException) throw throwable
    return if (throwable is T){
        block(throwable)
        TryResult(null)
    }else{
        this
    }
}

/**
 * [TryResult]的拓展中缀函数，作为捕获异常逻辑链的末尾执行逻辑
 * @param block 末尾执行逻辑
 * */
inline infix fun TryResult.finally(block: () -> Unit){
    block()
    if (this.throwable != null) throw throwable
}