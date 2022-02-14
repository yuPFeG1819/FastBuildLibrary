package com.yupfeg.base.tools.ext

import android.content.res.Resources

/**[Int]拓展函数，dp值转成px[Int]值*/
@Suppress("unused")
fun Int.dipToPx() : Int  = (this * Resources.getSystem().displayMetrics.density).toInt()
/**[Int]拓展函数，px值转换成dp[Int]值*/
@Suppress("unused")
fun Int.toDip() : Int = (this / Resources.getSystem().displayMetrics.density).toInt()

/**[Int]拓展函数，sp值转成px[Int]值*/
@Suppress("unused")
fun Int.spToPx() : Int = (this * Resources.getSystem().displayMetrics.scaledDensity).toInt()
/**[Int]拓展函数，px值转换为sp的[Int]值*/
@Suppress("unused")
fun Int.toSp() : Int = (this / Resources.getSystem().displayMetrics.scaledDensity).toInt()

/**[Float]拓展函数，dp值转换成px值*/
@Suppress("unused")
fun Float.dipToPx() : Int =  (this * Resources.getSystem().displayMetrics.density).toInt()
/**[Float]拓展函数，px值转换成dp值*/
@Suppress("unused")
fun Float.toDip() : Int = (this / Resources.getSystem().displayMetrics.density).toInt()

/**[Int]拓展函数，sp值转成px[Int]值*/
@Suppress("unused")
fun Float.spToPx() : Int = (this * Resources.getSystem().displayMetrics.scaledDensity).toInt()
/**[Int]拓展函数，px值转换为sp的[Int]值*/
@Suppress("unused")
fun Float.toSp() : Int = (this / Resources.getSystem().displayMetrics.scaledDensity).toInt()

/**[Double]拓展函数，dp值转换成px值*/
@Suppress("unused")
fun Double.dipToPx() : Int = (this * Resources.getSystem().displayMetrics.density).toInt()
/**[Double]拓展函数，dp值转换成px值*/
@Suppress("unused")
fun Double.toDip() : Int = (this / Resources.getSystem().displayMetrics.density).toInt()