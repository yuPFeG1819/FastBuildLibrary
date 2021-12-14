package com.yupfeg.base.domain.list.helper

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.yupfeg.base.domain.extra.ListPageable
import com.yupfeg.base.domain.list.helper.intercepor.FilterChain
import com.yupfeg.base.domain.list.helper.intercepor.ListFilterInterceptor
import com.yupfeg.base.tools.bridge.LoadMoreDataTransformer
import com.yupfeg.livedata.MutableStateLiveData
import com.yupfeg.livedata.StateLiveData
import com.yupfeg.logger.ext.logd

/**
 * 列表数据过滤处理帮助类
 * * 推荐仅在`UseCase`内使用，`UseCase`需要实现[ListUseCaseFilterable]
 *
 * 需要先调用[setOriginListDataSource]设置
 *
 * @author yuPFeG
 * @date 2021/03/17
 */
@Deprecated("已废弃，目前无法整合请求过滤功能（分页需要限制请求）")
open class ListFilterDataHelper{

    /**
     * 子类实际业务用例的列表数据
     * */
    protected val mOriginList : MutableList<Any> = mutableListOf()

    /**对外公开的，实际业务的原始列表数据*/
    @Suppress("unused")
    val originListData : List<Any>
        get() = mOriginList

    //<editor-fold desc="空页面">

    private var mEmptyViewData : Any ?= null

    /**
     * 设置空数据显示的item数据
     * * 需要在外部设置要空页面对应的`ItemDelegate`才会生效
     * */
    fun setEmptyViewData(itemData : Any?){
        mEmptyViewData = itemData
    }

    //</editor-fold>

    private val mInterceptors : MutableList<ListFilterInterceptor> = mutableListOf()

    fun addInterceptor(interceptor: ListFilterInterceptor){
        mInterceptors.add(interceptor)
    }

    //<editor-fold desc="列表数据源">

    private var mOriginDataSource : ((pageIndex : Int)->Unit) ?= null

    /**设置原始列表数据来源*/
    fun setOriginListDataSource(dataSource : (pageIndex : Int)->Unit){
        this.mOriginDataSource = dataSource
    }

    //</editor-fold>

    //<editor-fold desc="订阅事件">

    /**
     * 外部分页加载列表adapter显示的数据
     */
    @Suppress("MemberVisibilityCanBePrivate")
    protected val mLoadMoreListLiveData : MutableLiveData<List<Any>> = MutableLiveData()

    /**
     * 对外公开，分页加载列表adapter列表数据变化事件的可观察对象
     * * 理论上包含原始业务列表数据与加载更多Footer的列表item显示数据
     * */
    val loadMoreListLiveData : LiveData<List<Any>>
        get() = mLoadMoreListLiveData

    /**
     * 列表加载出现错误的单次事件
     * */
    @Suppress("MemberVisibilityCanBePrivate")
    protected val mLoadMoreErrorEvent : MutableStateLiveData<Throwable>
            = MutableStateLiveData()

    /**
     * 对外公开，分页列表加载出现错误事件的可观察对象
     * * 用于外部对于出现错误时特殊处理使用
     * */
    val loadMoreErrorEvent : StateLiveData<Throwable>
        get() = mLoadMoreErrorEvent

    //</editor-fold>

    //<editor-fold desc="请求列表数据">

    /**
     * 获取列表数据
     * * 外部获取对应列表数据的默认入口
     * */
    fun getPageListData(){
        val allowQueryList = proceedFilterRequestChain()
        if(allowQueryList){
            mOriginDataSource?.invoke(1)
        }
    }

    /**
     * 开启过滤是否请求
     * */
    private fun proceedFilterRequestChain(): Boolean {
        var isAllowRequest = true
        for (interceptor in mInterceptors) {
            isAllowRequest = interceptor.allowRequest()
            if (!isAllowRequest) break
        }
        return isAllowRequest
    }

    //</editor-fold>

    //<editor-fold desc="列表数据处理">



    /**
     * 处理最终请求的数据结果，并发送到UI数据页面
     * * 在数据源请求返回结果时调用，预处理后分发给UI显示
     * @param pageIndex 当前请求页数
     * @param newList 当前页数的返回列表数据
     * */
    fun handlePageListResult(newList : List<Any>?){
        mOriginList
        //清空原有数据
        cleanOriginListData()
        dispatchNewListData(newList)
    }

    /**
     * 预处理RxJava数据源的列表数据流
     * * 实现分页加载功能的数据bean类需要实现[ListPageable]接口
     * @param requestPageIndex 当前数据请求页数
     * */
    fun <T : ListPageable> preHandleListResultFromRxSource(
        requestPageIndex : Int,
        doOnNextAction : (T)->Unit,
    ) : LoadMoreDataTransformer<T> {
        return LoadMoreDataTransformer(
            doOnNextAction = doOnNextAction,
            doOnError = {
                doOnListLoadError(requestPageIndex,it)
            }
        )
    }

    //TODO 后续添加协程的预处理操作

    private fun proceedHandleResult() {
        var tempList: List<Any>? = null
        val chain = FilterChain(
            originList = originListData,
            preHandleList = originListData,
            isInterruptChain = false
        )
        for (interceptor in mInterceptors) {
            tempList = interceptor.intercept(chain)
            chain.preHandleList = tempList
            if (chain.isInterruptChain)  break
        }

        tempList?.also {
            dispatchNewListData(it)
        }?:run {
            //分发空视图
            disPatchListEmptyData()
        }
    }

    //</editor-fold>


    //<editor-fold desc="预处理列表数据">



    /**
     * 预处理分页加载过程出现的错误
     * @param pageIndex 当前请求的页数
     * @param throwable 数据请求过程出现的错误
     */
    @Suppress("ProtectedInFinal")
    protected open fun doOnListLoadError(pageIndex : Int, throwable: Throwable){
        if (pageIndex > 1){
            //如果不是第一页数据，则更新加载更多Item数据
//            dispatchLoadMoreListData(ListLoadMoreState.ERROR)
        }else{
            cleanOriginListData()
            //显示空视图Item
            dispatchNewListData(null)
        }
        dispatchLoadMoreErrorEvent(throwable)
    }

    /**清理原始业务列表数据*/
    protected open fun cleanOriginListData() {
        if (mOriginList.isNotEmpty()){
            mOriginList.clear()
        }
    }

    //</editor-fold desc="预处理列表数据">

    //<editor-fold desc="分发UI数据">

    /**
     * 分发新的列表数据
     * @param newList 数据源返回的列表数据
     */
    fun dispatchNewListData(newList : List<Any>?){
        mLoadMoreListLiveData.value = newList
    }

    /**
     * 分发给UI处理错误信息
     * @param throwable 数据请求过程出现的错误
     * */
    fun dispatchLoadMoreErrorEvent(throwable: Throwable){
        mLoadMoreErrorEvent.value = throwable
    }

    /**
     * 分发空视图数据给UI订阅显示
     * */
    fun disPatchListEmptyData(){
        mEmptyViewData?.also {itemData->
            //分发给UI显示数据
            mLoadMoreListLiveData.value = listOf(itemData)
        }?:run {
            logd("")
        }
    }

    //</editor-fold>

}