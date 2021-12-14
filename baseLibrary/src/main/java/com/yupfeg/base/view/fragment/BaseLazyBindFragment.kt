package com.yupfeg.base.view.fragment

import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding

/**
 * 兼容DataBinding的懒加载Fragment
 * @author yuPFeG
 * @date 2021/04/29
 */
@Suppress("unused")
abstract class BaseLazyBindFragment<T : ViewDataBinding> : BaseBindFragment<T>(){

    /**是否已完成懒加载*/
    @Suppress("MemberVisibilityCanBePrivate")
    protected var isLazyLoaded = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {}

    override fun onResume() {
        super.onResume()
        if (!isLazyLoaded && !isHidden){
            //处于显示状态，且没有完成懒加载
            onLazyInit()
            isLazyLoaded = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        isLazyLoaded = false
    }


    protected open fun onLazyInit(){
        initView(mRootView!!,null)
        initData()
    }
}