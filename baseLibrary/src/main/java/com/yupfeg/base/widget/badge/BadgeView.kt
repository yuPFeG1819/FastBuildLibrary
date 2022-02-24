package com.yupfeg.base.widget.badge

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.annotation.ColorInt
import com.yupfeg.base.R
import kotlin.math.max

/**
 * 未读消息红点标记View
 * TODO 1. 后续尝试添加绑定到目标视图功能
 * TODO 2. 添加全屏拖拽动画
 * TODO 3. 添加消失动画
 *
 * @author yuPFeG
 * @date 2022/02/17
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
open class BadgeView(
    context : Context, attr : AttributeSet?,defStyleAttr : Int
) : View(context,attr,defStyleAttr){

    companion object{
        private const val DEF_MIN_RADIUS = 2f
        private const val DEF_TEXT_SIZE = 10f
        private const val DEF_PADDING = 2f
        private const val DEF_MAX_NUM = 99
        private const val DEF_GRAPH_RADIUS = 2f
    }

    /**
     * 标签视图的中心点坐标
     * - 在绑定目标视图后初始化
     * */
    protected val mBadgeCenterPointF = PointF()

//    /**
//     * 绑定视图对象
//     * */
//    protected var mTargetView : View? = null

    /**目标中心的Y轴偏移量*/
    protected var mCenterOffsetY : Float = 0f
    /**目标中心的X轴偏移量*/
    protected var mCenterOffsetX : Float = 0f

    // </editor-fold>

    /**最大显示数字*/
    protected var mMaxNum : Int = DEF_MAX_NUM

    /**
     * 显示数字
     * */
    protected var mBadgeNum : Int = 0

    /**
     * 红点内容（数字或文本）
     * */
    protected var mBadgeText : String? = null

    /**文本画笔*/
    protected val mTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    /**背景画笔*/
    protected val mBgPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    /**背景描边画笔*/
    protected val mStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG)

    /**
     * 背景绘制的矩形范围，仅提供与过长内容时使用
     * */
    protected val mBackgroundRectF = RectF()

    /**
     * 文本内容绘制的矩形范围
     * - 提供确定背景绘制的范围
     * */
    protected val mBadgeTextRectF = RectF()

    private var mBadgePadding : Float = DEF_PADDING

    protected var mBadgeWidth : Int = 0
    protected var mBadgeHeight : Int = 0

    /**最小的圆形半径*/
    protected var mMinRadius : Float = DEF_MIN_RADIUS

    /**
     * 当前的标记视图的显示模式
     * */
    protected var mBadgeMode : BadgeMode = BadgeMode.GRAPH_LIMIT

    /**
     * 标记视图的显示模式
     * */
    enum class BadgeMode{
        /**数字限制，超出最大数字范围后，显示最大数字与+号的组合*/
        NUM_LIMIT,
        /**精确模式，不限制最大数字，完全显示所有数字内容*/
        EXACT,
        /**形状限制模式，超出最大数字范围后，显示图形*/
        GRAPH_LIMIT
    }

    // <editor-fold desc="特殊模式的绘制属性">

    /**图形限制模式的圆形半径*/
    protected var mGraphRadius : Float = DEF_GRAPH_RADIUS

    /**
     * 图形限制模式圆形的绘制中心点
     * */
    protected var mGraphCenterPointF = PointF()

    /**
     * 图形限制模式图形的绘制画笔
     * */
    protected var mGraphPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    // </editor-fold>

    constructor(context: Context) : this(context,null)
    constructor(context: Context,attrs: AttributeSet?) : this(context,attrs,0)

    init {
        //设置关闭硬件加速的离屏缓存
//        setLayerType(LAYER_TYPE_SOFTWARE, null)
        mBgPaint
        initPaint()
        initAttribute(this.context,attr)
    }

    private fun initPaint(){
        mTextPaint.style = Paint.Style.FILL_AND_STROKE
        mTextPaint.textAlign = Paint.Align.CENTER

        mBgPaint.style = Paint.Style.FILL_AND_STROKE

        mStrokePaint.style = Paint.Style.STROKE
        mStrokePaint.strokeCap = Paint.Cap.ROUND

        mGraphPaint.style = Paint.Style.FILL
    }

    // <editor-fold desc="初始化自定义属性">

    /**
     * 初始化自定义属性
     * @param context
     * @param attrs
     * */
    private fun initAttribute(context: Context,attrs: AttributeSet?){
        attrs?:return
        val displayMetrics = resources.displayMetrics
        val typeArray = context.obtainStyledAttributes(attrs, R.styleable.BadgeView)

        mBadgeNum = typeArray.getInt(R.styleable.BadgeView_badgeNum,0)
        mBadgePadding = typeArray.getDimension(R.styleable.BadgeView_badgePadding,
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEF_PADDING,displayMetrics)
        )
        mMinRadius = typeArray.getDimension(R.styleable.BadgeView_minRadius,
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEF_MIN_RADIUS,displayMetrics)
        )
        mCenterOffsetX = typeArray.getDimension(R.styleable.BadgeView_badgeOffsetX,0f)
        mCenterOffsetY = typeArray.getDimension(R.styleable.BadgeView_badgeOffsetY,0f)

        val textColor = typeArray.getColor(R.styleable.BadgeView_badgeTextColor,Color.WHITE)
        val textSize = typeArray.getDimension(
            R.styleable.BadgeView_badgeTextSize,
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, DEF_TEXT_SIZE,displayMetrics)
        )

        val bgColor = typeArray.getColor(R.styleable.BadgeView_badgeColor,Color.RED)
        val strokeColor = typeArray.getColor(R.styleable.BadgeView_badgeStrokeWidth,Color.WHITE)
        val strokeWidth = typeArray.getDimension(R.styleable.BadgeView_badgeStrokeWidth,0f)
        val specialColor = typeArray.getColor(R.styleable.BadgeView_badgeGraphColor,Color.WHITE)
        mGraphRadius = typeArray.getDimension(
            R.styleable.BadgeView_badgeGraphRadius,
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, DEF_GRAPH_RADIUS,displayMetrics
            )
        )

        typeArray.recycle()

        setBadgeTextInternal(mBadgeNum)
        mTextPaint.textSize = textSize
        mTextPaint.color = textColor
        mBgPaint.color = bgColor
        mStrokePaint.color = strokeColor
        mStrokePaint.strokeWidth = strokeWidth
        mGraphPaint.color = specialColor
    }

    // </editor-fold>


