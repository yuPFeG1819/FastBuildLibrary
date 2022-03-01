package com.yupfeg.base.widget.recyclerview

import android.view.ViewGroup
import androidx.recyclerview.widget.*
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE
import com.yupfeg.base.widget.recyclerview.strategy.BaseItemStrategy
import java.util.concurrent.Executor
import kotlin.math.max

/**列表item数据类型的别名*/
typealias ItemStrategyDataClass = Class<out Any>

/**列表item策略类的别名*/
typealias AdapterItemStrategy = BaseItemStrategy<Any, RecyclerView.ViewHolder>

/**
 * 不限制具体item数据类型的RecyclerView列表适配器
 * * 将adapter与具体业务逻辑数据类型解耦，仅根据列表item的数据类型，将具体显示逻辑交由委托类处理
 *
 * @param mItemStrategies adapter的item策略类Map，管理当前adapter内部所有具体业务逻辑的item委托类
 * @param diffExecutor diff计算执行的线程池
 *ActivityThread
 * @author yuPFeG
 * @date 2020/10/20
 */
class RecyclerListAdapter private constructor(
    private val mItemStrategies : Map<ItemStrategyDataClass, AdapterItemStrategy>,
    private val diffExecutor : Executor? = null
): RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    companion object{
        /**
         * 利用可变参数添加不固定的item委托类，创建RecyclerView的adapter实例
         * @param strategies
         * @param diffExecutor diff计算执行的线程池
         * @return
         */
        @Suppress("UNCHECKED_CAST", "unused")
        fun createAdapter(
            vararg strategies: BaseItemStrategy<*, *>,
            diffExecutor: Executor? = null,
        ) : RecyclerListAdapter{
            val map = mutableMapOf<ItemStrategyDataClass, AdapterItemStrategy>()
            for (strategy in strategies) {
                val itemStrategy = strategy as? AdapterItemStrategy ?: continue
                map[itemStrategy.dataClass] = itemStrategy
            }
            return RecyclerListAdapter(map, diffExecutor)
        }

    }

    /**DiffUtil帮助类实例，管理Adapter数据*/
    private val mDiffHelper : AsyncListDiffer<Any> = createAsyncDiffInstance()

    /**
     * viewType作为key的Item委托类缓存Map类
     * * 用于简化通过item的viewType来获取对应item委托类的重复操作。
     * */
    private val mViewTypeItemStrategies =
        mItemStrategies.mapKeys { it.value.itemType }

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
        //开启尝试根据itemType与itemId进行获取缓存机制
        setHasStableIds(true)
    }

    //<editor-fold desc="==============抽象方法实现===============">

    override fun getItemId(position: Int): Long {
        val itemData = adapterList[position]
        return getItemStrategy(itemData.javaClass).getItemId(itemData, position)
    }

    override fun getItemViewType(position: Int): Int {
        return getItemStrategy(getListItem(position).javaClass).itemType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemStrategy = mViewTypeItemStrategies.getValue(viewType)
        return itemStrategy.createViewHolder(
            parent, itemStrategy.inflateLayoutView(parent),this
        )
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
        val itemStrategy = getItemStrategy(itemData.javaClass)
        //这里的payloads参数是从notify()方法中的payload集合而来（DiffUtil里的getChangePayload也能获取到）
        //需要判断payloads.isEmpty(),只要有值就能执行定向部分刷新，不会重新渲染整个item
        if (payloads.isEmpty()) {
            itemStrategy.bindViewHolder(holder, itemData, position,null)
        } else {
            itemStrategy.bindViewHolder(holder, itemData, position,payloads[0])
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
        //兼容GridLayoutManager，便于控制itemViewHolder的宽度
        (recyclerView.layoutManager as? GridLayoutManager)?.setGridSpanSizeLookup()
        super.onAttachedToRecyclerView(recyclerView)
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)
        val itemStrategy = getItemStrategyByItemType(holder.itemViewType)
        itemStrategy.onViewAttachedToWindow(holder)
        //兼容StaggeredGridLayoutManager，控制item在视图内的宽度
        takeIf { itemStrategy.spanSize > 1 }?.run {
            (holder.itemView.layoutParams as? StaggeredGridLayoutManager.LayoutParams)
                ?.isFullSpan = true
        }
    }

    //回收ViewHolder失败后回调，如果返回true，表示该项ViewHolder需要被强制回收
    override fun onFailedToRecycleView(holder: RecyclerView.ViewHolder): Boolean {
        return getItemStrategyByItemType(holder.itemViewType).onFailRecycledView(holder)
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        getItemStrategyByItemType(holder.itemViewType).onViewRecycled(holder)
        super.onViewRecycled(holder)
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        getItemStrategyByItemType(holder.itemViewType).onViewDetachedFromWindow(holder)
        super.onViewDetachedFromWindow(holder)
    }

    //</editor-fold desc="==============抽象方法实现==============“>

    /**
     * [GridLayoutManager]拓展函数，设置itemViewHolder占据列表视图的列数
     * * 多用于设置部分列表item占用宽度，如spanSize = 3，而layoutManager设置spanCount也为3，则item占据完整一行
     * */
    private fun GridLayoutManager.setGridSpanSizeLookup(){
        //设置item所占据列数
        val maxSpanCount = this.spanCount
        this.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup(){
            override fun getSpanSize(position: Int): Int {
                return when(val currSpanSize = getItemStrategyFromPosition(position).spanSize){
                    in 1..maxSpanCount -> currSpanSize
                    in Int.MIN_VALUE..1 -> 1
                    else -> maxSpanCount
                }
            }
        }
    }

    /**
     * 获取item的数据类型对应的item策略实例
     * @param itemClass item策略对应的数据类型
     * */
    private fun getItemStrategy(itemClass: ItemStrategyDataClass)
            = mItemStrategies.getValue(itemClass)

    /**
     * 获取itemType对应的item策略实例
     * @param itemType item委托类的itemType，[BaseItemStrategy.itemType]
     * */
    private fun getItemStrategyByItemType(itemType: Int)
            = mViewTypeItemStrategies.getValue(itemType)

    /**
     * 根据列表item下标获取对应类型的item委托类
     * @param position 列表item下标
     */
    private fun getItemStrategyFromPosition(position: Int)
            = mItemStrategies.getValue(getListItem(position).javaClass)

    /**
     * 创建diff异步处理类对象
     * @return
     */
    private fun createAsyncDiffInstance() : AsyncListDiffer<Any>{
        val diffItemCallBack = createDiffItemCallBack()
        diffExecutor?:return AsyncListDiffer(this, diffItemCallBack)

        val diffConfig =  AsyncDifferConfig.Builder(diffItemCallBack)
            .setBackgroundThreadExecutor(diffExecutor)
            .build()

        return AsyncListDiffer(AdapterListUpdateCallback(this), diffConfig)
    }

    /**
     * 创建diff计算item比较差异回调
     */
    private fun createDiffItemCallBack() : DiffUtil.ItemCallback<Any>{
        return object : DiffUtil.ItemCallback<Any>() {
            override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
                if (oldItem::class != newItem::class) {
                    return false
                }
                return mItemStrategies[oldItem::class.java]
                    ?.areItemsTheSame(oldItem, newItem) ?: false
            }

            override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
                if (oldItem::class != newItem::class) {
                    return false
                }
                return mItemStrategies[oldItem::class.java]
                    ?.areContentsTheSame(oldItem, newItem) ?: false
            }

            override fun getChangePayload(oldItem: Any, newItem: Any): Any? {
                if (oldItem::class != newItem::class) {
                    return false
                }
                return mItemStrategies[oldItem::class.java]
                    ?.getChangePayload(oldItem, newItem)
            }
        }
    }
}


/**
 * 列表分页预加载状态
 */
enum class ListLoadMoreState{
    /*正常*/
    NORMAL,
    /*加载到最底了*/
    THE_END,
    /*加载中..*/
    LOADING,
    /*网络异常*/
    ERROR
}