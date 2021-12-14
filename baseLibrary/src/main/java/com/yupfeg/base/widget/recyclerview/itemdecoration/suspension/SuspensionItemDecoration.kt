package com.yupfeg.base.widget.recyclerview.itemdecoration.suspension

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.TypedValue
import android.view.View
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yupfeg.logger.ext.logd

/**
 * 可悬浮的RecyclerView的item装饰类
 *  * 仅适用于列表bean类实现[ISuspensionExtra]接口
 * @author yuPFeG
 * @date 2020/08/08
 */
@Suppress("unused")
class SuspensionItemDecoration(private val mContext : Context) : RecyclerView.ItemDecoration(){

    companion object{
        /**默认标题高度*/
        private const val DEF_TITLE_HEIGHT = 40f
        /**默认标题文本大小*/
        private const val DEF_TITLE_FONT_SIZE = 14f
        /**默认的标题文本距左边的间距*/
        private const val DEF_TITLE_LEFT_MARGIN = 15f

        private const val LOG_TAG = "SuspensionItemDecoration"
    }

    /**实现了[ISuspensionExtra]接口的bean类列表数据*/
    private var mListData : List<ISuspensionExtra> ?= null
    /**悬浮、分组标题栏高度*/
    private var mTitleHeight : Int

    /**悬浮、分组标题栏文本距左侧间距*/
    private var mTitleTextLeftMargin : Int

    /**悬浮分组标题栏文字大小的Rect*/
    private var mTitleTextBounds : Rect = Rect()
    /**绘制悬浮分组标题文本的画笔*/
    private var mPaint = Paint()

    /**分组标题栏文本颜色*/
    private var mTitleTextColor = Color.parseColor("#B9BBBE")
    /**分组标题栏背景颜色*/
    private var mTitleBarBgColor = Color.parseColor("#F8F8F8")

    /**
     * recyclerView包含的headerView数量
     * * HeaderView不处于绘制分组标题栏的范围内
     * */
    @Suppress("MemberVisibilityCanBePrivate")
    var headerViewCount : Int = 0


