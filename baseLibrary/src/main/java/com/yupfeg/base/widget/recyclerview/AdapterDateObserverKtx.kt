package com.yupfeg.base.widget.recyclerview

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.yupfeg.base.tools.lifecycle.LifecycleEndObserver
import java.lang.NullPointerException

/**
 * [RecyclerView.Adapter]的空数据校验的item更新事件订阅类
 * @author yuPFeG
 * @date 2022/03/04
 */
class AdapterDataEmptyObserver(
    private val adapter: RecyclerView.Adapter<*>,
    private val doOnEmptyCheck : (Boolean)->Unit
) : RecyclerView.AdapterDataObserver() {

    private val isDataEmpty get() = adapter.itemCount == 0

    //调用notifyDataSetChanged时分发
    override fun onChanged() {
        super.onChanged()
        doOnEmptyCheck(isDataEmpty)
    }

    //调用notifyItemRangeInserted时分发
    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
        super.onItemRangeInserted(positionStart, itemCount)
        doOnEmptyCheck(isDataEmpty)
    }

    //调用notifyItemRangeInserted时分发
    override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
        super.onItemRangeRemoved(positionStart, itemCount)
        doOnEmptyCheck(isDataEmpty)
    }
}

/**
 * [RecyclerView]的拓展函数，订阅列表空数据的校验变化
 * @param owner 当前视图生命周期对象
 * @param doOnEmptyCheck 列表数据变化时，校验数据集是否为空的执行操作
 * */
@Suppress("unused")
fun RecyclerView.observeAdapterDataEmpty(owner: LifecycleOwner, doOnEmptyCheck: (Boolean) -> Unit){
    this.adapter?:throw NullPointerException("RecyclerView needs set the adapter")
    this.observeAdapterItemChange(owner,AdapterDataEmptyObserver(this.adapter!!,doOnEmptyCheck))
}


/**
 * [RecyclerView]的拓展函数，订阅适配器列表数据集更新
 * - 通常在需要在外部额外监听数据变化处理
 * @param owner 当前视图生命周期对象
 * @param dataObserver 数据更新变化
 * */
fun RecyclerView.observeAdapterItemChange(
    owner : LifecycleOwner,
    dataObserver : RecyclerView.AdapterDataObserver
){
    this.adapter?:throw NullPointerException("RecyclerView needs set the adapter")

    this.adapter?.registerAdapterDataObserver(dataObserver)
    owner.lifecycle.addObserver(LifecycleEndObserver(endState = Lifecycle.State.DESTROYED){
        //在视图声明周期结束时，移除适配器数据变化订阅
        this.adapter?.unregisterAdapterDataObserver(dataObserver)
    })
}

