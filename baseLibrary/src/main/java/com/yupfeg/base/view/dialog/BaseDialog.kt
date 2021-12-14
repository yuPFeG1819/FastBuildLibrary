package com.yupfeg.base.view.dialog

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import androidx.annotation.LayoutRes
import androidx.annotation.StyleRes
import androidx.appcompat.app.AppCompatDialog
import com.yupfeg.base.R
import com.yupfeg.base.tools.system.getScreenWidth

/**
 * 基类dialog
 * @author yuPFeG
 * @date 2021/05/24
 */
@Suppress("unused")
abstract class BaseDialog(
    context: Context,
    @LayoutRes private val layoutId : Int,
    @StyleRes dialogStyle : Int = R.style.FastBuildSimpleDialogStyle
) : AppCompatDialog(context, dialogStyle){

    /**
     * dialog视图的显示位置
     * 默认为[Gravity.CENTER],显示在父视图的中心
     * */
    @Suppress("MemberVisibilityCanBePrivate")
    protected val windowGravity : Int
        get() = Gravity.CENTER

    /**dialog宽度占屏幕的百分比，默认为屏幕宽度的75%*/
    protected open val dialogScreenWidthPercent : Float
        get() = 0.75f

    /**是否允许返回键关闭弹窗，默认为false*/
    protected open val isAllowCancelable : Boolean
        get() = false

    init {
        this.setContentView(layoutId)
        this.initView()
        this.initWindowSetting()
    }

    abstract fun initView()

    protected open fun initWindowSetting(){
        //设置是否允许返回键关闭
        setCancelable(isAllowCancelable)
        window?.apply {
            val params = attributes
            params.gravity = windowGravity    //设置dialog位置
            attributes = params
            //设置背景透明
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setDialogLayout()
        }
    }

    /**设置dialog的尺寸*/
    protected open fun Window.setDialogLayout(){
        val screenWidth = getScreenWidth(context)
        //设置dialog宽度为屏幕宽度的指定百分比，高度自适应
        setLayout((screenWidth * dialogScreenWidthPercent).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}