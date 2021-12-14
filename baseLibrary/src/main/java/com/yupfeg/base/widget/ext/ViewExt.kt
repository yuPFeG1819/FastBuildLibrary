package com.yupfeg.base.widget.ext

import android.annotation.TargetApi
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.RelativeLayout
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.view.*
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
//TODO 利用KOtlin-DSL方式，构建drawable shape
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
 * @param solidColor 背景填充色,可以利用[Color.parse]或者[ContextCompat.getColor]获取颜色值
 * @param radius 矩形的圆角弧度
 * @param strokeWidth 描边大小(dp)
 * @param strokeColor 描边颜色,可以利用[Color.parse]或者[ContextCompat.getColor]获取颜色值
 */
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
 * @param solidColor 背景填充色，可以利用[Color.parse]或者[ContextCompat.getColor]获取颜色值
 * @param topLeftRadius 矩形左上角的圆角弧度
 * @param topRightRadius 矩形右上角的圆角弧度
 * @param downLeftRadius 矩形左下角的圆角弧度
 * @param downRightRadius 矩形右下角的圆角弧度
 * @param strokeWidth 描边的宽度(dp)
 * @param strokeColor 描边颜色，可以利用[Color.parse]或者[ContextCompat.getColor]获取颜色值
 */
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

/**
 * [View]拓展函数，隐藏软键盘
 */
@Suppress("unused")
fun View.hideInputKeyBoard() {
    val controller = ViewCompat.getWindowInsetsController(this)
    controller?.also {
        //Android R 开始提供的系统窗口管理
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        it.show(WindowInsetsCompat.Type.ime())
    }?:run{
        val imm = this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        //强制隐藏键盘
        imm.hideSoftInputFromWindow(this.windowToken, 0)
    }
}

/**
 * [View]的拓展函数，软键盘是否可见
 * @return 在API 20以下，永远为false
 * */
fun View.isInputKeyBoardVisible() : Boolean{
    val insets = ViewCompat.getRootWindowInsets(this)
    return insets?.isVisible(WindowInsetsCompat.Type.ime())?:false
}

/**
 * [View]拓展函数，设置软键盘监听
 * 内部利用setOnApplyWindowInsetsListener，如果外部调用同样函数，会覆盖该实现
 * @param listener 软键盘状态回调监听，imeVisible-是否显示，imeHeight-软键盘高度
 * */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
fun View.setKeyBoardListener(listener : (imeVisible : Boolean,imeHeight : Int)->Unit){
    ViewCompat.setOnApplyWindowInsetsListener(this){_,insets->
        val imeVisible = insets.isVisible(WindowInsetsCompat.Type.ime())
        val imeHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
        listener(imeVisible,imeHeight)
        insets
    }
}


// </editor-fold>

// <editor-fold desc="适配系统窗口">

/**
 * [View]的拓展函数，请求根视图再次分发WindowInsets，触发`onApplyWindowInsets(WindowInsets)`
 * * 通常在视图已添加后，避免不触发触发`onApplyWindowInsets`
 * */
fun View.requestApplyInsetsWhenAttached() {
    if (isAttachedToWindow) {
        // We're already attached, just request as normal
        requestApplyInsets()
    } else {
        // We're not attached to the hierarchy, add a listener to
        // request when we are
        addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
                v.removeOnAttachStateChangeListener(this)
                v.requestApplyInsets()
            }

            override fun onViewDetachedFromWindow(v: View) = Unit
        })
    }
}


/**
 * [View]拓展函数，延伸视图内容到系统状态栏
 *
 * * 适配全屏模式，在状态栏隐藏时恢复原始高度，显示后再叠加状态栏高度
 * * 警告，使用[RelativeLayout]调用该函数，会导致内部centerInParent等属性出现不符合预期情况
 * @param extraPaddingTop 是否增加视图顶部额外Padding，默认为false,
 * 如果为true则表示会在视图内部额外增加状态栏高度的Padding
 * @param windowVisibleListener 系统状态栏显示状态变化监听
 * */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
@JvmOverloads
fun View.fitSystemStatusBar(
    extraPaddingTop : Boolean = false,
    windowVisibleListener : ((isStatusBarVisible:Boolean)->Unit)? = null
){
    var initialHeight = 0
    val initialPadding = this.paddingTop
    var isEnable = false
    ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
        val statusBarInsets = insets.getInsets(
            WindowInsetsCompat.Type.statusBars()
        )
        val statusHeight = statusBarInsets.top
        v.layoutParams.apply {
            if (initialHeight == 0) initialHeight = height
            height = if (statusHeight <= 0){
                //系统状态栏已隐藏，视图复原
                v.updatePadding(top = initialPadding)
                if (isEnable){
                    isEnable = true
                    windowVisibleListener?.invoke(isEnable)
                }
                initialHeight
            }else{
                //系统状态栏显示，视图增高
                if (extraPaddingTop) v.updatePadding(top = initialPadding + statusHeight)
                if (!isEnable){
                    isEnable = true
                    windowVisibleListener?.invoke(isEnable)
                }
                initialHeight + statusHeight
            }
        }
        insets
    }
    //避免不触发onApplyWindowInsets
    requestApplyInsetsWhenAttached()
}

/**
 * [View]的拓展函数，将视图内容延伸到系统导航栏
 * * 最好在外部判断是否存在系统导航栏
 * * 警告，使用[RelativeLayout]调用该函数，会导致内部centerInParent等属性出现不符合预期情况
 * @param extraPaddingBottom 是否增加视图顶部额外Padding，默认为false,
 * 如果为true则表示会在视图内部额外增加状态栏高度的Padding
 * @param windowVisibleListener 系统导航栏显示状态变化监听
 * */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
@JvmOverloads
fun View.fitSystemNavigationWindow(
    extraPaddingBottom : Boolean = false,
    windowVisibleListener : ((isStatusBarVisible:Boolean)->Unit)? = null
){
    var initialHeight = 0
    val initialPadding = this.paddingBottom
    ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
        val navigationBarInsets = insets.getInsets(
            WindowInsetsCompat.Type.navigationBars()
        )
        val navigationHeight = navigationBarInsets.bottom
        val layoutParams = v.layoutParams.apply {
            if (initialHeight == 0) initialHeight = height
            height = if (navigationHeight <= 0){
                //系统导航栏已隐藏，视图高度复原
                v.updatePadding(bottom = initialPadding)
                windowVisibleListener?.invoke(false)
                initialHeight
            }else{
                //系统导航栏显示，视图高度增高
                if (extraPaddingBottom) v.updatePadding(bottom = initialPadding + navigationHeight)
                windowVisibleListener?.invoke(true)
                initialHeight + navigationHeight
            }
        }
        v.layoutParams = layoutParams
        insets
    }
    //避免不触发onApplyWindowInsets
    requestApplyInsetsWhenAttached()
}

// </editor-fold>

