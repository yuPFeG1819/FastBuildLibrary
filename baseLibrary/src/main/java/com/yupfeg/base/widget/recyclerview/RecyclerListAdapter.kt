package com.yupfeg.base.widget.recyclerview

import android.view.ViewGroup
import androidx.recyclerview.widget.*
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE
import com.yupfeg.base.widget.recyclerview.delegate.BaseListItemDelegate
import kotlin.math.max

/**列表item数据类型的别名*/
typealias DataDelegateClass = Class<out Any>

/**列表item委托类的别名*/
typealias AdapterItemDelegate = BaseListItemDelegate<Any, RecyclerView.ViewHolder>

/**
 * 不限制具体item数据类型的RecyclerView列表适配器
 * * 参考[官方方案](https://github.com/google/iosched/blob/89df01ebc19d9a46495baac4690c2ebfa74946dc/mobile/src/main/java/com/google/samples/apps/iosched/ui/feed/FeedAdapter.kt)
 * * 参考[掘金上的委托方案](https://juejin.cn/post/6882531923537707015#heading-4)
 * * 将adapter与具体业务逻辑数据类型解耦，仅根据列表item的数据类型，将具体显示逻辑交由委托类处理
 * @param mItemDelegates adapter的item委托类集合，管理当前adapter内部所有具体业务逻辑的item委托类
 * @param diffConfig diff计算的配置
 *
 * @author yuPFeG
 * @date 2020/10/20
 */
