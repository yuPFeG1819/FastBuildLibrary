package com.yupfeg.base.tools.ext

import android.view.View

/**
 * [Int]拓展函数，构建`EXACTLY`模式的[View.MeasureSpec]
 * - 视图尺寸为精确值，设置多少就为多少
 * */
fun Int.makeExactlyMeasureSpec()
    = View.MeasureSpec.makeMeasureSpec(this,View.MeasureSpec.EXACTLY)

/**
 * [Int]拓展函数，构建`AT_MOST`模式的[View.MeasureSpec]
 * - 视图尺寸尽可能满足子视图的要求，最多能用多少就用多少
 * */
fun Int.makeAtMostMeasureSpec()
    = View.MeasureSpec.makeMeasureSpec(this,View.MeasureSpec.AT_MOST)

/**
 * [Int]拓展函数，构建`UNSPECIFIED`模式的[View.MeasureSpec]
 * - 视图尺寸不确定
 * */
fun Int.makeUnspecifiedMeasureSpec()
    = View.MeasureSpec.makeMeasureSpec(this,View.MeasureSpec.UNSPECIFIED)