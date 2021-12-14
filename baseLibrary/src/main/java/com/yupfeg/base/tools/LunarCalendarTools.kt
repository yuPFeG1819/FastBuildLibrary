package com.yupfeg.base.tools

import android.annotation.SuppressLint
import com.bigkoo.pickerview.utils.ChinaDate.leapDays
import com.bigkoo.pickerview.utils.ChinaDate.monthDays
import com.yupfeg.logger.ext.logw
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


/**
 * 农历相关工具类提取文件
 * @author yuPFeG
 * @date 2020/08/14
 */

/**
 * 1901-2099年对应的农历 16进制信息
 * * 0-4位 其十进制位表示
 * * 5-6位 其十进制值表示春节所在公历月份
 * * 7-19位，分别表示农历 13月-1月（在闰月有13个月份），每一位表示1个月份（1表示大月为30天，0表示小月为29天）
 * * 20-23位，其十进制表示闰月月份，值为0表示无闰月
 *
 * * https://img-blog.csdn.net/20170919153827392?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvQ2VjaWxfS3dlaQ==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/Center
 * */
private val LUNAR_INFO: LongArray = longArrayOf(
    0x04ae53, 0x0a5748, 0x5526bd, 0x0d2650, 0x0d9544, 0x46aab9, 0x056a4d, 0x09ad42,
    0x24aeb6, 0x04ae4a,/*1901-1910*/
    0x6a4dbe, 0x0a4d52, 0x0d2546, 0x5d52ba, 0x0b544e, 0x0d6a43, 0x296d37, 0x095b4b,
    0x749bc1, 0x049754,/*1911-1920*/
    0x0a4b48, 0x5b25bc, 0x06a550, 0x06d445, 0x4adab8, 0x02b64d, 0x095742, 0x2497b7,
    0x04974a, 0x664b3e,/*1921-1930*/
    0x0d4a51, 0x0ea546, 0x56d4ba, 0x05ad4e, 0x02b644, 0x393738, 0x092e4b, 0x7c96bf,
    0x0c9553, 0x0d4a48,/*1931-1940*/
    0x6da53b, 0x0b554f, 0x056a45, 0x4aadb9, 0x025d4d, 0x092d42, 0x2c95b6, 0x0a954a,
    0x7b4abd, 0x06ca51,/*1941-1950*/
    0x0b5546, 0x555abb, 0x04da4e, 0x0a5b43, 0x352bb8, 0x052b4c, 0x8a953f, 0x0e9552,
    0x06aa48, 0x6ad53c,/*1951-1960*/
    0x0ab54f, 0x04b645, 0x4a5739, 0x0a574d, 0x052642, 0x3e9335, 0x0d9549, 0x75aabe,
    0x056a51, 0x096d46,/*1961-1970*/
    0x54aebb, 0x04ad4f, 0x0a4d43, 0x4d26b7, 0x0d254b, 0x8d52bf, 0x0b5452, 0x0b6a47,
    0x696d3c, 0x095b50,/*1971-1980*/
    0x049b45, 0x4a4bb9, 0x0a4b4d, 0xab25c2, 0x06a554, 0x06d449, 0x6ada3d, 0x0ab651,
    0x093746, 0x5497bb,/*1981-1990*/
    0x04974f, 0x064b44, 0x36a537, 0x0ea54a, 0x86b2bf, 0x05ac53, 0x0ab647, 0x5936bc,
    0x092e50, 0x0c9645,/*1991-2000*/
    0x4d4ab8, 0x0d4a4c, 0x0da541, 0x25aab6, 0x056a49, 0x7aadbd, 0x025d52, 0x092d47,
    0x5c95ba, 0x0a954e,/*2001-2010*/
    0x0b4a43, 0x4b5537, 0x0ad54a, 0x955abf, 0x04ba53, 0x0a5b48, 0x652bbc, 0x052b50,
    0x0a9345, 0x474ab9,/*2011-2020*/
    0x06aa4c, 0x0ad541, 0x24dab6, 0x04b64a, 0x69573d, 0x0a4e51, 0x0d2646, 0x5e933a,
    0x0d534d, 0x05aa43,/*2021-2030*/
    0x36b537, 0x096d4b, 0xb4aebf, 0x04ad53, 0x0a4d48, 0x6d25bc, 0x0d254f, 0x0d5244,
    0x5daa38, 0x0b5a4c,/*2031-2040*/
    0x056d41, 0x24adb6, 0x049b4a, 0x7a4bbe, 0x0a4b51, 0x0aa546, 0x5b52ba, 0x06d24e,
    0x0ada42, 0x355b37,/*2041-2050*/
    0x09374b, 0x8497c1, 0x049753, 0x064b48, 0x66a53c, 0x0ea54f, 0x06b244, 0x4ab638,
    0x0aae4c, 0x092e42,/*2051-2060*/
    0x3c9735, 0x0c9649, 0x7d4abd, 0x0d4a51, 0x0da545, 0x55aaba, 0x056a4e, 0x0a6d43,
    0x452eb7, 0x052d4b,/*2061-2070*/
    0x8a95bf, 0x0a9553, 0x0b4a47, 0x6b553b, 0x0ad54f, 0x055a45, 0x4a5d38, 0x0a5b4c,
    0x052b42, 0x3a93b6,/*2071-2080*/
    0x069349, 0x7729bd, 0x06aa51, 0x0ad546, 0x54daba, 0x04b64e, 0x0a5743, 0x452738,
    0x0d264a, 0x8e933e,/*2081-2090*/
    0x0d5252, 0x0daa47, 0x66b53b, 0x056d4f, 0x04ae45, 0x4a4eb9, 0x0a4d4c, 0x0d1541,
    0x2d92b5          /*2091-2099*/
)

