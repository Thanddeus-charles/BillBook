package com.billbook.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.billbook.data.model.Currency
import com.billbook.data.model.TransactionType
import com.billbook.data.repository.TransactionWithCategory
import com.billbook.utils.FormatUtils
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TransactionItem(
    transactionWithCategory: TransactionWithCategory,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val transaction = transactionWithCategory.transaction
    val category = transactionWithCategory.category
    val isExpense = transaction.type == TransactionType.EXPENSE
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 分类图标
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(category?.color?.toLong() ?: 0xFF999999)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = category?.icon ?: "📦",
                    fontSize = 24.sp
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // 信息
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = category?.name ?: "未分类",
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )
                if (transaction.note.isNotBlank()) {
                    Text(
                        text = transaction.note,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault()).format(transaction.date),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // 金额
            Text(
                text = "${if (isExpense) "-" else "+"}${FormatUtils.formatMoney(transaction.amount, transaction.currency)}",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = if (isExpense) Color(0xFFE74C3C) else Color(0xFF27AE60)
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // 操作按钮
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "编辑", tint = MaterialTheme.colorScheme.primary)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "删除", tint = Color(0xFFE74C3C))
            }
        }
    }
}

@Composable
fun SummaryCard(
    income: Double,
    expense: Double,
    currency: Currency,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "本月收支",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // 收入
                Column {
                    Text(
                        text = "收入",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                    Text(
                        text = FormatUtils.formatMoney(income, currency),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF27AE60)
                    )
                }
                
                // 支出
                Column {
                    Text(
                        text = "支出",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                    Text(
                        text = FormatUtils.formatMoney(expense, currency),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE74C3C)
                    )
                }
                
                // 结余
                Column {
                    Text(
                        text = "结余",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                    Text(
                        text = FormatUtils.formatMoney(income - expense, currency),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

@Composable
fun CategorySelector(
    categories: List<com.billbook.data.model.Category>,
    selectedCategoryId: Long?,
    onCategorySelected: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        categories.chunked(4).forEach { rowCategories ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                rowCategories.forEach { category ->
                    val isSelected = category.id == selectedCategoryId
                    CategoryChip(
                        category = category,
                        isSelected = isSelected,
                        onClick = { onCategorySelected(category.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryChip(
    category: com.billbook.data.model.Category,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(8.dp)
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(
                    if (isSelected) Color(category.color)
                    else Color(category.color).copy(alpha = 0.2f)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(text = category.icon, fontSize = 24.sp)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = category.name,
            fontSize = 12.sp,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}
