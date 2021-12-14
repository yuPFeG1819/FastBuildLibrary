package com.yupfeg.base.tools.system

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.res.Resources
import android.graphics.Color
import android.os.Build
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment

// <editor-fold desc="状态栏、导航栏高度">

/**
 * 获取系统导航栏高度
 * @return 如果当前设备存在导航栏返回导航栏高度, 否则0
 */
@Suppress("unused")
fun getSystemNavigationBarHeight() : Int{
    val resources = Resources.getSystem()
    val resourceId: Int = resources.getIdentifier(
        "navigation_bar_height", "dimen", "android"
    )
    return if (resourceId != 0) {
        resources.getDimensionPixelSize(resourceId)
    } else {
        0
    }
}

/**
 * 获取系统状态栏高度
 */
@Suppress("unused")
fun getSystemStatusBarHeight(): Int {
    val resources = Resources.getSystem()
    var statusBarHeight = 24
    val resId = resources.getIdentifier(
        "status_bar_height", "dimen", "android"
    )
    statusBarHeight = if (resId != 0) {
        resources.getDimensionPixelSize(resId)
    } else {
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            statusBarHeight.toFloat(), Resources.getSystem().displayMetrics
        ).toInt()
    }
    return statusBarHeight
}

/**
 * [Activity]的拓展函数，是否存在导航栏
 */
val Activity.isNavigationBar: Boolean
    get() {
        val vp = window.decorView as? ViewGroup
        if (vp != null) {
            for (i in 0 until vp.childCount) {
                vp.getChildAt(i).context.packageName
                if (vp.getChildAt(i).id != -1 && "navigationBarBackground" ==
                    resources.getResourceEntryName(vp.getChildAt(i).id)
                ) return true
            }
        }
        return false
    }

// </editor-fold>



/**
 * [Activity]的拓展函数，开启全屏模式
 *
 * * 全屏模式会将状态栏与导航栏关闭
 * * 目前测试在Android R 以下效果不好，鸿蒙系统会移除状态栏
 * @param isEnable 是否开启全屏模式
 */
@SuppressLint("ObsoleteSdkInt")
@Suppress("unused")
@JvmOverloads
fun Activity.setFullScreenMode(isEnable: Boolean = true){
    if (Build.VERSION.SDK_INT < 30){
        fullScreenImplBeforeR(isEnable)
        return
    }

    val controller = ViewCompat.getWindowInsetsController(window.decorView)
    controller?.apply {
        systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_BARS_BY_SWIPE
        if (isEnable){
            //隐藏所有系统视图
            hide(WindowInsetsCompat.Type.systemBars())
        }else{
            //显示所有系统视图
            show(WindowInsetsCompat.Type.systemBars())
        }
    }
}

/**
 * [Activity]的拓展函数，Android 10以前的全屏模式实现
 * * 可能会导致不隐藏导航栏
 * @param enabled
 */
@Deprecated("目前并不能直接实现全屏模式")
private fun Activity.fullScreenImplBeforeR(enabled : Boolean){
    val systemUiVisibility = window.decorView.systemUiVisibility
    window.decorView.systemUiVisibility = if (enabled) {
        systemUiVisibility or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    } else {
        systemUiVisibility or View.SYSTEM_UI_FLAG_LAYOUT_STABLE and View.SYSTEM_UI_FLAG_FULLSCREEN.inv()
    }
}

/**
 * [Activity]的拓展函数，是否显示系统导航栏
 * * 只有在系统开启了导航栏时才能生效
 * @param isEnable
 * */
fun Activity.setSystemNavigationBarEnable(isEnable: Boolean = true){
    if (Build.VERSION.SDK_INT < 30){
        setNavigationBarImplBeforeR(isEnable)
        return
    }

    val controller = ViewCompat.getWindowInsetsController(window.decorView)
    controller?.apply {
        systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_BARS_BY_SWIPE
        if (isEnable){
            //隐藏所有系统视图（状态栏）
            hide(WindowInsetsCompat.Type.navigationBars())
        }else{
            //显示所有系统视图（状态栏）
            show(WindowInsetsCompat.Type.navigationBars())
        }
    }
}

/**
 * [Activity]的拓展函数，在Android R以下的是否显示系统导航栏
 * @param isEnable
 * */
