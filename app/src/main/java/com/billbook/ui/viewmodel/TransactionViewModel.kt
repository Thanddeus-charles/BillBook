package com.billbook.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.billbook.data.model.*
import com.billbook.data.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {
    
    private val _selectedDate = MutableStateFlow(Date())
    val selectedDate: StateFlow<Date> = _selectedDate.asStateFlow()
    
    private val _selectedCurrency = MutableStateFlow(Currency.CNY)
    val selectedCurrency: StateFlow<Currency> = _selectedCurrency.asStateFlow()
    
    val transactions: StateFlow<List<TransactionWithCategory>> = selectedDate
        .flatMapLatest { date ->
            val yearMonth = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(date)
            transactionRepository.getTransactionsByMonth(yearMonth)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    
    val monthlyStats: StateFlow<MonthlyStats> = selectedDate
        .flatMapLatest { date ->
            val yearMonth = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(date)
            flow { 
                emit(transactionRepository.getMonthlyStats(yearMonth, _selectedCurrency.value))
            }
        }
        .stateIn(
            viewModelScope, 
            SharingStarted.WhileSubscribed(),
            MonthlyStats("", 0.0, 0.0, 0.0, Currency.CNY)
        )
    
    val expenseCategories: StateFlow<List<CategoryStat>> = selectedDate
        .flatMapLatest { date ->
            flow {
                val calendar = Calendar.getInstance().apply { time = date }
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                val startDate = calendar.time
                calendar.add(Calendar.MONTH, 1)
                calendar.add(Calendar.DAY_OF_MONTH, -1)
                val endDate = calendar.time
                
                emit(transactionRepository.getCategoryStats(TransactionType.EXPENSE, startDate, endDate))
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    
    val incomeCategories: StateFlow<List<CategoryStat>> = selectedDate
        .flatMapLatest { date ->
            flow {
                val calendar = Calendar.getInstance().apply { time = date }
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                val startDate = calendar.time
                calendar.add(Calendar.MONTH, 1)
                calendar.add(Calendar.DAY_OF_MONTH, -1)
                val endDate = calendar.time
                
                emit(transactionRepository.getCategoryStats(TransactionType.INCOME, startDate, endDate))
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    
    fun selectDate(date: Date) {
        _selectedDate.value = date
    }
    
    fun selectCurrency(currency: Currency) {
        _selectedCurrency.value = currency
    }
    
    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionRepository.insertTransaction(transaction)
        }
    }
    
    fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionRepository.updateTransaction(transaction)
        }
    }
    
    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionRepository.deleteTransaction(transaction)
        }
    }
    
    fun previousMonth() {
        val calendar = Calendar.getInstance().apply { time = _selectedDate.value }
        calendar.add(Calendar.MONTH, -1)
        _selectedDate.value = calendar.time
    }
    
    fun nextMonth() {
        val calendar = Calendar.getInstance().apply { time = _selectedDate.value }
        calendar.add(Calendar.MONTH, 1)
        _selectedDate.value = calendar.time
    }
}
