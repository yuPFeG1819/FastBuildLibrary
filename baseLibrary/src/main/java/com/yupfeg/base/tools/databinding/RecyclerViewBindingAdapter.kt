package com.yupfeg.base.tools.databinding

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yupfeg.base.widget.recyclerview.RecyclerListAdapter
import com.yupfeg.base.widget.recyclerview.itemdecoration.config.SimpleItemDividerConfig
import com.yupfeg.base.widget.recyclerview.itemdecoration.ext.addDslSimpleDivider

/**
 * [RecyclerView]的拓展函数，设置adapter
 * * DataBinding专用函数，所有DataBinding属性在xml都需要以["@{}"]赋值，否则会报错，且属性必须为可空状态
 * @param adapter
 * @receiver [RecyclerView]
 * */
@Suppress("unused")
@BindingAdapter(value = ["recyclerAdapter"])
fun RecyclerView.bindRecyclerViewAdapter(adapter : RecyclerView.Adapter<RecyclerView.ViewHolder>?){
    adapter?:return
    this.adapter = adapter
}

/**
 * [RecyclerView]设置列表数据
 * * DataBinding专用函数，所有`DataBinding`属性在xml都需要以["@{}"]赋值，否则会报错
 * * 如果赋值为`LiveData`对象，必须给`Binding`对象赋值`LifecycleOwner`，
 * 否则会无法识别类型，直接赋值为`LiveData`
 * @param listData 列表item数据
 * @receiver [RecyclerView]
 * */
@Suppress("unused")
@BindingAdapter(value = ["adapterList"])
fun RecyclerView.bindAdapterListData(listData : List<Any>?){
    this.adapter?: throw NullPointerException("you must set adapter to RecyclerView")

    (this.adapter as? RecyclerListAdapter)?.apply {
        adapterList = listData?: arrayListOf()
    } ?:run {
        throw NullPointerException("you must set RecyclerListAdapter to RecyclerView adapter")
    }
}

/**
 * [RecyclerView]拓展函数，设置layoutManager
 * * DataBinding专用函数，所有DataBinding属性在xml都需要以["@{}"]赋值，否则会报错，且属性必须为可空状态
 * @param layoutManager
 * @receiver [RecyclerView]
 * */
@Suppress("unused")
@BindingAdapter(value = ["layoutManager"])
fun RecyclerView.bindLayoutManager(layoutManager : RecyclerView.LayoutManager?){
    this.layoutManager = layoutManager
}

/**
 * [RecyclerView]拓展函数，设置layoutManager
 * * DataBinding专用函数，所有DataBinding属性在xml都需要以["@{}"]赋值，否则会报错，且属性必须为可空状态
 * @param onLoadMoreAction 预加载执行操作
 * @param prefetchSize 开启预加载的阈值，距底部[prefetchSize]个item之前，执行预加载操作，默认为1
 * @receiver [RecyclerView]
 * */
@Suppress("unused")
@BindingAdapter(
    value = ["onLoadMoreAction","prefetchSizeLimit"],
    requireAll = false
)
fun RecyclerView.bindListLoadMoreAction(onLoadMoreAction : (()->Unit)?,
                                       prefetchSize : Int = 1){
    this.adapter?: throw NullPointerException("you must set adapter to RecyclerView")
    (this.adapter as? RecyclerListAdapter)?.also {
        it.onPreLoadAction = onLoadMoreAction
        it.prefetchSizeLimit = prefetchSize
    }?: throw NullPointerException("you must set RecyclerListAdapter to RecyclerView adapter")
}

/**
 * [RecyclerView]拓展函数，添加简单分割线或item间距
 * * DataBinding专用函数，所有DataBinding属性在xml都需要以["@{}"]赋值，否则会报错，且属性必须为可空状态
 * @param dividerInitAction 分割线dsl方式初始化
 * @receiver [RecyclerView]
 */
@Suppress("unused")
@BindingAdapter(value = ["simpleDivider"])
fun RecyclerView.bindAddSimpleDivider(dividerInitAction: SimpleItemDividerConfig.()->Unit){
    this.addDslSimpleDivider(dividerInitAction)
}

/**
 * [RecyclerView]拓展函数，添加额外的item装饰类
 * * DataBinding专用函数，所有DataBinding属性在xml都需要以["@{}"]赋值，否则会报错，且属性必须为可空状态
 * @param list 多个item装饰类的集合，按添加顺序进行渲染
 */
@Suppress("unused")
@BindingAdapter(value = ["itemDecorations"])
fun RecyclerView.bindAddItemDecorations(list : List<RecyclerView.ItemDecoration>?){
    list?:return
    for (decoration in list) {
        this.addItemDecoration(decoration)
    }
}

/**
 * [RecyclerView]拓展函数，添加滑动监听
 * * DataBinding专用函数，所有DataBinding属性在xml都需要以["@{}"]赋值，否则会报错，且属性必须为可空状态
 * @param listener 滑动监听
 */
@Suppress("unused")
@BindingAdapter(value = ["onListScrollListener"])
fun RecyclerView.bindAddScrollListener(listener : RecyclerView.OnScrollListener?){
    listener?:return
    this.addOnScrollListener(listener)
}
