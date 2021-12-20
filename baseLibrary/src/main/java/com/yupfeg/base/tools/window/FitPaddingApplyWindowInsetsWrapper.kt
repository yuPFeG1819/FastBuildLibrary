package com.yupfeg.base.tools.window

import android.view.View
import android.view.ViewGroup
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.yupfeg.logger.ext.logd

/**
 * WindowInset变化监听的包装类
 * - 适配系统视图增加额外Padding值，将视图内容正确延伸到系统视图
 * @author yuPFeG
 * @date 2021/12/19
 */
class FitPaddingApplyWindowInsetsListenerWrapper(
    private var initialPadding: ViewInitialPadding,
    private var fitTarget : FitWindowInsetTarget,
) : OnApplyWindowInsetsListener {
    /**记录视图原始高度*/
    private var initialHeight: Int = 0
    private var originListener : OnApplyWindowInsetsListener?= null

    constructor(
        view: View,
        fitTarget: FitWindowInsetTarget,
        listener : OnApplyWindowInsetsListener? = null
    ) : this(view.recordInitialPadding(),fitTarget){
        this.originListener = listener
    }

    override fun onApplyWindowInsets(v: View, insets: WindowInsetsCompat): WindowInsetsCompat {
        val extraHeight = fitTarget.getExtraHeight(insets)
        logd("onApplyWindowInsets 额外适配${fitTarget}的高度：$extraHeight")
        v.updateLayoutParams<ViewGroup.LayoutParams> {
            if (initialHeight == 0) initialHeight = height
            height = if (extraHeight <= 0) {
                //系统栏已隐藏，视图复原
                fitTarget.resetInitialPadding(v, initialPadding)
                initialHeight
            } else {
                //系统栏显示，视图增高
                fitTarget.addExtraPadding(v, initialPadding, extraHeight)
                initialHeight + extraHeight
            }
        }

        // 默认返回WindowInsetsCompat.CONSUMED，
        // 表示停止将 insets 分派给其子项以避免遍历整个视图层次结构，提升性能
        return originListener?.onApplyWindowInsets(v, insets) ?: WindowInsetsCompat.CONSUMED
    }
}