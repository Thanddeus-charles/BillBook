package com.billbook.utils

import android.icu.text.NumberFormat
import com.billbook.data.model.Currency
import java.text.SimpleDateFormat
import java.util.*

object FormatUtils {
    private val currencySymbols = mapOf(
        Currency.CNY to "¥",
        Currency.USD to "$",
        Currency.EUR to "€",
        Currency.JPY to "¥",
        Currency.HKD to "HK$",
        Currency.GBP to "£"
    )
    
    private val currencyNames = mapOf(
        Currency.CNY to "人民币",
        Currency.USD to "美元",
        Currency.EUR to "欧元",
        Currency.JPY to "日元",
        Currency.HKD to "港币",
        Currency.GBP to "英镑"
    )
    
    fun formatMoney(amount: Double, currency: Currency = Currency.CNY): String {
        val symbol = currencySymbols[currency] ?: "¥"
        return "$symbol${String.format("%.2f", amount)}"
    }
    
    fun getCurrencySymbol(currency: Currency): String {
        return currencySymbols[currency] ?: "¥"
    }
    
    fun getCurrencyName(currency: Currency): String {
        return currencyNames[currency] ?: currency.name
    }
    
    fun formatDate(date: Date, pattern: String = "yyyy-MM-dd"): String {
        return SimpleDateFormat(pattern, Locale.getDefault()).format(date)
    }
    
    fun formatDateTime(date: Date): String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(date)
    }
    
    fun getMonthYearText(date: Date): String {
        return SimpleDateFormat("yyyy年MM月", Locale.CHINA).format(date)
    }
    
    fun getYearMonthString(date: Date): String {
        return SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(date)
    }
}

fun Date.startOfMonth(): Date {
    val calendar = Calendar.getInstance().apply { time = this@startOfMonth }
    calendar.set(Calendar.DAY_OF_MONTH, 1)
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.time
}

fun Date.endOfMonth(): Date {
    val calendar = Calendar.getInstance().apply { time = this@endOfMonth }
    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
    calendar.set(Calendar.HOUR_OF_DAY, 23)
    calendar.set(Calendar.MINUTE, 59)
    calendar.set(Calendar.SECOND, 59)
    return calendar.time
}
