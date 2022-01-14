package com.yupfeg.base.tools.time

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

//<editor-fold desc="Long类型日期相关拓展函数">

/**
 * [Long]类型拓展函数，毫秒数转化为指定格式的日期字符串
 * @param targetFormat 目标日期字符串的日期格式，默认为[yyyy-MM-dd HH:mm:ss]
 * @param locale [Locale]，默认为[Locale.CHINA]
 */
@Suppress("unused")
fun Long.toDateFormatString(
    targetFormat: String = DateTimeTools.DEFAULT_DATE_TIME_FORMAT,
    locale: Locale = Locale.CHINA
) : String{
    val date = Date(this)
    val mTargetFormat = if (targetFormat.isNotEmpty()) targetFormat
    else DateTimeTools.DEFAULT_DATE_TIME_FORMAT
    val format = SimpleDateFormat(mTargetFormat,locale)
    return format.format(date)
}

/**
 * [Long]类型拓展函数，毫秒转日历格式
 * @param locale [Locale]，默认为[Locale.CHINA]
 */
@Suppress("unused")
fun Long.toCalendar(locale : Locale = Locale.CHINA) : Calendar {
    val calendar = Calendar.getInstance(locale)
    calendar.time = Date(this)
    return calendar
}

/**
 * 秒数转化时间字符串的格式
 * */
enum class TimeStringFormat{
    HH_MM_SS,HH_MM,MM_SS
}

/**
 * [Long]类型拓展函数，秒数转化为时分秒的字符串
 * @param format [TimeStringFormat]转化的字符串格式
 * @return
 * [TimeStringFormat.HH_MM_SS]，显示[hh:mm:ss]格式的字符串，不足10位时补0
 *
 * [TimeStringFormat.HH_MM]，显示[hh:mm]格式的字符串，不足10位时补0
 *
 * [TimeStringFormat.MM_SS]，显示[mm:ss]格式的字符串，不足10位时补0
 */
@Suppress("unused")
fun Long.toTimeString(format : TimeStringFormat = TimeStringFormat.HH_MM_SS) : String{
    val second = this
    if (second < 10) {
        return groupTimeStringByFormat(second = "0$second",format = format)
    }
    if (second < 60) {
        return groupTimeStringByFormat(second = "$second",format = format)
    }
    if (second < 3600) {
        val minute = second / 60
        val firstMinuteSecond = second - minute * 60
        return groupTimeStringByFormat(
            minute = if (minute < 10) "0$minute" else "$minute",
            second = if (firstMinuteSecond < 10) "0$firstMinuteSecond" else "$firstMinuteSecond",
            format = format
        )
    }
    val hour = second / 3600
    val minute = (second - hour * 3600) / 60
    val moreSecond = second - hour * 3600 - minute * 60
    return groupTimeStringByFormat(
        hour = if(hour < 10) "0$hour" else "$hour",
        minute = if (minute < 10) "0$minute" else "$minute",
        second = if (moreSecond <10) "0$moreSecond" else "$moreSecond",
        format = format
    )
}

/**
 * 根据字符串格式组合时间字符串
 * @param hour 小时，默认为"00"
 * @param minute 分钟字符串，默认为“00”
 * @param second 秒字符串，默认为”00“,
 * @param format 字符串格式
 * */
private fun groupTimeStringByFormat(
    hour : String = "00",
    minute : String = "00",
    second : String = "00",
    format : TimeStringFormat
) : String{
    return when(format){
        TimeStringFormat.HH_MM_SS -> "$hour:$minute:$second"
        TimeStringFormat.HH_MM -> "$hour:$minute"
        TimeStringFormat.MM_SS -> "$minute:$second"
    }
}

/**
 * [Long]类型拓展函数，计算当前时间戳与其他时间戳的相差秒数
 * @param otherTimeMillis 时间戳（ms）
 * @return 相差秒数（s）
 * */
@Suppress("unused")
fun Long.calculateDiffSecond(otherTimeMillis: Long) : Long{
    val diffMs = abs(otherTimeMillis - this)
    return diffMs/1000
}

//</editor-fold desc="Long类型日期相关拓展函数">

//<editor-fold desc="String类型日期相关拓展函数">

/**
 * [String]类型拓展函数，日期字符串转化为[targetFormat]格式的[String]
 * @param fromFormat 当前日期字符串所对应的日期格式 ，默认为[yyyy-MM-dd HH:mm:ss]
 * @param targetFormat 需要转化成的目标日期字符串所对应的日期格式，默认为[yyyy-MM-dd HH:mm:ss]
 * @param locale [Locale]，默认为[Locale.CHINA]
 */
@Suppress("unused")
fun String.toFormatDateString(
    fromFormat: String = DateTimeTools.DEFAULT_DATE_TIME_FORMAT,
    targetFormat: String = DateTimeTools.DEFAULT_DATE_TIME_FORMAT,
    locale : Locale = Locale.CHINA
) : String?{
    this.takeIf { it.isNotEmpty() }?.also {strDate->
        val date = strDate.parseDateFormat(fromFormat,locale)
        return date?.toStringByFormat(targetFormat)
    }?: run { return null }
    return null
}