class RecyclerListAdapter(
    private val mItemDelegates : Map<DataDelegateClass, AdapterItemDelegate>,
    private val diffConfig : AsyncDifferConfig<Any>? = null
): RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    companion object{
        /**
         * 利用可变参数添加不固定的item委托类，创建RecyclerView的adapter实例
         * @param delegates
         * @param diffConfig diff计算的配置
         * @return
         */
        @Suppress("UNCHECKED_CAST", "unused")
        fun createAdapter(
            vararg delegates: BaseListItemDelegate<*, *>,
            diffConfig: AsyncDifferConfig<Any>? = null,
        ) : RecyclerListAdapter{
            val map = mutableMapOf<DataDelegateClass, AdapterItemDelegate>()
            for (delegate in delegates) {
                val itemDelegate = delegate as? AdapterItemDelegate
                itemDelegate ?: continue
                map[itemDelegate.dataClass] = itemDelegate
            }
            return RecyclerListAdapter(map, diffConfig)
        }

    }

    /**DiffUtil帮助类实例，管理Adapter数据*/
    private val mDiffHelper : AsyncListDiffer<Any> = createAsyncDiffHelper()

    /**
     * viewType作为key的Item委托类缓存Map类
     * * 用于简化通过item的viewType来获取对应item委托类的重复操作。
     * */
    private val mViewTypeItemDelegates =
        mItemDelegates.mapKeys { it.value.itemType }

    /**当前列表滑动状态*/
    private var mCurrScrollState : Int = SCROLL_STATE_IDLE

    //<editor-fold desc="列表数据集相关">

    /**
     * 当前列表数据集
     * * 对数据的修改交由diff帮助类处理
     */
    var adapterList : List<Any>
        set(value) {
            //交由DiffUtil计算差异化，计算结束后，自动执行最小量的更新
            mDiffHelper.submitList(value)
        }
        get() = mDiffHelper.currentList

    /**
     * 获取指定index的列表item数据
     * @param position 列表index
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun getListItem(position: Int) : Any = adapterList[position]

    /**
     * 更新指定item项显示
     * * 使用payload执行局部定向更新
     * @param itemData item数据
     * @param position item下标索引
     */
    @Suppress("unused")
    fun updateItemDateChanged(itemData : Any, position: Int){
        val newList = adapterList.toMutableList()
        newList[position] = itemData
        adapterList = newList
        notifyItemChanged(position,"payloadUpdate")
    }

    /**
     * 局部定向更新指定item项的显示
     * @param position
     * */
    @Suppress("unused")
    fun notifyItemPayloadChanged(position: Int){
        notifyItemChanged(position,"payloadUpdate")
    }

    //</editor-fold desc="列表数据集相关">

    //<editor-fold desc="分页加载更多相关">

    /**触发预加载操作时执行动作*/
    var onPreLoadAction : (()->Unit) ?= null

    /**预加载的提前阀值，提前多少item开始获取下一页列表数据 */
    var prefetchSizeLimit : Int = 1

    /**
     * 校验当前是否满足预加载的条件
     * @param position 当前列表加载index，只在缓存未命中时执行onBind
     * @return true-执行预加载操作
     */
    private fun checkPreLoadMore(position: Int) =
        onPreLoadAction?.let {
            //当前下标数超出预加载的阀值，且处于滑动状态，才执行预加载操作
            position == max(itemCount - 1 - prefetchSizeLimit, 0)
                    && mCurrScrollState != SCROLL_STATE_IDLE

        }?:false

    //</editor-fold desc="分页加载相关“>

    init {
        //开启根据typeId和itemId进行获取缓存机制
        setHasStableIds(true)
    }

    //<editor-fold desc="==============生命周期方法===============">

    override fun getItemId(position: Int): Long {
        val itemData = adapterList[position]
        return getItemDelegate(itemData.javaClass).getItemId(itemData, position)
    }

    override fun getItemViewType(position: Int): Int {
        return getItemDelegate(getListItem(position).javaClass).itemType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemDelegate = mViewTypeItemDelegates.getValue(viewType)
        return itemDelegate.createViewHolder(parent, itemDelegate.inflateLayoutView(parent))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        onBindViewHolder(holder, position, mutableListOf())
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        val itemData = getListItem(position)
        val itemDelegate = getItemDelegate(itemData.javaClass)
        //这里的payloads参数是从notify()方法中的payload集合而来（DiffUtil里的getChangePayload也能获取到）
        //需要判断payloads.isEmpty(),只要有值就能执行定向部分刷新，不会重新渲染整个item
        if (payloads.isEmpty()) {
            itemDelegate.bindViewHolder(holder, itemData, position,null)
        } else {
            itemDelegate.bindViewHolder(holder, itemData, position,payloads[0])
        }

        if (checkPreLoadMore(position)){
            //执行预加载下一页操作
            onPreLoadAction?.invoke()
        }
    }

    override fun getItemCount(): Int {
        return mDiffHelper.currentList.size
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                mCurrScrollState = newState
                super.onScrollStateChanged(recyclerView, newState)
            }
        })
        //兼容GridLayoutManager
        (recyclerView.layoutManager as? GridLayoutManager)?.also {layoutManager->
            //设置item所占据列数
            val maxSpanCount = layoutManager.spanCount
            layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup(){
                override fun getSpanSize(position: Int): Int {
                    return when(val currSpanSize = getItemDelegateFromPosition(position).spanSize){
                        in 1..maxSpanCount -> currSpanSize
                        in Int.MIN_VALUE..1 -> 1
                        else -> maxSpanCount
                    }
                }
            }
        }

        super.onAttachedToRecyclerView(recyclerView)
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)
        val itemDelegate = getItemDelegateByItemType(holder.itemViewType)
        itemDelegate.onViewAttachedToWindow(holder)
        //兼容StaggeredGridLayoutManager
        takeIf { itemDelegate.spanSize == 1}
            ?.run {
                (holder.itemView.layoutParams as? StaggeredGridLayoutManager.LayoutParams)
                    ?.isFullSpan = true
            }
    }

    //回收ViewHolder失败后回调，如果返回true，表示该项ViewHolder需要被强制回收
    override fun onFailedToRecycleView(holder: RecyclerView.ViewHolder): Boolean {
        return getItemDelegateByItemType(holder.itemViewType).onFailRecycledView(holder)
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        getItemDelegateByItemType(holder.itemViewType).onViewRecycled(holder)
        super.onViewRecycled(holder)
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        getItemDelegateByItemType(holder.itemViewType).onViewDetachedFromWindow(holder)
        super.onViewDetachedFromWindow(holder)
    }

    //</editor-fold desc="==============生命周期方法==============“>

    /**
     * 获取item的数据类型对应的item委托类
     * @param itemClass item委托类的数据类型
     * */
    private fun getItemDelegate(itemClass: DataDelegateClass)
            = mItemDelegates.getValue(itemClass)

    /**
     * 获取itemType对应的item委托类
     * @param itemType item委托类的itemType，[BaseListItemDelegate.itemType]
     * */
    private fun getItemDelegateByItemType(itemType: Int)
            = mViewTypeItemDelegates.getValue(itemType)

    /**
     * 根据列表item下标获取对应类型的item委托类
     * @param position 列表item下标
     */
    private fun getItemDelegateFromPosition(position: Int)
            = mItemDelegates.getValue(getListItem(position).javaClass)

    /**
     * 创建diff异步帮助类对象
     * @return
     */
    private fun createAsyncDiffHelper() : AsyncListDiffer<Any>{
        return if (diffConfig != null){
            AsyncListDiffer(AdapterListUpdateCallback(this), diffConfig)
        }else {
            AsyncListDiffer(this, object : DiffUtil.ItemCallback<Any>() {
                override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
                    if (oldItem::class != newItem::class) {
                        return false
                    }
                    return mItemDelegates[oldItem::class.java]
                        ?.areItemsTheSame(oldItem, newItem) ?: false
                }

                override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
                    if (oldItem::class != newItem::class) {
                        return false
                    }
                    return mItemDelegates[oldItem::class.java]
                        ?.areContentsTheSame(oldItem, newItem) ?: false
                }

                override fun getChangePayload(oldItem: Any, newItem: Any): Any? {
                    if (oldItem::class != newItem::class) {
                        return false
                    }
                    return mItemDelegates[oldItem::class.java]
                        ?.getChangePayload(oldItem, newItem)
                }
            })
        }
    }
}
