package com.billbook.data.repository

import com.billbook.data.dao.*
import com.billbook.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepository @Inject constructor(
    private val transactionDao: TransactionDao,
    private val categoryDao: CategoryDao,
    private val currencyDao: CurrencyDao
) {
    fun getAllTransactions(): Flow<List<TransactionWithCategory>> = 
        combine(
            transactionDao.getAllTransactions(),
            categoryDao.getAllCategories()
        ) { transactions, categories ->
            transactions.map { transaction ->
                TransactionWithCategory(
                    transaction = transaction,
                    category = categories.find { it.id == transaction.categoryId }
                )
            }
        }
    
    fun getTransactionsByMonth(yearMonth: String): Flow<List<TransactionWithCategory>> =
        combine(
            transactionDao.getTransactionsByMonth(yearMonth),
            categoryDao.getAllCategories()
        ) { transactions, categories ->
            transactions.map { transaction ->
                TransactionWithCategory(
                    transaction = transaction,
                    category = categories.find { it.id == transaction.categoryId }
                )
            }
        }
    
    fun getTransactionsBetween(startDate: Date, endDate: Date): Flow<List<TransactionWithCategory>> =
        combine(
            transactionDao.getTransactionsBetween(startDate, endDate),
            categoryDao.getAllCategories()
        ) { transactions, categories ->
            transactions.map { transaction ->
                TransactionWithCategory(
                    transaction = transaction,
                    category = categories.find { it.id == transaction.categoryId }
                )
            }
        }
    
    suspend fun getMonthlyStats(yearMonth: String, currency: Currency = Currency.CNY): MonthlyStats {
        val transactions = transactionDao.getTransactionsByMonth(yearMonth).first()
        val rate = currencyDao.getRateToCNY(currency) ?: 1.0
        
        val income = transactions
            .filter { it.type == TransactionType.INCOME }
            .sumOf { it.amount / rate }
        
        val expense = transactions
            .filter { it.type == TransactionType.EXPENSE }
            .sumOf { it.amount / rate }
        
        return MonthlyStats(
            yearMonth = yearMonth,
            income = income,
            expense = expense,
            balance = income - expense,
            currency = currency
        )
    }
    
    suspend fun getCategoryStats(
        type: TransactionType, 
        startDate: Date, 
        endDate: Date
    ): List<CategoryStat> {
        val categorySums = transactionDao.getCategorySumByDateRange(type, startDate, endDate)
        val categories = categoryDao.getAllCategories().first()
        
        val total = categorySums.sumOf { it.total }
        
        return categorySums.map { sum ->
            val category = categories.find { it.id == sum.categoryId }
            CategoryStat(
                category = category,
                amount = sum.total,
                percentage = if (total > 0) (sum.total / total * 100).toFloat() else 0f
            )
        }.sortedByDescending { it.amount }
    }
    
    suspend fun insertTransaction(transaction: Transaction): Long =
        transactionDao.insert(transaction)
    
    suspend fun updateTransaction(transaction: Transaction) =
        transactionDao.update(transaction)
    
    suspend fun deleteTransaction(transaction: Transaction) =
        transactionDao.delete(transaction)
    
    suspend fun getTransactionById(id: Long): TransactionWithCategory? {
        val transaction = transactionDao.getById(id) ?: return null
        val category = categoryDao.getById(transaction.categoryId)
        return TransactionWithCategory(transaction, category)
    }
}

data class TransactionWithCategory(
    val transaction: Transaction,
    val category: Category?
)

data class MonthlyStats(
    val yearMonth: String,
    val income: Double,
    val expense: Double,
    val balance: Double,
    val currency: Currency
)

data class CategoryStat(
    val category: Category?,
    val amount: Double,
    val percentage: Float
)
