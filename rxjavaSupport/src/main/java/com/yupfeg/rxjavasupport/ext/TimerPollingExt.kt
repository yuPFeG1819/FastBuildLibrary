package com.yupfeg.rxjavasupport.ext

import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import java.util.concurrent.TimeUnit


// <editor-fold desc="无条件轮询">
/**
 * [Observable]的扩展函数，执行无条件的无限定时轮询操作，直到订阅被取消
 * - 即使出现异常也同样会继续轮询
 * @param delay 定时延迟时间
 * @param unit 时间单位,默认为[TimeUnit.SECONDS]
 */
@Suppress("unused")
fun <T : Any> Observable<T>.timerPolling(
    delay : Long,
    unit : TimeUnit = TimeUnit.SECONDS
) : Observable<T> {
    return this.compose{
        //step1-1:当发生错误时，延迟重新发送
        it.retryWhen {errorObservable->
            //这里不能直接发起新的订阅，否则无效
            //在Function函数中，必须对输入的 Observable<Any>进行处理，
            //此处使用flatMap操作符接收上游的数据
            errorObservable.flatMap {
                //必须使用Observable来创建，这里要求返回为ObservableSource的子类
                Observable.timer(delay,unit)
            }
        }
        //step1-2:当订阅结束onComplete()时，延迟重新发送
        .repeatWhen {completeObservable->
            //这里不能直接发起新的订阅，否则无效
            //在Function函数中，必须对输入的 Observable<Any>进行处理，
            //此处使用flatMap操作符接收上游的数据
            completeObservable.flatMap {
                //必须使用Observable来创建，这里要求返回为ObservableSource的子类
                Observable.timer(delay,unit)
            }
        }
    }
}

/**
 * [Flowable]的扩展函数，执行无条件的无限定时轮询操作，直到订阅被取消
 * - 即使出现异常也同样会继续轮询
 * @param delay 定时延迟时间
 * @param unit 时间单位,默认为[TimeUnit.SECONDS]
 */
@Suppress("unused")
fun <T : Any> Flowable<T>.timerPolling(
    delay : Long,
    unit : TimeUnit = TimeUnit.SECONDS
) : Flowable<T>{
    return this.compose{
        //step1-1:当发生错误时，延迟重新发送
        it.retryWhen {errorFlowable->
            //这里不能直接发起新的订阅，否则无效
            //在Function函数中，必须对输入的 Observable<Any>进行处理，
            //此处使用flatMap操作符接收上游的数据
            errorFlowable.flatMap {
                //必须使用Flowable来创建，这里要求返回为FlowableSource的子类
                Flowable.timer(delay,unit)
            }
        }
            //step1-2:当订阅事件结束onComplete()时，延迟重新发送
            .repeatWhen {completeFlowable->
                //这里不能直接发起新的订阅，否则无效
                //在Function函数中，必须对输入的 Observable<Any>进行处理，
                //此处使用flatMap操作符接收上游的数据
                completeFlowable.flatMap {
                    //必须使用Flowable来创建，这里要求返回为Publisher的子类
                    Flowable.timer(delay,unit)
                }
            }
    }
}

// </editor-fold>

// <editor-fold desc="有条件轮询">

/**
 * [Observable]的扩展函数，执行有条件的定时轮询操作,满足结束条件后，结束轮询
 * - 即使出现异常也同样会继续轮询
 * @param delay 定时延迟时间
 * @param unit 时间单位,默认为[TimeUnit.SECONDS]
 * @param cancelFilter 结束轮询的条件，传入一个判断函数
 */
