package com.yupfeg.base.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

/**
 * 基类Fragment
 * @author yuPFeG
 * @date 2020/02/14
 */
abstract class BaseFragment : Fragment(){

    protected var mRootView : View ?= null

    // <editor-fold desc="生命周期">

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        //防止onCreateView重复创建
        //如果为null，说明还没有缓存当前的View，因此会进行缓存，反之则直接利用
        return mRootView?: getViewRoot(inflater, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView(viewGroup = view,savedInstanceState = savedInstanceState)
        initData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mRootView = null
    }

    // </editor-fold>

    // <editor-fold desc="抽象方法，规范fragment类格式">
    /**布局文件id*/
    abstract val layoutId: Int

    /**
     * 子类实现的初始化布局控件
     * @param viewGroup inflate的layout ViewGroup
     * @param savedInstanceState savedInstanceState
     */
    protected abstract fun initView(viewGroup: View, savedInstanceState: Bundle?)

    /**订阅ViewModel层状态变化*/
    protected abstract fun initData()

    // </editor-fold>

    protected open fun getViewRoot(inflater: LayoutInflater, container: ViewGroup?): View {
        return inflater.inflate(layoutId, container, false).apply {
            mRootView = this
        }
    }
}
