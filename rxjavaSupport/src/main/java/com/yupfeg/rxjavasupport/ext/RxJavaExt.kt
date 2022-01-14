package com.yupfeg.rxjavasupport.ext

import com.yupfeg.dispatcher.ExecutorProvider
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.util.concurrent.Executor

// <editor-fold desc="BehaviorSubject拓展函数">

/**
 * 复制原有[BehaviorSubject]持有的数据，按照[map]函数进行转化
 *
 * @param T
 * @param map
 * @receiver
 */
@Suppress("unused")
inline fun <reified T> BehaviorSubject<T>.copyMap(map: (T) -> T) {
    val oldValue: T? = value
    oldValue?.also {
        onNext(map(oldValue))
    }?:throw NullPointerException("BehaviorSubject<${T::class.java}> not contain value.")
}

/**
 * Copy map from list
 *
 * @param T
 * @param map
 * @receiver
 */
@Suppress("unused")
inline fun <reified T> BehaviorSubject<T>.copyMapFromList(map: (T) -> List<T>) {
    val oldValue: T? = value
    oldValue?.also {
        map(oldValue).forEach { onNext(it) }
    }?: throw NullPointerException("BehaviorSubject<${T::class.java}> not contain value.")
}

// </editor-fold>

// <editor-fold desc="Complete拓展函数">

/**
 * [Completable]拓展方法，将上游事件源置于子线程
 * * 在一个事件流中仅能生效一次
 * @param targetExecutor 目标子线程的线程池，默认使用[ExecutorProvider.ioExecutor]
 * */
@Suppress("unused")
fun Completable.upToBackThread(
    targetExecutor : Executor = ExecutorProvider.ioExecutor
) : Completable {
    return this.subscribeOn(Schedulers.from(targetExecutor))
}

/**
 * [Completable]拓展函数，将下游事件源置于子线程
 * @param targetExecutor 目标子线程的线程池，默认使用[ExecutorProvider.ioExecutor]
 *
 * */
@Suppress("unused")
fun Completable.downToBackThread(
    targetExecutor : Executor = ExecutorProvider.ioExecutor
) : Completable {
    return this.observeOn(Schedulers.from(targetExecutor))
}

/**
 * [Flowable]类型的拓展函数，将上游事件源置于子线程，下游置于主线程
 * @param targetExecutor 目标子线程的线程池，默认使用[ExecutorProvider.ioExecutor]
 */
@Suppress("unused")
fun Completable.upToBackThreadDownToMain(
    targetExecutor: Executor = ExecutorProvider.ioExecutor
) : Completable {
    return this.compose{
        it.subscribeOn(Schedulers.from(targetExecutor))
            .observeOn(AndroidSchedulers.mainThread())
    }
}

/**
 * [Completable]的拓展函数，将下游事件源置于子线程
 * */
@Suppress("unused")
fun Completable.downToMainThread() : Completable{
    return this.observeOn(AndroidSchedulers.mainThread())
}

// </editor-fold>

// <editor-fold desc="Maybe拓展函数">
/**
 * [Maybe]拓展方法，将上游事件源置于子线程
 * * 在一个事件流中仅能生效一次
 * @param targetExecutor 目标子线程的线程池,默认使用[ExecutorProvider.ioExecutor]
 * */
@Suppress("unused")
fun <T : Any> Maybe<T>.upToBackThread(
    targetExecutor: Executor = ExecutorProvider.ioExecutor
) : Maybe<T> {
    return this.subscribeOn(Schedulers.from(targetExecutor))
}

/**
 * [Maybe]的拓展函数，将下游事件源置于子线程
 * @param targetExecutor 目标子线程的线程池，默认使用[ExecutorProvider.ioExecutor]
 * */
@Suppress("unused")
fun <T : Any> Maybe<T>.downToBackThread(
    targetExecutor: Executor = ExecutorProvider.ioExecutor
) : Maybe<T>{
    return this.observeOn(Schedulers.from(targetExecutor))
}

/**
 * [Maybe]的拓展函数，将下游事件源置于子线程
 * */
@Suppress("unused")
fun <T : Any> Maybe<T>.downToMainThread() : Maybe<T>{
    return this.observeOn(AndroidSchedulers.mainThread())
}

