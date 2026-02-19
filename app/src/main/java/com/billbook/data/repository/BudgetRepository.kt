package com.billbook.data.repository

import com.billbook.data.dao.BudgetDao
import com.billbook.data.dao.TransactionDao
import com.billbook.data.model.Budget
import com.billbook.data.model.BudgetPeriod
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BudgetRepository @Inject constructor(
    private val budgetDao: BudgetDao,
    private val transactionDao: TransactionDao
) {
    fun getAllActiveBudgets(): Flow<List<BudgetWithProgress>> =
        budgetDao.getAllActiveBudgets().map { budgets ->
            budgets.map { budget ->
                val startDate = getPeriodStartDate(budget.period)
                val spent = if (budget.categoryId != null) {
                    budgetDao.getSpentAmountForCategory(budget.categoryId, startDate)
                } else {
                    // Total budget - sum all expenses in period
                    val transactions = transactionDao.getTransactionsBetween(
                        startDate, 
                        Date()
                    ).first().filter { it.type == com.billbook.data.model.TransactionType.EXPENSE }
                    transactions.sumOf { it.amount }
                }
                
                BudgetWithProgress(
                    budget = budget,
                    spent = spent,
                    remaining = budget.amount - spent,
                    percentage = if (budget.amount > 0) (spent / budget.amount * 100).toFloat() else 0f
                )
            }
        }
    
    fun getTotalBudget(): Flow<BudgetWithProgress?> =
        budgetDao.getTotalBudget().map { budget ->
            budget?.let {
                val startDate = getPeriodStartDate(it.period)
                val transactions = transactionDao.getTransactionsBetween(
                    startDate,
                    Date()
                ).first().filter { t -> t.type == com.billbook.data.model.TransactionType.EXPENSE }
                val spent = transactions.sumOf { t -> t.amount }
                
                BudgetWithProgress(
                    budget = it,
                    spent = spent,
                    remaining = it.amount - spent,
                    percentage = if (it.amount > 0) (spent / it.amount * 100).toFloat() else 0f
                )
            }
        }
    
    suspend fun insertBudget(budget: Budget): Long = budgetDao.insert(budget)
    
    suspend fun updateBudget(budget: Budget) = budgetDao.update(budget)
    
    suspend fun deleteBudget(budget: Budget) = budgetDao.delete(budget)
    
    private fun getPeriodStartDate(period: BudgetPeriod): Date {
        val calendar = Calendar.getInstance()
        return when (period) {
            BudgetPeriod.WEEKLY -> {
                calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
                calendar.time
            }
            BudgetPeriod.MONTHLY -> {
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.time
            }
            BudgetPeriod.YEARLY -> {
                calendar.set(Calendar.DAY_OF_YEAR, 1)
                calendar.time
            }
        }
    }
}

data class BudgetWithProgress(
    val budget: Budget,
    val spent: Double,
    val remaining: Double,
    val percentage: Float
)
