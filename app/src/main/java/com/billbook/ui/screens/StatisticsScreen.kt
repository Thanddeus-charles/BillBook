package com.billbook.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.billbook.ui.components.PieChart
import com.billbook.ui.viewmodel.TransactionViewModel
import com.billbook.utils.FormatUtils
import com.billbook.utils.getMonthYearText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    onNavigateBack: () -> Unit,
    viewModel: TransactionViewModel = hiltViewModel()
) {
    val expenseCategories by viewModel.expenseCategories.collectAsState()
    val incomeCategories by viewModel.incomeCategories.collectAsState()
    val monthlyStats by viewModel.monthlyStats.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { viewModel.previousMonth() }) {
                            Icon(Icons.Default.ChevronLeft, contentDescription = "上个月")
                        }
                        Text(selectedDate.getMonthYearText())
                        IconButton(onClick = { viewModel.nextMonth() }) {
                            Icon(Icons.Default.ChevronRight, contentDescription = "下个月")
                        }
                    }
                },
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
                .verticalScroll(rememberScrollState())
        ) {
            // 月度汇总
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatItem("总收入", monthlyStats.income, MaterialTheme.colorScheme.primary)
                        StatItem("总支出", monthlyStats.expense, MaterialTheme.colorScheme.error)
                        StatItem("结余", monthlyStats.balance, MaterialTheme.colorScheme.secondary)
                    }
                }
            }
            
            // 支出/收入切换
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("支出分析") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("收入分析") }
                )
            }
            
            val data = if (selectedTab == 0) expenseCategories else incomeCategories
            
            if (data.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "暂无${if (selectedTab == 0) "支出" else "收入"}数据",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                // 饼图
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                ) {
                    PieChart(data = data, modifier = Modifier.fillMaxSize())
                }
                
                // 分类列表
                data.forEach { stat ->
                    val category = stat.category
                    if (category != null) {
                        ListItem(
                            headlineContent = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("${category.icon} ${category.name}")
                                    Spacer(modifier = Modifier.weight(1f))
                                    Text(
                                        FormatUtils.formatMoney(stat.amount, monthlyStats.currency),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            },
                            supportingContent = {
                                LinearProgressIndicator(
                                    progress = { stat.percentage / 100f },
                                    modifier = Modifier.fillMaxWidth(),
                                    color = androidx.compose.ui.graphics.Color(category.color),
                                    trackColor = androidx.compose.ui.graphics.Color(category.color).copy(alpha = 0.2f)
                                )
                                Text(
                                    "${String.format("%.1f", stat.percentage)}%",
                                    fontSize = 12.sp,
                                    modifier = Modifier.align(Alignment.End)
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: Double, color: androidx.compose.ui.graphics.Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            FormatUtils.formatMoney(value),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}
