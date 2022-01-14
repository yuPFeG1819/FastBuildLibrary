package com.yupfeg.base.tools.time

import com.yupfeg.logger.ext.logd
import java.text.SimpleDateFormat
import java.util.*


/**
 * 日期时间相关工具类
 * @author yuPFeG
 * @date 2019/12/16
 */
object DateTimeTools {
    /**默认年月日，时分秒的日期字符串格式*/
    const val DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss"

    /**一天的毫秒数*/
    private const val DAY_MS_UNIT = 1000 * 3600 * 24
    /**二天的毫秒数*/
    private const val SECOND_DAY_MS_UNIT = 1000 * 3600 * 48
    /**三天的毫秒数*/
    private const val THIRD_DAY_MS_UNIT = 1000 * 3600 * 72

    /**
     * 获取指定时间戳的月份字符串
     * @param timestamp 目标时间戳,默认为当前毫秒数
     */
    @Suppress("unused")
    fun getMonthStringFromTimeMillis(timestamp: Long = System.currentTimeMillis()): String {
        val calendar = Calendar.getInstance(Locale.CHINA)
        calendar.time = Date(timestamp)
        return "${calendar.get(Calendar.MONTH) + 1}月"
    }

    /**
     * 对指定日期字符串进行处理，延后指定日历字段后，输出日期字符串
     * @param strDate 指定日期字符串
     * @param fromFormat 原始的日期字符串所对应的日期格式 ，默认为[yyyy-MM-dd HH:mm:ss]
     * @param outFormat 输出返回目标日期字符串所对应的日期格式，默认为[yyyy-MM-dd HH:mm:ss]
     * @param locale [Locale]，默认为[Locale.CHINA]
     * @param calendarField 日历字段 ,默认为[Calendar.HOUR_OF_DAY]表示日期的24小时制的小时数
     * @param afterNum 延后num，如：2表示延后字段2个单位，-1表示提前字段1个单位
     */
    @Suppress("unused")
    fun getDateStringByAfterCalendarField(
        strDate: String,
        fromFormat: String = DEFAULT_DATE_TIME_FORMAT,
        outFormat: String = DEFAULT_DATE_TIME_FORMAT,
        locale: Locale = Locale.CHINA,
        calendarField: Int = Calendar.HOUR_OF_DAY,
        afterNum: Int = 1
    ): String {
        val calendar = strDate.toCalendar(fromFormat, locale)
        calendar ?: return ""
        calendar.add(calendarField, afterNum)
        return calendar.time.toStringByFormat(outFormat, locale) ?: ""
    }

    /**
     * 对指定时间戳进行处理，延后指定日历字段
     * @param timeMillis 时间戳（ms）
     * @param locale [Locale]，默认为[Locale.CHINA]
     * @param calendarField 日历字段 ,默认为[Calendar.HOUR_OF_DAY]表示日期的24小时制的小时数
     * @param afterNum 延后num，如：2表示延后字段2个单位，-1表示提前字段1个单位
     * @return 延迟（提前）日历字段后的时间戳
     */
    @Suppress("unused")
    fun getTimeMillisByAfterCalendarField(
        timeMillis: Long,
        locale: Locale = Locale.CHINA,
        calendarField: Int = Calendar.HOUR_OF_DAY,
        afterNum: Int = 1
    ): Long {
        val calendar = timeMillis.toCalendar(locale)
        calendar.add(calendarField, afterNum)
        return calendar.time.time
    }

    /**
     * 获取从指定日期开始，指定天数后的日期列表数据
     * @param startDate 起始日期，默认为当前日期
     * @param dayCount 日期天数，默认为7天
     * @param locale [Locale]，默认为[Locale.CHINA]
     */
    @Suppress("unused")
    fun getBetweenDateListAfterDayCount(
        startDate: Date = Date(), dayCount: Int = 7,
        locale: Locale = Locale.CHINA
    ): List<Date> {
        val tempStart = Calendar.getInstance(locale)
        tempStart.time = startDate
        //重置时间为当天的0点
        tempStart.set(Calendar.HOUR_OF_DAY, 0)
        tempStart.set(Calendar.MINUTE, 0)
        tempStart.set(Calendar.SECOND, 0)
        tempStart.set(Calendar.MILLISECOND, 0)
        val dateList = mutableListOf<Date>()
        for (index in 0 until dayCount) {
            dateList.add(tempStart.time)
            logd("dataTime", "日期列表数据：$dateList")
            //添加指定的时间给定日历领域，天数+1
            tempStart.add(Calendar.DAY_OF_YEAR, 1)
        }
        return dateList
    }

    /**
     * 获取指定时间戳的星期数
     * @param timestamp 时间戳(ms)
     * @param locale [Locale]，默认为[Locale.CHINA]
     * @return 星期数 1~7表示周一到周日
     */
    @Suppress("unused")
    fun getDayWeekIndexByTimeMillis(
        timestamp: Long,
        locale: Locale = Locale.CHINA
    ) : Int {
        return getDayWeekNumFromDate(Date(timestamp),locale)
    }

