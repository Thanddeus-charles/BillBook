package com.billbook.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.billbook.data.model.*
import com.billbook.ui.components.CategorySelector
import com.billbook.ui.viewmodel.CategoryViewModel
import com.billbook.ui.viewmodel.TransactionViewModel
import com.billbook.utils.FormatUtils
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    onNavigateBack: () -> Unit,
    viewModel: TransactionViewModel = hiltViewModel(),
    categoryViewModel: CategoryViewModel = hiltViewModel()
) {
    var selectedType by remember { mutableStateOf(TransactionType.EXPENSE) }
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf<Long?>(null) }
    var selectedDate by remember { mutableStateOf(Date()) }
    var selectedCurrency by remember { mutableStateOf(Currency.CNY) }
    
    val categories by if (selectedType == TransactionType.EXPENSE) 
        categoryViewModel.expenseCategories.collectAsState() 
    else 
        categoryViewModel.incomeCategories.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("记一笔") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // 收入/支出切换
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                FilterChip(
                    selected = selectedType == TransactionType.EXPENSE,
                    onClick = { 
                        selectedType = TransactionType.EXPENSE
                        selectedCategoryId = null
                    },
                    label = { Text("支出") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.errorContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                )
                Spacer(modifier = Modifier.width(16.dp))
                FilterChip(
                    selected = selectedType == TransactionType.INCOME,
                    onClick = { 
                        selectedType = TransactionType.INCOME
                        selectedCategoryId = null
                    },
                    label = { Text("收入") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 金额输入
            OutlinedTextField(
                value = amount,
                onValueChange = { if (it.matches(Regex("^\\d*\\.?\\d{0,2}$"))) amount = it },
                label = { Text("金额") },
                prefix = { Text(FormatUtils.getCurrencySymbol(selectedCurrency)) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal
                )
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 货币选择
            Text("货币", fontWeight = FontWeight.Medium)
            Row {
                Currency.entries.forEach { currency ->
                    FilterChip(
                        selected = selectedCurrency == currency,
                        onClick = { selectedCurrency = currency },
                        label = { Text(currency.name) },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 分类选择
            Text("分类", fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(8.dp))
            CategorySelector(
                categories = categories,
                selectedCategoryId = selectedCategoryId,
                onCategorySelected = { selectedCategoryId = it }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 备注
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("备注") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            // 保存按钮
            Button(
                onClick = {
                    selectedCategoryId?.let { categoryId ->
                        val transaction = Transaction(
                            amount = amount.toDoubleOrNull() ?: 0.0,
                            currency = selectedCurrency,
                            type = selectedType,
                            categoryId = categoryId,
                            note = note,
                            date = selectedDate
                        )
                        viewModel.addTransaction(transaction)
                        onNavigateBack()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = amount.isNotBlank() && selectedCategoryId != null
            ) {
                Text("保存", fontSize = 18.sp, modifier = Modifier.padding(vertical = 8.dp))
            }
        }
    }
}
