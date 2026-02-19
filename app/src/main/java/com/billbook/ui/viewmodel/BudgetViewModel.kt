package com.billbook.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.billbook.data.model.Budget
import com.billbook.data.model.BudgetPeriod
import com.billbook.data.model.Currency
import com.billbook.data.repository.BudgetRepository
import com.billbook.data.repository.BudgetWithProgress
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BudgetViewModel @Inject constructor(
    private val budgetRepository: BudgetRepository
) : ViewModel() {
    
    val budgets: StateFlow<List<BudgetWithProgress>> = budgetRepository.getAllActiveBudgets()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    
    val totalBudget: StateFlow<BudgetWithProgress?> = budgetRepository.getTotalBudget()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)
    
    fun addBudget(amount: Double, currency: Currency, period: BudgetPeriod, categoryId: Long? = null) {
        viewModelScope.launch {
            val budget = Budget(
                amount = amount,
                currency = currency,
                period = period,
                categoryId = categoryId
            )
            budgetRepository.insertBudget(budget)
        }
    }
    
    fun updateBudget(budget: Budget) {
        viewModelScope.launch {
            budgetRepository.updateBudget(budget)
        }
    }
    
    fun deleteBudget(budget: Budget) {
        viewModelScope.launch {
            budgetRepository.deleteBudget(budget)
        }
    }
}
