package com.billbook.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

enum class TransactionType {
    INCOME, EXPENSE
}

enum class Currency {
    CNY, USD, EUR, JPY, HKD, GBP
}

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val icon: String,
    val color: Long,
    val type: TransactionType,
    val orderIndex: Int = 0,
    val isBuiltIn: Boolean = false
)

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Double,
    val currency: Currency = Currency.CNY,
    val type: TransactionType,
    val categoryId: Long,
    val note: String = "",
    val date: Date = Date(),
    val createdAt: Date = Date()
)

@Entity(tableName = "budgets")
data class Budget(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val categoryId: Long? = null, // null means total budget
    val amount: Double,
    val currency: Currency = Currency.CNY,
    val period: BudgetPeriod = BudgetPeriod.MONTHLY,
    val startDate: Date = Date(),
    val isActive: Boolean = true
)

enum class BudgetPeriod {
    WEEKLY, MONTHLY, YEARLY
}

@Entity(tableName = "currencies")
data class ExchangeRate(
    @PrimaryKey
    val currency: Currency,
    val rateToCNY: Double, // 1单位货币 = ? 人民币
    val updatedAt: Date = Date()
)
