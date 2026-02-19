package com.billbook.data.database

import android.content.Context
import com.billbook.data.model.Category
import com.billbook.data.model.Currency
import com.billbook.data.model.ExchangeRate
import com.billbook.data.model.TransactionType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object DatabaseSeeder {
    
    private val defaultCategories = listOf(
        // 支出分类
        Category(name = "餐饮", icon = "🍔", color = 0xFFFF6B6B, type = TransactionType.EXPENSE, orderIndex = 0, isBuiltIn = true),
        Category(name = "交通", icon = "🚗", color = 0xFF4ECDC4, type = TransactionType.EXPENSE, orderIndex = 1, isBuiltIn = true),
        Category(name = "购物", icon = "🛍️", color = 0xFF45B7D1, type = TransactionType.EXPENSE, orderIndex = 2, isBuiltIn = true),
        Category(name = "娱乐", icon = "🎮", color = 0xFF96CEB4, type = TransactionType.EXPENSE, orderIndex = 3, isBuiltIn = true),
        Category(name = "居住", icon = "🏠", color = 0xFFDDA0DD, type = TransactionType.EXPENSE, orderIndex = 4, isBuiltIn = true),
        Category(name = "医疗", icon = "🏥", color = 0xFFFF9999, type = TransactionType.EXPENSE, orderIndex = 5, isBuiltIn = true),
        Category(name = "教育", icon = "📚", color = 0xFF98D8C8, type = TransactionType.EXPENSE, orderIndex = 6, isBuiltIn = true),
        Category(name = "其他支出", icon = "📝", color = 0xFFBDC3C7, type = TransactionType.EXPENSE, orderIndex = 7, isBuiltIn = true),
        
        // 收入分类
        Category(name = "工资", icon = "💰", color = 0xFF2ECC71, type = TransactionType.INCOME, orderIndex = 0, isBuiltIn = true),
        Category(name = "奖金", icon = "🎁", color = 0xFFF39C12, type = TransactionType.INCOME, orderIndex = 1, isBuiltIn = true),
        Category(name = "投资", icon = "📈", color = 0xFF3498DB, type = TransactionType.INCOME, orderIndex = 2, isBuiltIn = true),
        Category(name = "兼职", icon = "💼", color = 0xFF9B59B6, type = TransactionType.INCOME, orderIndex = 3, isBuiltIn = true),
        Category(name = "其他收入", icon = "🎯", color = 0xFF1ABC9C, type = TransactionType.INCOME, orderIndex = 4, isBuiltIn = true)
    )
    
    private val defaultExchangeRates = listOf(
        ExchangeRate(currency = Currency.CNY, rateToCNY = 1.0),
        ExchangeRate(currency = Currency.USD, rateToCNY = 7.2),
        ExchangeRate(currency = Currency.EUR, rateToCNY = 7.8),
        ExchangeRate(currency = Currency.JPY, rateToCNY = 0.048),
        ExchangeRate(currency = Currency.HKD, rateToCNY = 0.92),
        ExchangeRate(currency = Currency.GBP, rateToCNY = 9.1)
    )
    
    suspend fun seedDatabase(context: Context) = withContext(Dispatchers.IO) {
        val db = AppDatabase.getDatabase(context)
        
        // 初始化分类
        if (db.categoryDao().getCount() == 0) {
            db.categoryDao().insertAll(defaultCategories)
        }
        
        // 初始化汇率
        db.currencyDao().insertAll(defaultExchangeRates)
    }
}
