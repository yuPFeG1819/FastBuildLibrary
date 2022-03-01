package com.yupfeg.base.widget.recyclerview.strategy

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yupfeg.base.R
import com.yupfeg.base.widget.ext.setThrottleClickListener
import com.yupfeg.base.widget.recyclerview.ListLoadMoreState
import com.yupfeg.base.widget.recyclerview.RecyclerListAdapter
import com.yupfeg.base.widget.recyclerview.viewHolder.BaseViewHolder

/**可分页的加载更多item功能接口*/
interface ILoadMoreItem{
    var loadMoreStatus : ListLoadMoreState
}

/**
 * 默认实现列表加载更多footerView状态的bean类
 * @author yuPFeG
 * @date 2020/10/23
 * */
data class LoadMoreFooterItemBean(
    val loadingText : String,
    val retryText : String,
    val endText : String,
    var status : ListLoadMoreState = ListLoadMoreState.NORMAL
) : ILoadMoreItem{
    override var loadMoreStatus: ListLoadMoreState
        get() = status
        set(value) {
            status = value
        }
}

/**
 * 加载更多item的点击事件
 * @author yuPFeG
 * @date 2020/10/23
 * */
interface OnLoadMoreItemClickListener{
    /**出现错误重试*/
    fun onErrorRetry()
}

/**
 * 简单的分页加载更多的item委托类
 * @author yuPFeG
 * @date 2020/10/23
 */
@Suppress("unused")
class SimpleLoadMoreListItemDelegate(
    private val onItemClick : OnLoadMoreItemClickListener ?= null
) : BaseItemStrategy<LoadMoreFooterItemBean, BaseViewHolder>(LoadMoreFooterItemBean::class.java){

    override val layoutId: Int
        get() = R.layout.recycler_item_def_load_more_footer

    override fun getItemId(itemData: LoadMoreFooterItemBean, position: Int): Long {
        return position.toLong()
    }

    override fun createViewHolder(
        parentView: ViewGroup,
        itemView: View,
        listAdapter: RecyclerListAdapter
    ): RecyclerView.ViewHolder {
        return BaseViewHolder.createNewInstance(itemView)
    }

    override fun bindViewHolder(
        viewHolder: BaseViewHolder,
        itemData: LoadMoreFooterItemBean,
        position: Int,
        payload: Any?
    ) {
        val isLoadingState : Boolean
        val isEndState : Boolean
        val isRetryState : Boolean

        when(itemData.status){
             ListLoadMoreState.NORMAL -> {
                 isLoadingState = false
                 isEndState = false
                 isRetryState = false
            }
            ListLoadMoreState.THE_END -> {
                isLoadingState = false
                isEndState = true
                isRetryState = false
                viewHolder.setText(R.id.tv_load_more_end_text,itemData.endText)
            }
            ListLoadMoreState.LOADING -> {
                isLoadingState = true
                isEndState = false
                isRetryState = false
                viewHolder.setText(R.id.tv_load_more_loading_text,itemData.loadingText)
            }
            ListLoadMoreState.ERROR -> {
                isLoadingState = false
                isEndState = false
                isRetryState = true
                viewHolder.setText(R.id.tv_load_more_retry_text,itemData.retryText)
            }
        }
        viewHolder.setVisibility(R.id.group_load_more_loading_state,isLoadingState)
            .setVisibility(R.id.group_load_more_retry_state,isRetryState)
            .setVisibility(R.id.tv_load_more_end_text,isEndState)
            .itemView.setThrottleClickListener{
                onItemClick?.takeIf { isRetryState }?.onErrorRetry()
            }

    }

    override fun areItemsTheSame(oldItem: LoadMoreFooterItemBean, newItem: LoadMoreFooterItemBean)
            = oldItem.status == newItem.status

    override fun areContentsTheSame(oldItem: LoadMoreFooterItemBean, newItem: LoadMoreFooterItemBean)
            = oldItem.status == newItem.status
}
