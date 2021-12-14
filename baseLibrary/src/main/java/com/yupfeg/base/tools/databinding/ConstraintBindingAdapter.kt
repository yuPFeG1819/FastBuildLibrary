package com.yupfeg.base.tools.databinding

import androidx.constraintlayout.widget.Guideline
import androidx.databinding.BindingAdapter

/**
 * 约束布局相关的DataBindingAdapter
 * @author yuPFeG
 * @date 2020/09/09
 */

/**
 * 设置约束布局的辅助线，距离底部margin值
 * @param end 距离底部（右侧）margin值
 */
@Suppress("unused")
@BindingAdapter("constraintGuideEnd")
fun Guideline.setConstraintGuideEnd(end : Int?){
    end?:return
    setGuidelineEnd(end)
}

/**
 * 设置约束布局的辅助线，距离顶部（左侧）margin值
 * @param start 距离顶部（左侧）margin值
 */
@Suppress("unused")
@BindingAdapter("constraintGuideBegin")
fun Guideline.setConstraintGuideBegin(start : Int?){
    start?:return
    setGuidelineBegin(start)
}

