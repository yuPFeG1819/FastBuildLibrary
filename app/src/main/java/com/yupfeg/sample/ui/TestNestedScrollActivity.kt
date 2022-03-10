package com.yupfeg.sample.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.yupfeg.base.tools.databinding.proxy.bindingActivity
import com.yupfeg.base.tools.window.fitImmersiveStatusBar
import com.yupfeg.base.view.activity.BaseActivity
import com.yupfeg.sample.R
import com.yupfeg.sample.databinding.ActivityNestedScrollLayoutBinding

/**
 * 测试嵌套滑动NestedScroll机制的demo页
 *
 * @author yuPFeG
 * @date 2022/01/17
 */
class TestNestedScrollActivity : BaseActivity(){
    private val mBinding : ActivityNestedScrollLayoutBinding by bindingActivity(layoutId)

    override val layoutId: Int
        get() = R.layout.activity_nested_scroll_layout

    override fun initView(savedInstanceState: Bundle?) {
//        setContentView(layoutId)
        fitImmersiveStatusBar()
//        mViewPager = findViewById(R.id.viewpager_nested_scroll_content)
    }

    override fun initData() {
        mBinding.viewpagerNestedScrollContent.adapter = object : FragmentStateAdapter(this){
            override fun getItemCount(): Int = 3

            override fun createFragment(position: Int): Fragment {
                return TestNestedFragment(position)
            }

            override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
                super.onAttachedToRecyclerView(recyclerView)
                recyclerView.setItemViewCacheSize(3)
            }
        }
    }
}
