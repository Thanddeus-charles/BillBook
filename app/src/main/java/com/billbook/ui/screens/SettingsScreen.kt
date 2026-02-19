package com.billbook.ui.screens

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.billbook.data.model.Currency
import com.billbook.export.DataExporter
import com.billbook.ui.viewmodel.TransactionViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: TransactionViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dataExporter = remember { DataExporter(context) }
    var showMessage by remember { mutableStateOf<String?>(null) }
    var selectedCurrency by remember { mutableStateOf(Currency.CNY) }
    
    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let {
            scope.launch {
                dataExporter.exportToJson(it)
                    .onSuccess { showMessage = "备份成功" }
                    .onFailure { showMessage = "备份失败: ${it.message}" }
            }
        }
    }
    
    val csvExportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        uri?.let {
            scope.launch {
                dataExporter.exportToCsv(it)
                    .onSuccess { showMessage = "导出成功" }
                    .onFailure { showMessage = "导出失败: ${it.message}" }
            }
        }
    }
    
    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            scope.launch {
                dataExporter.importFromJson(it)
                    .onSuccess { showMessage = "导入成功" }
                    .onFailure { showMessage = "导入失败: ${it.message}" }
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("设置") },
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
            // 货币设置
            SettingsSection(title = "货币设置") {
                ListItem(
                    headlineContent = { Text("默认货币") },
                    supportingContent = { Text(selectedCurrency.name) },
                    trailingContent = {
                        var expanded by remember { mutableStateOf(false) }
                        Box {
                            TextButton(onClick = { expanded = true }) {
                                Text("更改")
                            }
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                Currency.entries.forEach { currency ->
                                    DropdownMenuItem(
                                        text = { Text("${currency.name} (${currency.name})") },
                                        onClick = {
                                            selectedCurrency = currency
                                            viewModel.selectCurrency(currency)
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                )
            }
            
            // 数据管理
            SettingsSection(title = "数据管理") {
                ListItem(
                    headlineContent = { Text("备份数据") },
                    supportingContent = { Text("导出为JSON格式备份") },
                    leadingContent = { Icon(Icons.Default.Backup, contentDescription = null) },
                    modifier = Modifier.clickable {
                        exportLauncher.launch(dataExporter.generateBackupFileName())
                    }
                )
                
                ListItem(
                    headlineContent = { Text("导出CSV") },
                    supportingContent = { Text("导出为Excel可打开的CSV格式") },
                    leadingContent = { Icon(Icons.Default.TableChart, contentDescription = null) },
                    modifier = Modifier.clickable {
                        csvExportLauncher.launch("billbook_export.csv")
                    }
                )
                
                ListItem(
                    headlineContent = { Text("恢复数据") },
                    supportingContent = { Text("从备份文件恢复") },
                    leadingContent = { Icon(Icons.Default.Restore, contentDescription = null) },
                    modifier = Modifier.clickable {
                        importLauncher.launch(arrayOf("application/json", "*/*"))
                    }
                )
            }
            
            // 关于
            SettingsSection(title = "关于") {
                ListItem(
                    headlineContent = { Text("版本") },
                    trailingContent = { Text("1.0.0") }
                )
                ListItem(
                    headlineContent = { Text("BillBook 记账") },
                    supportingContent = { Text("简单好用的个人记账应用") }
                )
            }
        }
    }
    
    showMessage?.let { message ->
        LaunchedEffect(message) {
            kotlinx.coroutines.delay(2000)
            showMessage = null
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
        ) {
            Column {
                content()
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}
