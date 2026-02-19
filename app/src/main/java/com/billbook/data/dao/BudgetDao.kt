package com.billbook.data.dao

import androidx.room.*
import com.billbook.data.model.Budget
import com.billbook.data.model.BudgetPeriod
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface BudgetDao {
    @Query("SELECT * FROM budgets WHERE isActive = 1")
    fun getAllActiveBudgets(): Flow<List<Budget>>
    
    @Query("SELECT * FROM budgets WHERE categoryId IS NULL AND isActive = 1 LIMIT 1")
    fun getTotalBudget(): Flow<Budget?>
    
    @Query("SELECT * FROM budgets WHERE categoryId = :categoryId AND isActive = 1")
    fun getBudgetByCategory(categoryId: Long): Flow<Budget?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(budget: Budget): Long
    
    @Update
    suspend fun update(budget: Budget)
    
    @Delete
    suspend fun delete(budget: Budget)
    
    @Query("""
        SELECT COALESCE(SUM(t.amount), 0) FROM transactions t
        WHERE t.categoryId = :categoryId 
        AND t.type = 'EXPENSE'
        AND t.date >= :startDate
    """)
    suspend fun getSpentAmountForCategory(categoryId: Long, startDate: Date): Double
}
