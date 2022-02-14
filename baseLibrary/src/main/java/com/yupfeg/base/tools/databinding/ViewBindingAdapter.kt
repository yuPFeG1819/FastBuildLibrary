package com.yupfeg.base.tools.databinding

import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsAnimationCompat
import androidx.databinding.BindingAdapter
import com.yupfeg.base.tools.ext.dipToPx
import com.yupfeg.base.tools.window.fitToSystemNavigationWindow
import com.yupfeg.base.tools.window.fitToSystemStatusBar
import com.yupfeg.base.tools.window.setWindowInsetsAnimationCompatCallBack
import com.yupfeg.base.widget.ext.ThrottleClickListenerWrapper
import com.yupfeg.base.widget.ext.setThrottleClickListener


/**
 * 对于view的控制是否可见状态
 * * DataBinding专用函数，所有DataBinding属性在xml都需要以["@{}"]赋值，否则会报错，且属性必须为可空状态
 * @param visible 是否可见
 */
@Suppress("unused")
@BindingAdapter("goneUnless")
fun View.bindGoneUnless(visible: Boolean?) {
    visible?:return
    visibility = if (visible) View.VISIBLE else View.GONE
}

/**
 * View拓展函数，设置View的高度
 * * DataBinding专用函数，所有DataBinding属性在xml都需要以["@{}"]赋值，否则会报错，且属性必须为可空状态
 * @param height view高度（dp）、WRAP_CONTENT、MATCH_PARENT
 * */
@Suppress("unused")
@BindingAdapter(value = ["viewHeight"])
fun View.bindLayoutHeight(height : Int?){
    height?:return
    val layoutParams = this.layoutParams
    when (height) {
        ViewGroup.LayoutParams.WRAP_CONTENT -> {
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
        }
        ViewGroup.LayoutParams.MATCH_PARENT -> {
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        }
        else -> {
            layoutParams.height = height.dipToPx()
        }
    }
    this.layoutParams = layoutParams
}

/**
 * View拓展函数，设置View的宽度
 * * DataBinding专用函数，所有DataBinding属性在xml都需要以["@{}"]赋值，否则会报错，且属性必须为可空状态
 * @param width view宽度（dp）、WRAP_CONTENT、MATCH_PARENT
 */
@Suppress("unused")
@BindingAdapter(value = ["viewWidth"])
fun View.bindLayoutWidth(width : Int?){
    width?:return
    val layoutParams = this.layoutParams
    when (height) {
        ViewGroup.LayoutParams.WRAP_CONTENT -> {
            layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
        }
        ViewGroup.LayoutParams.MATCH_PARENT -> {
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        }
        else -> {
            layoutParams.width = width.dipToPx()
        }
    }
    this.layoutParams = layoutParams
}

/**
 * View拓展函数，bind背景drawable
 * * DataBinding专用函数，所有DataBinding属性在xml都需要以["@{}"]赋值，否则会报错，且属性必须为可空状态
 * @param drawable 背景图
 */
@Suppress("unused")
@BindingAdapter("backgroundDrawable")
fun View.bindBackgroundDrawable(drawable : Drawable?){
    drawable?.let {
        background = it
    }
}

/**
 * View拓展函数，bind view的背景颜色
 * * DataBinding专用函数，所有DataBinding属性在xml都需要以["@{}"]赋值，否则会报错，且属性必须为可空状态
 * @param colorResId 背景颜色id
 */
@Suppress("unused")
@BindingAdapter("backgroundColorResource")
fun View.bindBackgroundColor(@ColorRes colorResId : Int?){
    colorResId?:return
    setBackgroundColor(ContextCompat.getColor(context,colorResId))
}

/**
 * View拓展函数，bind view的背景drawable resId
 * * DataBinding专用函数，所有DataBinding属性在xml都需要以["@{}"]赋值，否则会报错，且属性必须为可空状态
 * @param drawableResId 背景颜色id
 */
@Suppress("unused")
@BindingAdapter("backgroundResource")
fun View.bindBackgroundDrawable(@DrawableRes drawableResId : Int?){
    drawableResId?:return
    setBackgroundResource(drawableResId)
}

/**
 * [View]拓展函数，对View设置防抖点击事件，默认为500ms
 * * DataBinding专用函数，所有DataBinding属性在xml都需要以["@{}"]赋值，否则会报错，且属性必须为可空状态
 * @param clickDelay 点击防抖延迟，默认为500ms
 * @param onClickListener 点击事件
 */
