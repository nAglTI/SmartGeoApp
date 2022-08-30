package com.nagl.smartgeoapp.api

import android.util.Log
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

/**
 * Обертка для работы со временем
 */
object TimeApi {
    private const val TAG = "DBInf"

    //возвращает текущее количество миллисекунд
    val sysdateLong: Long
        get() {
            val sysdate = Date()
            return sysdate.time
        }

    //возваращает дату в формате YMDHM
    fun getMillisecondsStringDateYMDHM(date: String?): Long {
        val formatForDateNow = SimpleDateFormat("yyyy-MM-dd HH:mm")
        formatForDateNow.timeZone = TimeZone.getTimeZone("Europe/Moscow")
        try {
            val orderDate = formatForDateNow.parse(date)
            return orderDate.time + 3600000 * 3
        } catch (e: Exception) {
            Log.d(TAG, "getMillisecondsStringDate error: $e")
        }
        return 0L
    }

    //возваращает дату в формате YMD
    fun getMillisecondsStringDateYMD(date: String?): Long {
        val formatForDateNow = SimpleDateFormat("yyyy-MM-dd")
        formatForDateNow.timeZone = TimeZone.getTimeZone("Europe/Moscow")
        try {
            val orderDate = formatForDateNow.parse(date)
            return orderDate.time + 3600000 * 3
        } catch (e: Exception) {
            Log.d(TAG, "getMillisecondsStringDate error: $e")
        }
        return 0L
    }

    //возваращает дату в формате YMDHMS
    fun getMillisecondsStringDateYMDHMS(date: String?): Long {
        val formatForDateNow = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        formatForDateNow.timeZone = TimeZone.getTimeZone("Europe/Moscow")
        try {
            val orderDate = formatForDateNow.parse(date)
            return orderDate.time + 3600000 * 3
        } catch (e: Exception) {
            Log.d(TAG, "getMillisecondsStringDate error: $e")
        }
        return 0L
    }

    //возвращает время в формате yyyy-MM-dd HH:mm:ss
    fun getDateInFormatYMDHMS(date: Date?): String {
        val formatForDateNow = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        formatForDateNow.timeZone = TimeZone.getTimeZone("Europe/Moscow")
        return formatForDateNow.format(date)
    }

    fun getDateInFormatYMDHMSWithDelim(date: Date?): String {
        val formatForDateNow = SimpleDateFormat("yyyy-MM-dd HH-mm-ss")
        formatForDateNow.timeZone = TimeZone.getTimeZone("Europe/Moscow")
        return formatForDateNow.format(date)
    }

    //возвращает время в формате yyyy-MM-dd HH:mm:ss
    fun getUTSDateInFormatYMDHMS(date: Date?): String {
        val formatForDateNow = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        formatForDateNow.timeZone = TimeZone.getTimeZone("UTC")
        return formatForDateNow.format(date)
    }

    //возвращает время в формате yyyy-MM-dd HH:mm
    fun getDateInFormatYMDHM(date: Date?): String {
        val formatForDateNow = SimpleDateFormat("yyyy-MM-dd HH:mm")
        formatForDateNow.timeZone = TimeZone.getTimeZone("Europe/Moscow")
        return formatForDateNow.format(date)
    }

    //возвращает время в формате yyyy-MMM-dd HH:mm
    fun getDateInFormatYMMMDHM(date: Date?): String {
        val formatForDateNow = SimpleDateFormat("dd-MMM-yy HH:mm", Locale.ENGLISH)
        formatForDateNow.timeZone = TimeZone.getTimeZone("Europe/Moscow")
        return formatForDateNow.format(date)
    }

    //возвращает время в формате yyyy-MM-dd
    fun getDateInFormatYMD(date: Date?): String {
        val formatForDateNow = SimpleDateFormat("yyyy-MM-dd")
        formatForDateNow.timeZone = TimeZone.getTimeZone("Europe/Moscow")
        return formatForDateNow.format(date)
    }

    //возвращает время в формате S
    fun getDateInFormatS(date: Date?): String {
        val formatForDateNow = SimpleDateFormat("ss")
        formatForDateNow.timeZone = TimeZone.getTimeZone("Europe/Moscow")
        return formatForDateNow.format(date)
    }
}