    init {
        mTitleHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
            DEF_TITLE_HEIGHT, mContext.resources.displayMetrics).toInt()
        val titleFontSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
            DEF_TITLE_FONT_SIZE,mContext.resources.displayMetrics)
        mTitleTextLeftMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
            DEF_TITLE_LEFT_MARGIN,mContext.resources.displayMetrics).toInt()
        mPaint.textSize = titleFontSize
        //开启抗锯齿
        mPaint.isAntiAlias = true

    }

    /**
     * 设置当前列表数据
     * * 用于列表数据更新后同步更新
     * @param listData 实现了[ISuspensionExtra]接口的bean类列表数据
     */
    fun setListData(listData : List<ISuspensionExtra>?){
        mListData = listData
    }

    /**
     * 设置悬浮、分组标题栏的高度
     * * 最好在调用[RecyclerView.addItemDecoration]前设置
     */
    fun setTitleBarHeight(height : Int) : SuspensionItemDecoration {
        this.mTitleHeight = height
        return this

    }

    /**
     * 设置悬浮、分组标题栏背景颜色
     * * 最好在调用[RecyclerView.addItemDecoration]前设置
     * */
    fun setTitleBarBgColor(@ColorRes bgColor : Int) : SuspensionItemDecoration {
        mTitleBarBgColor = ContextCompat.getColor(mContext,bgColor)
        return this
    }

    /**
     * 设置悬浮、分组标题栏的文本颜色
     * * 最好在调用[RecyclerView.addItemDecoration]前设置
     * */
    fun setTitleBarTextColor(@ColorRes textColor : Int) : SuspensionItemDecoration {
        mTitleTextColor = ContextCompat.getColor(mContext,textColor)
        return this
    }

    fun setHeaderViewCount(viewCount : Int) : SuspensionItemDecoration {
        this.headerViewCount = viewCount
        return this
    }

    /**
     * 设置需要item向内的padding区域，额外显示的矩形范围
     * * 通过设置[outRect]的大小来控制矩形范围
     */
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        //默认设置矩形范围为0,0,0,0
        super.getItemOffsets(outRect, view, parent, state)
        //获取当前可见的列表index
        val position = (view.layoutParams as RecyclerView.LayoutParams).viewLayoutPosition - headerViewCount
        logd(LOG_TAG,"--------------->> getItemOffsets start")
        //过滤不需要配置的矩形范围的情况
        if (mListData?.isNullOrEmpty() == true
            || position > mListData?.lastIndex?:-1
            || position < 0){
            //itemView在复用重置时，会短暂将position置为-1
            return
        }

        mListData?.getOrNull(position)?.let {itemData->
            logd(LOG_TAG,"ItemOffset position : ${position},isShowSuspension : ${itemData.isShowSuspension}")
            if (!itemData.isShowSuspension) return@let
            if (position == 0){
                //第一项，默认增加分组标题栏的位置
                outRect.set(0,mTitleHeight,0,0)
            }else if (itemData.suspensionText.isNotEmpty()){
                val lastItemTitle = (mListData?.getOrNull(position - 1)?.suspensionText?:"")
                if (lastItemTitle.isNotEmpty() && itemData.suspensionText != lastItemTitle){
                    //存在悬浮标题文本，且与上一个悬浮标题不一致，则需要增加新的分组标题栏的位置
                    outRect.set(0,mTitleHeight,0,0)
                }
            }
            logd(LOG_TAG,"ItemOffset position : ${position},outRect : $outRect")
        }
    }

    /**
     * 先于[onDrawOver]调用，绘制在ItemView的下层，用于绘制Title区域背景和文字.
     * * 超出[getItemOffsets]方法设置的rect范围，会被itemView遮挡
     */
    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        val left = parent.paddingLeft.toFloat()
        val right = (parent.width - parent.paddingRight).toFloat()
        logd(LOG_TAG,"------------->> onDraw start")
        //不存在数据显示，则不需要绘制item
        if (mListData.isNullOrEmpty()) return

        for(index in 0..parent.childCount){
            val childView = parent.getChildAt(index)
            childView?:continue
            val layoutParams = childView.layoutParams as RecyclerView.LayoutParams
            val position = layoutParams.viewLayoutPosition - headerViewCount
            //过滤不需要分组标题栏的item项
            if ((position !in 0..(mListData?.lastIndex?:0))
                || mListData?.getOrNull(position)?.isShowSuspension == false){
                continue
            }

            mListData?.getOrNull(position)?.let { itemData ->
                if (position == 0){
                    logd(LOG_TAG,"onDraw first position need drawTitleBarToCanvas")
                    //第一项item默认需要绘制分组标题文本
                    drawTitleBarToCanvas(
                        canvas = c,left = left,right = right,
                        childView = childView,childParams = layoutParams,
                        titleText = itemData.suspensionText
                    )
                }else if (itemData.suspensionText.isNotEmpty()){
                    val lastItemTitle = (mListData?.getOrNull(position - 1)?.suspensionText?:"")
                    if (lastItemTitle.isNotEmpty() && itemData.suspensionText != lastItemTitle){
                        logd(LOG_TAG,"onDraw position : $position  need draw other suspension title")
                        //存在分组标题文本，且与上一个分组标题不一致，则绘制新的分组标题栏
                        drawTitleBarToCanvas(
                            canvas = c,left = left,right = right,
                            childView = childView,childParams = layoutParams,
                            titleText = itemData.suspensionText
                        )
                    }
                }
            }



        }
    }

    /**
     * 后于[onDraw]调用，绘制在RecyclerView的最上层，用于绘制悬浮Title区域的背景和文本.
     */
    override fun onDrawOver(canvas : Canvas, parent: RecyclerView,
                            state: RecyclerView.State) {
        super.onDrawOver(canvas, parent, state)
        val position: Int = (parent.layoutManager as LinearLayoutManager)
            .findFirstVisibleItemPosition() - headerViewCount
        logd(LOG_TAG,"---------------->> onDrawOver start")
        //过滤不需要绘制悬浮标题的item项
        if (mListData.isNullOrEmpty() || (position !in 0..(mListData?.lastIndex?:0))
            || mListData?.getOrNull(position)?.isShowSuspension == false){
            return
        }
        val titleText = mListData?.getOrNull(position)?.suspensionText?:""
        //获取第一个可见的itemView
        val childView = parent.findViewHolderForLayoutPosition(position)?.itemView
        //定义一个flag，Canvas是否位移过的标志
        var isMoveCanvas = false

        childView?: return

        if ((position + 1) < mListData?.size?:0){
            if(titleText != mListData?.getOrNull(position+1)?.suspensionText?:""){
                //当前第一个可见view的分组标题文本，与下一个item的分组标题文本不相同，准备切换到下一个标题item
                if (childView.height + childView.top < mTitleHeight){
                    //当前第一个可见View的可见高度小于悬浮标题栏高度，则开始移动画布（看起来像动画）
                    //在移动画布前，需要保存画布状态
                    canvas.save()
                    isMoveCanvas = true
                    //上滑时，将canvas上移 （y为负数） ,
                    // 所以后面canvas 画出来的Rect和Text都上移了，有种切换的动画感觉
                    canvas.translate(
                        0f, (childView.height + childView.top - mTitleHeight).toFloat()
                    )
                    logd(
                        LOG_TAG,"onDrawOver position : $position, canvas translate \n " +
                                "childView height :${childView.height + childView.top} " +
                                ", titleBar height : $mTitleHeight"
                    )
                }
            }
        }
        //-------------------绘制在列表最顶层的悬浮标题栏----------------------
        //先设置为标题栏背景颜色
        mPaint.color = mTitleBarBgColor
        canvas.drawRect(
            parent.paddingLeft.toFloat(),
            parent.paddingTop.toFloat(),
            (parent.right - parent.paddingRight).toFloat(),
            (parent.paddingTop + mTitleHeight).toFloat(),
            mPaint
        )
        //设置为标题文本颜色
        mPaint.color = mTitleTextColor
        //获取文本所占的尺寸
        mPaint.getTextBounds(titleText,0,titleText.length,mTitleTextBounds)
        logd(
            LOG_TAG,"onDrawOver position : $position," +
                    "draw text textBounds : $mTitleTextBounds \n" +
                    "text left : ${childView.paddingLeft}"
        )
        //绘制文本到标题栏的垂直居中位置(悬浮在RecyclerView顶部，而且遮挡所有item)
        canvas.drawText(titleText,
            childView.paddingLeft.toFloat() + mTitleTextLeftMargin,
            (parent.paddingTop + mTitleHeight -
                    (mTitleHeight/2 - mTitleTextBounds.height()/2)).toFloat(),
            mPaint
        )
        logd(LOG_TAG,"onDrawOver position : $position,draw text and rect to canvas")
        if (isMoveCanvas){
            //恢复画布到之前保存的状态
            canvas.restore()
            logd(LOG_TAG,"onDrawOver position : $position,canvas restore")
        }
    }

    /**
     * 绘制分组标题栏的文本与背景
     * @param canvas [onDraw]的画布
     * @param left 可绘制区域的最左边位置
     * @param right 可绘制区域的最右边位置
     * @param childView itemView
     * @param childParams itemView的[RecyclerView.LayoutParams]
     * @param titleText 绘制的文本字符串
     */
    private fun drawTitleBarToCanvas(canvas: Canvas,left : Float,right : Float,childView : View,
                                     childParams : RecyclerView.LayoutParams,titleText : String){
        //先设置为标题栏背景颜色
        mPaint.color = mTitleBarBgColor
        canvas.drawRect(
            left,
            (childView.top - childParams.topMargin - mTitleHeight).toFloat(),
            right,
            (childView.top - childParams.topMargin).toFloat(),
            mPaint
        )
        //设置为标题文本颜色
        mPaint.color = mTitleTextColor
        //获取文本所占的尺寸
        mPaint.getTextBounds(titleText,0,titleText.length,mTitleTextBounds)
        logd(
            LOG_TAG,"drawTitleBarToCanvas mTitleTextBounds ： $mTitleTextBounds \n " +
                "text left : ${childView.paddingLeft}")
        //绘制文本到标题栏的垂直居中位置
        canvas.drawText(titleText,
            childView.paddingLeft.toFloat() + mTitleTextLeftMargin,
            (childView.top - childParams.topMargin - (mTitleHeight/2 - mTitleTextBounds.height()/2)).toFloat(),
            mPaint
        )
    }
}