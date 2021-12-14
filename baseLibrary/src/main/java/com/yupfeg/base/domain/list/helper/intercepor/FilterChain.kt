package com.yupfeg.base.domain.list.helper.intercepor

/**
 * 列表数据过滤处理链路数据传输类
 * @author yuPFeG
 * @date 2021/03/19
 */
@Deprecated("已废弃")
data class FilterChain(
    /**原始业务请求数据*/
    val originList : List<Any>?,
    /**上一个拦截处理后的数据*/
    var preHandleList : List<Any>?,
    /**是否中断后续处理链调用*/
    var isInterruptChain : Boolean = false
)