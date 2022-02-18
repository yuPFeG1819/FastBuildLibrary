package com.yupfeg.base.widget

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.util.TypedValue.COMPLEX_UNIT_DIP
import android.util.TypedValue.COMPLEX_UNIT_SP
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.annotation.ColorInt
import androidx.annotation.MainThread
import com.yupfeg.base.R
import kotlin.math.max
import kotlin.math.min

/**
 * 圆弧型进度条
 * @author yuPFeG
 * @date 2022/02/11
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
open class ArcProgressBar(
    context : Context,
    attrs : AttributeSet?,
    defStyleAttr : Int
) : View(context,attrs, defStyleAttr){

    companion object{
        private const val DEFAULT_SWEEP_ANGLE : Float = 210f
        /**默认的进度视图范围尺寸，单位px*/
        private const val DEFAULT_VIEW_SIZE = 150
        /**默认描边宽度*/
        private const val DEFAULT_STROKE_WIDTH = 12f
        /**默认标签文本大小*/
        private const val DEFAULT_LABEL_TEXT_SIZE = 18f
        /**默认的百分比文本大小*/
        private const val DEFAULT_PERCENT_TEXT_SIZE = 20f
        /**默认的百分比单位的文本大小*/
        private const val DEFAULT_PERCENT_UNIT_TEXT_SIZE = 14f
        /**默认动画执行时间，单位ms*/
        private const val DEFAULT_ANIM_DURATION = 2000
    }

    // <editor-fold desc="圆弧绘制属性">

    /**进度圆弧的绘制画笔*/
    protected val mProgressPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    /**进度条背景圆弧的绘制画笔*/
    protected val mNormalPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    /**
     * 圆弧背景的渐变颜色集合
     * - 在没有测量流程时，暂存着色器颜色集合
     * */
    @ColorInt
    protected var mTempNormalColors : IntArray? = null

    /**
     * 圆弧进度条的渐变颜色集合
     * - 在没有测量流程时，暂存着色器颜色集合
     * */
    @ColorInt
    protected var mTempProgressColors : IntArray? = null

    /**弧线的起始角度，默认为3点方向，即x轴右侧*/
    protected var mStartAngle : Float = 0f

    /**
     * 设置圆弧的划过角度，360则表示绘制一个完整的圆环
     * */
    protected var mMaxSweepAngle : Float = DEFAULT_SWEEP_ANGLE

    /**
     * 进度圆弧的边界范围矩形
     * */
    protected var mArcRectF = RectF()

    /**
     * 最小的圆弧范围，单位为px
     * */
    protected var mMinRectSize = DEFAULT_VIEW_SIZE

    /**
     * 圆弧的半径
     * */
    protected var mRadius : Float = 0f

    /**
     * 圆弧描边的画笔尺寸, px
     * */
    protected var mStrokeWidth : Float = 14f

    /**圆弧的圆心X坐标，只在完成视图测量过程后存在*/
    protected var mCenterX : Float = 0f
    /**圆弧的圆心Y坐标，只在完成视图测量过程后存在*/
    protected var mCenterY : Float = 0f

    // </editor-fold>

    // <editor-fold desc="文本绘制相关属性">

    /**圆弧中心文本绘制画笔*/
    protected var mTextPaint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    /**
     * 圆弧中心标签文本字体大小
     * */
    protected var mLabelTextSize : Float = 14f

    /**
     * 圆弧中心标签文本颜色
     * */
    protected var mLabelTextColor : Int = Color.BLACK

    /**
     * 百分比顶部的标签文本内容，默认为null
     * */
    protected var mLabelText : String? = null

    /**
     * 是否显示进度百分比，默认为true
     * */
    protected var isShowPercent : Boolean = true
    /**
     * 是否显示整数的百分比
     * - 默认为true,如果为false则保留2位小数
     * */
    protected var isIntPercent : Boolean = true

    /**
     * 百分比文本字体大小
     * */
    protected var mPercentTextSize : Float = 15f

    /**
     * 百分比的单位文本字体大小
     * */
    protected var mPercentUnitTextSize : Float = 12f

    /**
     * 百分比的文本颜色
     * */
    protected var mPercentTextColor : Int = Color.BLACK

    /**
     * 百分比的单位文本颜色
     * */
    protected var mPercentUnitTextColor : Int = Color.GRAY

    /**
     * 百分比与单位之间的文本间距，px
     * */
    protected var mPercentUnitTextPadding : Float = 0f


    // </editor-fold>

    /**
     * 当前进度
     * */
    protected var mCurrProgress : Float = 0f

    /**最大进度*/
    protected var mMaxProgress : Int = 100

    /**
     * 是否已执行完成测量流程
     * - 确保能够正确获取测量相关数据
     * */
    protected var isMeasured : Boolean = false

    /**
     * 是否需要更新进度条着色器
     * - 避免重复创建对象
     * */
    protected var isNeedBuildProgressShader : Boolean = false
    /**
     * 是否需要更新进度条背景着色器
     * - 避免重复创建对象
     * */
    protected var isNeedBuildNormalShader : Boolean = false

    /**
     * 进度变化监听
     * */
    protected var mProgressChangeListener : OnProgressChangeListener? = null

    constructor(context: Context) : this(context,null)
    constructor(context: Context,attrs: AttributeSet?) : this(context,attrs,0)

    init {
        initArcPaint()
        initAttribute(context,attrs)
    }

    /**
     * 初始化圆弧的绘制画笔属性
     * */
    private fun initArcPaint(){
        mProgressPaint.reset()
        mProgressPaint.isAntiAlias = true
        //仅描边
        mProgressPaint.style = Paint.Style.STROKE
        //圆弧型画笔
        mProgressPaint.strokeCap = Paint.Cap.ROUND

        mNormalPaint.reset()
        mNormalPaint.isAntiAlias = true
        //仅描边
        mNormalPaint.style = Paint.Style.STROKE
        //圆弧型画笔
        mNormalPaint.strokeCap = Paint.Cap.ROUND
    }

    // <editor-fold desc="自定义属性">

    /**
     * 初始化自定义属性
     * @param context
     * @param attrs
     * */
    private fun initAttribute(context: Context,attrs: AttributeSet?){
        attrs?:return
        val typeArray = context.obtainStyledAttributes(attrs, R.styleable.ArcProgressBar)

        mStartAngle = typeArray.getFloat(R.styleable.ArcProgressBar_startAngle,0f)
        mMaxSweepAngle = typeArray.getFloat(
            R.styleable.ArcProgressBar_sweepAngle, DEFAULT_SWEEP_ANGLE
        )
        mStrokeWidth = typeArray.getFloat(
            R.styleable.ArcProgressBar_arcStrokeWidth,DEFAULT_STROKE_WIDTH
        )
        //进度条进度
        mMaxProgress = typeArray.getInt(R.styleable.ArcProgressBar_arcMaxProgress,100)
        mCurrProgress = typeArray.getFloat(R.styleable.ArcProgressBar_arcProgress,0f)
        //进度条颜色
        val normalColor = typeArray.getColor(R.styleable.ArcProgressBar_arcNormalColor,Color.GRAY)
        val progressColor = typeArray.getColor(R.styleable.ArcProgressBar_arcProgressColor,Color.BLUE)

        val displayMetrics = resources.displayMetrics
        //标签文本
        mLabelText = typeArray.getString(R.styleable.ArcProgressBar_arcLabelText)
        mLabelTextSize = typeArray.getDimension(
            R.styleable.ArcProgressBar_arcLabelTextSize,
            TypedValue.applyDimension(COMPLEX_UNIT_SP, DEFAULT_LABEL_TEXT_SIZE,displayMetrics)
        )
        mLabelTextColor = typeArray.getColor(
            R.styleable.ArcProgressBar_arcLabelTextColor,Color.BLACK
        )
        //百分比文本
        mPercentTextSize = typeArray.getDimension(
            R.styleable.ArcProgressBar_arcPercentTextSize,
            TypedValue.applyDimension(COMPLEX_UNIT_SP, DEFAULT_PERCENT_TEXT_SIZE,displayMetrics)
        )
        mPercentTextColor = typeArray.getColor(
            R.styleable.ArcProgressBar_arcPercentTextColor,Color.GRAY
        )
        isIntPercent = typeArray.getBoolean(R.styleable.ArcProgressBar_arcIntPercent,true)

        mPercentUnitTextSize = typeArray.getDimension(
            R.styleable.ArcProgressBar_arcPercentUnitTextSize,
            TypedValue.applyDimension(COMPLEX_UNIT_SP, DEFAULT_PERCENT_UNIT_TEXT_SIZE,displayMetrics)
        )
        mPercentUnitTextColor = typeArray.getColor(
            R.styleable.ArcProgressBar_arcPercentUnitTextColor,Color.GRAY
        )
        mPercentUnitTextPadding = typeArray.getDimension(
            R.styleable.ArcProgressBar_arcPercentUnitTextPadding, 0f
        )

        //typedArray用完之后需要回收，防止内存泄漏
        typeArray.recycle()

        mProgressPaint.strokeWidth = mStrokeWidth
        mNormalPaint.strokeWidth = mStrokeWidth
        //移除shader,否则会导致后续颜色值无法生效
        mNormalPaint.shader = null
        mNormalPaint.color = normalColor
        mProgressPaint.shader = null
        mProgressPaint.color = progressColor
    }

    // </editor-fold>

    // <editor-fold desc="进度相关公开方法">

    /**
     * 设置进度百分比
     * @param progress 进度百分比，0~100(默认最大值)
     * */
    @MainThread
    fun setProgress(progress : Float){
        if (progress < 0) throw IllegalAccessException("arc progress cant less than 0")
        mCurrProgress = progress
        invalidate()

        mProgressChangeListener?.onProgressChange(mCurrProgress,mMaxProgress)
    }

    /**
     * 获取当前经度百分比
     * */
    fun getProgress() : Float = mCurrProgress

    /**
     * 设置最大显示进度，默认为100
     * @param progress
     * */
    @MainThread
    fun setMaxProgress(progress : Int){
        mMaxProgress = progress
        invalidate()
    }

    fun getMaxProgress() : Float = mMaxSweepAngle

    /**
     * 设置进度条的进度，同时开启属性动画更新进度
     * @param from 起始进度百分比，默认为从当前进度
     * @param to 目标进度百分比
     * @param duration 动画持续时间
     * @return [ValueAnimator]属性动画对象，在视图移除时，注意关闭未结束的动画
     * */
    @JvmOverloads
    open fun setProgressWithAnimate(
        from : Float = mCurrProgress, to : Float,
        duration : Int = DEFAULT_ANIM_DURATION
    ) : ValueAnimator{
        val endValue = min(to,mMaxProgress.toFloat())
        val fromValue = max(from,0f)

        return ObjectAnimator.ofFloat(
            this,"progress",fromValue,endValue
        ).apply {
            this.duration = duration.toLong()
            this.interpolator = LinearInterpolator()
            start()
        }
    }

    // </editor-fold>

    // <editor-fold desc="圆弧角度公开方法">

    /**
     * 设置进度条圆弧的起始角度
     * @param angle 起始角度
     * */
    @MainThread
    fun setStartAngle(angle : Float){
        mStartAngle = angle
        invalidate()
    }

    /**获取进度条圆弧的起始角度*/
    fun getStartAngle() : Float = mStartAngle

    /**
     * 设置进度条圆弧的展开角度
     * @param angle 划过角度，360则表示绘制完整的圆环
     * */
    @MainThread
    fun setMaxSweepAngle(angle: Float){
        mMaxSweepAngle = angle
        invalidate()
    }

    /**获取进度条圆弧展开角度*/
    fun getMaxSweepAngle() : Float = mMaxSweepAngle

    // </editor-fold>

    // <editor-fold desc="圆弧颜色公开方法">

    /**
     * 设置进度条圆弧背景颜色
     * @param color 背景颜色Int值，推荐使用`ContextCompat.getColor()`获取颜色值
     * */
    @MainThread
    fun setNormalColor(@ColorInt color: Int){
        mNormalPaint.shader = null
        mNormalPaint.color = color
        invalidate()
    }

    /**
     * 设置进度条圆弧背景的渐变色
     * @param color 渐变颜色值，如果只有一个颜色则直接设置单色
     * */
    @MainThread
    fun setNormalColors(@ColorInt vararg color : Int){
        if (color.isEmpty()) return

        if (color.size == 1){
            setNormalColor(color[0])
            return
        }

        if (isMeasured){
            isNeedBuildNormalShader = false
            val shader = SweepGradient(mCenterX,mCenterY,color,null)
            setNormalShader(shader)
        }else{
            isNeedBuildNormalShader = true
            mTempNormalColors = color
        }
    }

    /**
     * 设置圆弧背景颜色的着色器
     * - 设置着色器后，自动忽略`setNormalColor`设置的单一颜色
     * @param shader 着色器，可为null，表示采用单一颜色
     * */
    @MainThread
    fun setNormalShader(shader : Shader?){
        mNormalPaint.shader = shader
        invalidate()
    }

    /**
     * 设置进度条圆弧颜色
     * @param color 进度颜色Int值，推荐使用`ContextCompat.getColor()`获取颜色值
     * */
    @MainThread
    fun setProgressColor(@ColorInt color: Int){
        mProgressPaint.shader = null
        mProgressPaint.color = color
        invalidate()
    }

    /**
     * 设置进度条圆弧的渐变颜色
     * @param color1 渐变起始颜色int值
     * @param color2 渐变结束颜色int值
     * */
    @MainThread
    fun setProgressColors(@ColorInt color1: Int,@ColorInt color2 : Int){
        setProgressColors(intArrayOf(color1,color2),null)
    }

    /**
     * 设置进度条圆弧的渐变颜色
     * @param colors 渐变颜色数组
     * @param positions 颜色数组中每个对应颜色的相对位置，从 0 开始，以 1.0 结束，默认位置为 null，颜色会自动均匀分布
     * */
    fun setProgressColors(@ColorInt colors : IntArray,positions: FloatArray? = null){
        if (isMeasured){
            isNeedBuildProgressShader = false
            val shader = SweepGradient(mCenterX,mCenterY, colors, positions)
            setProgressShader(shader)
        }else{
            isNeedBuildProgressShader = true
            mTempProgressColors = colors
        }
    }

    /**
     * 设置圆弧进度条的着色器
     * - 设置着色器后，自动忽略`setNormalColor`设置的单一颜色
     * @param shader 着色器，可为null，表示采用单一颜色
     */
    @MainThread
    fun setProgressShader(shader: Shader?){
        mProgressPaint.shader = shader
        invalidate()
    }

    // </editor-fold>

    // <editor-fold desc="圆弧文本公开方法">

    /**
     * 设置标签文本内容，默认内容为null，不显示
     * @param text 文本内容，显示在百分比文本上方
     * */
    @MainThread
    open fun setLabelText(text : String){
        mLabelText = text
        invalidate()
    }

    /**
     * 设置标签文本大小
     * @param size 文本尺寸，单位sp
     * */
    @MainThread
    fun setLabelTextSize(size : Int){
        mLabelTextSize = TypedValue.applyDimension(
            COMPLEX_UNIT_SP,size.toFloat(),resources.displayMetrics
        )
        invalidate()
    }

    /**
     * 设置标签文本颜色
     * @param color 文本颜色int值
     * */
    @MainThread
    fun setLabelTextColor(@ColorInt color: Int){
        mLabelTextColor = color
        invalidate()
    }

    /**
     * 设置百分比文本字体大小
     * @param size 字体大小，单位sp
     * */
    fun setPercentTextSize(size : Int){
        mPercentTextSize = TypedValue.applyDimension(
            COMPLEX_UNIT_SP,size.toFloat(),resources.displayMetrics
        )
        invalidate()
    }

    /**
     * 设置百分比文本颜色
     * @param color 文本颜色int值
     * */
    fun setPercentTextColor(@ColorInt color: Int){
        mPercentTextColor = color
        invalidate()
    }

    /**
     * 设置百分比单位的字体大小
     * @param size 字体大小，单位sp
     * */
    fun setPercentUnitTextSize(size : Int){
        mPercentUnitTextSize = TypedValue.applyDimension(
            COMPLEX_UNIT_SP,size.toFloat(),resources.displayMetrics
        )
        invalidate()
    }

    /**
     * 设置百分比单位的字体颜色
     * @param color 字体颜色
     * */
    fun setPercentUnitTextColor(@ColorInt color : Int){
        mPercentUnitTextColor = color
        invalidate()
    }

    /**
     * 设置百分比单位文本与百分比文本间的额外间距
     * @param padding 额外间距，单位dp
     * */
    fun setPercentUnitPadding(padding : Float){
        mPercentUnitTextPadding = TypedValue.applyDimension(
            COMPLEX_UNIT_DIP, padding,resources.displayMetrics
        )
        invalidate()
    }

    // </editor-fold>

    // <editor-fold desc="测量尺寸">

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        //计算圆心坐标
        mCenterX = (measuredWidth + paddingLeft - paddingRight) / 2.0f
        mCenterY = (measuredHeight + paddingTop - paddingBottom) / 2.0f

        val measureViewHeight = measureViewSize(mMinRectSize,heightMeasureSpec)
        val measureViewWidth = measureViewSize(mMinRectSize,heightMeasureSpec)

        //计算圆弧的半径 (视图宽度-横向或纵向内间距值 - 画笔宽度) / 2
        mRadius = (measureViewWidth - calculatePadding() - mStrokeWidth) / 2.0f

        val startX = mCenterX - mRadius
        val startY = mCenterY - mRadius
        val diameter = mRadius * 2
        //限制圆弧的范围
        mArcRectF.set(startX,startY,startX + diameter,startY + diameter)
        //设置测量的视图尺寸
        setMeasuredDimension(measureViewWidth,measureViewHeight)
        isMeasured = true
        //初始化着色器
        initArcShader()
    }

    /**
     * 测量视图的尺寸，并限制View的最小尺寸
     * */
    private fun measureViewSize(defSize : Int,measureSpec: Int) : Int{
        val measureMode = MeasureSpec.getMode(measureSpec)
        val measureSize = MeasureSpec.getSize(measureSpec)
        return when(measureMode){
            //精确模式
            MeasureSpec.EXACTLY -> measureSize
            //尽可能满足视图尺寸，限制最小尺寸
            MeasureSpec.AT_MOST -> min(defSize,measureSize)
            else -> defSize
        }
    }

    /**
     * 计算圆弧绘制的Padding，取横向与纵向中最大的值
     * */
    private fun calculatePadding() : Int
        = max(paddingLeft + paddingRight, paddingTop + paddingBottom)

    /**
     * 初始化圆环的着色器
     * */
    protected open fun initArcShader(){
        mTempNormalColors?.takeIf { isNeedBuildNormalShader }?.also { colors->
            val shader = SweepGradient(mCenterX,mCenterY,colors,null)
            mNormalPaint.shader = shader
            isNeedBuildNormalShader= false
        }

        mTempProgressColors?.takeIf { isNeedBuildProgressShader }?.also { colors->
            val shader = SweepGradient(mCenterY,mCenterY,colors,null)
            mProgressPaint.shader = shader
            isNeedBuildProgressShader = false
        }
    }

    // </editor-fold>

    // <editor-fold desc="绘制内容">

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawCircleArc(canvas)
        //绘制标签文本
        drawLabelText(canvas)
        //绘制百分比文本
        drawPercentText(canvas)
    }

    /**
     * 绘制圆弧
     * - 分离圆弧与文本的绘制，提供子类覆写
     * @param canvas
     * */
    protected open fun drawCircleArc(canvas: Canvas){
        //绘制圆弧背景
        canvas.drawArc(mArcRectF,mStartAngle,mMaxSweepAngle,false,mNormalPaint)
        //绘制进度条圆弧
        canvas.drawArc(mArcRectF,mStartAngle,
            mMaxSweepAngle * calculateRadio(),
            false,mProgressPaint
        )
    }

    /**
     * 计算进度比例
     * */
    private fun calculateRadio() : Float = mCurrProgress * 1f / mMaxProgress

    /**
     * 绘制标签文本
     * @param canvas 画布
     * */
    protected open fun drawLabelText(canvas: Canvas){
        if (!isShowPercent || mLabelText.isNullOrEmpty()) return
        resetTextPaint()
        mTextPaint.textSize = mLabelTextSize
        mTextPaint.color = mLabelTextColor
        canvas.drawText(mLabelText!!,mCenterX,getLabelTextBaseline(),mTextPaint)
    }

    /**
     * 获取标签文本的绘制基线位置Y坐标
     * */
    protected open fun getLabelTextBaseline() : Float{
        val fontMetrics = mTextPaint.fontMetrics
        //计算文本高度
        val labelFontHeight = fontMetrics.bottom - fontMetrics.top
        //计算文本的基线Y坐标，圆心Y坐标 - 文本高度/2 - 文本基线到文本底部的偏移量（正值）
        //即在中心向上挪动半个文本高度作为文本的bottom线，进而再得到baseline的坐标
        return mCenterY - labelFontHeight / 2f - fontMetrics.bottom
    }

    /**
     * 绘制百分比文本
     * @param canvas
     */
    protected open fun drawPercentText(canvas: Canvas){
        if (!isShowPercent) return
        //绘制百分比文本
        mTextPaint.textSize = mPercentTextSize
        mTextPaint.color = mPercentTextColor
        val percentBaselineY = getPercentTextBaseline()
        canvas.drawText(getPercentText(),mCenterX,percentBaselineY,mTextPaint)

        //绘制百分比单位
        mTextPaint.textSize = mPercentUnitTextSize
        mTextPaint.color = mPercentUnitTextColor
        val unitStartX = getPercentUnitTextStartX("%")
        canvas.drawText("%",unitStartX,percentBaselineY,mTextPaint)
    }

    /**
     * 获取百分比文本的绘制基线位置Y坐标
     */
    protected open fun getPercentTextBaseline() : Float{
        val fontMetrics = mTextPaint.fontMetrics
        return if (mLabelText.isNullOrEmpty()){
            //如果不显示标签文本，则文本的基线高度绘制在中心位置
            mCenterX - fontMetrics.bottom
        }else{
            //计算文本的基线Y坐标，圆心Y坐标 - 文本基线到文本顶部的偏移量（负值）
            //相当于将圆心Y坐标作为百分比文本的top线
            mCenterY - fontMetrics.top
        }
    }

    /**
     * 重置文本的绘制画笔属性
     * */
    protected open fun resetTextPaint(){
        mTextPaint.reset()
        mTextPaint.isAntiAlias = true
        mTextPaint.style = Paint.Style.FILL_AND_STROKE
        mTextPaint.textAlign = Paint.Align.CENTER
    }

    /**
     * 获取进度百分比的显示文本
     * */
    protected open fun getPercentText() : String{
        val progress = mCurrProgress * 100f / mMaxProgress
        return if (isIntPercent) "${progress.toInt()}"
        else String.format("%.2f",progress)
    }

    /**
     * 获取百分比单位文本的起始坐标
     * @param unit 百分比文本
     * */
    @Suppress("SameParameterValue")
    protected open fun getPercentUnitTextStartX(unit : String) : Float{
        //计算右侧圆弧的坐标
        val arcInnerX = mArcRectF.right - mStrokeWidth
        val textWidth = mTextPaint.measureText(unit)
        return arcInnerX - textWidth - mRadius/3 + mPercentUnitTextPadding
    }

    // </editor-fold>

    /**
     * 进度变化监听
     * */
    interface OnProgressChangeListener{
        fun onProgressChange(progress : Float,max : Int)
    }

    /**
     * 设置进度变化监听
     * @param listener
     * */
    fun setOnProgressChangeListener(listener : OnProgressChangeListener){
        mProgressChangeListener = listener
    }

}