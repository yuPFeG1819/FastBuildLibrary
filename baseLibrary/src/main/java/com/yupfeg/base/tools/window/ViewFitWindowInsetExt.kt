package com.yupfeg.base.tools.window

import android.annotation.TargetApi
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.core.view.*
import com.yupfeg.logger.ext.logd

/**
 * 用于记录当前视图初始Padding值
 * */
data class ViewInitialPadding(
    val left: Int,
    val top: Int,
    val right: Int,
    val bottom: Int
)

fun View.recordInitialPadding() : ViewInitialPadding = ViewInitialPadding(
    top = this.paddingTop, left = this.paddingLeft,
    right = this.paddingRight, bottom = this.paddingBottom
)

// <editor-fold desc="适配系统窗口">

/**
 * [View]拓展函数，延伸视图内容到系统状态栏
 * - 仅适配Android 5.0以上版本
 * - 适配状态栏显示隐藏，在状态栏隐藏时恢复原始高度，显示后再叠加状态栏高度
 * - 警告：如果使用[RelativeLayout]视图调用该函数，会导致内部centerInParent等属性出现不符合预期情况
 * @param extraPadding 是否增加视图顶部额外Padding，默认为false,
 * 如果为true则表示会在视图内部额外增加状态栏高度的Padding
 * @param onApplyWindowInsets 额外监听windowInset变化回调，默认为null，
 * */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
@JvmOverloads
fun View.fitToSystemStatusBar(
    extraPadding : Boolean = false,
    onApplyWindowInsets : OnApplyWindowInsetsListener? = null
){
    if (this is RelativeLayout){
        throw IllegalArgumentException(
            "RelativeLayout is cant fit to StatusBar,centerInParent child view will change"
        )
    }

    val wrapper = FitPaddingApplyWindowInsetsListenerWrapper(
        view = this, fitTarget = StatusBarFitTarget(extraPadding),listener = onApplyWindowInsets
    )
    ViewCompat.setOnApplyWindowInsetsListener(this,wrapper)
    //避免不触发onApplyWindowInsets
    requestApplyInsetsWhenAttached()
}

/**
 * [View]的拓展函数，将视图内容延伸到系统导航栏
 * * 最好在外部判断是否存在系统导航栏
 * * 警告，使用[RelativeLayout]调用该函数，会导致内部centerInParent等属性出现不符合预期情况
 * @param extraPadding 是否增加视图底部额外Padding，默认为false,
 * 如果为true则表示会在视图内部额外增加状态栏高度的Padding
 * @param onApplyWindowInsets 系统导航栏显示状态变化监听,在Android R以下设置无效
 * */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
@JvmOverloads
fun View.fitToSystemNavigationWindow(
    extraPadding : Boolean = false,
    onApplyWindowInsets : OnApplyWindowInsetsListener? = null
){
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R){
        //Android R底部WindowInset不会触发
        fitToNavigationBarWindowBeforeR()
        return
    }
    val wrapper = FitPaddingApplyWindowInsetsListenerWrapper(
        view = this, fitTarget = NavigationBarFitTarget(extraPadding),listener = onApplyWindowInsets
    )
    ViewCompat.setOnApplyWindowInsetsListener(this,wrapper)
    //避免不触发onApplyWindowInsets
    requestApplyInsetsWhenAttached()
}

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
private fun View.fitToNavigationBarWindowBeforeR(extraPadding : Boolean = false){
    post {
        //需要在视图树构建完成后，才能获取RootWindowInset
        val isNavigationExist = rootWindowInsetsCompat?.isVisible(
            WindowInsetsCompat.Type.navigationBars()
        )?:true
        logd("是否存在导航栏$isNavigationExist")
        if (!isNavigationExist) return@post
        val navigationBarHeight = getNavigationBarHeight()
        logd("获取导航栏高度：$navigationBarHeight")
        if (extraPadding){
            updatePadding(bottom = paddingBottom + navigationBarHeight)
        }
        updateLayoutParams<ViewGroup.LayoutParams> {
            height += navigationBarHeight
        }
    }
}



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

// </editor-fold>