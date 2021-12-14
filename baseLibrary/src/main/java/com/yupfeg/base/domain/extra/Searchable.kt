package com.yupfeg.base.domain.extra

/**
 * 具有检索关键字功能的业务用例抽象接口
 * @author yuPFeG
 * @date 2020/12/17
 */
@Suppress("unused")
interface Searchable {

    /**搜索关键字 */
    var keyword: String?

}