package com.yupfeg.base.domain.list.helper

import androidx.annotation.MainThread
import com.yupfeg.base.R
import com.yupfeg.base.domain.list.ListUseCaseFilterable
import com.yupfeg.base.domain.extra.ListPageable
import com.yupfeg.base.provider.ResourceContentProvider
import com.yupfeg.base.tools.bridge.LoadMoreDataTransformer
import com.yupfeg.base.widget.recyclerview.ListLoadMoreState
import com.yupfeg.base.widget.recyclerview.strategy.LoadMoreFooterItemBean
import com.yupfeg.base.widget.recyclerview.strategy.ILoadMoreItem
import com.yupfeg.logger.ext.logd

/**
 * 分页加载列表数据过滤处理帮助类
 * * 推荐仅在`UseCase`内使用，`UseCase`需要实现[ListUseCaseFilterable]
 *
 * @author yuPFeG
 * @date 2021/03/17
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
open class LoadMoreFilterDataHelper(
    /**单页的请求数据量 */
    protected val requestCount : Int = DEF_PAGE_DATA_COUNT,
    /**原始集合的数据来源*/
    private var originDataSource : ((pageIndex : Int)->Unit)
) : BaseListFilterDataHelper(){
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

    //<editor-fold desc="分页请求成员变量">

    /**
     * 当前加载页数
     * * 只可在子类内部修改
     * */
    @Suppress("MemberVisibilityCanBePrivate")
    var currPageIndex : Int = 1
        protected set(value){
            field = if (value <= 1) 1
            else value
        }

    /**
     * 列表数据源的数据总量
     * * 仅用于计算最大页数
     * */
    @Suppress("MemberVisibilityCanBePrivate")
    var totalDataCount : Int = 0
        protected set(value){
            field = if (value <= 0) 0
            else value
        }

    /**当前加载更多的状态*/
    private var mCurrLoadMoreState : ListLoadMoreState = ListLoadMoreState.NORMAL

    //</editor-fold>

    //<editor-fold desc="分页加载 item">

    private var mLoadMoreItemData : ILoadMoreItem = defaultLoadMoreItem.copy()

    /**
     * 设置分页加载更多的列表item显示数据
     * * 不设置则采用默认的显示item[LoadMoreFooterItemBean]
     * @param itemData 分页加载item项bean类，需要实现[ILoadMoreItem]接口
     * */
    fun setLoadMoreFooterItem(itemData: ILoadMoreItem?){
        itemData?.apply { mLoadMoreItemData = this }
    }

    //</editor-fold>


    /**设置原始列表数据来源*/
    fun setOriginListDataSource(dataSource : (pageIndex : Int)->Unit){
        this.originDataSource = dataSource
    }


    /**
     * 获取列表数据
     * * 外部获取对应列表数据的默认入口
     * @param isLoadMore 是否为加载更多
     * */
    open fun getPageListData(isLoadMore : Boolean){
        takeUnless { isLoadMore }?.also {
            originDataSource.invoke(1)
        }?:run {
            prepareObtainLoadMoreData()
        }
    }

    /**
     * 处理的数据请求结果，并发送到UI数据页面
     * * 在数据源请求返回结果时调用，处理后分发给UI显示
     * @param pageIndex 当前请求页数
     * @param newList 当前页数的返回列表数据
     * */
    open fun setNewListData(pageIndex: Int, newList : List<Any>?){
        if (pageIndex == 1){
            //第一页时，清空原有数据
            cleanOriginListData()
        }
        dispatchNewListData(pageIndex, newList)
    }


    //<editor-fold desc="预处理集合数据">

    /**
     * 预处理可分页的原始集合数据的RxJava数据流
     * * 实现分页加载功能的数据bean类需要实现[ListPageable]接口
     * @param requestPageIndex 当前数据请求页数
     * */
    open fun <T : ListPageable> preProcessPageableList(
        requestPageIndex : Int
    ) : LoadMoreDataTransformer<T> {
        return LoadMoreDataTransformer(
            doOnNextAction = {result->
                doOnListLoadMore(requestPageIndex,result.totalCount)
            },
            doOnError = {
                doOnLoadMoreError(requestPageIndex,it)
            }
        )
    }

    /**
     * 子类在执行列表分页时需要调用该方法，更新请求成功的页数与总计数据量
     * @param pageIndex 请求成功的分页页数
     * @param totalCount 数据源总计列表item数据量
     * */
    protected open fun doOnListLoadMore(pageIndex: Int,totalCount : Int){
        currPageIndex = pageIndex
        totalDataCount = totalCount
    }

    /**
     * 预处理分页加载过程出现的错误
     * @param pageIndex 当前请求的页数
     * @param throwable 数据请求过程出现的错误
     */
    protected open fun doOnLoadMoreError(pageIndex : Int, throwable: Throwable){
        if (pageIndex > 1){
            //如果不是第一页数据，则更新加载更多Item数据
            dispatchLoadMoreListData(ListLoadMoreState.ERROR)
        }else{
            cleanOriginListData()
            //显示空视图Item
            dispatchNewListData(1,null)
        }
        dispatchLoadMoreErrorEvent(throwable)
    }

    /**
     * 准备获取分页列表数据
     * */
    protected open fun prepareObtainLoadMoreData(){
        takeUnless {
            // 如果当前加载的数据还不满一页，则不执行上拉加载操作
            getOriginDataCount() < requestCount || totalDataCount <= 0
        }?.takeUnless {
            //过滤已处于正在分页加载更多的操作，防止重复触发请求
            mCurrLoadMoreState == ListLoadMoreState.LOADING
        }?.also {
            //计算是否获取列表数据
            calculateObtainLoadMoreData()
        }?:run {
            logd("ignore load more action")
        }
    }

    /**计算是否请求列表数据*/
    protected open fun calculateObtainLoadMoreData() {
        //计算总页数
        val totalPageSize = if (totalDataCount % requestCount == 0) {
            totalDataCount / requestCount
        } else {
            totalDataCount / requestCount + 1
        }

        if (getOriginDataCount() < totalDataCount && currPageIndex < totalPageSize) {
            dispatchLoadMoreListData(ListLoadMoreState.LOADING)
            //存在更多数据，加载下一页 (切换FooterView显示状态为Loading)
            originDataSource.invoke(currPageIndex+1)
        } else {
            //已经到底了，loadMoreItem显示为TheEnd
            dispatchLoadMoreListData(ListLoadMoreState.THE_END)
        }
    }

    //</editor-fold desc="预处理列表数据">

    //<editor-fold desc="分发UI数据">

    /**
     * 分发新的列表数据
     * * 仅用于请求数据层成功后更新原始列表数据使用
     * @param pageIndex 当前请求成功的页数, 第一页时，会清空原有数据
     * @param newList 数据源返回的列表数据，如果数据为空，则添加空视图显示
     */
    @MainThread
    protected open fun dispatchNewListData(pageIndex: Int, newList : List<Any>?){
        newList.takeUnless { it.isNullOrEmpty() }?.also {list->
            addAllOriginList(list)
            //分发显示列表数据
            dispatchLoadMoreListData(ListLoadMoreState.NORMAL)
        }?:takeIf{ pageIndex == 1 }?.run {
            //分发显示列表空视图
            disPatchListEmptyViewData()
        }
    }

    /**
     * 分发列表数据给UI订阅显示
     * @param loadMoreState 加载更多的状态[ListLoadMoreState]
     * */
    @MainThread
    protected open fun dispatchLoadMoreListData(loadMoreState : ListLoadMoreState){
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
        //分发给UI显示数据
        dispatchFilteredListData(newItemList)
    }


    //</editor-fold>

    /**
     * 获取加载更多FooterItem数据
     * @param loadMoreState  加载更多的状态[ListLoadMoreState]
     **/
    protected open fun updateLoadMoreItemData(loadMoreState : ListLoadMoreState) : Any{
        mLoadMoreItemData.loadMoreStatus = loadMoreState
        return mLoadMoreItemData
    }
}