@Suppress("unused")
fun <T : Any> Observable<T>.timerPollingUntil(
    delay : Long,
    unit : TimeUnit = TimeUnit.SECONDS,
    cancelFilter : ()->Boolean
) : Observable<T> {
    return this.compose{
        //step1-1:当发生错误时，如不满足结束条件，延迟指定时间，重新订阅原始事件源
        it.retryWhen {errorObservable->
            //这里不能直接发起新的订阅，否则无效
            //在Function函数中，必须对输入的 Observable<Any>进行处理，
            // 此处使用flatMap操作符接收上游的数据
            //此处决定是否重新订阅 & 发送原来的Observable,即轮询
            //此处有2种情况：
            //1.若返回1个Complete()/Error()事件，则不重新订阅&发送原来的Observable,即轮询结束
            //2.若返回其余事件，则重新订阅 & 发送原来的Observable,则继续轮询
            errorObservable.flatMap {
                if (cancelFilter()){
                    //满足结束条件，发送error,结束轮询
                    return@flatMap Observable.error<Exception>(
                        TimerPollingOverException("timerPolling is cancel")
                    )
                }
                //不满足结束轮询条件，继续轮询订阅原始事件源
                //必须使用Observable来创建，这里要求返回为ObservableSource的子类
                Observable.timer(delay,unit)
            }
        }
        //step1-2:当订阅事件结束onComplete()时，如不满足结束条件，延迟指定时间，重新订阅原始事件源
        .repeatWhen {completeObservable->
            //这里不能直接发起新的订阅，否则无效
            //在Function函数中，必须对输入的 Observable<Any>进行处理，
            // 此处使用flatMap操作符接收上游的数据
            //此处决定是否重新订阅 & 发送原来的Observable,即轮询
            //此处有2种情况：
            //1.若返回1个Complete()/Error()事件，则不重新订阅&发送原来的Observable,即轮询结束
            //2.若返回其余事件，则重新订阅 & 发送原来的Observable,则继续轮询
            completeObservable.flatMap {
                if (cancelFilter()){
                    //满足结束条件，发送error,结束轮询
                    return@flatMap Observable.error<Exception>(
                        TimerPollingOverException("timerPolling is cancel")
                    )
                }
                //不满足结束轮询条件，继续轮询订阅原始事件源
                //必须使用Observable来创建，这里要求返回为ObservableSource的子类
                Observable.timer(delay,unit)
            }
        }
    }
}

/**
 * [Flowable]的扩展函数，执行有条件的定时轮询操作,满足结束条件后，结束轮询
 * - 即使出现异常也同样会继续轮询
 * @param delay 定时延迟时间
 * @param unit 时间单位,默认为[TimeUnit.SECONDS]
 * @param cancelFilter 结束轮询的条件，传入一个判断函数
 */
@Suppress("unused")
fun <T : Any> Flowable<T>.timerPollingUntil(
    delay : Long,
    unit : TimeUnit = TimeUnit.SECONDS,
    cancelFilter : ()->Boolean
) : Flowable<T>{
    return this.compose{
        //step1-1:当发生错误时，如不满足结束条件，延迟指定时间，重新订阅原始事件源
        it.retryWhen {errorFlowable->
            //这里不能直接发起新的订阅，否则无效
            //在Function函数中，必须对输入的 Flowable<Any>进行处理，
            // 此处使用flatMap操作符接收上游的数据
            //此处决定是否重新订阅 & 发送原来的Flowable,即轮询
            //此处有2种情况：
            //1.若返回1个Complete()/Error()事件，则不重新订阅&发送原来的Observable,即轮询结束
            //2.若返回其余事件，则重新订阅 & 发送原来的Observable,则继续轮询
            errorFlowable.flatMap {
                if (cancelFilter()){
                    //满足结束条件，发送error,结束轮询
                    return@flatMap Flowable.error<Exception>(
                        TimerPollingOverException("timerPolling is cancel")
                    )
                }
                //不满足结束轮询条件，继续轮询订阅原始事件源
                //必须使用Flowable来创建，这里要求返回为FlowableSource的子类
                Flowable.timer(delay,unit)
            }
        }
        //step1-2:当订阅事件结束onComplete()时，如不满足结束条件，延迟指定时间，重新订阅原始事件源
        .repeatWhen {completeFlowable->
            //这里不能直接发起新的订阅，否则无效
            //在Function函数中，必须对输入的 Flowable<Any>进行处理，
            // 此处使用flatMap操作符接收上游的数据
            //此处决定是否重新订阅 & 发送原来的Flowable,即轮询
            //此处有2种情况：
            //1.若返回1个Complete()/Error()事件，则不重新订阅&发送原来的Flowable,即轮询结束
            //2.若返回其余事件，则重新订阅 & 发送原来的Flowable,则继续轮询
            completeFlowable.flatMap {
                if (cancelFilter()){
                    //满足结束条件，发送error,结束轮询
                    return@flatMap Flowable.error<Exception>(
                        TimerPollingOverException("timerPolling is cancel")
                    )
                }
                //不满足结束轮询条件，继续轮询订阅原始事件源
                //必须使用Flowable来创建，这里要求返回为Publisher的子类
                Flowable.timer(delay,unit)
            }
        }
    }
}

