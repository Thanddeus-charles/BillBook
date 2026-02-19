package com.billbook.data.dao

import androidx.room.*
import com.billbook.data.model.Category
import com.billbook.data.model.TransactionType
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories ORDER BY orderIndex")
    fun getAllCategories(): Flow<List<Category>>
    
    @Query("SELECT * FROM categories WHERE type = :type ORDER BY orderIndex")
    fun getCategoriesByType(type: TransactionType): Flow<List<Category>>
    
    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getById(id: Long): Category?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: Category): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<Category>)
    
    @Update
    suspend fun update(category: Category)
    
    @Delete
    suspend fun delete(category: Category)
    
    @Query("SELECT COUNT(*) FROM categories")
    suspend fun getCount(): Int
}
