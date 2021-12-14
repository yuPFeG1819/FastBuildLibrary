package com.yupfeg.base.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

/**
 * 适配DataBinding的fragment基类
 * @author yuPFeG
 * @date 2021/04/29
 */
@Suppress("unused")
abstract class BaseBindFragment<T : ViewDataBinding> : BaseFragment(){
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
            //在使用Navigation时，由于Fragment与View的生命周期不一致，所有要使用viewLifecycleOwner
            lifecycleOwner = this@BaseBindFragment.viewLifecycleOwner
        }
        return mRootView
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }

    protected open fun inflateBindFragment(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) : T{
        return DataBindingUtil.inflate(inflater, layoutId, container, false)
            ?:throw NullPointerException("cant inflate layout from layoutId")
    }
}