/**
 * [Observable]的扩展函数，执行有条件的定时轮询操作
 * * 直到满足结束条件或者出现错误后，结束轮询
 * @param delay 定时延迟时间
 * @param unit 时间单位,默认为[TimeUnit.SECONDS]
 * @param cancelFilter 结束轮询的条件，传入一个判断函数
 */
@Suppress("unused")
fun <T : Any> Observable<T>.timerPollingUntilOrError(
    delay : Long,
    unit : TimeUnit = TimeUnit.SECONDS,
    cancelFilter : ()->Boolean
) : Observable<T> {
    return this.compose{
        //step1-2:当订阅事件结束onComplete()时，如不满足结束条件，延迟指定时间，重新订阅原始事件源
        it.repeatWhen {completeObservable->
                //这里不能直接发起新的订阅，否则无效
                //在Function函数中，必须对输入的 Observable<Any>进行处理，
                // 此处使用flatMap操作符接收上游的数据
                //此处决定是否重新订阅 & 发送原来的Observable,即轮询
                //此处有2种情况：
                //1.若返回1个Complete()/Error()事件，则不重新订阅&发送原来的Observable,即轮询结束
                //2.若返回其余事件，则重新订阅 & 发送原来的Observable,则继续轮询
                completeObservable.flatMap {
                    if (cancelFilter()){
                        //满足结束条件，结束轮询
                        return@flatMap Observable.error<Exception>(
                            TimerPollingOverException("timerPolling is cancel")
                        )
                    }
                    //不满足结束轮询条件，继续轮询订阅原始事件源
                    //必须使用Observable来创建，这里要求返回为ObservableSource的子类
                    Observable.timer(delay,unit)
                }
            }
    }
}

/**
 * [Flowable]的扩展函数，执行有条件的定时轮询操作,
 * * 直到满足结束条件或出现异常后，结束轮询
 * @param delay 定时延迟时间
 * @param unit 时间单位,默认为[TimeUnit.SECONDS]
 * @param cancelFilter 结束轮询的条件，传入一个判断函数
 */
@Suppress("unused")
fun <T : Any> Flowable<T>.timerPollingUntilOrError(
    delay : Long,
    unit : TimeUnit = TimeUnit.SECONDS,
    cancelFilter : ()->Boolean
) : Flowable<T> {
    //step 当订阅事件结束onComplete()时，如不满足结束条件，延迟指定时间，重新订阅原始事件源
    return this.repeatWhen { completeFlowable ->
        //这里不能直接发起新的订阅，否则无效
        //在Function函数中，必须对输入的 Flowable<Any>进行处理，
        // 此处使用flatMap操作符接收上游的数据
        //此处决定是否重新订阅 & 发送原来的Flowable,即轮询
        //此处有2种情况：
        //1.若返回1个Complete()/Error()事件，则不重新订阅&发送原来的Flowable,即轮询结束
        //2.若返回其余事件，则重新订阅 & 发送原来的Flowable,则继续轮询
        completeFlowable.flatMap {
            if (cancelFilter()) {
                //满足结束条件，结束轮询
                return@flatMap Flowable.error<Exception>(
                    TimerPollingOverException("timerPolling is cancel")
                )
            }
            //不满足结束轮询条件，继续轮询订阅原始事件源
            //必须使用Flowable来创建，这里要求返回为Publisher的子类
            Flowable.timer(delay, unit)
        }
    }
}

/**定时轮询结束发出的错误，不需要记录错误，只作为一个提示*/
class TimerPollingOverException(msg : String) : Exception(msg){
    override fun toString(): String {
        return "this time polling over exception only a tip , no error"
    }
}