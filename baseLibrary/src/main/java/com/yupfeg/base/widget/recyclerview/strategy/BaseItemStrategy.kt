package com.yupfeg.base.widget.recyclerview.strategy

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.yupfeg.base.widget.recyclerview.RecyclerListAdapter
import com.yupfeg.base.widget.recyclerview.viewHolder.BaseBindingViewHolder

/**
 * 具体数据类型item的处理逻辑的抽象父类
 * @param T 具体数据类型
 * @param VH ViewHolder实现类，使用DataBinding可继承自[BaseBindingViewHolder]，
 * 传统方式可继承自[RecyclerView.ViewHolder]
 * @author yuPFeG
 * @date 2020/10/20
 */
abstract class BaseItemStrategy<T,in VH : RecyclerView.ViewHolder>(
    val dataClass: Class<out T>
) : DiffUtil.ItemCallback<T>(){

    companion object{
        /**默认缓存池的缓存数量，与列表缓存池大小的默认值相同*/
        private const val DEF_POOL_CACHE_SIZE = 5
    }

    /**item的布局id*/
    @get:LayoutRes
    abstract val layoutId : Int

    /**
     * item在列表中占据的列数，默认为1
     * * 提供给`GridLayoutManager`与`StaggeredGridLayoutManager` 使用
     * * 如`GridLayoutManager`设置的spanCount = 3，此时[spanSize] = 3则可以占据视图完整一行位置
     * * 如使用`StaggeredGridLayoutManager`则，只要设置[spanSize]不为1即可占据完整一行位置
     * */
    open var spanSize : Int = 1

    /**item的类型，默认为[layoutId]*/
    open val itemType : Int
        get() = layoutId

    /**
     * 当前ViewType在缓存池内的缓存数量，通常不需要修改
     * - 若ViewType对应的ItemView复用性较低，或者较大较长，占用内存较多，避免由于View在缓存池内强引用而无法回收视图。
     * 可以将ViewType对应在缓存池内的缓存数量适当降低，在适当时刻能够释放ItemView。
     * */
    open val viewTypePoolCacheSize = DEF_POOL_CACHE_SIZE

    /**
     * ViewType对应的缓存池大小需要修改
     * */
    val isPoolCacheNeedChange : Boolean
        get() = viewTypePoolCacheSize != DEF_POOL_CACHE_SIZE

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
     * @param listAdapter 列表的原始adapter实例，更多用于列表item设置回调时，获取对应实体
     * */
    abstract fun createViewHolder(
        parentView : ViewGroup,itemView : View,listAdapter : RecyclerListAdapter
    ) : RecyclerView.ViewHolder

    /**绑定ViewHolder数据
     * * 对应[RecyclerView.Adapter.onBindViewHolder]
     * @param viewHolder
     * @param itemData
     * @param position
     * @param payload
     * */
    abstract fun bindViewHolder(viewHolder : VH,itemData : T,position: Int,payload: Any?)

    /**
     * 在viewHolder回收失败时回调
     * * 通常在ViewHolder处于正在执行动画状态，而被回收失败
     * @param viewHolder
     * @return true-该ViewHolder需要被强制回收，否则为false
     */
    open fun onFailRecycledView(viewHolder: VH) : Boolean = false

    /**
     * 在viewHolder被回收到缓存时回调
     * * 对应[RecyclerView.Adapter.onViewRecycled]
     * @param viewHolder
     */
    open fun onViewRecycled(viewHolder: VH){
        //默认回收dataBinding viewHolder的绑定信息
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

    // <editor-fold desc="diff定向刷新相关">

    override fun getChangePayload(oldItem: T, newItem: T): Any? {
        return "changePayload"
    }

    // </editor-fold>

}

