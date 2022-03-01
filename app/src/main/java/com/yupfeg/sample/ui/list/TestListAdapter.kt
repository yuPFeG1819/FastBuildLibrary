package com.yupfeg.sample.ui.list

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.yupfeg.base.widget.grid.BaseNineGridAdapter
import com.yupfeg.base.widget.recyclerview.RecyclerListAdapter
import com.yupfeg.base.widget.recyclerview.strategy.BaseItemStrategy
import com.yupfeg.base.widget.recyclerview.viewHolder.BaseBindingViewHolder
import com.yupfeg.sample.R
import com.yupfeg.sample.databinding.RecyclerItemTestListBinding

class TestListItemBean{
    var id : Long = 0

    var userName : String? = null

    var content : String? = null

    var time : String? = null

    var userImg : String? = null

    var imgDates : List<String>? = null

    var singleImgWidth : Int = 0
    var singleImgHeight : Int = 0
}

interface OnTestListItemClickListener{

    fun onShard(itemBean : TestListItemBean)

    fun onComment(itemBean : TestListItemBean)

    fun onPraise(itemBean : TestListItemBean)
}

/**
 *
 * @author yuPFeG
 * @date
 */
class TestListItemStrategy(
  private val onItemCLickListener : OnTestListItemClickListener
) : BaseItemStrategy<TestListItemBean,TestListItemViewHolder>(TestListItemBean::class.java){
    override val layoutId: Int
        get() = R.layout.recycler_item_test_list

    override fun getItemId(itemData: TestListItemBean, position: Int): Long {
        return itemData.id
    }

    override fun createViewHolder(
        parentView: ViewGroup,
        itemView: View,
        listAdapter: RecyclerListAdapter
    ): RecyclerView.ViewHolder {
        return TestListItemViewHolder(itemView,onItemCLickListener)
    }

    override fun bindViewHolder(
        viewHolder: TestListItemViewHolder,
        itemData: TestListItemBean,
        position: Int,
        payload: Any?
    ) {
        viewHolder.bind(itemData, position)
    }

    override fun areItemsTheSame(oldItem: TestListItemBean, newItem: TestListItemBean): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: TestListItemBean, newItem: TestListItemBean): Boolean {
        return oldItem.content == newItem.content && oldItem.imgDates == newItem.imgDates
                && oldItem.userName == newItem.userName && oldItem.userImg == newItem.userImg
    }


}

class TestListItemViewHolder(
    itemView : View,
    onItemCLickListener : OnTestListItemClickListener
) : BaseBindingViewHolder<TestListItemBean,RecyclerItemTestListBinding>(itemView){
    private val mGridAdapter = ImgNineGridAdapter()

    init{

        mBinding.nineGridLayoutTestListItem.setViewAdapter(mGridAdapter)
        mBinding.onItemClickListener = onItemCLickListener
    }

    override fun onBind(
        binding: RecyclerItemTestListBinding,
        itemData: TestListItemBean,
        position: Int
    ) {
        mBinding.itemData = itemData
        mGridAdapter.setImgList(itemData.imgDates?: emptyList())
        mGridAdapter.setSingleViewSize(itemData.singleImgWidth,itemData.singleImgHeight)
        binding.nineGridLayoutTestListItem.notifyItemChanged()
    }


}

private class ImgNineGridAdapter : BaseNineGridAdapter(){

    private val mImgList : MutableList<String> = mutableListOf()

    fun setImgList(imgs : List<String>){
        mImgList.clear()
        mImgList.addAll(imgs)
    }

    override val count: Int
        get() = mImgList.size

    override fun getView(viewGroup: ViewGroup, position: Int): View {
        val imageView =  ImageView(viewGroup.context)
        imageView.setBackgroundColor(ContextCompat.getColor(viewGroup.context, android.R.color.holo_red_light))
        imageView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        return imageView
    }

}