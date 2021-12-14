package com.yupfeg.base.domain.list.helper.intercepor

/**
 * 列表数据过滤拦截器接口声明
 * @author yuPFeG
 * @date 2021/03/19
 */
@Deprecated("无法使用单一组合")
interface ListFilterInterceptor {

    /**
     * 请求列表数据
     * @return 是否允许请求原始数据
     * */
    fun allowRequest() : Boolean

    /**
     * 数据源列表数据拦截
     * @param chain 列表数据处理链路
     * @return 如果返回为null或者空集合，则会直接结束处理责任链路，返回正常集合数据，则继续向下游处理
     * */
    fun intercept(chain: FilterChain) : List<Any>?

}
