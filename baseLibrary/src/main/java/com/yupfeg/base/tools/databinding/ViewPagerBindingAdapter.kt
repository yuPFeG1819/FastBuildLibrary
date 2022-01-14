package com.yupfeg.base.tools.databinding

import androidx.databinding.BindingAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2

/**
 * [ViewPager2]的拓展函数，设置是否允许滑动
 * * DataBinding专用函数，所有DataBinding属性在xml都需要以["@{}"]赋值，否则会报错
 * @param enable true-允许viewPager滑动，false-禁止滑动
 * */
@Suppress("unused")
@BindingAdapter(value = ["setViewPagerEnableScroll"])
fun ViewPager2.bindEnableScroll(enable : Boolean){
    this.isUserInputEnabled = enable
}

/**
 * [ViewPager2]的拓展函数，设置当前选中导航index
 * * DataBinding专用函数，所有DataBinding属性在xml都需要以["@{}"]赋值，否则会报错
 * @param index 当前选中的
 * @param smoothScroll 是否平滑移动
 */
@Suppress("unused")
@BindingAdapter(value = ["setCurrNavIndex","setSmoothScroll"])
fun ViewPager2.bindCurrNavIndex(index : Int?, smoothScroll : Boolean = false){
    adapter ?: return
    if (currentItem == index) return
    if (index?:0 > adapter?.itemCount?:0) return
    this.setCurrentItem(index?:0,smoothScroll)
}

/**
 * [ViewPager2]的拓展函数，设置Fragment列表adapter
 * * DataBinding专用函数，所有DataBinding属性在xml都需要以["@{}"]赋值，否则会报错
 * @param adapter fragment
 */
@Suppress("unused")
@BindingAdapter(value = ["setFragmentAdapter"])
fun ViewPager2.bindFragmentAdapter(adapter : FragmentStateAdapter?){
    this.adapter = adapter
}

/**
 * [ViewPager2]的拓展函数，设置ViewPager的当前页两侧保留的page数量
 * * DataBinding专用函数，所有DataBinding属性在xml都需要以["@{}"]赋值，否则会报错
 * @param pageLimit 保留页面数
 */
@Suppress("unused")
@BindingAdapter(value = ["setOffscreenPageLimit"])
fun ViewPager2.bindOffscreenPageLimit(pageLimit : Int?){
    pageLimit?:return
    this.offscreenPageLimit = pageLimit
}