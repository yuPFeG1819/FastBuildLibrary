package com.yupfeg.base.widget.grid

/**
 * 九宫格视图item的适配器基类
 * @author yuPFeG
 * @date 2022/02/21
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
abstract class BaseNineGridAdapter : NineGridLayout.Adapter{

    private var mSingleViewHeight : Int = 0

    private var mSingleViewWidth : Int = 0

    /**
     * 设置只有单个视图时的View尺寸，通常由后台提供
     * @param width 单个item视图时的宽度，px
     * @param height 单个item视图时的高度，px
     * */
    fun setSingleViewSize(width : Int,height : Int){
        mSingleViewWidth = width
        mSingleViewHeight = height
    }

    final override val singleViewHeight : Int
        get() = mSingleViewHeight

    final override val singleViewWidth : Int
        get() = mSingleViewWidth

}