//    @Deprecated("暂未实现绑定视图功能")
//    fun bindTargetView(view: View){
//        mTargetView = view
//
//        (this.parent as? ViewGroup)?.also {
//            //如果以绑定到其他View，则从原有父视图移除当前视图
//            it.removeView(this)
//        }
//
//        (view.parent as? ViewGroup)?.also {
//            //TODO 从父容器内移除目标视图，将目标视图与红点视图添加到另一个位置后，再重新添加回目标视图
//            //TODO TabLayot,BottomNavigationView内部是否支持这样的方式待确定
//            it.addView(this)
//        }
//
//        findBadgeViewCenter()
//    }

    // <editor-fold desc="测量尺寸">

    override fun onSizeChanged(w : Int, h : Int, oldw : Int, oldh : Int){
        mBadgeWidth = w
        mBadgeHeight = h
        measureTextRect()
        findBadgeViewCenter()
    }

    /**
     * 查找当前标签视图的中心位置
     * */
    private fun findBadgeViewCenter(){
//        mTargetView?: throw NullPointerException("you should bind target view")
//        val locations = IntArray(2)
//        val rectWidth = max(mBadgeTextRectF.width(),mBackgroundRectF.height())

        //TODO 目标视图坐标的右上角，作为标签视图的中心位置
        //计算中心点位置
        mBadgeCenterPointF.x = mBadgeWidth/2 + mCenterOffsetX
        mBadgeCenterPointF.y = mBadgeHeight/2 + mCenterOffsetY
    }

    /**
     * 测量文本内容的绘制范围
     * */
    private fun measureTextRect(){
        mBadgeTextRectF.setEmpty()
        mBadgeText?.takeIf { it.isNotEmpty() }?.also {
            mBadgeTextRectF.right = mTextPaint.measureText(it)
            val fontMetrics = mTextPaint.fontMetrics
            mBadgeTextRectF.bottom = fontMetrics.bottom - fontMetrics.top
        }?:run {
            //如果内容为空，则设置默认圆点尺寸
            val defRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,5f,resources.displayMetrics)
            mBadgeTextRectF.right = defRadius
            mBadgeTextRectF.bottom = defRadius
        }
    }

    // </editor-fold>

    // <editor-fold desc="绘制内容">

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //字符为null则只绘制圆形红点，一个数字（字符）绘制圆形，多个字符绘制圆角矩形，
        //其中还有分`数字+`模式,还是省略号模式，纯文本模式

        val badgeRadius = getCircleRadius()

        //绘制圆点背景
        drawBadgeBackground(canvas,badgeRadius)
        //绘制文本
        drawBadgeText(canvas)
        //绘制图形限制内容
        drawGraphLimit(canvas,badgeRadius)
    }

    /**
     * 获取圆形的半径
     * */
    protected open fun getCircleRadius() : Float{
        if (mBadgeNum <= 0){
            return mMinRadius + mBadgePadding
        }

        val radius = if (mBadgeNum < 10){
            //只有一个数字时，取文本最长边作为背景圆形的半径
            val max = max(mBadgeTextRectF.height(),mBadgeTextRectF.width())
            max/2 + mBadgePadding/2
        }else{
            val textWidthHalf = mBadgeTextRectF.width()/2
            val textHeightHalf = mBadgeTextRectF.height()/2
            mBackgroundRectF.left = mBadgeCenterPointF.x - textWidthHalf - mBadgePadding
            mBackgroundRectF.top = mBadgeCenterPointF.y - textHeightHalf - mBadgePadding
            mBackgroundRectF.right = mBadgeCenterPointF.x + textWidthHalf + mBadgePadding
            mBackgroundRectF.bottom = mBadgeCenterPointF.y + textHeightHalf + mBadgePadding
            mBackgroundRectF.height()/2
        }

        return if (radius < mMinRadius) mMinRadius else radius
    }

    /**
     * 绘制标签背景
     * @param canvas
     * @param radius 标签背景的圆形半径（圆角矩形的圆角半径）
     */
    protected open fun drawBadgeBackground(canvas: Canvas,radius : Float){
        if (mBadgeText.isNullOrEmpty()) {
            //文本内容为空，则只绘制圆点
            canvas.drawCircle(mBadgeCenterPointF.x,mBadgeCenterPointF.y,radius,mBgPaint)
            if (mStrokePaint.strokeWidth > 0){
                canvas.drawCircle(mBadgeCenterPointF.x,mBadgeCenterPointF.y,radius,mStrokePaint)
            }
            return
        }

        if (mBadgeTextRectF.width() > mBadgeTextRectF.height() || isShowGraphMode()){
            //文本较长，或特殊图形限制模式时，则绘制横向的圆角矩形
            canvas.drawRoundRect(mBackgroundRectF,radius,radius,mBgPaint)
            if (mStrokePaint.strokeWidth > 0) {
                canvas.drawRoundRect(mBackgroundRectF, radius, radius, mStrokePaint)
            }
            return
        }

        //文本较短，直接绘制圆形
        canvas.drawCircle(mBadgeCenterPointF.x,mBadgeCenterPointF.y,radius,mBgPaint)
        if (mStrokePaint.strokeWidth > 0) {
            canvas.drawCircle(mBadgeCenterPointF.x, mBadgeCenterPointF.y, radius, mStrokePaint)
        }
    }

    /**
     * 绘制标签内容
     * @param canvas
     * */
    protected open fun drawBadgeText(canvas: Canvas){
        if (mBadgeText.isNullOrEmpty()) return
        //特殊模式时，不需要绘制文本内容
        if (isShowGraphMode()) return

        val fontMetrics = mTextPaint.fontMetrics
        //计算文本高度
        val textHeight = fontMetrics.bottom - fontMetrics.top
        val textBaseline = mBadgeCenterPointF.y + (textHeight)/2 - fontMetrics.bottom
        //以文本区域测量的总高度，作为背景的圆点的基础高度，以文本区域测量的总宽度，作为背景的圆形的基础高度
        canvas.drawText(mBadgeText!!,mBadgeCenterPointF.x,textBaseline,mTextPaint)
    }

    /**
     * 绘制特殊模式的内容
     * @param canvas
     * @param badgeRadius 标签背景的圆角
     */
    protected open fun drawGraphLimit(canvas: Canvas, badgeRadius : Float){
        if(!isShowGraphMode()) return

        //绘制特别模式，内部显示三个小圆，表示省略号
        mGraphCenterPointF.set(mBadgeCenterPointF)
        canvas.drawCircle(
            mGraphCenterPointF.x,mGraphCenterPointF.y,mGraphRadius,mGraphPaint
        )
        //绘制左侧圆形，默认位置圆心的左侧的1/4半径位置
        mGraphCenterPointF.set(mBadgeCenterPointF.x - badgeRadius/2, mBadgeCenterPointF.y)
        canvas.drawCircle(
            mGraphCenterPointF.x,mGraphCenterPointF.y,mGraphRadius,mGraphPaint
        )
        //绘制右侧圆形，默认位置在圆心的右侧的1/4半径位置
        mGraphCenterPointF.set(mBadgeCenterPointF.x + badgeRadius/2, mBadgeCenterPointF.y)
        canvas.drawCircle(
            mGraphCenterPointF.x,mGraphCenterPointF.y,mGraphRadius,mGraphPaint
        )
    }

    /**
     * 是否显示特殊模式图形
     * */
    protected open fun isShowGraphMode() : Boolean{
        return mBadgeMode == BadgeMode.GRAPH_LIMIT && mBadgeNum > mMaxNum
    }

    // </editor-fold>

    // <editor-fold desc="设置标记文本内容">

    /**
     * 设置标记数字
     * @param num 未读消息数量
     * */
    fun setBadgeNum(num : Int){
        mBadgeNum = if (num <= 0) 0 else num
        setBadgeTextInternal(mBadgeNum)
        invalidate()
    }

    private fun setBadgeTextInternal(num : Int){
        if (num <= 0){
            mBadgeText = ""
            return
        }

        mBadgeText = when(mBadgeMode){
            BadgeMode.NUM_LIMIT -> if (num > mMaxNum) "${mMaxNum}+" else "$num"
            BadgeMode.EXACT,BadgeMode.GRAPH_LIMIT -> "$num"
        }
    }

    /**
     * 设置可显示最大数字
     * @param num
     * */
    fun setMaxNum(num : Int){
        mMaxNum = num
        setBadgeTextInternal(mBadgeNum)
        invalidate()
    }

    /**
     * 设置标记显示模式
     * @param mode 显示模式[BadgeMode]
     * */
    fun setBadgeMode(mode: BadgeMode){
        mBadgeMode = mode
        setBadgeTextInternal(mBadgeNum)
        invalidate()
    }

    // </editor-fold>

    // <editor-fold desc="设置显示类型">

    // <editor-fold desc="设置背景绘制属性">

    /**
     * 设置标签背景绘制圆形的最小半径
     * @param radius 圆形半径
     * */
    fun setMinRadius(radius: Int){
        mMinRadius = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,radius.toFloat(),resources.displayMetrics
        )
    }

    /**
     * 设置标签背景颜色
     * @param color 背景颜色int值
     * */
    fun setBadgeColor(@ColorInt color : Int){
        mBgPaint.color = color
        invalidate()
    }

    /**
     * 设置背景绘制额外间距
     * @param padding 额外间距，作用于四个顶边，单位dp
     * */
    fun setBadgePadding(padding : Float){
        mBadgePadding = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,padding,resources.displayMetrics
        )
        invalidate()
    }

    /**
     * 设置背景描边
     * @param size 描边宽度，单位dp
     * @param color 描边颜色int值
     * */
    fun setBadgeStroke(size : Int,@ColorInt color : Int){
        mStrokePaint.strokeWidth = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,size.toFloat(),resources.displayMetrics
        )
        mStrokePaint.color = color
        invalidate()
    }

    /**
     * 设置图形限制模式的圆形半径
     * @param radius 圆形半径，单位dp
     * */
    fun setGraphModeCircleRadius(radius : Int){
        mGraphRadius = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,radius.toFloat(),resources.displayMetrics
        )
        if (!isShowGraphMode()) return
        invalidate()
    }

    /**
     * 设置图形限制模式的图形颜色
     * @param color 图形颜色
     * */
    fun setGraphModeColor(@ColorInt color: Int){
        mGraphPaint.color = color
        if (!isShowGraphMode()) return
        invalidate()
    }

    // </editor-fold>

}