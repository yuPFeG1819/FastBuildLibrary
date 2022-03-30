package com.yupfeg.base.widget.recyclerview

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.LinearSmoothScroller.SNAP_TO_END
import androidx.recyclerview.widget.LinearSmoothScroller.SNAP_TO_START
import androidx.recyclerview.widget.RecyclerView

/**
 * [RecyclerView]的拓展函数，平滑移动列表到指定索引位置并将对应ItemView置顶
 * - 不论目标列表索引当前是否已显示都会执行
 * @param position 列表索引
 * */
@Suppress("unused")
fun RecyclerView.smoothScrollToPositionOnStart(position: Int){
    smoothScrollToPosition(position,SNAP_TO_START)
}

/**
 * [RecyclerView]的拓展函数，平滑移动列表到指定索引位置并对应ItemView置底
 * - 不论目标列表索引当前是否已显示都会执行
 * @param position 列表索引
 * */
@Suppress("unused")
fun RecyclerView.smoothScrollToPositionOnEnd(position: Int){
    smoothScrollToPosition(position,SNAP_TO_END)
}

/**
 * [RecyclerView]的拓展函数，平滑滑动到指定索引，并置顶或置底
 * - 不论目标列表索引当前是否已显示都将执行
 * @param position 列表索引
 * @param snapPreference 平滑滑动的结束位置，[SNAP_TO_START]与[SNAP_TO_END]
 * */
private fun RecyclerView.smoothScrollToPosition(position : Int,snapPreference: Int){
    layoutManager?.also {
        val smoothScroller = StickLinearSmoothScroller(this.context,snapPreference)
        smoothScroller.targetPosition = position
        it.startSmoothScroll(smoothScroller)
    }
}

/**
 * 优化`smoothScrollToPosition`进行平滑滑动的实际操作类，将目标索引ItemView置顶或置底，不论目标索引当前是否可见
 * - 在`RecyclerView`内调用`smoothScrollToPosition`时，会代理给`LayoutManager`进行平滑滑动，
 * 内部就创建一个[LinearSmoothScroller]进行滑动
 * - 但原始`smoothScrollToPosition`的实现只能将目标`position`滑动到屏幕可见位置，
 * 如果目标`position`已经在屏幕则不会有任何动作。
 * 参考思路: [平滑移动并置顶](https://blog.csdn.net/weixin_39428125/article/details/89032646)
 * @param snapPreference 平滑滑动的结束位置类型，[SNAP_TO_START]与[SNAP_TO_END]
 */
open class StickLinearSmoothScroller(
    context: Context,
    private val snapPreference : Int
) : LinearSmoothScroller(context){

    override fun getHorizontalSnapPreference(): Int {
        return snapPreference
    }

    override fun getVerticalSnapPreference(): Int {
        return snapPreference
    }
}

/**
 * [RecyclerView]的拓展函数，快捷调用[LinearLayoutManager]的`scrollToPositionWithOffset`
 * - 只在设置为[LinearLayoutManager]时生效。
 * - 不论指定[position]所在的ItemView是否显示在可见范围内，都会直接滑动到顶部。
 * - ``
 * @param position 列表索引
 * @param offset itemView的顶部 和 RecyclerView 的顶部之间的距离，px ，默认为0px
 * */
@Suppress("unused")
fun RecyclerView.scrollToPositionOffset(position : Int,offset : Int = 0){
    (this.layoutManager as? LinearLayoutManager)?.scrollToPositionWithOffset(position, offset)
}