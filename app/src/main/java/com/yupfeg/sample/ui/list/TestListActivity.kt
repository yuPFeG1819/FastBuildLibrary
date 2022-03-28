package com.yupfeg.sample.ui.list

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.yupfeg.base.tools.databinding.proxy.bindingActivity
import com.yupfeg.base.tools.ext.showShortToast
import com.yupfeg.base.tools.process.ProcessLifecycleOwner
import com.yupfeg.base.tools.window.fitImmersiveStatusBar
import com.yupfeg.base.view.activity.BaseActivity
import com.yupfeg.base.viewmodel.ext.viewModelDelegate
import com.yupfeg.base.widget.recyclerview.RecyclerListAdapter
import com.yupfeg.base.widget.recyclerview.strategy.SimpleLoadMoreListItemDelegate
import com.yupfeg.sample.R
import com.yupfeg.sample.databinding.ActivityTestListBinding
import com.yupfeg.sample.widget.ITitleConfig

/**
 *
 * @author yuPFeG
 * @date 2022/02/25
 */
class TestListActivity : BaseActivity(){
    private val mBinding : ActivityTestListBinding by bindingActivity(layoutId)
    private val mViewModel : TestListViewModel by viewModelDelegate()

    private val mListAdapter : RecyclerListAdapter by lazy(LazyThreadSafetyMode.NONE){
        createListAdapter()
    }

    override val layoutId: Int
        get() = R.layout.activity_test_list

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.apply {
            viewModel = mViewModel
            viewConfig = BindingConfig()

            //itemView不会改变recyclerView的高度时，提高性能
            rvTestList.setHasFixedSize(true)
            rvTestList.setItemViewCacheSize(5)
            rvTestList.isNestedScrollingEnabled = false
        }

        //允许沉浸式状态栏
        fitImmersiveStatusBar(isDarkText = true)
    }

    override fun initData() {
        mViewModel.getTestListData()
        mViewModel.articleUseCase.queryArticleList(1)
    }

    private fun createListAdapter() : RecyclerListAdapter{
        val itemStrategy = TestListItemStrategy(object : OnTestListItemClickListener{
            override fun onShard(itemBean: TestListItemBean) {
                //测试利用Activity栈移除多个Activity
                ProcessLifecycleOwner.activityStack
                    .finishAllActivityIgnoreActivity(TestListActivity::class.java)
                showShortToast("分享${itemBean.id}")
            }

            override fun onComment(itemBean: TestListItemBean) {
                showShortToast("评价${itemBean.id}")
            }

            override fun onPraise(itemBean: TestListItemBean) {
                showShortToast("点赞${itemBean.id}")
            }

        })
        val loadMoreItemDelegate = SimpleLoadMoreListItemDelegate()
        return RecyclerListAdapter.createAdapter(itemStrategy,loadMoreItemDelegate)
    }


    inner class BindingConfig : ITitleConfig{

        val listAdapter : RecyclerListAdapter
            get() = mListAdapter

        val layoutManager = LinearLayoutManager(this@TestListActivity)

        override fun back() {
            finish()
        }

        override val titleName: LiveData<String>
            get() = mViewModel.titleName

    }
}