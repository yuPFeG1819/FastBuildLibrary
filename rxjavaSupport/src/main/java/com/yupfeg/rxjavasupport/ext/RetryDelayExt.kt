package com.yupfeg.rxjavasupport.ext

import io.reactivex.rxjava3.core.*
import org.reactivestreams.Publisher
import java.util.concurrent.TimeUnit
import io.reactivex.rxjava3.functions.Function

// <editor-fold desc="延迟重试">

/**
 * [Completable]的拓展函数.
 * 事件流错误后，指定次数延迟重试原始事件源
 * @param retryCount 重试次数
 * @param delay 重试延迟的时间
 * @param unit 延迟时间的单位
 */
@Suppress("unused")
fun Completable.retry(
    retryCount : Int = 1,
    delay: Long,
    unit: TimeUnit = TimeUnit.SECONDS
) : Completable{
    return this.retryWhen(FlowableRetryWithDelay(retryCount,delay, unit))
}

/**
 * [Observable]的拓展函数.
 * 事件流错误后，指定次数延迟重试原始事件源
 * @param retryCount 重试次数
 * @param delay 重试延迟的时间
 * @param unit 延迟时间的单位
 */
@Suppress("unused")
fun <T : Any> Observable<T>.retry(
    retryCount : Int = 1,
    delay: Long,
    unit: TimeUnit = TimeUnit.SECONDS
) : Observable<T>{
    return this.retryWhen(ObservableRetryWithDelay(retryCount,delay, unit))
}

/**
 * [Maybe]的拓展函数.
 * 事件流错误后，指定次数延迟重试原始事件源
 * @param retryCount 重试次数
 * @param delay 重试延迟的时间
 * @param unit 延迟时间的单位
 */
@Suppress("unused")
fun <T : Any> Maybe<T>.retry(
    retryCount : Int = 1,
    delay: Long,
    unit: TimeUnit = TimeUnit.SECONDS
) : Maybe<T>{
    return this.retryWhen(FlowableRetryWithDelay(retryCount,delay, unit))
}

/**
 * [Flowable]的拓展函数.
 * 事件流错误后，指定次数延迟重试原始事件源
 * @param retryCount 重试次数
 * @param delay 重试延迟的时间
 * @param unit 延迟时间的单位
 */
@Suppress("unused","SpellCheckingInspection")
fun <T : Any> Flowable<T>.retry(
    retryCount : Int = 1, delay: Long,
    unit: TimeUnit = TimeUnit.SECONDS
) : Flowable<T>{
    return this.retryWhen(FlowableRetryWithDelay(retryCount,delay, unit))
}

// </editor-fold>


/**
 * [Observable]的延迟重试执行方法对象
 * @param maxRetryCount 最大重试次数
 * @param delay 重试延迟的时间
 * @param unit 延迟时间的单位
 * */
internal class ObservableRetryWithDelay(
    private val maxRetryCount : Int = 1,
    private val delay: Long = 1,
    private val unit : TimeUnit = TimeUnit.SECONDS
) : Function<Observable<Throwable>, ObservableSource<*>> {
    /**当前重试次数*/
    private var retryCount : Int = 0

    override fun apply(errorObservable : Observable<Throwable>): ObservableSource<*> {
        //这里不能直接发起新的订阅，否则无效
        //在Function函数中，必须对输入的 Observable<Any>进行处理，
        // 此处使用flatMap操作符接收上游的数据
        return errorObservable.flatMap {throwable->
            if (++retryCount <= maxRetryCount){
                //必须使用Observable来创建，这里要求返回为ObservableSource的子类
                return@flatMap Observable.timer(delay,unit)
            }
            //超出最大重试次数
            return@flatMap Observable.error<Exception>(throwable)
        }
    }
}

/**
 * [Flowable] 、 [Maybe] 、 [Complete]的延迟重试执行方法对象
 * @param maxRetryCount 最大重试次数
 * @param delay 重试延迟的时间
 * @param unit 延迟时间的单位
 * */
@Suppress("SpellCheckingInspection", "KDocUnresolvedReference")
internal class FlowableRetryWithDelay(
    private val maxRetryCount : Int = 1,
    private val delay: Long = 1,
    private val unit : TimeUnit = TimeUnit.SECONDS
) : Function<Flowable<Throwable>, Publisher<*>> {
    /**当前重试次数*/
    private var retryCount : Int = 0

    override fun apply(errorFlowable: Flowable<Throwable>): Publisher<*> {
        //这里不能直接发起新的订阅，否则无效
        //在Function函数中，必须对输入的 Flowable<Any>进行处理，
        // 此处使用flatMap操作符接收上游的数据
        //此处决定是否重新订阅 & 发送原来的Flowable,即轮询
        return errorFlowable.flatMap {throwable->
            if (++retryCount <= maxRetryCount){
                //必须使用Observable来创建，这里要求返回为ObservableSource的子类
                return@flatMap Flowable.timer(delay,unit)
            }
            //超出最大重试次数
            return@flatMap Flowable.error<Exception>(throwable)
        }
    }
}