/**
 * [Maybe]的拓展函数，将上游事件源置于子线程，下游置于主线程
 * @param targetExecutor 目标子线程的线程池,默认使用[ExecutorProvider.ioExecutor]
 */
@Suppress("unused")
fun <T : Any> Maybe<T>.upToBackThreadDownToMain(
    targetExecutor: Executor = ExecutorProvider.ioExecutor
) : Maybe<T> {
    return this.compose {
        it.subscribeOn(Schedulers.from(targetExecutor))
            .observeOn(AndroidSchedulers.mainThread())
    }
}

// </editor-fold>

// <editor-fold desc="Observable拓展函数">

/**
 * [Observable]拓展函数，将上游事件源置于子线程
 * * 在一个事件流中仅能生效一次
 * @param targetExecutor 目标子线程的线程池,默认使用[ExecutorProvider.ioExecutor]
 * */
@Suppress("unused")
fun <T : Any> Observable<T>.upToBackThread(
    targetExecutor: Executor = ExecutorProvider.ioExecutor
) : Observable<T> {
    return this.subscribeOn(Schedulers.from(targetExecutor))
}

/**
 * [Observable]拓展函数，将下游事件源置于子线程
 * @param targetExecutor 目标子线程的线程池,默认使用[ExecutorProvider.ioExecutor]
 * */
@Suppress("unused")
fun <T : Any> Observable<T>.downToBackThread(
    targetExecutor: Executor = ExecutorProvider.ioExecutor
) : Observable<T>{
    return this.observeOn(Schedulers.from(targetExecutor))
}

/**
 * [Observable]的拓展函数，将下游事件源置于子线程
 * */
@Suppress("unused")
fun <T : Any> Observable<T>.downToMainThread() : Observable<T>{
    return this.observeOn(AndroidSchedulers.mainThread())
}

/**
 * [Observable]拓展函数，将上游事件源置于子线程，下游置于主线程
 * @param targetExecutor 目标子线程的线程池,默认使用[ExecutorProvider.ioExecutor]
 */
@Suppress("unused")
fun <T : Any> Observable<T>.upToBackThreadDownToMain(
    targetExecutor: Executor = ExecutorProvider.ioExecutor
) : Observable<T> {
    return this.compose {
        it.subscribeOn(Schedulers.from(targetExecutor))
            .observeOn(AndroidSchedulers.mainThread())
    }
}

// </editor-fold>


// <editor-fold desc="Flowable拓展函数">

/**
 * [Flowable]拓展函数，将上游事件源置于子线程
 * * 在一个事件流中仅能生效一次
 * @param targetExecutor 目标子线程的线程池,默认使用[ExecutorProvider.ioExecutor]
 * */
@Suppress("unused")
fun <T : Any> Flowable<T>.upToBackThread(
    targetExecutor: Executor = ExecutorProvider.ioExecutor
) : Flowable<T> {
    return this.subscribeOn(Schedulers.from(targetExecutor))
}

/**
 * [Flowable]类型的拓展函数，将上游事件源置于子线程，下游置于主线程
 * @param targetExecutor 目标子线程的线程池，默认使用[ExecutorProvider.ioExecutor]
 */
@Suppress("unused","SpellCheckingInspection")
fun <T : Any> Flowable<T>.upToBackThreadDownToMain(
    targetExecutor: Executor = ExecutorProvider.ioExecutor
) : Flowable<T> {
    return this.compose{
        it.subscribeOn(Schedulers.from(targetExecutor))
            .observeOn(AndroidSchedulers.mainThread())
    }
}

/**
 * [Flowable]拓展函数，将下游事件置于子线程
 * @param targetExecutor 目标子线程的线程池，默认使用[ExecutorProvider.ioExecutor]
 */
@Suppress("unused","SpellCheckingInspection")
fun <T : Any> Flowable<T>.downToBackThread(
    targetExecutor : Executor = ExecutorProvider.ioExecutor
) : Flowable<T> {
    return observeOn(Schedulers.from(targetExecutor))
}

/**
 * [Flowable]拓展函数，将下游事件源置于子线程
 * */
@Suppress("unused","SpellCheckingInspection")
fun <T : Any> Flowable<T>.downToMainThread() : Flowable<T>{
    return this.observeOn(AndroidSchedulers.mainThread())
}

// </editor-fold>

