package com.yupfeg.base.view.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.yupfeg.base.tools.system.getScreenWidth

/**
 * DialogFragment基类
 * @author yuPFeG
 * @date 19/11/16
 */
abstract class BaseDialogFragment : DialogFragment(){
    protected var mRootView : View ?= null

    //<editor-fold desc="子类可修改属性">

    /**dialog宽度占屏幕的百分比，默认为屏幕宽度的75%*/
    protected open val dialogScreenWidthPercent : Float
        get() = 0.75f

    /**是否不允许返回键关闭弹窗，默认为false*/
    protected open val isNotAllowCancelable : Boolean
        get() = false

    /**
     * dialog视图的显示位置
     * 默认为[Gravity.CENTER],显示在父视图的中心
     * */
    @Suppress("MemberVisibilityCanBePrivate")
    protected open val windowGravity : Int
        get() = Gravity.CENTER

    /**dialog的动画样式id，只在!=0时，使用对应样式*/
    @Suppress("MemberVisibilityCanBePrivate")
    protected open val dialogAnimStyleId : Int
        get() = 0

    //</editor-fold desc="子类可修改属性">

    //<editor-fold desc="dialog显示方法">

    override fun show(manager: FragmentManager, tag: String?) {
        //防止出现IllegalStateException :Can not perform this action after onSaveInstanceState异常
        if(!manager.isStateSaved && !manager.isDestroyed){
            super.show(manager, tag)
        }
    }

    //</editor-fold desc="dialog显示方法">

    //<editor-fold desc="抽象方法">

    /**
     * 设置布局resId
     * @return layout resId
     */
    protected abstract val contentLayoutId : Int

    /**子类实现的初始化布局控件 */
    protected abstract fun initView(viewGroup : View)

    /**子类实现的初始化数据*/
    protected abstract fun initData()

    //</editor-fold desc="抽象方法">

    //<editor-fold desc="生命周期">

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        //去掉默认标题栏
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return inflater.inflate(contentLayoutId,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //每次DialogFragment调用show()方法时，都会调用一次onViewCreated()
        initView(view)
        initData()
    }

    override fun onStart() {
        super.onStart()
        dialog?.setCancelable(isNotAllowCancelable)
        dialog?.setCanceledOnTouchOutside(!isNotAllowCancelable)
        initDialogWindow()
    }

    //</editor-fold desc="生命周期">

    //<editor-fold desc="dialog window 操作">


    /**设置弹窗整体的宽度*/
    protected open fun initDialogWindow(){
        dialog?.window?.apply {
            val params = attributes
            params.gravity = windowGravity    //设置dialog位置
            attributes = params
            //设置背景透明
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setDialogLayout()
            takeIf { dialogAnimStyleId > 0 }?.setWindowAnimations(dialogAnimStyleId)
        }
    }

    /**设置dialog的尺寸*/
    protected open fun Window.setDialogLayout(){
        //获取屏幕宽度
        val screenWidth = getScreenWidth(context)
        //设置dialog宽度为屏幕宽度的指定百分比，高度自适应
        setLayout((screenWidth * dialogScreenWidthPercent).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    //</editor-fold desc="dialog window 操作">

}