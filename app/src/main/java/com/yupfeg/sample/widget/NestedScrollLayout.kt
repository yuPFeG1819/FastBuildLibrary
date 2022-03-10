package com.yupfeg.sample.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.math.MathUtils
import androidx.core.view.NestedScrollingParent2
import androidx.core.view.NestedScrollingParent3
import androidx.core.view.NestedScrollingParentHelper
import androidx.core.view.ViewCompat
import com.yupfeg.logger.ext.logd

/**
 * 测试嵌套滑动NestedScroll机制的滑动布局
 * - 参考学习[【透镜系列】看穿 > NestedScrolling 机制](https://juejin.cn/post/6844903761060577294#heading-20)
 * @author yuPFeG
 * @date
 */
class NestedScrollLayout(
    context: Context,attrs : AttributeSet?,defStyleAttr : Int
) : ConstraintLayout(context,attrs, defStyleAttr),NestedScrollingParent3,NestedScrollingParent2{

    private val mNestedParentHelper = NestedScrollingParentHelper(this)
    private var mHeadViewHeight : Int = 0

    constructor(context: Context) : this(context,null)

    constructor(context: Context,attrs: AttributeSet?) : this(context,attrs,0)

    // <editor-fold desc="覆写ViewGroup测量">

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val newHeightSpec = prepareChild(widthMeasureSpec,heightMeasureSpec)
        super.onMeasure(widthMeasureSpec, newHeightSpec)
    }

    private fun prepareChild(widthMeasureSpec : Int,heightMeasureSpec: Int) : Int{
        val mChildCount = childCount
        return if (mChildCount > 0){
            //默认取第一项View作为顶部视图
            val headView = getChildAt(0)
            //先单独测量这个顶部视图
            measureChildWithMargins(headView, widthMeasureSpec, 0, MeasureSpec.UNSPECIFIED, 0)
            mHeadViewHeight = headView.measuredHeight
            //由于单独测量的HeadView的高度，需要在原有基础上添加HeadView的高度大小
            MeasureSpec.makeMeasureSpec(
                MeasureSpec.getSize(heightMeasureSpec) + mHeadViewHeight,MeasureSpec.EXACTLY
            )
        }else{
            heightMeasureSpec
        }
    }

    // </editor-fold>

    // <editor-fold desc="实现NestedScrollingParent3接口">

    override fun onStartNestedScroll(child: View, target: View, axes: Int, type: Int): Boolean {
        //只支持垂直方向的嵌套滑动
        return axes and ViewCompat.SCROLL_AXIS_VERTICAL != 0
    }

    override fun onNestedScrollAccepted(child: View, target: View, axes: Int, type: Int) {
        mNestedParentHelper.onNestedScrollAccepted(child, target, axes, type)
    }

    override fun onStopNestedScroll(target: View, type: Int) {
        mNestedParentHelper.onStopNestedScroll(target, type)
    }

    override fun getNestedScrollAxes(): Int {
        return mNestedParentHelper.nestedScrollAxes
    }

    //内层滑动控件滑动完成后触发，NestedScrollingParent3接口实现
    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int,
        consumed: IntArray
    ) {
        //在子视图滑动后，滑动剩余部分，可能需要区分Type是TOUCH还是Fling滑动
        logd("content list : $target , dyUnconsumed : $dyUnconsumed ," +
                "isFling : ${type == ViewCompat.TYPE_NON_TOUCH}")
        //用户下滑 lastTouchY < eventY，内层视图未消费完成（无法继续下滑），继续滑动外层视图向下移动
        if (dyUnconsumed < 0){
            nestedScrollDown(dyUnconsumed,consumed)
        }
    }

    //内层滑动控件滑动完成后触发，NestedScrollingParent2接口实现
    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int
    ) {
        logd("content list : $target , dyUnconsumed : $dyUnconsumed" +
                "isFling : ${type == ViewCompat.TYPE_NON_TOUCH}")
        //用户下滑 lastTouchY < eventY，内层视图未消费完成（无法继续下滑），继续滑动外层视图向下移动
        if (dyUnconsumed < 0){
            nestedScrollDown(dyUnconsumed,null)
        }
    }

    //内层滑动控件滑动之前触发，
    //在子视图滑动前，先由父布局处理滑动事件，需要区分是TOUCH还是Fling滑动
    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        //用户手势上滑 lastTouchY > eventY
        logd("onNestedPreScroll dy : $dy , isFling : ${type == ViewCompat.TYPE_NON_TOUCH})")
        if(dy > 0){
            nestedScrollUp(dy, type, consumed)
        }
    }

    // </editor-fold>

    /***
     *
     */
    private fun nestedScrollUp(dy: Int, type: Int, consumed: IntArray?){
        //未消费的y轴偏移量
        // y < 0表示滑动偏移量为向上的滑动
        //由于视图滑动与偏移量是相反的，所以这里需要视图向下滑动
        val lastScrollY = scrollY
        scrollBy(0,dy)
        //记录当前滑动距离
        val consumedY = scrollY - lastScrollY
        consumed?.set(1,consumedY)
    }

    private fun nestedScrollDown(dyUnconsumed: Int,consumed: IntArray?){
        val lastScrollY = scrollY
        scrollBy(0,dyUnconsumed)
        //记录当前滑动距离
        val consumedY = scrollY - lastScrollY
        consumed?.also {
            it[1] += consumedY
        }
    }

    override fun scrollTo(x: Int, y: Int) {
        //限制当前视图滑动画布Canvas的移动距离，最大不超过HeadView的高度
        val validY = MathUtils.clamp(y, 0, mHeadViewHeight)
        super.scrollTo(x, validY)
    }

}