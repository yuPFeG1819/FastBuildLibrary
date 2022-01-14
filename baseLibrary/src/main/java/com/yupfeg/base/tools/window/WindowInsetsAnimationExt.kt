package com.yupfeg.base.tools.window

import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsAnimationCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.yupfeg.logger.ext.logd


/**
 * [View]的拓展函数，设置WindowInsets的兼容动画回调监听
 * - 可能需要在注册表文件的Activity设置`android:windowSoftInputMode="adjustResize"`才能兼容到API 30以下的低版本
 * - 最低兼容到Android 5.0
 * @param callback 兼容的windowInset动画回调，所有WindowInsets操作都会触发，如状态栏、导航栏、软键盘显示隐藏等操作
 */
fun View.setWindowInsetsAnimationCompatCallBack(callback : WindowInsetsAnimationCompat.Callback?){
    callback?:return
    ViewCompat.setWindowInsetsAnimationCallback(this,callback)
}

/**
 * 视图跟随变化的windowInset动画执行回调
 * - 简化用于软键盘将视图顶起来的效果实现
 */
open class ViewFollowWindowInsetAnimationCallBack(
    private val target : View
) : WindowInsetsAnimationCompat.Callback(DISPATCH_MODE_CONTINUE_ON_SUBTREE){

    override fun onProgress(
        insets: WindowInsetsCompat,
        runningAnimations: MutableList<WindowInsetsAnimationCompat>
    ): WindowInsetsCompat {
        val ime = insets.getIme()
        val navigation = insets.getNavigationBar()
        logd("ime动画进行时 ：top : ${ime.top},bottom : ${ime.bottom} \n" +
                " 底部导航栏 ： bottom ${navigation.bottom}")
        target.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            //注意不要把margin设置为负数，否则如果存在导航栏，会不能触发延伸内容到导航栏高度
            val newMargin = ime.bottom - navigation.bottom
            bottomMargin = if (newMargin > 0) newMargin else 0
        }
        return insets
    }
}