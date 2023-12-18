package vu.pham.todotaskapp.utils

import android.annotation.SuppressLint
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {

    @SuppressLint("SimpleDateFormat")
    fun convertDateFormat(date: Date, pattern: String):String{
        val simpleDateFormat = SimpleDateFormat(pattern, Locale.ENGLISH)
        return simpleDateFormat.format(date)
    }
    @SuppressLint("SimpleDateFormat")
    fun getDates(startDate: String, endDate: String): ArrayList<Date> {
        val dates = ArrayList<Date>()
        val df = SimpleDateFormat("yyyy-MM-dd")
        var date1: Date? = null
        var date2: Date? = null
        try {
            date1 = df.parse(startDate)
            date2 = df.parse(endDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        val cal1 = date1?.let {
            val cal1 = Calendar.getInstance()
            cal1.time = it
            cal1.set(Calendar.HOUR_OF_DAY, 0)
            cal1.set(Calendar.MINUTE, 0)
            cal1.set(Calendar.SECOND, 0)
            cal1
        }

        val cal2 = date2?.let {
            val cal2 = Calendar.getInstance()
            cal2.time = it
            cal2.set(Calendar.HOUR_OF_DAY, 0)
            cal2.set(Calendar.MINUTE, 0)
            cal2.set(Calendar.SECOND, 0)
            cal2
        }

        cal1?.let {
            while (!cal1.after(cal2)) {
                dates.add(cal1.time)
                cal1.add(Calendar.DATE, 1)
            }
        }
        return dates
    }
}