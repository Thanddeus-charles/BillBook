package com.billbook.data.dao

import androidx.room.*
import com.billbook.data.model.*
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<Transaction>>
    
    @Query("SELECT * FROM transactions WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getTransactionsBetween(startDate: Date, endDate: Date): Flow<List<Transaction>>
    
    @Query("SELECT * FROM transactions WHERE type = :type ORDER BY date DESC")
    fun getTransactionsByType(type: TransactionType): Flow<List<Transaction>>
    
    @Query("SELECT * FROM transactions WHERE categoryId = :categoryId ORDER BY date DESC")
    fun getTransactionsByCategory(categoryId: Long): Flow<List<Transaction>>
    
    @Query("""
        SELECT * FROM transactions 
        WHERE strftime('%Y-%m', date / 1000, 'unixepoch') = :yearMonth 
        ORDER BY date DESC
    """)
    fun getTransactionsByMonth(yearMonth: String): Flow<List<Transaction>>
    
    @Query("""
        SELECT SUM(amount) FROM transactions 
        WHERE type = :type AND date BETWEEN :startDate AND :endDate
    """)
    suspend fun getSumByTypeAndDateRange(type: TransactionType, startDate: Date, endDate: Date): Double?
    
    @Query("""
        SELECT categoryId, SUM(amount) as total FROM transactions 
        WHERE type = :type AND date BETWEEN :startDate AND :endDate
        GROUP BY categoryId
    """)
    suspend fun getCategorySumByDateRange(
        type: TransactionType, 
        startDate: Date, 
        endDate: Date
    ): List<CategorySum>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: Transaction): Long
    
    @Update
    suspend fun update(transaction: Transaction)
    
    @Delete
    suspend fun delete(transaction: Transaction)
    
    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getById(id: Long): Transaction?
}

data class CategorySum(
    val categoryId: Long,
    val total: Double
)
