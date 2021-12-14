package com.yupfeg.base.view.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Lifecycle

/**
 * 懒加载Fragment基类
 * * Activity利用add+show+hide模式加载多个同级Fragment适用
 * * Fragment内部利用add+show+hide模式嵌套多个同级Fragment适用
 * * AndroidX下，外部配合使用[FragmentTransaction.setMaxLifecycle]来控制懒加载，
 * 前提是已经添加到[FragmentTransaction]
 * * 需要显示的Fragment,设置最大Lifecycle为[Lifecycle.State.RESUMED],生命周期会走到[Fragment.onResume]
 * * 需要隐藏的Fragment，设置最大Lifecycle为[Lifecycle.State.CREATED]，生命周期只会走到[Fragment.onStart]
 * *
 * @author yuPFeG
 * @date 2020/08/05
 */
@Suppress("unused")
abstract class BaseLazyFragment : BaseFragment(){
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