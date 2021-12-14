package com.yupfeg.base.tools.databinding.ext

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

/**
 * 创建layoutId对应的DataBinding类实例
 * @param inflater
 * @param layoutId 布局id
 * @param container
 * @return 如果指定视图已绑定，则直接返回对应实例，否则创建新实例
 * */
@Suppress("unused")
inline fun <reified T : ViewDataBinding> requireBindingInflate(
    inflater: LayoutInflater, @LayoutRes layoutId: Int, container: ViewGroup?
): T = requireNotNull(DataBindingUtil.inflate(inflater, layoutId, container, false)){
    "cannot find the matched layout."
}

/**
 * 绑定根View，获取视图对应的DataBinding实例
 * @param view 根视图view
 * @return 如果指定视图已绑定，则直接返回对应实例，否则绑定后创建实例
 * */
@Suppress("unused")
inline fun <reified T : ViewDataBinding> requireBindingView(view: View): T =
    requireNotNull(DataBindingUtil.bind(view)) { "cannot find the matched layout." }

