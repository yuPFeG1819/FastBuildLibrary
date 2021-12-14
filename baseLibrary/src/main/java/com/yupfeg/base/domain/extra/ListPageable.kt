package com.yupfeg.base.domain.extra

/**
 * 具有列表分页加载功能的data bean类拓展接口
 * @author yuPFeG
 * @date 2020/12/17
 */
@Suppress("unused")
interface ListPageable {
    /**列表数据源的总计数据量*/
    val totalCount : Int
}