@Deprecated("虽然能够开启隐藏导航栏，但会导致状态栏不显示")
private fun Activity.setNavigationBarImplBeforeR(isEnable: Boolean){
    val systemUiVisibility = window.decorView.systemUiVisibility
    if (isEnable) {
        window.decorView.systemUiVisibility = systemUiVisibility and
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION and View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    } else {
        window.decorView.systemUiVisibility = systemUiVisibility or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    }
}

// <editor-fold desc="沉浸式状态栏">

/**
 * [Activity]的拓展函数，开启沉浸式状态栏
 * * 可兼容到Android 5.0
 * @param color 状态栏颜色，默认为[Color.TRANSPARENT]，
 * 如果为透明颜色，最好再调用`ViewCompat.setOnApplyWindowInsetsListener`函数将对应视图内容到状态栏
 * @param isDarkText 是否为暗色状态栏文本
 */
@Suppress("unused")
@SuppressLint("ObsoleteSdkInt")
@JvmOverloads
fun Activity.immersiveStatusBar(
    @ColorInt color: Int = Color.TRANSPARENT,
    isDarkText: Boolean? = false
) {
    //允许应用视图填充系统视图，需要配合OnApplyWindowInsetsListener使用
    WindowCompat.setDecorFitsSystemWindows(window,false)
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    this.window.statusBarColor = color

//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
//        var systemUiVisibility = window.decorView.systemUiVisibility
//        systemUiVisibility = systemUiVisibility and
//                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN and
//                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//        window.decorView.systemUiVisibility = systemUiVisibility
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
//        this.window.statusBarColor = color
//    }
    isDarkText?.also {
        setStatusBarDarkText(it)
    }
}

// </editor-fold>


//<editor-fold desc="状态栏背景颜色">


/**
 *  [Activity]的拓展函数，设置状态栏颜色 (只在Android 5.0以上生效)
 * * 设置沉浸式后再设置状态栏颜色是无效的
 *  @param colorRes resId
 * */
@Suppress("unused")
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
fun Activity.setStatusBarColorRes(@ColorRes colorRes: Int = android.R.color.transparent) =
    setStatusBarColor(ContextCompat.getColor(this,colorRes))

/**
 *   [Activity]的拓展函数，设置状态栏颜色(只在Android 5.0以上生效)
 * * 设置沉浸式后再设置状态栏颜色是无效的
 *  @param color getColor的颜色值
 *  */
@SuppressLint("ObsoleteSdkInt")
@Suppress("unused")
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
fun Activity.setStatusBarColor(@ColorInt color: Int) {
    Color.TRANSPARENT
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        this.window?.statusBarColor = color
    }
}

/**
 * [Fragment]的拓展函数，设置状态栏颜色(只在Android 5.0以上生效)
 * * 设置沉浸式后再设置状态栏颜色是无效的
 *  @param colorRes resId
 *  */
@Suppress("unused")
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
fun Fragment.setStatusBarColorRes(@ColorRes colorRes: Int) = activity?.setStatusBarColorRes(colorRes)

/**
 *  [Fragment]的拓展函数，设置状态栏颜色(只在Android 5.0以上生效)
 * * 设置沉浸式后再设置状态栏颜色是无效的
 *  @param color getColor的颜色值
 * */
@Suppress("unused")
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
fun Fragment.setStatusBarColor(@ColorInt color: Int) = activity?.setStatusBarColor(color)

//</editor-fold>

// <editor-fold desc="状态栏文本颜色">

/**
 * [Activity]的拓展函数，设置系统状态栏的亮色文本
 * * 最低只兼容到Android 6.0 , API 23以下版本无效果
 * @param isEnable 是否开启亮色文本，true-文本颜色为黑色，false-文本颜色为白色
 * */
@Suppress("unused")
@TargetApi(Build.VERSION_CODES.M)
fun Activity.setStatusBarDarkText(isEnable : Boolean = true){
    val controller = ViewCompat.getWindowInsetsController(window.decorView)
    controller?.isAppearanceLightStatusBars = isEnable
}

/**
 * [Activity]的拓展函数，设置系统导航栏亮色文本
 * * 最低只兼容到Android 6.0 , API 23以下版本无效果
 * @param isEnable 是否开启亮色文本，true-文本颜色为白色，false-文本颜色为黑色
 * */
@Suppress("unused")
@TargetApi(Build.VERSION_CODES.M)
fun Activity.setNavigationBarLightText(isEnable: Boolean = true){
    val controller = ViewCompat.getWindowInsetsController(window.decorView)
    controller?.isAppearanceLightNavigationBars = isEnable
}

// </editor-fold>