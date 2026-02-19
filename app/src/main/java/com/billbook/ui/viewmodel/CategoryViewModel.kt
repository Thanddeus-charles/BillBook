package com.billbook.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.billbook.data.model.Category
import com.billbook.data.model.TransactionType
import com.billbook.data.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository
) : ViewModel() {
    
    val allCategories: StateFlow<List<Category>> = categoryRepository.getAllCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    
    val expenseCategories: StateFlow<List<Category>> = categoryRepository.getCategoriesByType(TransactionType.EXPENSE)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    
    val incomeCategories: StateFlow<List<Category>> = categoryRepository.getCategoriesByType(TransactionType.INCOME)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    
    fun addCategory(name: String, icon: String, color: Long, type: TransactionType) {
        viewModelScope.launch {
            val maxOrder = allCategories.value.filter { it.type == type }.maxOfOrNull { it.orderIndex } ?: -1
            val category = Category(
                name = name,
                icon = icon,
                color = color,
                type = type,
                orderIndex = maxOrder + 1
            )
            categoryRepository.insertCategory(category)
        }
    }
    
    fun updateCategory(category: Category) {
        viewModelScope.launch {
            categoryRepository.updateCategory(category)
        }
    }
    
    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            categoryRepository.deleteCategory(category)
        }
    }
}
