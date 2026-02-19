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
import com.billbook.ui.components.*
import com.billbook.ui.viewmodel.TransactionViewModel
import com.billbook.utils.FormatUtils
import com.billbook.utils.getMonthYearText
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAddClick: () -> Unit,
    onNavigateToStats: () -> Unit,
    onNavigateToBudget: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: TransactionViewModel = hiltViewModel()
) {
    val transactions by viewModel.transactions.collectAsState()
    val monthlyStats by viewModel.monthlyStats.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val scope = rememberCoroutineScope()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { viewModel.previousMonth() }) {
                            Icon(Icons.Default.ChevronLeft, contentDescription = "上个月")
                        }
                        Text(
                            text = selectedDate.getMonthYearText(),
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = { viewModel.nextMonth() }) {
                            Icon(Icons.Default.ChevronRight, contentDescription = "下个月")
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "设置")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(Icons.Default.Add, contentDescription = "记一笔")
            }
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "首页") },
                    label = { Text("首页") },
                    selected = true,
                    onClick = { }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.PieChart, contentDescription = "统计") },
                    label = { Text("统计") },
                    selected = false,
                    onClick = onNavigateToStats
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = "预算") },
                    label = { Text("预算") },
                    selected = false,
                    onClick = onNavigateToBudget
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // 月度汇总卡片
            SummaryCard(
                income = monthlyStats.income,
                expense = monthlyStats.expense,
                currency = monthlyStats.currency
            )
            
            // 交易列表
            if (transactions.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.ReceiptLong,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "本月暂无记录",
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                        Text(
                            text = "点击右下角 + 开始记账",
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    }
                }
            } else {
                LazyColumn {
                    items(transactions) { item ->
                        TransactionItem(
                            transactionWithCategory = item,
                            onEdit = { /* TODO: 编辑 */ },
                            onDelete = { 
                                scope.launch {
                                    viewModel.deleteTransaction(item.transaction)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
