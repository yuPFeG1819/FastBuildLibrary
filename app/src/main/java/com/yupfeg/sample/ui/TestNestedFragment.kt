package com.yupfeg.sample.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yupfeg.base.tools.databinding.proxy.bindingFragment
import com.yupfeg.base.view.fragment.BaseFragment
import com.yupfeg.sample.R
import com.yupfeg.sample.databinding.FramentNestedInnerContentBinding

/**
 *
 * @author yuPFeG
 * @date
 */
class TestNestedFragment(private val index : Int) : BaseFragment(){

    private val mDataBinding : FramentNestedInnerContentBinding by bindingFragment()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return mDataBinding.root
    }

    override val layoutId: Int
        get() = R.layout.frament_nested_inner_content

    override fun initView(viewGroup: View, savedInstanceState: Bundle?) {
//        mRecyclerView = viewGroup.findViewById(R.id.recycler_test_nested_scroll)
//        mRecyclerView?.layoutManager = LinearLayoutManager(this.requireContext())
        mDataBinding.recyclerTestNestedScroll.apply {
            layoutManager = LinearLayoutManager(this@TestNestedFragment.requireContext())
        }
    }

    override fun initData() {
        val list = List(20){i->
            TestFootItem(
                name = "${index + 1} 商品名称$i",
                desc = "${index + 1} 商品描述1111${i}",
                price = "￥200"

            )
        }
        val adapter = TestListAdapter(list)
        mDataBinding.recyclerTestNestedScroll.adapter = adapter
    }
}

data class TestFootItem(
    val name : String,
    val desc : String,
    val price : String
)

private class TestListAdapter(private val mList: List<TestFootItem>) : RecyclerView.Adapter<TestViewHolder>(){

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_item_foot_list,parent,false)
        return TestViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TestViewHolder, position: Int) {
        val itemData = mList[position]
        holder.apply {
            tvName.text = itemData.name
            tvDesc.text = itemData.desc
            tvPrice.text = itemData.price
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }



}

private class TestViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
    val tvName : TextView = itemView.findViewById(R.id.tv_foot_list_item_name)
    val tvDesc : TextView = itemView.findViewById(R.id.tv_foot_list_item_desc)
    val tvPrice : TextView = itemView.findViewById(R.id.tv_foot_list_item_price)
}