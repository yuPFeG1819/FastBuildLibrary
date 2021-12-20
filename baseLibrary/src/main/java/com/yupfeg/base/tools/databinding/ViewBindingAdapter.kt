package com.yupfeg.base.tools.databinding

import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.yupfeg.base.tools.toPx
import com.yupfeg.base.tools.window.fitToSystemNavigationWindow
import com.yupfeg.base.tools.window.fitToSystemStatusBar
import com.yupfeg.base.widget.ext.ThrottleClickListenerWrapper
import com.yupfeg.base.widget.ext.setThrottleClickListener


/**
 * 对于view的控制是否可见状态
 * * DataBinding专用函数，所有DataBinding属性在xml都需要以["@{}"]赋值，否则会报错
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
 * * DataBinding专用函数，所有DataBinding属性在xml都需要以["@{}"]赋值，否则会报错
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
            layoutParams.height = height.toPx()
        }
    }
    this.layoutParams = layoutParams
}

/**
 * View拓展函数，设置View的宽度
 * * DataBinding专用函数，所有DataBinding属性在xml都需要以["@{}"]赋值，否则会报错
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
            layoutParams.width = width.toPx()
        }
    }
    this.layoutParams = layoutParams
}

/**
 * View拓展函数，bind背景drawable
 * * DataBinding专用函数，所有DataBinding属性在xml都需要以["@{}"]赋值，否则会报错
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
 * * DataBinding专用函数，所有DataBinding属性在xml都需要以["@{}"]赋值，否则会报错
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
 * * DataBinding专用函数，所有DataBinding属性在xml都需要以["@{}"]赋值，否则会报错
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
 * * DataBinding专用函数，所有DataBinding属性在xml都需要以["@{}"]赋值，否则会报错
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
 * * DataBinding专用函数，所有DataBinding属性在xml都需要以["@{}"]赋值，否则会报错
 * @param isSelected 是否为选中状态
 */
@Suppress("unused")
@BindingAdapter(value = ["setSelectedStatus"])
fun View.bindViewSelectedStatus(isSelected: Boolean?){
    isSelected?:return
    setSelected(isSelected)
}

// <editor-fold desc="状态栏适配">

/**
 * [View]拓展函数，设置视图适配状态栏（增加高度）
 * * DataBinding专用函数，所有DataBinding属性在xml都需要以["@{}"]赋值，否则会报错
 * */
@Suppress("unused")
@BindingAdapter(
    value = ["fitTopSystemWindow", "fitSystemWindowExtraPadding"],
    requireAll = false
)
fun View.bindFitTopSystemWindow(isFitStatusBar : Boolean?,extraPadding : Boolean?){
    isFitStatusBar?:return
    if (!isFitStatusBar) return
    fitToSystemStatusBar(extraPadding?:false)
}

/**
 * [View]拓展函数，设置视图适配导航栏（增加高度）
 * * DataBinding专用函数，所有DataBinding属性在xml都需要以["@{}"]赋值，否则会报错
 * */
@Suppress("unused")
@BindingAdapter(
    value = ["fitBottomSystemWindow","fitSystemWindowExtraPadding"],
    requireAll = false
)
fun View.bindFitBottomSystemWindow(isFitNavigation : Boolean?,extraPadding: Boolean?){
    isFitNavigation?:return
    if (!isFitNavigation) return
    fitToSystemNavigationWindow(extraPadding?:false)
}

// </editor-fold>