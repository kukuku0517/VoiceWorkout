package com.example.domain

import java.io.Serializable
import java.security.InvalidParameterException
import java.text.SimpleDateFormat
import java.util.*

class Time private constructor(private var calendar: Calendar) : Serializable {

    companion object {
        val DAY_OF_WEEK = arrayOf("토", "일", "월", "화", "수", "목", "금")

        @JvmStatic
        fun now(): Time {
            return Time(Calendar.getInstance())
        }

        @JvmStatic
        fun from(calendar: Calendar): Time {
            return Time(calendar)
        }

        @JvmStatic
        fun from(date: Date): Time {
            val calendar = Calendar.getInstance()
            calendar.time = date
            return Time(calendar)
        }

        @JvmStatic
        fun from(timestamp: String): Time {

            val year = Integer.parseInt(timestamp.substring(0, 4))
            val month = Integer.parseInt(timestamp.substring(5, 7)) - 1
            val day = Integer.parseInt(timestamp.substring(8, 10))

            var hourOfDay = 0
            var minute = 0
            var second = 0
            if (timestamp.length > 10) {
                hourOfDay = Integer.parseInt(timestamp.substring(11, 13))
                minute = Integer.parseInt(timestamp.substring(14, 16))
                second = Integer.parseInt(timestamp.substring(17, 19))
            }

            val calendar = Calendar.getInstance()
            calendar.set(year, month, day, hourOfDay, minute, second)
            calendar.set(Calendar.MILLISECOND, 0)

            if (timestamp.endsWith("Z")) {
                calendar.timeZone = TimeZone.getTimeZone("UTC")
            }
            return Time(calendar)
        }

        @JvmStatic
        fun from(duration: Duration): Time {
            val calendar = Calendar.getInstance()
            calendar.time = Date(duration.millis)
            return Time(calendar)
        }

        @JvmStatic
        fun from(alarmTime: AlarmTime): Time {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, alarmTime.hour)
            calendar.set(Calendar.MINUTE, alarmTime.minute)
            calendar.set(Calendar.SECOND, alarmTime.second)
            calendar.set(Calendar.MILLISECOND, 0)
            return Time(calendar)
        }

