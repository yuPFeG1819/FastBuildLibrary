package com.yupfeg.base.widget.recyclerview

/**
 * 列表分页预加载状态
 * @author yuPFeG
 * @date 2020/10/25
 */
enum class ListLoadMoreState{
    /*正常*/
    NORMAL,
    /*加载到最底了*/
    THE_END,
    /*加载中..*/
    LOADING,
    /*网络异常*/
    ERROR
}