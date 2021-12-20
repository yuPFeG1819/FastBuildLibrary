package com.yupfeg.base.tools.window

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.WindowManager
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment

// <editor-fold desc="状态栏、导航栏、软键盘">

/**
 * [View]拓展函数，获取`RootWindowInsets`的兼容性属性
 * */
inline val View.rootWindowInsetsCompat
    get() = ViewCompat.getRootWindowInsets(this)

/**
 * [View]拓展函数，获取`WindowInsetsController`的兼容性属性
 * */
@Suppress("unused")
inline val View.windowInsetsControllerCompat
    get() = ViewCompat.getWindowInsetsController(this)

/**
 * [Activity]拓展函数，获取系统导航栏高度
 * @param isIgnoreVisible 是否忽略已隐藏的导航栏
 * @return 如果当前设备存在导航栏返回导航栏高度, 否则0
 */
@Suppress("unused")
fun Activity.getNavigationBarHeight(isIgnoreVisible : Boolean = true) : Int{
    val rootInsets = window.decorView.rootWindowInsetsCompat
    return if (isIgnoreVisible) {
        rootInsets?.getInsetsIgnoringVisibility(
            WindowInsetsCompat.Type.navigationBars()
        )?.bottom ?: 0
    }else{
        rootInsets?.getInsets(WindowInsetsCompat.Type.navigationBars())?.bottom ?: 0
    }
}

/**
 * [Activity]拓展函数，获取系统状态栏高度
 * @param isIgnoreVisible 是否忽略已隐藏的状态栏
 * @return isIgnoreVisible-true时，在状态栏隐藏时无法获取高度，返回0
 */
@Suppress("unused")
fun Activity.getStatusBarHeight(isIgnoreVisible : Boolean = true): Int {
    val rootInsets = ViewCompat.getRootWindowInsets(window.decorView)
    return if (isIgnoreVisible) {
        rootInsets?.getInsetsIgnoringVisibility(WindowInsetsCompat.Type.statusBars())?.top ?: 0
    }else{
        rootInsets?.getInsets(WindowInsetsCompat.Type.statusBars())?.top ?: 0
    }
}

/**
 * [Activity]拓展函数，获取软键盘高度
 * @return 在软键盘隐藏时无法获取高度，返回0
 */
@Suppress("unused")
fun Activity.getKeyboardHeight() : Int{
    val rootInsets = window.decorView.rootWindowInsetsCompat
    return rootInsets?.getInsets(WindowInsetsCompat.Type.ime())?.top ?: 0
}


/**
 * [View]拓展函数，获取系统状态栏高度
 * @param isIgnoreVisible 是否忽略已隐藏的状态栏
 * @return isIgnoreVisible-true时，在状态栏隐藏时无法获取高度，返回0
 * */
@Suppress("unused")
fun View.getStatusBarHeight(isIgnoreVisible: Boolean = true) : Int{
    val rootInsets = this.rootWindowInsetsCompat
    return if (isIgnoreVisible) {
        rootInsets?.getInsetsIgnoringVisibility(WindowInsetsCompat.Type.statusBars())?.top ?: 0
    }else{
        rootInsets?.getInsets(WindowInsetsCompat.Type.statusBars())?.top ?: 0
    }
}


/**
 * [View]拓展函数，获取系统导航栏高度
 * @param isIgnoreVisible 是否忽略已隐藏的状态栏
 * @return isIgnoreVisible-true时，在状态栏隐藏时无法获取高度，返回0
 * */
@Suppress("unused")
fun View.getNavigationBarHeight(isIgnoreVisible: Boolean = true) : Int{
    val rootInsets = this.rootWindowInsetsCompat
    return if (isIgnoreVisible) {
        rootInsets?.getInsetsIgnoringVisibility(WindowInsetsCompat.Type.navigationBars())?.bottom ?: 0
    }else{
        rootInsets?.getInsets(WindowInsetsCompat.Type.navigationBars())?.bottom ?: 0
    }
}

/**
 * [Activity]的拓展属性，状态栏是否显示
 * */
@Suppress("unused")
val Activity.isStatusBarVisible : Boolean
    get() = window.decorView.rootWindowInsetsCompat?.isVisible(
        WindowInsetsCompat.Type.statusBars()
    ) ?: true

/**
 * [Activity]的拓展属性，是否存在导航栏
 */