/**
 * 用于保存中文的月份
 */
private val CHINESE_NUMBER = arrayOf(
    "一", "二", "三", "四", "五",
    "六", "七", "八", "九", "十", "十一", "腊"
)

@SuppressLint("SimpleDateFormat")
private val chineseDateFormat = SimpleDateFormat("yyyy年MM月dd日")

/**
 * 获取指定天数的农历天数的中文字符串
 * @param calendar 农历日期
 */
fun getLunarCalendarDayTextFromCalendar(calendar : Calendar) : String?{
    var baseDate: Date? = null
    try {
        baseDate = chineseDateFormat.parse("1900年1月31日")
    } catch (e: ParseException) {
        logw(e)
    }
    baseDate?:return null
    // 求出和1900年1月31日相差的天数
    var offsetDay = ((calendar.time.time - baseDate.time) / 86400000L)

    var tempYear = 1900
    /**当年的农历总天数*/
    var daysOfYear = 0
    // 用offsetDay减去每个农历年的天数
    // 计算当天是农历第几天
    // tempLunarYear最终结果是农历的年份
    // offsetDay是当年的第几天
    for(year in 1900..2099){
        if (offsetDay <= 0) break
        daysOfYear = computeLunarCalendarDayByCalendarYear(year)
        offsetDay -= daysOfYear
        tempYear += 1
    }

    if (offsetDay < 0) {
        offsetDay += daysOfYear
        tempYear -= 1
    }
    // 农历年份
    val lunarYear = tempYear
    //农历闰月的月份
    val leapMonth = getLeapMonth(lunarYear)
    /**是否为闰月*/
    var isLeap = false
    // 用当年的天数offset,逐个减去每月（农历）的天数，求出当天是本月的第几天
    var daysOfMonth = 0
    var tempMonth = 1
    while (tempMonth < 13 && offsetDay > 0) {

        // 闰月
        if (leapMonth > 0 && tempMonth == leapMonth + 1 && !isLeap) {
            --tempMonth
            isLeap = true
            daysOfMonth = leapDays(lunarYear)
        } else daysOfMonth = monthDays(lunarYear, tempMonth)
        offsetDay -= daysOfMonth
        // 解除闰月
        if (isLeap && tempMonth == leapMonth + 1) isLeap = false
        tempMonth++
    }

    // offset为0时，并且刚才计算的月份是闰月，要校正
    if (offsetDay == 0.toLong() && leapMonth > 0 && tempMonth == leapMonth + 1) {
        if (isLeap) {
            isLeap = false
        } else {
            isLeap = true
            --tempMonth
        }
    }
    // offset小于0时，也要校正
    if (offsetDay < 0) {
        offsetDay += daysOfMonth;
        --tempMonth

    }
    val lunarDay = (offsetDay + 1).toInt()
    return getChinaDayString(lunarDay)
}

/**
 * 计算指定公历年份的农历总天数
 * @param year 要计算的公历年份
 */
private fun computeLunarCalendarDayByCalendarYear(year : Int) : Int{
    var i : Long = 0x8000
    var sum = 348
    while (i > 0x8) {
        val lunarYear = LUNAR_INFO[year - 1900]
        if ( lunarYear and i != 0.toLong()) sum += 1
        //向右移一位
        i = i shr 1
    }
    return sum + leapDays(year)
}

/**
 * 计算指定公历[year]年的农历闰月天数
 * @param year 将要计算的年份
 */
private fun getLeapDays(year: Int): Int {
    return if (getLeapMonth(year) != 0) {
        if (LUNAR_INFO[year - 1900] and 0x10000 != 0.toLong()) {
            30
        } else{
            29
        }
    } else 0
}

/**
 * 计算指定公历[year]年闰哪个月 1-12 , 没闰传回 0
 * @param year 将要计算的年份
 * @return 传回农历 year年闰哪个月 1-12 , 没闰传回 0
 */
private fun getLeapMonth(year: Int): Int {
    return (LUNAR_INFO[year - 1900] and 0xf).toInt()
}


/**
 * 返化成中文格式农历的天数字符串
 * @param day
 * @return
 */
fun getChinaDayString(day: Int): String? {
    val chineseTen = arrayOf("初", "十", "廿", "卅")
    val n = if (day % 10 == 0) 9 else day % 10 - 1
    if (day > 30) return ""
    return if (day == 10) "${chineseTen[0]}${CHINESE_NUMBER[9]}"
    else chineseTen[day / 10] + CHINESE_NUMBER[n]
}