        @JvmStatic
        fun of(year: Int, month: Int, day: Int, hour: Int = 0, min: Int = 0, sec: Int = 0): Time {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month - 1)
            calendar.set(Calendar.DAY_OF_MONTH, day)
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, min)
            calendar.set(Calendar.SECOND, sec)
            calendar.set(Calendar.MILLISECOND, 0)
            return Time(calendar)
        }
    }

    operator fun plus(duration: Duration): Time {
        val millis = this.calendar.timeInMillis + duration.millis
        return from(Duration.millis(millis))
    }

    operator fun minus(duration: Duration): Time {
        val millis = this.calendar.timeInMillis - duration.millis
        return from(Duration.millis(millis))
    }

    operator fun plus(duration: DurationOfMonth): Time {
        val newCal = this.calendar.clone() as Calendar
        newCal.add(Calendar.MONTH, duration.month)
        return from(newCal)
    }

    operator fun minus(duration: DurationOfMonth): Time {
        val newCal = this.calendar.clone() as Calendar
        newCal.add(Calendar.MONTH, -duration.month)
        return from(newCal)
    }


    operator fun compareTo(time: Time): Int {
        return this.calendar.compareTo(time.calendar)
    }

    operator fun rangeTo(other: Time): List<Time> {
        val ranges = mutableListOf<Time>()
        var temp = this.clone().getStartOfDay()

        if (temp >= other) {
            return ranges
        }
        while (temp < other) {
            ranges.add(temp.clone())
            temp += Duration.ofDay(1)
        }
        ranges.add(other)
        return ranges
    }

    infix fun until(other: Time): List<Time> {
        val ranges = mutableListOf<Time>()
        var temp = this.clone().getStartOfDay()

        if (temp >= other) {
            return ranges
        }
        while (temp < other) {
            ranges.add(temp.clone())
            temp += Duration.ofDay(1)
        }
        return ranges.toList()
    }

    override fun equals(other: Any?): Boolean {
        return if (other is Time) {
            this.calendar.compareTo(other.calendar) == 0
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        return this.calendar.hashCode()
    }

    override fun toString(): String {
        return toTimestamp()
    }

    fun toMillis(): Long {
        return calendar.timeInMillis
    }

    fun getSec(): Int {
        return calendar.get(Calendar.SECOND)
    }

    fun getMin(): Int {
        return calendar.get(Calendar.MINUTE)
    }

    fun getHour(): Int {
        return calendar.get(Calendar.HOUR_OF_DAY)
    }

    fun getDay(): Int {
        return calendar.get(Calendar.DAY_OF_MONTH)
    }

    fun getMonth(): Int {
        return calendar.get(Calendar.MONTH) + 1
    }

    fun getYear(): Int {
        return calendar.get(Calendar.YEAR)
    }

    fun getDayOfWeekString(): String {
        return DAY_OF_WEEK[((7 + getDay()) % 7)]
    }

    fun getDayOfWeek(): Int {
        return calendar.get(Calendar.DAY_OF_WEEK)
    }

    fun getDayOfMonth(): Int {
        return calendar.get(Calendar.DAY_OF_MONTH)
    }

    fun getEndOfMonth():Int{
        return calendar.getMaximum(Calendar.DAY_OF_MONTH)
    }

    fun getAmPm(): Int {
        return calendar.get(Calendar.AM_PM)
    }

    class TimeParam {
        var year: Int? = null
        var month: Int? = null
        var day: Int? = null
        var hour: Int? = null
        var minute: Int? = null
        var second: Int? = null
        var isAm: Boolean? = null

    }

    fun set(timeParam: TimeParam.() -> Unit) {
        val newCal = calendar.clone() as Calendar
        val param = TimeParam()
        timeParam.invoke(param)
        param.apply {
            year?.let { newCal.set(Calendar.YEAR, it) }
            month?.let { newCal.set(Calendar.MONTH, it - 1) }
            day?.let { newCal.set(Calendar.DAY_OF_MONTH, it) }
            hour?.let { newCal.set(Calendar.HOUR_OF_DAY, it) }
            minute?.let { newCal.set(Calendar.MINUTE, it) }
            second?.let { newCal.set(Calendar.SECOND, it) }
            isAm?.let {
                newCal.get(Calendar.HOUR_OF_DAY) //TODO ????????????????????????????
                newCal.set(Calendar.AM_PM, if (it) Calendar.AM else Calendar.PM)
            }
        }
        this.calendar = newCal
    }

    //utility

    fun diff(time: Time): Duration {
        return Duration.millis(toMillis() - time.toMillis())
    }

    @Suppress("UnnecessaryVariable")
    fun diffInMonth(to: Time): Int {

        val diffYear = to.getYear() - this.getYear()
        val diffMonth = diffYear * 12 + to.getMonth() - this.getMonth()
        return diffMonth
    }

    fun clone(): Time {
        return Time(calendar.clone() as Calendar)
    }

    fun format(
            format: String,
            timeDiff: Boolean = false
    ): String {

        val dateFormat = SimpleDateFormat(format, Locale.US)
        if (timeDiff) {
            dateFormat.timeZone = TimeZone.getTimeZone("GMT")
        }
        val date = calendar.time
        return dateFormat.format(date)
    }

    fun toTimestamp(): String {
        return format("yyyy-MM-dd'T'HH:mm:ss")
    }

    fun toDateTime(): String {
        return format("yyyy-MM-dd")
    }

    fun toAlarmTime(): AlarmTime {
        return AlarmTime.of(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE))
    }

    fun toCalendar(): Calendar = calendar
    fun toTimeMillis(): Long = calendar.timeInMillis
    fun toDate(): Date = calendar.time


    fun getStartOfDay(): Time {
        val newCal = calendar.clone() as Calendar

        newCal.set(Calendar.HOUR_OF_DAY, 0)
        newCal.set(Calendar.MINUTE, 0)
        newCal.set(Calendar.SECOND, 0)
        newCal.set(Calendar.MILLISECOND, 0)
        return Time(newCal)
    }

    fun getStartOfMonth(): Time {
        val newTime = getStartOfDay()
        return newTime.clone().apply { set { day = 1 } }
    }

    fun getLastMonday(): Time {
        val dayDiff = (14 + (this.calendar.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY)) % 7
        return (clone() - Duration.ofDay(dayDiff)).getStartOfDay()
    }

    fun getNextSunday(): Time {
        return getLastMonday() + Duration.ofDay(6)
    }

}

