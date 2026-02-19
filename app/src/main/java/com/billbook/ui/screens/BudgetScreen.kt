package com.billbook.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.billbook.data.model.BudgetPeriod
import com.billbook.data.model.Currency
import com.billbook.data.repository.BudgetWithProgress
import com.billbook.ui.viewmodel.BudgetViewModel
import com.billbook.ui.viewmodel.CategoryViewModel
import com.billbook.utils.FormatUtils

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun BudgetScreen(
    onNavigateBack: () -> Unit,
    viewModel: BudgetViewModel = hiltViewModel()
) {
    val budgets by viewModel.budgets.collectAsState()
    val totalBudget by viewModel.totalBudget.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("预算管理") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "添加预算")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // 总预算卡片
            totalBudget?.let { budget ->
                TotalBudgetCard(budget)
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            Text(
                "分类预算",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            if (budgets.filter { it.budget.categoryId != null }.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "暂无分类预算",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn {
                    items(budgets.filter { it.budget.categoryId != null }) { budget ->
                        BudgetItem(budget)
                    }
                }
            }
        }
    }
    
    if (showAddDialog) {
        AddBudgetDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { amount, currency, period, categoryId ->
                viewModel.addBudget(amount, currency, period, categoryId)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun TotalBudgetCard(budget: BudgetWithProgress) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "本月总预算",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                FormatUtils.formatMoney(budget.budget.amount, budget.budget.currency),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(16.dp))
            LinearProgressIndicator(
                progress = { budget.percentage.coerceIn(0f, 100f) / 100f },
                modifier = Modifier.fillMaxWidth(),
                color = when {
                    budget.percentage >= 100f -> MaterialTheme.colorScheme.error
                    budget.percentage >= 80f -> MaterialTheme.colorScheme.tertiary
                    else -> MaterialTheme.colorScheme.primary
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "已用: ${FormatUtils.formatMoney(budget.spent, budget.budget.currency)}",
                    fontSize = 14.sp
                )
                Text(
                    "剩余: ${FormatUtils.formatMoney(budget.remaining, budget.budget.currency)}",
                    fontSize = 14.sp
                )
            }
            Text(
                "${String.format("%.1f", budget.percentage)}%",
                fontSize = 14.sp,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

@Composable
private fun BudgetItem(budget: BudgetWithProgress) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "分类预算",
                    fontWeight = FontWeight.Medium
                )
                Text(
                    FormatUtils.formatMoney(budget.budget.amount, budget.budget.currency),
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { budget.percentage.coerceIn(0f, 100f) / 100f },
                modifier = Modifier.fillMaxWidth(),
                color = when {
                    budget.percentage >= 100f -> MaterialTheme.colorScheme.error
                    budget.percentage >= 80f -> MaterialTheme.colorScheme.tertiary
                    else -> MaterialTheme.colorScheme.primary
                }
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "已用 ${FormatUtils.formatMoney(budget.spent)} · 剩余 ${FormatUtils.formatMoney(budget.remaining)}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddBudgetDialog(
    onDismiss: () -> Unit,
    onConfirm: (Double, Currency, BudgetPeriod, Long?) -> Unit,
    categoryViewModel: CategoryViewModel = hiltViewModel()
) {
    var amount by remember { mutableStateOf("") }
    var selectedCurrency by remember { mutableStateOf(Currency.CNY) }
    var selectedPeriod by remember { mutableStateOf(BudgetPeriod.MONTHLY) }
    val expenseCategories by categoryViewModel.expenseCategories.collectAsState()
    var selectedCategoryId by remember { mutableStateOf<Long?>(null) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("添加预算") },
        text = {
            Column {
                OutlinedTextField(
                    value = amount,
                    onValueChange = { if (it.matches(Regex("^\\d*\\.?\\d{0,2}$"))) amount = it },
                    label = { Text("预算金额") },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal
                    )
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text("周期", fontWeight = FontWeight.Medium)
                Row {
                    BudgetPeriod.entries.forEach { period ->
                        FilterChip(
                            selected = selectedPeriod == period,
                            onClick = { selectedPeriod = period },
                            label = { 
                                Text(when(period) {
                                    BudgetPeriod.WEEKLY -> "每周"
                                    BudgetPeriod.MONTHLY -> "每月"
                                    BudgetPeriod.YEARLY -> "每年"
                                })
                            },
                            modifier = Modifier.padding(end = 4.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text("适用分类（不选为总预算）", fontWeight = FontWeight.Medium)
                FlowRow {
                    expenseCategories.forEach { category ->
                        FilterChip(
                            selected = selectedCategoryId == category.id,
                            onClick = { 
                                selectedCategoryId = if (selectedCategoryId == category.id) null else category.id 
                            },
                            label = { Text("${category.icon} ${category.name}") },
                            modifier = Modifier.padding(end = 4.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    amount.toDoubleOrNull()?.let {
                        onConfirm(it, selectedCurrency, selectedPeriod, selectedCategoryId)
                    }
                },
                enabled = amount.isNotBlank()
            ) {
                Text("确定")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}
