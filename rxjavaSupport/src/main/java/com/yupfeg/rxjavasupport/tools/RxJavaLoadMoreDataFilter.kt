package com.yupfeg.rxjavasupport.tools

import com.yupfeg.base.domain.extra.ListPageable
import com.yupfeg.base.domain.list.LoadMoreListDataFilter

/**
 * 支持RxJava的分页加载数据处理类
 * @author yuPFeG
 * @date 2021/12/19
 */
@Suppress("unused")
open class RxJavaLoadMoreDataFilter(
    /**单页的请求数据量 */
    requestCount : Int = DEF_PAGE_DATA_COUNT,
    /**原始集合的数据来源*/
    originDataSource : ((pageIndex : Int)->Unit)
) : LoadMoreListDataFilter(requestCount,originDataSource){

    /**
     * 预处理可分页的原始集合数据的RxJava数据流
     * * 实现分页加载功能的数据bean类需要实现[ListPageable]接口
     * @param requestPageIndex 当前数据请求页数
     * */
    open fun <T : ListPageable> preProcessListData(
        requestPageIndex : Int
    ) : LoadMoreDataTransformer<T> {
        return LoadMoreDataTransformer(
            doOnNextAction = {
                updateListPageIndex(requestPageIndex)
            },
            doOnError = {
                doOnLoadMoreError(requestPageIndex, it)
            }
        )
    }
}
