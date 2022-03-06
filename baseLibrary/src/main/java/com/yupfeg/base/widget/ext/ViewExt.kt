package com.yupfeg.base.widget.ext

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

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
