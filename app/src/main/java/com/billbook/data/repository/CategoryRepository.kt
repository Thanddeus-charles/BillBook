package com.billbook.data.repository

import com.billbook.data.dao.CategoryDao
import com.billbook.data.model.Category
import com.billbook.data.model.TransactionType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepository @Inject constructor(
    private val categoryDao: CategoryDao
) {
    fun getAllCategories(): Flow<List<Category>> = categoryDao.getAllCategories()
    
    fun getCategoriesByType(type: TransactionType): Flow<List<Category>> = 
        categoryDao.getCategoriesByType(type)
    
    suspend fun insertCategory(category: Category): Long = categoryDao.insert(category)
    
    suspend fun updateCategory(category: Category) = categoryDao.update(category)
    
    suspend fun deleteCategory(category: Category) = categoryDao.delete(category)
    
    suspend fun getCategoryById(id: Long): Category? = categoryDao.getById(id)
}
