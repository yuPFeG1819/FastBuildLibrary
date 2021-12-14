package com.yupfeg.base.tools

import android.app.Activity
import android.graphics.Rect
import android.view.ViewTreeObserver
import android.view.Window
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.annotation.NonNull
import com.yupfeg.base.tools.system.getSystemNavigationBarHeight
import com.yupfeg.logger.ext.logd
import kotlin.math.abs


/**
 * 软键盘输入状态变化的委托处理类
 * @author yuPFeG
 * @date 2021/06/28
 */
@Suppress("unused")
class KeyBoardInputChangeMediator {

    private var sDecorViewDelta = 0

    private var mGlobalLayoutListener : ViewTreeObserver.OnGlobalLayoutListener? = null
    /**
     * 软键盘状态变化监听
     * */
    interface OnSoftInputChangedListener {
        fun onSoftInputChanged(height: Int)
    }

    /**
     * 注册软键盘状态变化监听
     * @param listener 软键盘状态变化监听
     * */
    fun registerSoftStateChangeListener(
        window : Window,
        listener : OnSoftInputChangedListener
    ){
        val flags: Int = window.attributes.flags
        if (flags and WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS != 0) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }

        val contentView = window.findViewById<FrameLayout>(android.R.id.content)
        val decorViewInvisibleHeightPre = intArrayOf(getDecorViewInvisibleHeight(window))

        mGlobalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
            val height = getDecorViewInvisibleHeight(window)
            if (decorViewInvisibleHeightPre[0] != height){
                listener.onSoftInputChanged(height)
                decorViewInvisibleHeightPre[0] = height
            }
        }
        //注册根节点视图的布局监听
        contentView.viewTreeObserver.addOnGlobalLayoutListener(mGlobalLayoutListener)
    }

    /**
     * 注销软键盘状态变化的监听
     * @param window
     * */
    fun unRegisterSoftStateChangeListener(window: Window){
        mGlobalLayoutListener?:return
        val contentView = window.findViewById<FrameLayout>(android.R.id.content)
        contentView.viewTreeObserver.removeOnGlobalLayoutListener(mGlobalLayoutListener)
    }

    /**
     * 软键盘是否处于显示状态
     *
     * @param activity The activity.
     * @return
     * `true` - yes.
     * `false` - no
     */
    fun isSoftInputVisible(activity: Activity): Boolean {
        return getDecorViewInvisibleHeight(activity.window) > 0
    }

    /**
     * 获取window内的最外层ViewGroup的隐藏高度
     * * 软键盘会遮挡最外层视图
     * @param window
     * */
    private fun getDecorViewInvisibleHeight(@NonNull window: Window): Int {
        val decorView = window.decorView
        val outRect = Rect()
        decorView.getWindowVisibleDisplayFrame(outRect)
        logd(
            "KeyboardUtils", "getDecorViewInvisibleHeight: "
                    + (decorView.bottom - outRect.bottom)
        )
        val delta: Int = abs(decorView.bottom - outRect.bottom)
        if (delta <= getSystemNavigationBarHeight()) {
            sDecorViewDelta = delta
            return 0
        }
        return delta - sDecorViewDelta
    }
}
