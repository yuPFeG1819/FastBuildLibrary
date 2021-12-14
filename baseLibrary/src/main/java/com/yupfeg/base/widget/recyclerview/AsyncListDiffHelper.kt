package com.yupfeg.base.widget.recyclerview

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import androidx.recyclerview.widget.*
import java.util.*
import java.util.concurrent.*

/**
 * 模仿[AsyncListDiffer]的RecyclerView列表diff处理类，
 * * 用于视图与具体数据处理的解耦
 * @author yuPFeG
 * @date 2020/10/20
 */
class AsyncListDiffHelper constructor(
    /**列表数据更新时的回调接口*/
    private val mListUpdateCallback: ListUpdateCallback,
    /**配置类，默认为null，如果使用配置类（不为null），则内部线程处理配置交由配置类处理*/
    private var mConfig: AsyncDifferConfig<Any>? = null
){

    companion object{
        /**进行diff计算的线程名称 */
        private const val COMPUTE_THREAD_NAME = "pool_diff_compute_thread"
        /**最大核心线程数量，维持最少2个核心线程 */
        private const val CORE_POOL_SIZE = 2
        /**Diff计算线程池最大容量 */
        private const val MAXIMUM_POOL_SIZE = 2
        /**线程池非核心线程存活时间（单位·秒） */
        private const val KEEP_ALIVE_SECONDS : Long = 30

        /**主线程的handler*/
        private val mHandler = Handler(Looper.getMainLooper())

        /**用于创建Diff计算线程的工厂类 */
        private class DiffThreadFactory : ThreadFactory {
            override fun newThread(runnable: Runnable): Thread {
                val result = Thread(runnable, COMPUTE_THREAD_NAME)
                //设置为守护线程
                result.isDaemon = true
                return result
            }
        }

        /**
         * 默认进行异步diff计算的线程池
         * * 使用[getBackGroundExecutor]方法获取单例线程池
         * */
        @Volatile
        private var mExecutorService: ExecutorService? = null

        /**
         * 获取异步计算线程池的单例
         * */
        private fun getBackGroundExecutor() : ExecutorService {
            fun newExecutor() : ExecutorService{
                return ThreadPoolExecutor(
                    CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
                    KEEP_ALIVE_SECONDS, TimeUnit.SECONDS,
                    LinkedBlockingQueue(), DiffThreadFactory()
                )
            }

            return mExecutorService ?: synchronized(this) {
                mExecutorService ?: newExecutor().also {
                    mExecutorService = it
                }
            }
        }

    }

    constructor(listUpdateCallback: ListUpdateCallback,
                diffCallback : DiffUtil.ItemCallback<Any>) : this(
        listUpdateCallback,
        mConfig = AsyncDifferConfig.Builder<Any>(diffCallback)
            .setBackgroundThreadExecutor(getBackGroundExecutor())
            .build()
    )

    init {
        mConfig?:run {
            val diffCallback = object : DiffUtil.ItemCallback<Any>(){
                override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
                    return oldItem.hashCode() == newItem.hashCode()
                }

                @SuppressLint("DiffUtilEquals")
                override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
                    return oldItem == newItem
                }

                override fun getChangePayload(oldItem: Any, newItem: Any): Any? {
                    return "updateChangePayload"
                }
            }

            mConfig = AsyncDifferConfig.Builder(diffCallback)
                .setBackgroundThreadExecutor(getBackGroundExecutor())
                .build()
        }

    }



    /**记录当前计算Diff次数，用于防止多次调用重复刷新问题 */
    private var mMaxScheduledGeneration = 0

    /**当前列表数据*/
    private var mList : MutableList<Any> = mutableListOf()

    /**提供给外部使用，只读的adapter列表数据*/
    val adapterList : List<Any>
        get() = Collections.unmodifiableList(mList)


    /**
     * 根据下标获取对应列表项
     * @param position 列表下标
     */
    fun getListItem(position: Int): Any {
        if (position > mList.lastIndex) {
            throw IndexOutOfBoundsException("index out of list size")
        }
        return mList[position]
    }

    /**
     * 刷新指定下标的列表数据
     * * 调用含有payload参数的notify方法，执行部分定向更新，而不是重新渲染整个item
     */
    fun updatePagedListItem(itemData: Any, index: Int) {
        val oldItem = mList.getOrNull(index)
        oldItem?:run {
            throw IndexOutOfBoundsException("index out list size")
        }
        mList[index] = itemData
        mListUpdateCallback.onChanged(index, 1, oldItem)
    }

    /**
     * 在原有数据基础上，添加新的数据集
     * @param listData 新一页的列表数据
     */
    fun appendListData(listData: List<Any>){
        if (listData.isEmpty()) return
        mList.addAll(listData)
        mListUpdateCallback.onInserted(mList.lastIndex, listData.size)
    }

    /**
     * 提交新的列表数据
     * * 通过新旧数据对比计算,在线程池异步计算，通过计算后的结果进行定向刷新
     * @param newList 列表数据
     */
    fun submitList(newList: List<Any>?){
        //fast return: new list is the as the old one, nothing update
        if (newList == mList) return

        // fast simple remove all
        if (newList.isNullOrEmpty()) {
            val countRemoved = mList.size
            mList.clear()
            // notify last, after list is updated
            mListUpdateCallback.onRemoved(0, countRemoved)
            return
        }

        //原数据集无数据时，直接添加数据，不进行diff计算
        if (mList.isEmpty()) {
            appendListData(newList)
            return
        }

        // 用于控制计算线程，防止在上一次submitList未完成时，
        // 又多次调用submitList，这里只返回使用最后一个计算的DiffResult
        val runGeneration = ++mMaxScheduledGeneration
        val oldList = mList.toList()

        val diffCallBack = object : DiffUtil.Callback(){
            override fun getOldListSize(): Int = oldList.size
            override fun getNewListSize(): Int = newList.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                val oldItem = oldList[oldItemPosition]
                val newItem = newList[newItemPosition]
                //处于不同类型，不是同一个item
                if (oldItem::class != newItem::class){
                    return false
                }
                return mConfig?.diffCallback?.areItemsTheSame(oldItem,newItem)?:false
            }

            override fun areContentsTheSame(
                oldItemPosition: Int, newItemPosition: Int
            ): Boolean {
                val oldItem = oldList[oldItemPosition]
                val newItem = newList[newItemPosition]
                return mConfig?.diffCallback?.areItemsTheSame(oldItem, newItem)?:false
            }

            override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
                val oldItem = oldList[oldItemPosition]
                val newItem = newList[newItemPosition]
                return mConfig?.diffCallback?.getChangePayload(oldItem, newItem)
            }
        }

        //使用DiffUtil计算差异化，以最小量刷新列表
        mConfig?.backgroundThreadExecutor?.execute {
            val diffResult = DiffUtil.calculateDiff(diffCallBack)
            mHandler.post {
                //只更新最后一次diff计算
                if (mMaxScheduledGeneration == runGeneration) {
                    //计算结束才开始赋值并刷新
                    mList = newList.toMutableList()
                    //刷新列表item
                    diffResult.dispatchUpdatesTo(mListUpdateCallback)
                }
            }
        }
    }
}