@Suppress("unused")
@BindingAdapter(value = ["clickDelay","onViewClick"], requireAll = false)
fun View.bindViewSingleClick(
    clickDelay : Int = ThrottleClickListenerWrapper.DEF_DELAY,
    onClickListener: View.OnClickListener?
){
    onClickListener?:return
    if (clickDelay == 0){
        setOnClickListener(onClickListener)
        return
    }
    setThrottleClickListener(delayTime = clickDelay, onClickListener = onClickListener)
}

/**
 * [View]拓展函数，设置View的选中状态
 * * DataBinding专用函数，所有DataBinding属性在xml都需要以["@{}"]赋值，否则会报错，且属性必须为可空状态
 * @param isSelected 是否为选中状态
 */
@Suppress("unused")
@BindingAdapter(value = ["setSelectedStatus"])
fun View.bindViewSelectedStatus(isSelected: Boolean?){
    isSelected?:return
    setSelected(isSelected)
}

/**
 * [View]拓展函数，设置绑定View的焦点变化监听
 * * DataBinding专用函数，所有DataBinding属性在xml都需要以["@{}"]赋值，否则会报错，且属性必须为可空状态
 * @param listener
 * */
@Suppress("unused")
@BindingAdapter(value = ["setOnFocusChange"])
fun View.bindFocusChangeListener(listener : View.OnFocusChangeListener?){
    listener?:return
    onFocusChangeListener = listener
}

/**
 * [View]拓展函数，设置绑定View的触控监听
 * * DataBinding专用函数，所有DataBinding属性在xml都需要以["@{}"]赋值，否则会报错，且属性必须为可空状态
 * @param listener 触控监听
 * */
@Suppress("unused")
@BindingAdapter(value = ["setOnTouchListener"])
fun View.bindOnTouchListener(listener : View.OnTouchListener?){
    listener?:return
    this.setOnTouchListener(listener)
}

// <editor-fold desc="状态栏适配">

/**
 * [View]拓展函数，设置视图适配状态栏（增加高度）
 * * DataBinding专用函数，所有DataBinding属性在xml都需要以["@{}"]赋值，否则会报错，且属性必须为可空状态
 * @param isFitToStatusBar 是否延伸视图内容到状态栏
 * @param isExtraPadding 是否添加额外的视图padding
 * */
@Suppress("unused")
@BindingAdapter(
    value = ["isFitToStatusBar", "isFitExtraPadding"],
    requireAll = false
)
fun View.bindFitToStatusBar(isFitToStatusBar : Boolean?, isExtraPadding : Boolean?){
    isFitToStatusBar?:return
    if (!isFitToStatusBar) return
    fitToSystemStatusBar(isExtraPadding?:false)
}

/**
 * [View]拓展函数，设置视图适配导航栏（增加高度）
 * * DataBinding专用函数，所有DataBinding属性在xml都需要以["@{}"]赋值，否则会报错，且属性必须为可空状态
 * */
@Suppress("unused")
@BindingAdapter(
    value = ["isFitToNavigation","isFitExtraPadding"],
    requireAll = false
)
fun View.bindFitToNavigationBar(isFitToNavigation : Boolean?, isExtraPadding: Boolean?){
    isFitToNavigation?:return
    if (!isFitToNavigation) return
    fitToSystemNavigationWindow(isExtraPadding?:false)
}

// </editor-fold>

// <editor-fold desc="WindowInset动画监听">

/**
 * [View]拓展函数，设置WindowInset的兼容动画回调
 * * DataBinding专用函数，所有DataBinding属性在xml都需要以["@{}"]赋值，否则会报错，且属性必须为可空状态
 * * Android 11以下需要在注册表文件中，Activity设置`android:windowSoftInputMode="adjustResize"`属性
 * @param callBack 兼容的windowInset动画回调，所有WindowInsets操作都会触发，如状态栏、导航栏、软键盘显示隐藏等操作
 * */
@Suppress("unused")
@BindingAdapter(value = ["windowInsetAnimationCallBack"])
fun View.bindWindowInsetAnimationCallBack(callBack : WindowInsetsAnimationCompat.Callback?){
    callBack?:return
    this.setWindowInsetsAnimationCompatCallBack(callBack)
}

// </editor-fold>