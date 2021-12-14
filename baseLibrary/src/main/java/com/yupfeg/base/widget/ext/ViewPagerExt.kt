package com.yupfeg.base.widget.ext

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2

/**
 * [ViewPager2]的拓展函数，绑定fragment列表数据
 * @param activity 当前所在activity
 * @param fragments fragment集合
 */
fun ViewPager2.bindFragmentList(activity : FragmentActivity,fragments : List<Fragment>){
    if (adapter != null) return
    this.adapter = object : FragmentStateAdapter(activity){
        override fun getItemCount(): Int = fragments.size
        override fun createFragment(position: Int) = fragments[position]
    }
}

/**
 * [ViewPager2]的拓展函数，绑定fragment列表数据
 * @param fragment 当前所在的fragment
 * @param contents fragment集合
 */
fun ViewPager2.bindFragmentList(fragment: Fragment,contents : List<Fragment>){
    if (adapter != null) return
    this.adapter = object : FragmentStateAdapter(fragment){
        override fun getItemCount(): Int = contents.size
        override fun createFragment(position: Int) = contents[position]

    }
}
