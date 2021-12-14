package com.yupfeg.base.domain.list

import androidx.lifecycle.LiveData
import com.yupfeg.base.domain.extra.ListPageable
import com.yupfeg.livedata.StateLiveData

/**
 * 具有列表功能的用例接口声明
 * * 仅由需要使用列表功能`UseCase`实现使用
 *
 * PS: 实现分页加载功能的数据bean类需要实现[ListPageable]接口
 * @author yuPFeG
 * @date 2021/03/18
 */
@Suppress("unused")
interface ListUseCaseFilterable {
    /**
     * 请求分页列表数据
     * @param isLoadMore 是否为分页加载
     * */
    fun queryPageListData(isLoadMore : Boolean = false)

    /**
     * 是否处于下拉刷新状态
     * * 过滤重复操作
     * */
    var isRefreshing : Boolean

    /**
     * 列表加载出现的错误事件
     * * 单次执行事件，UI层调用[StateLiveData.observe]
     * 或者[StateLiveData.observe]订阅处理该事件
     */
    val listErrorEvent : StateLiveData<Throwable>

    /**
     * 过滤后的列表数据LiveData
     * * 在UI层调用[LiveData.observe]方法订阅，或者直接赋值给`DataBinding`订阅
     * */
    val listFilteredLiveData : LiveData<List<Any>>
}