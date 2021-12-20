package com.yupfeg.rxjavasupport.tools

import com.yupfeg.base.domain.extra.ListPageable
import io.reactivex.rxjava3.core.*
import org.reactivestreams.Publisher

/**
 * 列表分页加载数据预处理的RxJava3变换操作符
 * @author yuPFeG
 * @date 2021/03/08
 */
@Suppress("unused")
class LoadMoreDataTransformer<T : ListPageable>(
    private val doOnNextAction : ((T)->Unit)?,
    private val doOnError : ((Throwable)->Unit)?
) : MaybeTransformer<T, T>, FlowableTransformer<T, T>, ObservableTransformer<T, T> {

    // <editor-fold desc="Maybe类型数据源预处理">

    override fun apply(upstream: Maybe<T>): MaybeSource<T> {
        return upstream
            // step:成功获取到数据，预处理数据
            .doOnSuccess { result-> doOnNextAction?.invoke(result) }
            // step:出现错误，预处理错误
            .doOnError { doOnError?.invoke(it) }
    }

    // </editor-fold>

    // <editor-fold desc="Flowable类型数据源预处理">

    override fun apply(upstream: Flowable<T>): Publisher<T> {
        return upstream
            // step:成功获取到数据，预处理数据
            .doOnNext { result-> doOnNextAction?.invoke(result) }
            // step:出现错误，预处理错误
            .doOnError { doOnError?.invoke(it) }
    }

    // </editor-fold>

    // <editor-fold desc="Observable类型数据源预处理">

    override fun apply(upstream: Observable<T>): ObservableSource<T> {
        return upstream
            // step:成功获取到数据，预处理数据
            .doOnNext { result-> doOnNextAction?.invoke(result) }
            // step:出现错误，预处理错误
            .doOnError { doOnError?.invoke(it) }
    }

    // </editor-fold>
}