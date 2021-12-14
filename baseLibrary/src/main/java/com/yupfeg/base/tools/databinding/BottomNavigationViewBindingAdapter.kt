package com.yupfeg.base.tools.databinding

import androidx.databinding.BindingAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * [BottomNavigationView]的拓展函数，设置是否允许滑动
 * * DataBinding专用函数，所有DataBinding属性在xml都需要以["@{}"]赋值，否则会报错
 * @param listener 选中监听，以menu的id为参数
 * */
@BindingAdapter("setNavMenuSelectedListener")
fun BottomNavigationView.setOnNavSelectedListener(
    listener : BottomNavigationView.OnNavigationItemSelectedListener?
){
    setOnNavigationItemSelectedListener(listener)
}