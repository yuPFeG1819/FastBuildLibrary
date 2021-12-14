package com.yupfeg.base.domain.list.helper.intercepor

import com.yupfeg.base.R
import com.yupfeg.base.domain.list.helper.ListFilterDataHelper
import com.yupfeg.base.provider.ResourceContentProvider
import com.yupfeg.base.widget.recyclerview.ListLoadMoreState
import com.yupfeg.base.widget.recyclerview.delegate.ILoadMoreItem
import com.yupfeg.base.widget.recyclerview.delegate.LoadMoreFooterItemBean
import kotlin.jvm.Throws

/**
 * 列表分页加载功能过滤拦截器
 * @author yuPFeG
 * @date 2021/03/19
 */
@Deprecated("已废弃，目前无法解决请求过滤功能")
class LoadMoreListFilterInterceptor(
    private val dataFilterHelper : ListFilterDataHelper,
    /**单页的请求数据量 */
    private val requestCount : Int = DEF_PAGE_DATA_COUNT,
) : ListFilterInterceptor {

    companion object {
        /**默认单页加载数据量，默认为10条数据*/
        private const val DEF_PAGE_DATA_COUNT = 10

        /**默认的加载更多item数据*/
        private val defaultLoadMoreItem = LoadMoreFooterItemBean(
            status = ListLoadMoreState.NORMAL,
            loadingText = ResourceContentProvider.getInstance()
                .getString(R.string.load_more_footer_loading),
            retryText = ResourceContentProvider.getInstance()
                .getString(R.string.load_more_footer_network_error),
            endText = ResourceContentProvider.getInstance()
                .getString(R.string.load_more_footer_end)
        )
    }

    //<editor-fold desc="分页请求成员属性">

    /**
     * 当前请求是否为加载更多
     * * 在发起请求前，修改该值
     * */
    var isQueryLoadMore : Boolean = false

    /**
     * 当前已加载页数
     * */
    var currPageIndex : Int = 1
        private set(value){
            field = if (value <= 1) 1
            else value
        }

    /**
     * 原始业务列表数据数
     * */
    var originDataCount : Int = 0
        private set(value){
            field = if (value < 0) 0
            else value
        }

    /**
     * 列表数据源的数据总量
     * * 仅用于计算最大页数
     * */
    var totalDataCount : Int = 0
        private set(value){
            field = if (value <= 0) 0
            else value
        }

    /**当前加载更多的状态*/
    private var mCurrLoadMoreState : ListLoadMoreState = ListLoadMoreState.NORMAL

    /**
     * 子类实际业务用例的列表数据
     * */
    private val mOriginList : MutableList<Any> = mutableListOf()

    //</editor-fold>


    private var mLoadMoreItemData : ILoadMoreItem = defaultLoadMoreItem.copy()

    /**
     * 设置分页加载itemData
     * @param itemData 分页加载item项bean类，需要实现[ILoadMoreItem]接口
     * */
    fun setLoadMoreItem(itemData: ILoadMoreItem?){
        itemData?.apply { mLoadMoreItemData = this }
    }

    /**
     * 更新列表分页数据
     * @param pageIndex 页数
     * @param dataCount 当前原始数据量
     * @param totalCount 总计数据源的数据连
     * */
    fun updateListPageData(pageIndex : Int, dataCount : Int, totalCount : Int){
        this.currPageIndex = pageIndex
        this.originDataCount = dataCount
        this.totalDataCount = totalCount
    }

    //<editor-fold desc="拦截器实现">

    override fun allowRequest(): Boolean {
        return if(isQueryLoadMore) true else prepareObtainLoadMoreData()
    }

    @Throws(Exception::class)
    override fun intercept(chain: FilterChain): List<Any>? {
        mOriginList.clear()
        chain.originList?.takeIf { it.isNotEmpty() }?.also {
            //缓存原始列表数据
            mOriginList.addAll(it)
        }

        chain.preHandleList?: run {
            //直接中断处理链
            chain.isInterruptChain = true
            return null
        }

        val newList = getNewListData(chain.preHandleList)
        newList?:also {
            chain.isInterruptChain = true
        }
        return newList
    }

    //</editor-fold>

    /**
     * 分发新的列表数据
     * * 仅用于请求数据层成功后更新原始列表数据使用
     * @param newList 数据源返回的列表数据，如果数据为空，则添加空视图显示
     */
    private fun getNewListData(newList : List<Any>?) : List<Any>?{
        return newList.takeUnless { it.isNullOrEmpty() }?.also {list->
            mOriginList.addAll(list)
            //分发显示列表数据
            prepareLoadMoreListData(ListLoadMoreState.NORMAL)
        }?: run {
            if (!isQueryLoadMore){
                //分发显示列表空视图
                dataFilterHelper.disPatchListEmptyData()
            }
            null
        }
    }

    /**
     * 准备获取分页列表数据
     * */
    private fun prepareObtainLoadMoreData() : Boolean{
        takeUnless {
            // 如果当前加载的数据还不满一页，则不执行上拉加载操作
            originDataCount < requestCount || totalDataCount <= 0
        }?.takeUnless {
            //过滤已处于正在分页加载更多的操作，防止重复触发请求
            mCurrLoadMoreState == ListLoadMoreState.LOADING
        }?.also {
            //计算是否获取列表数据
            return calculateObtainLoadMoreData()
        }
        return false
    }

    /**计算是否请求列表数据*/
    private fun calculateObtainLoadMoreData() : Boolean{
        //计算总页数
        val totalPageSize = if (totalDataCount % requestCount == 0) {
            totalDataCount / requestCount
        } else {
            totalDataCount / requestCount + 1
        }

        val newLoadMoreState : ListLoadMoreState
        val allowRequest : Boolean
        if (mOriginList.size < totalDataCount && currPageIndex < totalPageSize) {
            //存在更多数据，加载下一页 (切换FooterView显示状态为Loading)
            newLoadMoreState = ListLoadMoreState.LOADING
            allowRequest = true
        } else {
            //已经到底了，loadMoreItem显示为TheEnd
            newLoadMoreState = ListLoadMoreState.THE_END
            allowRequest = false
        }
        val newListData = prepareLoadMoreListData(newLoadMoreState)
        dataFilterHelper.dispatchNewListData(newListData)
        return allowRequest
    }

    /**
     * 获取加载更多FooterItem数据
     * @param loadMoreState  加载更多的状态[ListLoadMoreState]
     **/
    private fun updateLoadMoreItemData(loadMoreState : ListLoadMoreState) : Any{
        mLoadMoreItemData.loadMoreStatus = loadMoreState
        return mLoadMoreItemData
    }

    /**
     * 准备分页列表数据
     * @param loadMoreState 加载更多的状态[ListLoadMoreState]
     * */
    private fun prepareLoadMoreListData(loadMoreState : ListLoadMoreState) : List<Any>{
        mCurrLoadMoreState = loadMoreState
        val newItemList = mutableListOf<Any>().apply {
            addAll(mOriginList)
        }

        when(loadMoreState){
            //默认状态不显示loadMore item
            ListLoadMoreState.NORMAL->{}
            else -> {
                //在末尾添加加载更多Item数据
                newItemList.add(updateLoadMoreItemData(loadMoreState))
            }
        }
        return newItemList
    }

}