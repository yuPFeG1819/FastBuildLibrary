package com.yupfeg.base.widget.recyclerview.itemdecoration.divider

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.yupfeg.base.tools.toPx
import com.yupfeg.base.widget.recyclerview.itemdecoration.config.SimpleItemDividerConfig


/**
 * recyclerView分割线绘制类
 * @author yuPFeG
 * @date 2019/10/15
 */
class SimpleDividerItemDecoration private constructor() : RecyclerView.ItemDecoration(){

    companion object{
        /**
         * 创建列表的分割线装饰类
         * @param config 配置类
         * */
        fun create(config: SimpleItemDividerConfig) : SimpleDividerItemDecoration {
            return SimpleDividerItemDecoration().apply {
                leftOffset = config.left.toPx()
                topOffset = config.top.toPx()
                rightOffset = config.right.toPx()
                bottomOffset = config.bottom.toPx()
                colorId = config.colorInt
                isExcludeLast = config.excludeLastItem
            }
        }
    }

    @Suppress("unused")
    constructor(left : Int, top : Int, right : Int,
                bottom : Int, @ColorInt colorInt: Int) : this(){
        this.leftOffset = left
        this.topOffset = top
        this.rightOffset = right
        this.bottomOffset = bottom
        this.colorId = colorInt
    }

    /**
     * item左侧辅助线（间距）尺寸，单位px
     * * 在itemView左侧留出的偏移量，留出绘制辅助线位置
     * */
    var leftOffset : Int = 0
    /**
     * item顶部辅助线（间距）尺寸，单位px
     * * 在itemView上方留出的偏移量，留出绘制辅助线位置
     * */
    var topOffset : Int = 0
    /**
     * item右侧辅助线（间距）尺寸，单位px
     * * 在itemView右侧留出的偏移量，留出绘制辅助线位置
     * */
    var rightOffset : Int = 0
    /**
     * item底部（间距）尺寸，单位px
     * * 同时在itemView下方留出的偏移量，留出绘制辅助线位置
     * */
    var bottomOffset : Int = 0
    /**
     * 分割线（间距）的绘制颜色id
     * * 默认为[Color.TRANSPARENT],不进行绘制，只留出item间的[horizontalSize]与[verticalSize]位置
     * * 推荐通过[ContextCompat.getColor]或者[Color.parseColor]获取
     * */
    @ColorInt var colorId: Int = Color.TRANSPARENT
        set(value) {
            field = value
            mPaint.color = value
        }

    /**是否排除最后一项item，默认为true，不对最后一项item绘制*/
    private var isExcludeLast : Boolean = true

    private val mPaint : Paint by lazy(LazyThreadSafetyMode.NONE) {
        Paint().apply {
            //开启抗锯齿
            isAntiAlias = true
            color = colorId
        }
    }

    /**
     * 获得item装饰的偏移量 ---- 就相当于空出一块矩形空间 ,提供[onDraw]的绘制区域
     *
     * * 每个item都会执行一次getItemOffsets()
     */
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView,
                                state: RecyclerView.State) {
        parent.getChildViewHolder(view) ?: return
        //outRect就是表示在item的上下左右所撑开的距离,默认值为0
        outRect.set(leftOffset, topOffset, rightOffset, bottomOffset)
    }

    /**
     * 在ItemView绘制之前调用(只会执行一次)
     * 先于[onDrawOver]调用，绘制在ItemView的下层
     * * 超出[getItemOffsets]方法设置的rect范围，会被itemView遮挡
     * */
    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(canvas, parent, state)
        takeUnless { mPaint.color == Color.TRANSPARENT } ?: return
        drawDivider(canvas,parent)
    }

    /**
     * 绘制分割线
     * @param canvas
     * @param parent
     */
    private fun drawDivider(canvas: Canvas, parent: RecyclerView) {
        //可见子View的数目
        val childCount = if (isExcludeLast) parent.childCount - 1 else parent.childCount
        for (i in 0 until childCount) {
            val childView = parent.getChildAt(i)
            if (leftOffset > 0){
                drawLeftRect(canvas, childView, parent)
            }

            if (topOffset > 0){
                drawTopRect(canvas, childView, parent)
            }

            if (rightOffset > 0){
                drawRightRect(canvas, childView, parent)
            }

            if (bottomOffset > 0){
                drawBottomRect(canvas, childView, parent)
            }
        }
    }

    /**
     * 在itemView左侧预留位置绘制[Rect]
     * @param canvas
     * @param childView itemView
     * @param parent [RecyclerView]
     * */
    private fun drawLeftRect(canvas: Canvas, childView : View,parent: RecyclerView){
        //RecyclerView的距离顶部padding的距离
        val top = parent.paddingTop.toFloat()
        //itemView距离左边界距离，减去分割线的宽度，加上左侧padding距离
        val left = (childView.left - this.leftOffset + parent.paddingLeft).toFloat()
        //itemView距离左边界距离
        val right = childView.left.toFloat()
        //RecyclerView的高度减去距离底部的padding距离
        val bottom = (parent.height - parent.paddingBottom).toFloat()
        canvas.drawRect(left, top, right, bottom, mPaint)
    }

    /**
     * 在itemView右侧预留位置绘制[Rect]
     * @param canvas
     * @param childView itemView
     * @param parent [RecyclerView]
     * */
    private fun drawRightRect(canvas: Canvas, childView : View,parent: RecyclerView){
        val top = parent.paddingTop.toFloat()
        //itemView距离右边界距离
        val left = childView.right.toFloat()
        //itemView距离右边界距离，加上分割线的宽度，减去右侧padding的距离
        val right = (childView.right + this.rightOffset - parent.paddingRight).toFloat()
        //RecyclerView的高度减去距离底部的padding距离
        val bottom = (parent.height - parent.paddingBottom).toFloat()
        canvas.drawRect(left, top, right, bottom, mPaint)
    }

    /**
     * 在itemView顶部预留位置绘制[Rect]
     * @param canvas    画布
     * @param childView    itemView
     * @param parent recyclerView
     */
    private fun drawTopRect(canvas: Canvas, childView: View, parent: View) {
        val top: Float = (childView.top - this.topOffset).toFloat()
        val left = parent.paddingLeft.toFloat()
        val right = (parent.width- parent.paddingRight).toFloat()
        val bottom = childView.top.toFloat()
        canvas.drawRect(left, top, right, bottom, mPaint)
    }

    /**
     * 在itemView底部预留位置绘制[Rect]
     * @param canvas    画布
     * @param childView    子item
     * @param parent recyclerView
     */
    private fun drawBottomRect(canvas: Canvas, childView: View, parent: View) {
        val top = childView.bottom.toFloat()
        val left = parent.paddingLeft.toFloat()
        val right = (parent.width- parent.paddingRight).toFloat()
        val bottom: Float = (childView.bottom + this.bottomOffset).toFloat()
        canvas.drawRect(left, top, right, bottom, mPaint)
    }


}