    /**
     * 获取指定日期的星期数
     * @param date 日期对象，默认为当前日期
     * @param locale [Locale]，默认为[Locale.CHINA]
     * @return 星期数 1~7表示周一到周日
     */
    @Suppress("unused")
    fun getDayWeekNumFromDate(
        date: Date = Date(),
        locale: Locale = Locale.CHINA
    ): Int {
        val calendar = Calendar.getInstance(locale)
        calendar.time = date
        return calendar.get(Calendar.DAY_OF_WEEK) - 1
    }

    /**
     * 获取当天00:00:00的毫秒数
     * @param locale [Locale]，默认为[Locale.CHINA]
     */
    @Suppress("unused")
    fun getTodayZeroTimeMillis(locale: Locale = Locale.CHINA): Long {
        val calendar = Calendar.getInstance(locale)
        calendar.timeZone = TimeZone.getDefault()
        calendar.time = Date()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    /**
     * 获取指定时间戳对应的天数，00:00:00的时间戳毫秒数
     * @param timestamp 目标时间戳(ms)
     * @param locale [Locale]，默认为[Locale.CHINA]
     */
    @Suppress("unused")
    fun getZeroTimeMillisFromTargetTimeMillis(
        timestamp: Long = 0,
        locale: Locale = Locale.CHINA
    ): Long {
        if (timestamp <= 0) {
            return getTodayZeroTimeMillis()
        }
        val calendar = Calendar.getInstance(locale)
        calendar.timeZone = TimeZone.getDefault()
        calendar.time = Date(timestamp)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    /**
     * 获取指定日期的日历上日期字符串
     * @param target 目标日期
     * @param dateFormat 日期字符串格式，默认为[MM-dd]
     * @return 如果目标日期处于[昨天-->后天]的时间段内，则显示前缀+日期，否则显示星期+日期格式
     */
    @Suppress("unused")
    fun getCalendarDateStringFromTargetDate(
        target: Date, dateFormat: String = "MM-dd",
        locale: Locale = Locale.CHINA
    ): String {
        val targetTimeMillis = target.time
        //计算当天的00:00的毫秒数
        val todayZeroTime = getTodayZeroTimeMillis()
        //获取最近几天时间戳的前缀文本
        val strTimePrefix = getRecentTimeFormatPrefix(
            todayZeroTime = todayZeroTime,
            targetTime = targetTimeMillis
        )
        val timeFormat = SimpleDateFormat(dateFormat, locale)
        val date = timeFormat.format(targetTimeMillis)
        return if (strTimePrefix.isEmpty()) {
            //如果日期前缀为空，则使用外部提供的前缀
            val weekPrefix = getDayWeekIndexByTimeMillis(targetTimeMillis)
            return "${weekPrefix}${date}"
        } else {
            "${strTimePrefix}${date}"
        }
    }

    /**
     * 获取指定日期的时间字符串
     * - 待后续优化，将中文文本替换为外部定义
     * @param target 目标日期
     * @param otherTimeFormat 日期字符串显示格式，默认为[MM/dd HH:mm]
     * @return 如果目标日期处于{[昨天-->后天]}的时间段内，则显示前缀+时间，否则显示日期+时间格式
     */
    @Suppress("unused")
    fun getCalendarTimeStringByTargetDate(
        target: Date, otherTimeFormat: String = "MM/dd HH:mm",
        locale: Locale = Locale.CHINA
    ): String {
        val targetTimeMillis = target.time
        //计算当天的00:00的毫秒数
        val todayZeroTime = getTodayZeroTimeMillis()
        val timeFormat: SimpleDateFormat
        //获取最近几天时间戳的前缀文本
        val strTimePrefix = getRecentTimeFormatPrefix(
            todayZeroTime = todayZeroTime,
            targetTime = targetTimeMillis
        )
        return if (strTimePrefix.isEmpty()) {
            //如果日期前缀为空，则使用外部提供的前缀
            timeFormat = SimpleDateFormat(otherTimeFormat, locale)
            return timeFormat.format(targetTimeMillis)
        } else {
            timeFormat = SimpleDateFormat("HH:mm", locale)
            "${strTimePrefix}${timeFormat.format(targetTimeMillis)}"
        }
    }

    /**
     * 获取最近几天时间戳的前缀文本
     * @param todayZeroTime 当前日期的0点毫秒数
     * @param targetTime 目标日期的毫秒数
     */
    private fun getRecentTimeFormatPrefix(todayZeroTime: Long, targetTime: Long): String {
        if (todayZeroTime > targetTime
            && todayZeroTime - targetTime <= DAY_MS_UNIT
        ) {
            return "昨天"
        } else if (targetTime > todayZeroTime) {
            return when (targetTime - todayZeroTime) {
                in 0 until DAY_MS_UNIT -> "今天"
                in DAY_MS_UNIT until SECOND_DAY_MS_UNIT -> "明天"
                in SECOND_DAY_MS_UNIT until THIRD_DAY_MS_UNIT -> "后天"
                else -> ""
            }
        }
        return ""
    }
}

