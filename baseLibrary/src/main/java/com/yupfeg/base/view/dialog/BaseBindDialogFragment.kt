package com.yupfeg.base.view.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

/**
 * 适配DataBinding的DialogFragment基类
 * @author yuPFeG
 * @date 2021/05/26
 */
abstract class BaseBindDialogFragment<T : ViewDataBinding> : BaseDialogFragment(){
    @Suppress("MemberVisibilityCanBePrivate")
    private var mBinding : T ?= null

    protected val binding : T
        get() = mBinding
            ?: throw NullPointerException("view data binding is null,check onCreateView function")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        mBinding = inflateBindFragment(inflater, container).apply {
            mRootView = this.root
            lifecycleOwner = this@BaseBindDialogFragment
        }
        return mRootView
    }

    private fun inflateBindFragment(inflater: LayoutInflater,
                                    container: ViewGroup?) : T{
        return DataBindingUtil.inflate(inflater, contentLayoutId, container, false)
            ?:throw NullPointerException("cant inflate layout from layoutId")
    }
}