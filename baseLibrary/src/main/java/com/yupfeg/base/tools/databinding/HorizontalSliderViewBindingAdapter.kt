package com.yupfeg.base.tools.databinding

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yupfeg.base.widget.indicator.HorizontalIndicatorView

/**
 * [HorizontalIndicatorView]的拓展函数，设置绑定水平方向的recyclerView
 * * DataBinding专用函数，所有DataBinding属性在xml都需要以["@{}"]赋值，否则会报错
 * @param recyclerView [RecyclerView]
 * */
@BindingAdapter("setHorizontalList")
fun HorizontalIndicatorView.bindHorizontalList(recyclerView: RecyclerView?){
    recyclerView?:return
    this.bindRecyclerView(recyclerView)
}