@Suppress("unused")
inline val Activity.isNavigationBarVisible: Boolean
    get() = window.decorView.rootWindowInsetsCompat?.isVisible(
        WindowInsetsCompat.Type.navigationBars()
    ) ?: true

/**
 * [Activity]的拓展函数，软键盘是否已显示
 * */
@Suppress("unused")
inline val Activity.isKeyboardVisible : Boolean
    get() = window.decorView.rootWindowInsetsCompat?.isVisible(
        WindowInsetsCompat.Type.navigationBars()
    ) ?: false

/**
 * [Activity]的拓展函数，显示软键盘
 * */
@Suppress("unused")
fun Activity.showKeyboard(){
    window.decorView.windowInsetsControllerCompat?.show(WindowInsetsCompat.Type.ime())
}

/**
 * [Activity]的拓展函数，隐藏软键盘
 * */
@Suppress("unused")
fun Activity.hideKeyboard(){
    window.decorView.windowInsetsControllerCompat?.hide(WindowInsetsCompat.Type.ime())
}

/**
 * [Activity]拓展函数，显示状态栏
 */
fun Activity.showStatusBar(){
    window.decorView.windowInsetsControllerCompat?.show(WindowInsetsCompat.Type.statusBars())
}

/**
 * [Activity]拓展函数，隐藏状态栏
 */
fun Activity.hideStatusBar(){
    window.decorView.windowInsetsControllerCompat?.hide(WindowInsetsCompat.Type.statusBars())
}

/**
 * [Activity]拓展函数，显示导航栏
 */
fun Activity.showNavigationBar(){
    if (isNavigationBarVisible)
    window.decorView.windowInsetsControllerCompat?.show(WindowInsetsCompat.Type.navigationBars())
}

/**
 * [Activity]拓展函数，隐藏导航栏
 * */
fun Activity.hideNavigationBar(){
    window.decorView.windowInsetsControllerCompat?.hide(WindowInsetsCompat.Type.navigationBars())
}

// </editor-fold>



/**
 * [Activity]的拓展函数，开启全屏模式
 * * 全屏模式会将状态栏与导航栏关闭
 * * 目前测试在Android R 以下效果很差，鸿蒙系统会移除状态栏
 * @param isEnable 是否开启全屏模式
 */
@SuppressLint("ObsoleteSdkInt")
@Suppress("unused")
fun Activity.setFullScreenMode(isEnable: Boolean = true){
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R){
        fullScreenImplBeforeR(isEnable)
        return
    }
    val controller = ViewCompat.getWindowInsetsController(window.decorView)
    controller?.apply {
        systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
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
 * * 机型适配可能有问题
 * @param enabled
 */
@Deprecated("全屏效果很差，不同机型适配不同，暂时无法取消全屏")
private fun Activity.fullScreenImplBeforeR(enabled : Boolean){
    val systemUiVisibility = window.decorView.systemUiVisibility
    window.decorView.systemUiVisibility = if (enabled) {
        View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.INVISIBLE or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    } else {
        View.SYSTEM_UI_FLAG_LAYOUT_STABLE and View.SYSTEM_UI_FLAG_FULLSCREEN.inv() xor
                View.VISIBLE xor View.SYSTEM_UI_FLAG_HIDE_NAVIGATION.inv() xor
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION.inv()
    }
}

// <editor-fold desc="沉浸式状态栏">

/**
 * [Activity]的拓展函数，开启沉浸式状态栏
 * - 可兼容到Android 5.0
 * - 需要配合具体View通过设置`OnApplyWindowInsetsListener`来处理insets冲突问题
 * @param color 状态栏颜色，默认为[Color.TRANSPARENT]，
 * 如果为透明颜色，最好再调用`ViewCompat.setOnApplyWindowInsetsListener`函数将对应视图内容到状态栏
 * @param isDarkText 是否为暗色状态栏文本
 */
@Suppress("unused")
@SuppressLint("ObsoleteSdkInt")
@JvmOverloads
fun Activity.fitImmersiveStatusBar(
    @ColorInt color: Int = Color.TRANSPARENT,
    isDarkText: Boolean? = false
) {
    //1.使内容区域全屏，允许应用视图填充系统视图，需要配合OnApplyWindowInsetsListener使用
    WindowCompat.setDecorFitsSystemWindows(window,false)
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    //2.设置状态栏颜色，最好为透明色
    this.window.statusBarColor = color
    this.window.navigationBarColor = color
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