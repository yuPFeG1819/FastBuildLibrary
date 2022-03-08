package com.yupfeg.base.widget.indicator

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.yupfeg.base.R
import com.yupfeg.logger.ext.logd

/**
 * 横向滑动的指示器View
 * * 与RecyclerView绑定使用
 * @author yuPFeG
 * @date 2021/05/11
 */
class HorizontalIndicatorView : View{

    private var mViewWidth : Int = 0
    private var mViewHeight : Int = 0
    // <editor-fold desc="滚动条绘制属性">
    private var mBackgroundRect : RectF ?= null
    private var mIndicatorRect : RectF ?= null
    /**滚动条背景颜色*/
    private var mSliderBackgroundColor : Int = Color.TRANSPARENT
    /**指示器颜色*/
    private var mIndicatorColor : Int = Color.TRANSPARENT

    /**
     * 指示器所占长度的比例
     * 默认比例为1
     * */
    @Suppress("MemberVisibilityCanBePrivate")
    var indicatorRatio : Float = 0.5f
        set(value){
            field = value
            updateIndicatorWidth()
            invalidate()
        }
    /**
     * 指示器水平方向的已滑动比例，0-1
     * 默认为0
     * */
    @Suppress("MemberVisibilityCanBePrivate")
    var scrollXRatio : Float = 0f
        set(value) {
            field = value
            updateIndicatorWidth()
            invalidate()
        }

    /**外层背景部分的画笔*/
    private val mBackgroundPaint : Paint by lazy(LazyThreadSafetyMode.NONE){
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            //开启抗锯齿
            isAntiAlias = true
            //防抖
            isDither = true
            style = Paint.Style.FILL
            color = mSliderBackgroundColor
        }
    }
    /**内层指示器部分画笔*/
    private val mIndicatorPaint : Paint by lazy(LazyThreadSafetyMode.NONE){
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            //开启抗锯齿
            isAntiAlias = true
            //防抖
            isDither = true
            style = Paint.Style.FILL
            color = mIndicatorColor
        }
    }
    /**滚动条背景圆角尺寸*/
    private var mSliderRoundSize : Float = 0f

    // </editor-fold>

    constructor(context : Context) : this(context,null,0)
    constructor(context: Context,attrs: AttributeSet?) : this(context,attrs,0)
    constructor(
        context: Context,
        attrs : AttributeSet?,
        defStyleAttr : Int
    ) : super(context,attrs,defStyleAttr){
        attrs?.getCustomAttrValue()
    }

    /**
     * [AttributeSet]的拓展函数，获取自定义属性值
     * */
    @SuppressLint("ResourceType")
    private fun AttributeSet.getCustomAttrValue(){
        val typedArray = context.obtainStyledAttributes(
            this, R.styleable.HorizontalIndicatorView
        )
        mIndicatorColor = typedArray.getColor(
            R.styleable.HorizontalIndicatorView_indicatorColor,Color.TRANSPARENT
        )
        mSliderBackgroundColor = typedArray.getColor(
            R.styleable.HorizontalIndicatorView_sliderBackgroundColor,Color.TRANSPARENT
        )
        typedArray.recycle()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        mViewHeight = height
        mViewWidth = width
        mSliderRoundSize = (height/2).toFloat()
        updateBackgroundRect()
    }

    private fun updateBackgroundRect(){
        mBackgroundRect?.apply {
            right = mViewWidth.toFloat()
            bottom = mViewHeight.toFloat()
        }?: run{
            mBackgroundRect = RectF(0f,0f,mViewWidth.toFloat(),mViewHeight.toFloat())
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        mBackgroundRect?.also { rectF->
            canvas?.drawRoundRect(rectF,mSliderRoundSize,mSliderRoundSize,mBackgroundPaint)
        }
        mIndicatorRect?.also { rectF ->
            canvas?.drawRoundRect(rectF,mSliderRoundSize,mSliderRoundSize,mIndicatorPaint)
        }
    }

    /**
     * 绑定导航按钮列表
     * @param recyclerView
     * */
    fun bindRecyclerView(recyclerView: RecyclerView){
        recyclerView.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            //可滚动的水平方向范围
            val scrollRange = recyclerView.computeHorizontalScrollRange()
            //最大可显示水平方向的尺寸
            val displayWidth = recyclerView.computeHorizontalScrollExtent()
            if (scrollRange == 0 || displayWidth == 0) return@addOnLayoutChangeListener
            //TODO 需要设置最大占用比例
            indicatorRatio = displayWidth * 1f / scrollRange
        }

        recyclerView.addOnScrollListener(HorizontalScrollListener())
    }

    private inner class HorizontalScrollListener : RecyclerView.OnScrollListener(){

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            //可滚动的水平方向范围
            val scrollRange = recyclerView.computeHorizontalScrollRange()
            //最大可显示水平方向的尺寸
            val extent = recyclerView.computeHorizontalScrollExtent()
            //水平方向已滚动距离，为0时表示已处于顶部。
            val offsetX = recyclerView.computeHorizontalScrollOffset()
            if (scrollRange == 0 || extent == 0) return
            //计算出溢出部分的宽度，即屏幕外剩下的宽度
            val maxEndX : Int = if (scrollRange > extent) scrollRange - extent else offsetX
            //更新指示器滑动比例
            scrollXRatio = offsetX * 1f / maxEndX
        }
    }

    /**
     * 更新指示器的宽度
     */
    private fun updateIndicatorWidth(){
        val leftOffset = mViewWidth * (1f-indicatorRatio) * scrollXRatio
        val right = leftOffset + mViewWidth * indicatorRatio
        val bottom = mViewHeight.toFloat()
        logd("指示器的左侧偏移量：${leftOffset}，右侧坐标位置：${right}")
        mIndicatorRect?.apply {
            set(leftOffset,0f,right,bottom)
        }?:run {
            mIndicatorRect = RectF(leftOffset,0f,right,bottom)
        }
    }

    /**
     * 重置指示器的滑动状态
     * */
    fun resetScrollState(){
        scrollXRatio = 0f
    }

}