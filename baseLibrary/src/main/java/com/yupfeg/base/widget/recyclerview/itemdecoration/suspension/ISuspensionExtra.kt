package com.yupfeg.base.widget.recyclerview.itemdecoration.suspension

/**
 * 需要分组悬浮功能的bean必须实现的拓展接口
 * @author yuPFeG
 * @date 2020/08/08
 */
interface ISuspensionExtra {

    /**是否需要显示分组悬浮标题文本*/
    val isShowSuspension : Boolean

    /**分组选择标题的文本显示字符串*/
    val suspensionText : String
}