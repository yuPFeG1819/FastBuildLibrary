package com.yupfeg.rxjavasupport.dialog

import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Lifecycle
import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider

/**
 * 提供AutoDispose框架使用的作用域范围的DialogFragment
 * @author yuPFeG
 * @date 2020/03/19
 */
@Suppress("unused")
open class AutoDisposeDialogFragment : DialogFragment(){

    /**提供给AutoDispose框架使用的作用域范围，与fragment的onDestroy绑定*/
    protected val scopeProvider: AndroidLifecycleScopeProvider by lazy {
        AndroidLifecycleScopeProvider.from(this, Lifecycle.Event.ON_DESTROY)
    }

}