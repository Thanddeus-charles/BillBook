package com.billbook.export

import android.content.Context
import android.net.Uri
import com.billbook.data.database.AppDatabase
import com.billbook.data.model.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.*

data class BackupData(
    val version: Int = 1,
    val exportDate: Date = Date(),
    val transactions: List<Transaction>,
    val categories: List<Category>,
    val budgets: List<Budget>,
    val exchangeRates: List<ExchangeRate>
)

class DataExporter(private val context: Context) {
    private val gson: Gson = GsonBuilder()
        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
        .setPrettyPrinting()
        .create()
    
    suspend fun exportToJson(uri: Uri): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val db = AppDatabase.getDatabase(context)
            
            val backupData = BackupData(
                transactions = db.transactionDao().getAllTransactions().first(),
                categories = db.categoryDao().getAllCategories().first(),
                budgets = db.budgetDao().getAllActiveBudgets().first(),
                exchangeRates = db.currencyDao().getAllRates().first()
            )
            
            val json = gson.toJson(backupData)
            
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                OutputStreamWriter(outputStream).use { writer ->
                    writer.write(json)
                }
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun exportToCsv(uri: Uri): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val db = AppDatabase.getDatabase(context)
            val transactions = db.transactionDao().getAllTransactions().first()
            val categories = db.categoryDao().getAllCategories().first()
            
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            
            val csvBuilder = StringBuilder()
            csvBuilder.appendLine("日期,类型,分类,金额,货币,备注")
            
            transactions.forEach { transaction ->
                val categoryName = categories.find { it.id == transaction.categoryId }?.name ?: "未知"
                val typeText = if (transaction.type == TransactionType.INCOME) "收入" else "支出"
                csvBuilder.appendLine(
                    "${dateFormat.format(transaction.date)}," +
                    "$typeText,$categoryName,${transaction.amount},${transaction.currency},${transaction.note}"
                )
            }
            
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                OutputStreamWriter(outputStream).use { writer ->
                    writer.write(csvBuilder.toString())
                }
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun importFromJson(uri: Uri): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val json = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                inputStream.bufferedReader().use { it.readText() }
            } ?: return@withContext Result.failure(Exception("无法读取文件"))
            
            val backupData = gson.fromJson(json, BackupData::class.java)
            val db = AppDatabase.getDatabase(context)
            
            // 导入分类
            backupData.categories.forEach { category ->
                db.categoryDao().insert(category)
            }
            
            // 导入交易记录
            backupData.transactions.forEach { transaction ->
                db.transactionDao().insert(transaction)
            }
            
            // 导入预算
            backupData.budgets.forEach { budget ->
                db.budgetDao().insert(budget)
            }
            
            // 导入汇率
            backupData.exchangeRates.forEach { rate ->
                db.currencyDao().insert(rate)
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun generateBackupFileName(): String {
        val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
        return "billbook_backup_${dateFormat.format(Date())}.json"
    }
}