class DurationOfMonth private constructor(val month: Int) {
    companion object {
        fun month(month: Int): DurationOfMonth {
            return DurationOfMonth(month)
        }
    }
}

class Duration private constructor(val millis: Long) {

    companion object {
        @JvmStatic
        fun millis(millis: Long): Duration {
            return Duration(millis)
        }

        @JvmStatic
        fun ofSec(second: Int): Duration {
            return Duration(second * 1000L)
        }

        @JvmStatic
        fun ofMin(min: Int): Duration {
            return Duration(min * 60 * 1000L)
        }

        @JvmStatic
        fun ofHour(hour: Int): Duration {
            return Duration(hour * 60 * 60 * 1000L)
        }

        @JvmStatic
        fun ofDay(day: Int): Duration {
            return Duration(day * 24 * 60 * 60 * 1000L)
        }
    }

    operator fun plus(duration: Duration): Duration {
        return Duration(this.millis + duration.millis)
    }

    operator fun compareTo(other: Duration): Int {
        return (this.millis - other.millis).toInt()
    }

    fun sec(): Int {
        return (millis / 1000).toInt()
    }

    fun min(): Int {
        return (millis / 1000 / 60).toInt()
    }

    fun hour(): Int {
        return (millis / 1000 / 60 / 60).toInt()
    }

    fun day(): Int {
        return (millis / 1000 / 60 / 60 / 24).toInt()
    }

    fun format(format: String = "dd일 HH시 mm분"): String {
        val day = millis / 1000 / 60 / 60 / 24
        val hour = millis / 1000 / 60 / 60 % 24
        val min = millis / 1000 / 60 % 60
        val sec = millis / 1000 % 60

        return format
                .replace("dd", day.toString())
                .replace("HH", hour.toString())
                .replace("hh", (hour % 12).toString())
                .replace("mm", min.toString())
                .replace("ss", sec.toString())
    }

    override fun equals(other: Any?): Boolean {
        return if (other !is Duration) {
            false
        } else {
            this.millis == other.millis
        }
    }

    override fun hashCode(): Int {
        return this.millis.toInt()
    }

    override fun toString(): String {
        return format("dd일 HH:mm:ss")
    }

}

class AlarmTime private constructor(val hour: Int,
                                    val minute: Int,
                                    val second: Int
) {
    companion object {
        fun of(hour: Int,
               minute: Int,
               second: Int = 0): AlarmTime {
            return AlarmTime(hour, minute, second)
        }

        @JvmStatic
        fun from(time: Time): AlarmTime {
            return AlarmTime(
                    time.getHour(),
                    time.getMin(),
                    time.getSec()
            )
        }

        @JvmStatic
        fun from(timestamp: String): AlarmTime {
            if (Regex("[0-2][0-9][0-5][0-9]").matches(timestamp)) {
                val hour = timestamp.substring(0, 2).toInt()
                val minute = timestamp.substring(2, 4).toInt()
                return of(hour, minute)
            } else {
                throw InvalidParameterException()
            }
        }
    }

    fun format(): String {
        return String.format("%02d%02d", hour, minute)
    }


}