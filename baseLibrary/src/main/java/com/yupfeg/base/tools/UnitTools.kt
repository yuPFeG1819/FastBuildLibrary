package com.yupfeg.base.tools

import android.content.res.Resources

/**[Int]拓展函数，转成px[Int]值*/
@Suppress("unused")
fun Int.toPx() : Int  = (this * Resources.getSystem().displayMetrics.density).toInt()
/**[Int]拓展函数，转换成dp[Int]值*/
@Suppress("unused")
fun Int.toDip() : Int = (this / Resources.getSystem().displayMetrics.density).toInt()
/**[Float]拓展函数，dp值转换成px值*/
@Suppress("unused")
fun Float.toPx() : Int =  (this * Resources.getSystem().displayMetrics.density).toInt()
/**[Float]拓展函数，px值转换成dp值*/
@Suppress("unused")
fun Float.toDip() : Int = (this / Resources.getSystem().displayMetrics.density).toInt()
/**[Double]拓展函数，dp值转换成px值*/
@Suppress("unused")
fun Double.toPx() : Int = (this * Resources.getSystem().displayMetrics.density).toInt()
/**[Double]拓展函数，dp值转换成px值*/
@Suppress("unused")
fun Double.toDip() : Int = (this / Resources.getSystem().displayMetrics.density).toInt()