/**
 * [String]类型拓展函数，解析UTC日期字符串转化为[Date]
 * @param fromFormat 日期字符串所对应的日期格式，默认为[yyyy-MM-dd HH:mm:ss]
 * @param locale [Locale]，默认为[Locale.CHINA]
 * */
@Suppress("unused")
fun String.parseUTCDataString(
    fromFormat: String = DateTimeTools.DEFAULT_DATE_TIME_FORMAT,
    locale: Locale = Locale.CHINA
) : String?{
    return this.takeIf { it.isNotEmpty() }?.also {strDate->
        val timeZone = TimeZone.getTimeZone("UTC")
        val date = strDate.parseDateFormat(fromFormat,locale,timeZone)
        date?.toStringByFormat(fromFormat)
    }?: run { null }
}

/**
 * [String]类型拓展函数，日期字符串转化成对应的时间戳
 * @param fromFormat 日期字符串所对应的日期格式，默认为[yyyy-MM-dd HH:mm:ss]
 * @param locale [Locale]，默认为[Locale.CHINA]
 */
@Suppress("unused")
fun String.toTimeMillis(
    fromFormat: String = DateTimeTools.DEFAULT_DATE_TIME_FORMAT,
    locale : Locale = Locale.CHINA
) = this.parseDateFormat(fromFormat, locale)?.time?:0


/**
 * [String]类型拓展函数，日期字符串转化为[Calendar]
 * @param fromFormat 日期字符串所对应的日期格式，默认为[yyyy-MM-dd HH:mm:ss]
 * @param locale [Locale]，默认为[Locale.CHINA]
 */
@Suppress("unused")
fun String.toCalendar(
    fromFormat: String = DateTimeTools.DEFAULT_DATE_TIME_FORMAT,
    locale: Locale = Locale.CHINA
) : Calendar?{

    val date = this.parseDateFormat(fromFormat,locale)
    date?: return null
    return Calendar.getInstance(locale).apply { time = date }
}

/**
 * [String]类型拓展函数，日期字符串转化为[Date]
 * @param fromFormat 日期字符串所对应的日期格式，默认为[yyyy-MM-dd HH:mm:ss]
 * @param locale [Locale]，默认为[Locale.CHINA]
 * */
@Suppress("unused")
fun String.parseDateFormat(
    fromFormat: String = DateTimeTools.DEFAULT_DATE_TIME_FORMAT,
    locale: Locale = Locale.CHINA,
    timeZone : TimeZone? = null
) : Date?{
    return if (this.isNotEmpty()){
        val formatPattern = if (fromFormat.isNotEmpty()) fromFormat
        else DateTimeTools.DEFAULT_DATE_TIME_FORMAT
        try {
            val dateFormat = SimpleDateFormat(formatPattern,locale)
            timeZone?.also { dateFormat.timeZone = it }
            dateFormat.parse(this)
        }catch (e : ParseException){
            null
        }
    }else{
        null
    }
}

/**
 * [String]类型拓展函数，比较当前日期字符串与[other]日期字符串的大小
 * @param other 日期字符串
 * @param format 日期字符串所对应的日期格式，默认为[yyyy-MM-dd HH:mm:ss]
 * @return 0 - 两个日期字符串相等，1 - 当前日期字符串较大，-1 - [other]日期较大
 */
@Suppress("unused")
fun String.compareDateString(
    other : String?,
    format : String = DateTimeTools.DEFAULT_DATE_TIME_FORMAT
) : Int{
    if (this.isEmpty()) return -1
    if (other.isNullOrEmpty()) return 1
    val currDate = this.parseDateFormat(format)
    val otherDate = other.parseDateFormat(format)
    return when {
        currDate?.time?:0 > otherDate?.time?:0 -> 1
        currDate?.time?:0 < otherDate?.time?:0 -> -1
        else -> 0
    }
}

//</editor-fold desc="String类型日期相关拓展函数">

//<editor-fold desc="Date类型拓展函数">

/**
 * [Date]类型拓展函数，日期转化为指定格式的日期字符串
 * @param dateFormat 日期字符串格式 默认为[yyyy-MM-dd HH:mm:ss]
 * @param locale [Locale]，默认为[Locale.CHINA]
 * @return 日期字符串，如果无法转化则为null
 */
@Suppress("unused")
fun Date.toStringByFormat(
    dateFormat: String = DateTimeTools.DEFAULT_DATE_TIME_FORMAT,
    locale: Locale = Locale.CHINA
) : String?{
    val targetFormat = if (dateFormat.isNotEmpty()) dateFormat
    else DateTimeTools.DEFAULT_DATE_TIME_FORMAT
    val format = SimpleDateFormat(targetFormat, locale)
    return format.format(this)?:null
}

//</editor-fold desc="Date类型拓展函数">