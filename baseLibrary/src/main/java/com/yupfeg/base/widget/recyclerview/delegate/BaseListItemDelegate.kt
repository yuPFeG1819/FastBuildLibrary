package com.yupfeg.base.widget.recyclerview.delegate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.yupfeg.base.widget.recyclerview.viewHolder.BaseBindingViewHolder

/**
 * 具体数据类型item的处理逻辑的抽象父类
 * @param T 具体数据类型
 * @param VH ViewHolder实现类，使用DataBinding可继承自[BaseBindingViewHolder]，
 * 传统方式可继承自[RecyclerView.ViewHolder]
 * @author yuPFeG
 * @date 2020/10/20
 */
abstract class BaseListItemDelegate<T,in VH : RecyclerView.ViewHolder>(
    val dataClass: Class<out T>
) : DiffUtil.ItemCallback<T>(){

    /**item的布局id*/
    @get:LayoutRes
    abstract val layoutId : Int

    /**
     * item在列表中占据的列数，默认为1
     * * 提供给GridLayoutManager与StaggeredGridLayoutManager 使用
     * */
    open var spanSize : Int = 1

    /**item的类型，默认为[layoutId]*/
    open val itemType : Int
        get() = layoutId

    /**获取当前item的布局View*/
    open fun inflateLayoutView(parentView: ViewGroup) : View =
        LayoutInflater.from(parentView.context).inflate(layoutId, parentView, false)

    /**
     * 获取itemId，
     * * 对应[RecyclerView.Adapter.getItemId]
     * * 用于再次匹配RecyclerView缓存的ViewHolder
     * */
    abstract fun getItemId(itemData : T,position : Int) : Long

    //<editor-fold desc="生命周期方法">

    /**
     * * 创建ViewHolder实例
     * * 对应[RecyclerView.Adapter.onCreateViewHolder]
     * @param parentView recyclerView的跟布局视图
     * @param itemView inflate[layoutId]的View
     * */
    abstract fun createViewHolder(parentView : ViewGroup,itemView : View) : RecyclerView.ViewHolder

    /**绑定ViewHolder数据
     * * 对应[RecyclerView.Adapter.onBindViewHolder]
     * */
    abstract fun bindViewHolder(viewHolder : VH,itemData : T,position: Int,payload: Any?)

    /**
     * 在viewHolder回收失败时回调
     * * 通常在ViewHolder处于正在执行动画状态，而被回收失败
     * @param viewHolder
     * @return true-该ViewHolder需要被强制回收否则为false
     */
    open fun onFailRecycledView(viewHolder: VH) : Boolean = false

    /**
     * 在viewHolder被回收到缓存时回调
     * * 对应[RecyclerView.Adapter.onViewRecycled]
     * @param viewHolder
     */
    open fun onViewRecycled(viewHolder: VH){
        (viewHolder as? BaseBindingViewHolder<*,*>)?.release()
    }

    /**
     * ItemView从视图中销毁时调用
     * * 对应[RecyclerView.Adapter.onViewDetachedFromWindow]
     * @param viewHolder
     * */
    open fun onViewDetachedFromWindow(viewHolder: VH) = Unit

    /**
     * ItemView添加到列表时调用
     * * 对应[RecyclerView.Adapter.onViewAttachedToWindow]
     * @param viewHolder
     * */
    open fun onViewAttachedToWindow(viewHolder: VH) = Unit

    //</editor-fold>
}

