package com.yupfeg.base.widget.recyclerview.viewHolder

import android.content.Context
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

/**
 * 适配了DataBinding的RecyclerView ViewHolder基类
 * *
 * @param itemView item布局导入后的根视图View
 * @author yuPFeG
 * @date 2020/10/20
 */
@Suppress("MemberVisibilityCanBePrivate","unused")
abstract class BaseBindingViewHolder<T,VDB : ViewDataBinding>(itemView : View) : RecyclerView.ViewHolder(itemView){

    protected val mContext : Context = itemView.context
    protected val mBinding : VDB by lazy(LazyThreadSafetyMode.NONE){
        requireNotNull(DataBindingUtil.bind(itemView)) {
            "cannot find the matched layout."
        }
    }

    /**
     * 绑定视图数据
     * @param itemData 列表item项对象
     * @param position 列表item索引
     * */
    fun bind(itemData : T,position : Int){
        mBinding.apply {
            onBind(mBinding, itemData, position)
            //在绑定数据更新后才会更新数据，防止item数据闪烁，必须在主线程调用该函数
            executePendingBindings()
        }
    }

    /**
     * 子类实现的绑定数据到View视图
     * @param binding item根视图的dataBinding对象
     * @param itemData 列表item项对象
     * @param position 列表item索引
     * */
    abstract fun onBind(binding : VDB,itemData : T,position : Int)

    open fun release(){
        mBinding.unbind()
    }

}