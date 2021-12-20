package com.yupfeg.base.domain.list

import androidx.annotation.MainThread
import com.yupfeg.base.R
import com.yupfeg.base.domain.extra.ListPageable
import com.yupfeg.base.provider.ResourceContentProvider
import com.yupfeg.base.widget.recyclerview.ListLoadMoreState
import com.yupfeg.base.widget.recyclerview.strategy.LoadMoreFooterItemBean
import com.yupfeg.base.widget.recyclerview.strategy.ILoadMoreItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach

/**
 * 分页加载列表数据过滤处理帮助类
 * * 推荐仅在`UseCase`内使用，`UseCase`需要实现[ListUseCaseFilterable]
 *
 * @author yuPFeG
 * @date 2021/03/17
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
open class LoadMoreListDataFilter(
    /**单页的请求数据量 */
    protected val requestCount : Int = DEF_PAGE_DATA_COUNT,
    /**原始集合的数据来源*/
    private var originDataSource : ((pageIndex : Int)->Unit)
) : BaseListDataFilter(){
    companion object {
        /**默认单页加载数据量，默认为10条数据*/
        const val DEF_PAGE_DATA_COUNT = 10
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


    //<editor-fold desc="分页加载 item">

    private var mLoadMoreItemData : ILoadMoreItem = defaultLoadMoreItem.copy()

    /**当前加载更多的状态*/
    protected val currLoadMoreState : ListLoadMoreState
        get() = mLoadMoreItemData.loadMoreStatus

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
            originDataSource(1)
        }?:run {
            requestLoadMoreData()
        }
    }

    /**
     * 处理的数据请求结果，并发送到UI数据页面
     * * 在数据源请求返回结果时调用，处理后分发给UI显示
     * @param newList 当前请求的列表数据
     * */
    open fun setNewListData(newList : List<Any>?){
        if (currPageIndex == 1){
            //第一页时，清空原有数据
            cleanOriginListData()
        }else if (newList.isNullOrEmpty()){
            //如果是分页加载时数据为空，则表示列表已经到底了，切换FooterView显示状态为END
            dispatchLoadMoreListData(ListLoadMoreState.THE_END)
            return
        }
        dispatchNewListData(currPageIndex, newList)
    }


    //<editor-fold desc="预处理集合数据">

    /**
     * [Flow]数据流拓展函数，预处理分页列表数据
     * @param requestPageIndex
     */
    open fun <T : ListPageable> Flow<T>.preProcessPageableList(requestPageIndex: Int){
        this.onEach { updateListPageIndex(requestPageIndex) }
            .catch { error-> doOnLoadMoreError(requestPageIndex,error) }
    }

    /**
     * 更新请求成功的页数
     * - 在执行成功列表分页时需要调用该方法更新页数
     * @param pageIndex 请求成功的分页页数
     * */
    protected open fun updateListPageIndex(pageIndex: Int){
        currPageIndex = pageIndex
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
     * 准备请求获取分页列表数据
     * */
    protected open fun requestLoadMoreData(){
        if (getOriginDataCount() < requestCount){
            // 如果当前加载的数据还不满一页，则不执行上拉加载操作
            return
        }

        if (currLoadMoreState == ListLoadMoreState.LOADING){
            //过滤已处于正在分页加载更多的操作，防止重复触发请求
            return
        }
        //在原有列表末尾添加正在加载item视图
        dispatchLoadMoreListData(ListLoadMoreState.LOADING)
        //尝试请求新一页数据
        originDataSource(currPageIndex+1)
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
        updateLoadMoreItemData(loadMoreState)
        val newItemList = ArrayList<Any>(mOriginList.size+1).apply {
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
     * 更新FooterItem数据
     * @param loadMoreState  加载更多的状态[ListLoadMoreState]
     **/
    protected open fun updateLoadMoreItemData(loadMoreState : ListLoadMoreState) : Any{
        mLoadMoreItemData.loadMoreStatus = loadMoreState
        return mLoadMoreItemData
    }
}