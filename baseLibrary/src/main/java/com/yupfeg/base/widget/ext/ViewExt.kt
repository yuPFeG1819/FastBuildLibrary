package com.yupfeg.base.widget.ext

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.yupfeg.base.tools.toPx

/**
 * View的拓展函数
 * @author yuPFeG
 * @date 2020/05/05
 */

// <editor-fold desc="防抖点击事件">
/**
 * [View]的拓展方法，设置防抖点击事件
 * @param delayTime 防抖时间，距离上次点击事件的最小允许点击时间
 * @param onClickAction
 */
@Suppress("unused")
fun View.setThrottleClickListener(
    delayTime : Int = ThrottleClickListenerWrapper.DEF_DELAY,
    onClickAction: ((View)->Unit)?
){
    setThrottleClickListener(delayTime, View.OnClickListener { view->
        onClickAction?.invoke(view)
    })
}

/**
 * [View]的拓展方法，设置防抖点击事件
 * @param delayTime 防抖时间，距离上次点击事件的最小允许点击时间
 * @param onClickListener [View.OnClickListener]
 */
@Suppress("unused")
fun View.setThrottleClickListener(
    delayTime : Int = ThrottleClickListenerWrapper.DEF_DELAY,
    onClickListener : View.OnClickListener?
){
    onClickListener?:return
    setOnClickListener(ThrottleClickListenerWrapper(delayTime,onClickListener))
}

/**
 * 过滤防抖的点击事件包装类
 * */
class ThrottleClickListenerWrapper(
    private val delayTime: Int = DEF_DELAY,
    private val clickListener: View.OnClickListener
) : View.OnClickListener{
    companion object{
        /**默认延迟时间*/
        const val DEF_DELAY = 500
    }

    /**上次点击时间*/
    private var mLastTime = 0L

    override fun onClick(v: View?) {
        val currentTimeMillis = System.currentTimeMillis()
        if (currentTimeMillis - mLastTime > delayTime){
            mLastTime = currentTimeMillis
            clickListener.onClick(v)
        }
    }
}

// </editor-fold>

// <editor-fold desc="视图背景Drawable">
//TODO 利用KOtlin-DSL方式，构建drawable shape
@Suppress("unused")
data class ViewBackgroundConfig(
    @ColorInt var solidColor : Int,
    @ColorRes val startColor : Int,
    @ColorRes val endColor : Int,
    var topLeftRadius : Float = 0f,
    var topRightRadius : Float = 0f,
    var downLeftRadius : Float = 0f,
    var downRightRadius : Float = 0f,
)

/**
 * 设置View的矩形背景
 * @param solidColor 背景填充色,可以利用`Color.parse`或者[ContextCompat.getColor]获取颜色值
 * @param radius 矩形的圆角弧度
 * @param strokeWidth 描边大小(dp)
 * @param strokeColor 描边颜色,可以利用`Color.parse`或者[ContextCompat.getColor]获取颜色值
 */
@Suppress("unused")
fun View.setRectBackground(@ColorInt solidColor : Int, radius : Float,
                           strokeWidth : Float = 0f, @ColorInt strokeColor : Int = 0){
    this.background = GradientDrawable().apply {
        setColor(solidColor)
        cornerRadius = radius
        if (strokeColor == 0 || strokeColor == 0){
            setStroke(strokeWidth.toPx(), strokeColor)
        }
    }
}


/**
 * 设置View的矩形背景
 * @param solidColor 背景填充色，可以利用`Color.parse`或者[ContextCompat.getColor]获取颜色值
 * @param topLeftRadius 矩形左上角的圆角弧度
 * @param topRightRadius 矩形右上角的圆角弧度
 * @param downLeftRadius 矩形左下角的圆角弧度
 * @param downRightRadius 矩形右下角的圆角弧度
 * @param strokeWidth 描边的宽度(dp)
 * @param strokeColor 描边颜色，可以利用`Color.parse`或者[ContextCompat.getColor]获取颜色值
 */
@Suppress("unused")
fun View.setRectBackground(@ColorRes solidColor : Int,
                           topLeftRadius : Float = 0f, topRightRadius : Float = 0f,
                           downLeftRadius : Float = 0f, downRightRadius : Float = 0f,
                           strokeWidth: Float = 0f, @ColorRes strokeColor : Int = 0){
    val context = this.context
    this.background = GradientDrawable().apply {
        setColor(ContextCompat.getColor(context,solidColor))
        cornerRadii = floatArrayOf(topLeftRadius,topRightRadius,downLeftRadius,downRightRadius)
        setStroke(strokeWidth.toPx(),ContextCompat.getColor(context,strokeColor))
    }
}

// </editor-fold>

// <editor-fold desc="软键盘操作">

/**
 * [View]拓展函数，显示软键盘
 * */
@Suppress("unused")
fun View.showInputKeyBoard() {
    val view = this
    val controller = ViewCompat.getWindowInsetsController(view)
    controller?.also {
        //Android R 开始提供的系统窗口管理
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        it.show(WindowInsetsCompat.Type.ime())
    }?:run {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        //显示软键盘
        imm.showSoftInput(view, 0)
    }
}

// </editor-fold>
