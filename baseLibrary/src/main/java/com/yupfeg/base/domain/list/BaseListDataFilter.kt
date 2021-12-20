package com.yupfeg.base.domain.list

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.yupfeg.base.domain.extra.EventReleasable
import com.yupfeg.livedata.MutableStateLiveData
import com.yupfeg.livedata.StateLiveData
import com.yupfeg.logger.ext.logd

/**
 * 列表数据过滤处理器的基类
 * * 如果需要`分页加载`功能，可以使用的其子类[LoadMoreListDataFilter]
 * @author yuPFeG
 * @date 2021/03/20
 */
@Suppress("unused")
abstract class BaseListDataFilter : EventReleasable{

    /**
     * 原始列表数据
     * */
    protected val mOriginList : MutableList<Any> = mutableListOf()


    /**
     * 对外公开，过滤后的adapter列表数据可观察`LiveData`
     * * 理论上包含原始业务列表数据与处理后的其他特殊item显示数据
     * */
    val listFilteredLiveData : LiveData<List<Any>>
        get() = mListFilteredLiveData

    /**
     * 处理过滤后的UI adapter显示的数据
     */
    @Suppress("MemberVisibilityCanBePrivate")
    protected val mListFilteredLiveData : MutableLiveData<List<Any>> = MutableLiveData()

    /**
     * 对外公开，分页列表加载出现错误事件的可观察对象
     * * 用于外部对于出现错误时特殊处理使用
     * */
    val listFilterErrorEvent : StateLiveData<Throwable>
        get() = mListFilterErrorEvent

    /**
     * 列表处理出现错误的单次执行事件
     * */
    @Suppress("MemberVisibilityCanBePrivate")
    protected val mListFilterErrorEvent : MutableStateLiveData<Throwable>
            = MutableStateLiveData()

    /**
     * 回收单次执行事件
     * */
    override fun releaseEvent() {
        mListFilterErrorEvent.releaseEvent()
    }

    //<editor-fold desc="错误视图item">

    @Suppress("MemberVisibilityCanBePrivate")
    protected var mErrorViewData : Any ?= null

    /**
     * 设置出现错误显示的item数据
     * * 需要在外部设置要错误视图对应的`ItemDelegate`才会生效
     * */
    fun setErrorViewData(itemData: Any?){
        mErrorViewData = itemData
    }

    //</editor-fold>

    //<editor-fold desc="空视图item">

    @Suppress("MemberVisibilityCanBePrivate")
    protected var emptyItemViewData : Any ?= null

    /**
     * 设置空数据显示的itemView数据
     * * 需要在外部设置空视图数据类型对应的`ItemStrategy`才会生效
     * */
    fun setEmptyViewData(itemData : Any?){
        emptyItemViewData = itemData
    }

    //</editor-fold>

    //<editor-fold desc="原始数据集操作">

    /**
     * 添加原始业务数据item
     * @param itemData
     */
    protected open fun addOriginListData(itemData: Any?){
        itemData?.also { mOriginList.add(it) }
    }

    /**
     * 添加原始业务集合数据
     * @param list
     * */
    protected open fun addAllOriginList(list : List<Any>?){
        list.takeUnless { it.isNullOrEmpty() }?.also{ mOriginList.addAll(it) }
    }

    /**清理原始业务列表数据*/
    protected open fun cleanOriginListData() {
        if (mOriginList.isNotEmpty()){
            mOriginList.clear()
        }
    }

    /**
     * 尝试从原始列表数据中取出[index]所在项item
     * @param index 集合索引
     * */
    protected open fun fetchOriginData(index : Int) : Any?{
        return mOriginList.getOrNull(index)
    }

    /**获取原始集合数据量*/
    protected open fun getOriginDataCount() : Int{
        return mOriginList.size
    }

    //</editor-fold desc="原始数据集操作">

    //<editor-fold desc="分发UI订阅数据">

    /**
     * 分发过滤后的列表数据给UI订阅显示
     * @param newList
     * */
    @MainThread
    protected open fun dispatchFilteredListData(newList : List<Any>){
        mListFilteredLiveData.value = newList
    }

    /**
     * 分发空视图数据给UI订阅显示
     * */
    @MainThread
    protected open fun disPatchListEmptyViewData(){
        emptyItemViewData?.also { itemData->
            //分发给UI显示数据
            mListFilteredLiveData.value = listOf(itemData)
        }?:run {
            logd("list empty item view is ignore")
        }
    }

    /**
     * 分发错误视图数据给UI订阅显示
     * */
    @MainThread
    protected open fun disPtahListErrorViewData(){
        mErrorViewData?.also {itemData->
            //分发给UI显示数据
            mListFilteredLiveData.value = listOf(itemData)
        }?:run {
            logd("list error item view is ignore")
        }
    }

    /**
     * 分发给UI处理错误信息
     * @param throwable 数据请求过程出现的错误
     * */
    @MainThread
    protected open fun dispatchLoadMoreErrorEvent(throwable: Throwable){
        mListFilterErrorEvent.value = throwable
    }

    //</editor-fold>
}