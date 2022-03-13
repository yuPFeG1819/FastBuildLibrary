package com.yupfeg.base.widget.grid

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import com.yupfeg.base.R
import kotlin.math.min

/**
 * 九宫格布局，
 * - 仿微博样式，在一个item时显示设置尺寸
 * @author yuPFeG
 * @date 2022/02/21
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
open class NineGridLayout(
    context: Context, attrs : AttributeSet?, defStyleAttr : Int
) : ViewGroup(
    context, attrs, defStyleAttr
){

    companion object{
        /**最大子视图的数量*/
        private const val MAX_CHILD_COUNT = 9
        /**最大子视图的排布列数*/
        private const val MAX_COLUMN_COUNT = 3
        /**默认更多文本尺寸*/
        private const val DEF_TEXT_SIZE = 22f
        /** 默认的item间距 */
        private const val DEF_SPACE = 4f
        /**半透明黑色的颜色值*/
        private const val HALF_TRANS_BLACK = "#80000000"
    }

    /**行数*/
    private var mRowNum : Int = 0
    /**列数*/
    private var mColumnNum : Int = 0

    /**暂存的子视图宽度，仅在测量阶段后有效*/
    protected var mChildViewWidth : Int = 0
    /**暂存的子视图高度，仅在测量阶段后有效*/
    protected var mChildViewHeight : Int = 0

    /**子视图的间距*/
    private var mChildViewSpace : Int = 0

    /**
     * 内部子View的适配器类
     * */
    protected var mItemAdapter : Adapter? = null

    /**
     * 最后一项View的绘制范围
     * */
    protected var mLastViewRect : Rect = Rect()

    protected val mTextPaint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    protected val mMaskPaint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    /**
     * 是否在最后一项View显示遮罩蒙版
     * * 只在子View数量超过最大限制时才生效
     * true-表示在超出最大item限制后，会在最后一个item显示遮罩层，展示剩余item数量
     * */
    var isShowMask : Boolean = true

    private var mOnItemClickListener : OnItemClickListener? = null

    constructor(context: Context) : this(context,null)

    constructor(context: Context,attrs: AttributeSet?) : this(context,attrs,0)

    init {
        initAttribute(this.context,attrs)
    }

    // <editor-fold desc="自定义属性">

    private fun initAttribute(context: Context,attrs: AttributeSet?){
        attrs?:return

        val typeArray = context.obtainStyledAttributes(R.styleable.NineGridLayout)
        mChildViewSpace = typeArray.getDimension(
            R.styleable.NineGridLayout_gridSpace,
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,DEF_SPACE,resources.displayMetrics
            )
        ).toInt()
        val textColor = typeArray.getColor(
            R.styleable.NineGridLayout_gridMoreNumTextColor,Color.WHITE
        )
        val textSize = typeArray.getDimension(
            R.styleable.NineGridLayout_gridMoreNumTextSize,
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,DEF_TEXT_SIZE,resources.displayMetrics
            )
        )
        isShowMask = typeArray.getBoolean(R.styleable.NineGridLayout_moreNumMask,true)
        val maskColor = typeArray.getColor(
            R.styleable.NineGridLayout_gridMoreMaskColor,
            Color.parseColor(HALF_TRANS_BLACK)
        )
        typeArray.recycle()

        mTextPaint.style = Paint.Style.FILL_AND_STROKE
        mTextPaint.textAlign = Paint.Align.CENTER
        mTextPaint.color = textColor
        mTextPaint.textSize = textSize

        mMaskPaint.color = maskColor
        mMaskPaint.style = Paint.Style.FILL_AND_STROKE

    }

    // </editor-fold>

    /**
     * 设置子视图的内部间距
     * @param space 间距，单位dp
     * */
    fun setItemSpace(space : Int){
        mChildViewSpace = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,space.toFloat(),resources.displayMetrics
        ).toInt()
    }

    /**
     * 设置剩余item数量的文本字体大小
     * - 仅在超出最大item数量后，显示剩余item数量
     * - 需要配合[isShowMask]设置为true(默认值)
     * @param textSize 字体大小，单位sp
     * */
    fun setMoreTextSize(textSize : Float){
        mTextPaint.textSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,textSize,resources.displayMetrics
        )
        invalidate()
    }

    /**
     * 设置剩余item数量的文本颜色
     * - 仅在超出最大item数量后，显示剩余item数量，默认为白色
     * - 需要配合[isShowMask]设置为true(默认值)
     * @param textColor 文本颜色Int值
     * */
    fun setMoreTextColor(@ColorInt textColor : Int){
        mTextPaint.color = textColor
        if (!isShowMask) return
        invalidate()
    }

    /**
     * 设置在item数量超过限制后的遮罩层颜色，默认为半透明黑色
     * - 需要配合[isShowMask]设置为true(默认值)
     * @param color 遮罩层颜色
     * */
    fun setMoreMaskColor(@ColorInt color : Int){
        mMaskPaint.color = color
        if (!isShowMask) return
        invalidate()
    }

    /**
     * 设置item视图的点击事件
     * */
    fun setOnItemClickListener(listener: OnItemClickListener){
        this.mOnItemClickListener = listener
    }

    // <editor-fold desc="测量尺寸">

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (childCount <= 0){
            //如果item数量为0，则仅保留父容器的宽度限制，高度置为0
            setMeasuredDimension(widthMeasureSpec,0)
            return
        }

        if (mRowNum == 0 || mColumnNum == 0){
            initRowAndColumn(childCount)
        }
        val minWidth = paddingLeft + paddingRight + suggestedMinimumWidth
        val groupWidthSpec = resolveSizeAndState(minWidth,widthMeasureSpec,1)
        val availableWidth = MeasureSpec.getSize(groupWidthSpec) - paddingLeft - paddingRight
        //根据父容器的可用尺寸，测量子View的尺寸
        measureChildView(availableWidth,heightMeasureSpec)
        val groupHeightSpec = resolveSizeAndState(
            calculateGroupHeight(),heightMeasureSpec,0
        )
        //保存计算后的ViewGroup尺寸
        setMeasuredDimension(groupWidthSpec,groupHeightSpec)
    }

    /**
     * 初始化九宫格的行数与列数
     * @param childCount 显示的子View数量
     * */
    private fun initRowAndColumn(childCount : Int){
        when(childCount){
            in 0..3 -> {
                mRowNum = 1
                mColumnNum = childCount
            }
            in 4..6 -> {
                mRowNum = 2
                //特殊情况，如果子View正好只有4个,则确保为2列，
                mColumnNum = if (childCount == 4) 2 else 3
            }
            else ->{
                mRowNum = 3
                mColumnNum = 3
            }
        }
    }

    /**
     * 测量子View的尺寸
     * @param availableWidth 父容器的可用宽度
     * @param heightMeasureSpec 当前父容器的高度限制
     * */
    protected open fun measureChildView(availableWidth : Int,heightMeasureSpec: Int) {
        if (childCount == 1){
            mItemAdapter?:return
            val adapter = mItemAdapter!!
            // 根据外部设置图片宽度设置单张图片的尺寸，可以在adapter内由外部提供
            // 宽高应该限制在父容器内
            val singleWidth = adapter.singleViewWidth
            val singleHeight = adapter.singleViewHeight
            mChildViewWidth = min(availableWidth,singleWidth)
            mChildViewHeight = singleHeight
            return
        }
        //正常情况，子View宽度按照 ：（可用宽度 - （间距宽度）） / 3 ，子View高度呈正方形表示
        mChildViewWidth = (availableWidth - mChildViewSpace * (childCount - 1)) / MAX_COLUMN_COUNT
        mChildViewHeight = mChildViewWidth
    }

    /**
     * 计算父容器的整体高度
     * */
    protected open fun calculateGroupHeight() : Int{
        // 子View高度 * 行数 + 间距 * 行数 + 上间距 + 下间距
        return mChildViewHeight * mRowNum + mChildViewSpace * (mRowNum -1) + paddingTop + paddingBottom
    }

    // </editor-fold>

    // <editor-fold desc="布局位置">

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        onLayoutChildren()
    }

    open fun onLayoutChildren(){
        if (mRowNum == 0 || mColumnNum == 0) return
        var usedLeft = paddingLeft
        var usedTop = paddingTop
        val newChildCount = childCount
        mLastViewRect.setEmpty()
        for(index in 0 until newChildCount){
            val childView = getChildAt(index)
            childView.layout(
                usedLeft,usedTop,usedLeft + mChildViewWidth,usedTop + mChildViewHeight
            )
            if (index == MAX_CHILD_COUNT-1){
                //记录九宫格的最后一项View的布局范围
                mLastViewRect.set(
                    usedLeft,usedTop,
                    usedLeft+mChildViewWidth,usedTop + mChildViewHeight
                )
            }

            //叠加已用宽度
            usedLeft += mChildViewWidth + mChildViewSpace
            //计算下一个索引View是否需要换行
            if ((index+1) % mColumnNum == 0){
                usedLeft = paddingLeft
                //叠加已用高度
                usedTop += mChildViewHeight + mChildViewSpace
            }
            //设置防抖点击事件
            childView.setOnClickListener(SingleClickListenerWrapper {
                dispatchChildrenClick(childView,index)
            })
        }
    }

    open fun dispatchChildrenClick(child : View,position: Int){
        mOnItemClickListener?.onItemClick(child,position)
    }

    // </editor-fold>

    // <editor-fold desc="绘制额外内容">

    override fun dispatchDraw(canvas: Canvas?) {
        super.dispatchDraw(canvas)
        canvas?:return
        //在绘制完所有子View之后，当子View超过最大可显示数量，需要在末尾显示剩余数量
        val totalChildCount = mItemAdapter?.count?:childCount
        if (totalChildCount > MAX_CHILD_COUNT){
            drawSurplusNumContent(canvas,totalChildCount)
        }
    }

    /**
     * 绘制剩余数量的文本
     * @param canvas
     * @param totalChildCount 总计的子View个数
     * */
    protected open fun drawSurplusNumContent(canvas: Canvas, totalChildCount : Int){
        if (mLastViewRect.isEmpty) return
        if (!isShowMask) return

        val text = "+${totalChildCount - MAX_CHILD_COUNT}"
        val lastViewHeight = mLastViewRect.height()
        val lastViewWidth = mLastViewRect.width()
        //绘制半透明背景
        canvas.drawRect(mLastViewRect,mMaskPaint)
        //计算文本尺寸
        val fontMetrics = mTextPaint.fontMetrics
        val textHeight = fontMetrics.bottom - fontMetrics.top
        //计算文本的基线位置，绘制位于最后一个View的中心
        val baseline = mLastViewRect.top + lastViewHeight/2f + (textHeight/2) - fontMetrics.bottom
        val startX = mLastViewRect.left + lastViewWidth/2f
        //绘制剩余View数量
        canvas.drawText(text,startX,baseline,mTextPaint)
    }

    // </editor-fold>

    // <editor-fold desc="添加item视图">

    /**
     * 校验是否已存在adapter
     * */
    fun hasAdapter() = mItemAdapter != null

    /**
     * 添加视图适配器，并更新item视图变化
     * @param adapter 子视图适配器
     * */
    fun setViewAdapter(adapter: Adapter){
        mItemAdapter = adapter
        notifyItemChanged()
    }

    /**
     * 更新item视图的变化
     * - 调用方法会触发布局请求
     * */
    fun notifyItemChanged(){
        mItemAdapter?: throw NullPointerException(
            "item adapter is null , you should set item adapter first"
        )

        val newCount = mItemAdapter!!.count
        val childCount = min(newCount,MAX_CHILD_COUNT)
        //根据新的视图数量，初始化九宫格的行数与列数
        initRowAndColumn(childCount)
        tryAddChangedViews(mItemAdapter!!,childCount)
        requestLayout()
    }

    /**
     * 尝试添加新的子视图到布局容器内
     * @param adapter 子视图的适配器
     * @param childCount 子View的总计数量
     * */
    private fun tryAddChangedViews(adapter: Adapter,childCount: Int){
        removeAllViews()
        for (i in 0 until childCount){
            val childView = adapter.getView(this,i)
            //延迟到布局阶段再添加子View
            addViewInLayout(childView,i,childView.layoutParams,true)
        }
    }
    // </editor-fold>

    private class SingleClickListenerWrapper(
        private val originListener : OnClickListener
    ) : OnClickListener{
        companion object{
            /**默认延迟时间*/
            const val DELAY_TIME = 400
        }

        /**上次点击时间*/
        private var mLastTime = 0L

        override fun onClick(v: View?) {
            val currentTimeMillis = System.currentTimeMillis()
            if (currentTimeMillis - mLastTime > DELAY_TIME){
                mLastTime = currentTimeMillis
                originListener.onClick(v)
            }
        }
    }

    /**
     * 九宫格Item视图点击事件
     * */
    interface OnItemClickListener{
        /**
         * Item点击时调用
         * @param view 触发点击事件的View
         * @param position 当前点击在九宫格内的索引
         * */
        fun onItemClick(view : View,position: Int)
    }

    /**
     * 提供九宫格子View内容的适配器
     * */
    interface Adapter{

        /**
         * 单个View时的高度，px
         * */
        val singleViewHeight : Int

        /**
         * 单个View时的宽度，px
         * */
        val singleViewWidth : Int

        /**
         * 子View的总计数量
         * */
        val count : Int

        /**
         * 获取子View对象
         * - 可以在子类重写
         * @param viewGroup 九宫格Layout[NineGridLayout]
         * @param position 当前View在容器的位置索引
         * */
        fun getView(viewGroup: ViewGroup,position: Int) : View

    }
}