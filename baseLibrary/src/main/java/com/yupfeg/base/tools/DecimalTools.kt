package com.yupfeg.base.tools

import java.math.BigDecimal

/**
 * Double类型的精确计算拓展文件
 * @author yuPFeG
 * @date 2020/07/22
 */

// 默认的需要精确至小数点后几位
private const val DECIMAL_POINT_NUMBER :Int = 2

/**
 * 精确模式
 * * ROUND_UP
 * 舍入远离零的舍入模式。
 * 在丢弃非零部分之前始终增加数字(始终对非零舍弃部分前面的数字加1)。
 * 注意，此舍入模式始终不会减少计算值的大小。
 * * ROUND_DOWN
 * * setScale(2, BigDecimal.ROUND_DOWN)  //删除多余的小数位，例如：2.125 → 2.12
 * 接近零的舍入模式。
 * 在丢弃某部分之前始终不增加数字(从不对舍弃部分前面的数字加1，即截短)。
 * 注意，此舍入模式始终不会增加计算值的大小。
 * * ROUND_CEILING
 * 接近正无穷大的舍入模式。
 * 如果 BigDecimal 为正，则舍入行为与 ROUND_UP 相同;
 * 如果为负，则舍入行为与 ROUND_DOWN 相同。
 * 注意，此舍入模式始终不会减少计算值。
 * * ROUND_FLOOR
 * 接近负无穷大的舍入模式。
 * 如果 BigDecimal 为正，则舍入行为与 ROUND_DOWN 相同;
 * 如果为负，则舍入行为与 ROUND_UP 相同。
 * 注意，此舍入模式始终不会增加计算值。
 * * ROUND_HALF_UP
 * 向“最接近的”数字舍入，如果与两个相邻数字的距离相等，则为向上舍入的舍入模式。
 * 如果舍弃部分 >= 0.5，则舍入行为与 ROUND_UP 相同;否则舍入行为与 ROUND_DOWN 相同。
 * 注意，这是我们大多数人在小学时就学过的舍入模式(四舍五入)。
 * * ROUND_HALF_DOWN
 * 向“最接近的”数字舍入，如果与两个相邻数字的距离相等，则为上舍入的舍入模式。
 * 如果舍弃部分 > 0.5，则舍入行为与 ROUND_UP 相同;否则舍入行为与 ROUND_DOWN 相同(五舍六入)。
 * * ROUND_HALF_EVEN 银行家舍入法
 * 向“最接近的”数字舍入，如果与两个相邻数字的距离相等，则向相邻的偶数舍入。
 * 如果舍弃部分左边的数字为奇数，则舍入行为与 ROUND_HALF_UP 相同;
 * 如果为偶数，则舍入行为与 ROUND_HALF_DOWN 相同。
 * 注意，在重复进行一系列计算时，此舍入模式可以将累加错误减到最小。
 * 此舍入模式也称为“银行家舍入法”，主要在美国使用。四舍六入，五分两种情况。
 * 如果前一位为奇数，则入位，否则舍去。
 * 以下例子为保留小数点1位，那么这种舍入方式下的结果。
 * 1.15>1.2 1.25>1.2
 * * ROUND_UNNECESSARY
 * 断言请求的操作具有精确的结果，因此不需要舍入。
 * 如果对获得精确结果的操作指定此舍入模式，则抛出ArithmeticException。
*/

/**
 * [Double]类型的精确到指定小数点位数的【加】操作
 * @param d1
 * @param d2
 * @param decimalNum 精确的小数点位数，默认为[DECIMAL_POINT_NUMBER] = 2位小数
 */
fun doubleDecimalAdd(d1:Double,d2:Double,decimalNum : Int = DECIMAL_POINT_NUMBER): Double {
    return BigDecimal(d1).add(BigDecimal(d2)).setScale(decimalNum,BigDecimal.ROUND_HALF_UP).toDouble()
}

/**
 * [Double]类型的精确到指定小数点位数的【减】操作
 * @param d1
 * @param d2
 * @param decimalNum 精确的小数点位数，默认为[DECIMAL_POINT_NUMBER] = 2位小数
 */
fun doubleDecimalSubtract(d1:Double,d2:Double,decimalNum : Int = DECIMAL_POINT_NUMBER): Double {
    return BigDecimal(d1).subtract(BigDecimal(d2)).setScale(decimalNum,BigDecimal.ROUND_HALF_UP).toDouble()
}

/**
 * [Double]类型的精确到指定小数点位数的【乘】操作
 * @param d1
 * @param d2
 * @param decimalNum 精确的小数点位数，默认为[DECIMAL_POINT_NUMBER] = 2位小数
 */
fun doubleDecimalMultiply(d1:Double,d2:Double,decimalNum : Int = DECIMAL_POINT_NUMBER): Double {
    return BigDecimal(d1).multiply(BigDecimal(d2)).setScale(decimalNum,BigDecimal.ROUND_HALF_UP).toDouble()
}

/**
 * [Double]类型的精确到指定小数点位数的【除】操作
 * @param d1
 * @param d2
 * @param decimalNum 精确的小数点位数，默认为[DECIMAL_POINT_NUMBER] = 2位小数
 */
fun doubleDecimalDivide(d1:Double,d2:Double,decimalNum : Int = DECIMAL_POINT_NUMBER): Double {
    return BigDecimal(d1).divide(BigDecimal(d2)).setScale(decimalNum,BigDecimal.ROUND_HALF_UP).toDouble()
}
