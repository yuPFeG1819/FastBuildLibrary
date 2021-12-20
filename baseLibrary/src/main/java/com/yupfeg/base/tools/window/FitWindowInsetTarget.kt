package com.yupfeg.base.tools.window

import android.view.View
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding

/**
 * 适配系统视图WindowInset的适配目标
 * @author yuPFeG
 * @date 2021/12/19
 */
interface FitWindowInsetTarget {

    /**
     * 获取需要额外增加的高度
     * @param windowInsets
     * */
    fun getExtraHeight(windowInsets: WindowInsetsCompat) : Int

    /**
     * 添加额外padding值到目标视图
     * @param view
     * @param padding 视图原本的padding值
     * @param extraHeight 添加的额外高度
     * */
    fun addExtraPadding(view : View, padding: ViewInitialPadding, extraHeight : Int)

    /**
     * 重置目标视图到原有padding值
     * @param view
     * @param padding 视图原本的padding值
     * */
    fun resetInitialPadding(view: View, padding: ViewInitialPadding)
}

/**
 * 系统状态栏适配目标
 * */
class StatusBarFitTarget(
    private val extraPaddingTop : Boolean = false
) : FitWindowInsetTarget{
    override fun getExtraHeight(windowInsets: WindowInsetsCompat) : Int{
        val insets = windowInsets.getInsets(WindowInsetsCompat.Type.statusBars())
        return insets.top
    }

    override fun addExtraPadding(view: View, padding: ViewInitialPadding, extraHeight : Int) {
        if (!extraPaddingTop) return
        view.updatePadding(top = padding.top + extraHeight)
    }

    override fun resetInitialPadding(view: View, padding: ViewInitialPadding) {
        view.updatePadding(top = padding.top)
    }

}

/**
 * 系统导航栏适配目标
 * */
class NavigationBarFitTarget(
    private val extraPadding : Boolean = false
) : FitWindowInsetTarget{
    override fun getExtraHeight(windowInsets: WindowInsetsCompat) : Int{
        val insets = windowInsets.getInsets(WindowInsetsCompat.Type.navigationBars())
        return insets.bottom
    }

    override fun addExtraPadding(view: View, padding: ViewInitialPadding, extraHeight: Int) {
        if (!extraPadding) return
        view.updatePadding(bottom = padding.bottom + extraHeight)
    }

    override fun resetInitialPadding(view: View, padding: ViewInitialPadding) {
        view.updatePadding(bottom = padding.bottom)
    }
}





