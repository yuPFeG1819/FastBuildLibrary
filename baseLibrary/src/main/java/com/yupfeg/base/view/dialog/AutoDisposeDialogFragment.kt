package com.yupfeg.base.view.dialog

import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Lifecycle
import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider

/**
 * 提供AutoDispose框架使用的作用域范围的DialogFragment
 * //TODO 后续移除到RxJava支持模块
 * @author yuPFeG
 * @date 2020/03/19
 */
@Deprecated("移除到RxJava支持模块")
open class AutoDisposeDialogFragment : DialogFragment(){

    /**提供给AutoDispose框架使用的作用域范围，与fragment的onDestroy绑定*/
    protected val scopeProvider: AndroidLifecycleScopeProvider by lazy {
        AndroidLifecycleScopeProvider.from(this, Lifecycle.Event.ON_DESTROY)
    }

}