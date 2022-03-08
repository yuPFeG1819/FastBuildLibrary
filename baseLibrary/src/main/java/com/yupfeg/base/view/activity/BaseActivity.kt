package com.yupfeg.base.view.activity

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.yupfeg.base.view.ext.setTransitionAnim

/**
 * 基类Activity
 * @author yuPFeG
 * @date 2020/02/14
 */
abstract class BaseActivity : AppCompatActivity(){

    // <editor-fold desc="生命周期">

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        doOnInitViewBefore()
        initView(savedInstanceState)
        initData()
    }

    override fun onBackPressed() {
        finish()
        setTransitionAnim()
    }

    // </editor-fold>


    // <editor-fold desc="抽象方法，统一约束activity格式">

    /**布局文件id*/
    @Suppress("unused")
    protected abstract val layoutId: Int

    /**最外层View的背景drawable，默认为设置的[decorViewBackgroundColor]颜色，用于防止过度绘制*/
    protected open val decorViewBackground : Drawable?
        get() = ColorDrawable(decorViewBackgroundColor)

    /**最外层View的背景颜色ResId，用于防止过度绘制*/
    protected open val decorViewBackgroundColor : Int
        get() = Color.WHITE

    /**需要在setContentView前设置功能，默认为空实现 */
    protected open fun doOnInitViewBefore(){
        setDecorViewBackground()
    }

    /**
     * 设置系统根布局背景色
     * * 防止过度绘制
     * */
    protected open fun setDecorViewBackground(){
        decorViewBackground?.also { background ->
            window.decorView.background = background
        }
    }

    /**
     * 初始化控件
     * * tip : 如果使用原始的布局方式，需要注意设置[setContentView]，
     * 否则推荐使用DataBinding，利用by关键字，通过`bindingActivity`函数代理DataBinding类，
     * 或者利用`DataBindingUtil.setContentView<T>(this, layoutId)`
     * */
    protected abstract fun initView(savedInstanceState: Bundle?)
    /**初始化数据*/
    protected abstract fun initData()

    // </editor-fold>
}