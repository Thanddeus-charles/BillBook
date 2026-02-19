package com.billbook.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.billbook.data.dao.*
import com.billbook.data.model.*
import java.util.Date

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
    
    @TypeConverter
    fun fromTransactionType(value: String): TransactionType {
        return TransactionType.valueOf(value)
    }
    
    @TypeConverter
    fun transactionTypeToString(type: TransactionType): String {
        return type.name
    }
    
    @TypeConverter
    fun fromCurrency(value: String): Currency {
        return Currency.valueOf(value)
    }
    
    @TypeConverter
    fun currencyToString(currency: Currency): String {
        return currency.name
    }
    
    @TypeConverter
    fun fromBudgetPeriod(value: String): BudgetPeriod {
        return BudgetPeriod.valueOf(value)
    }
    
    @TypeConverter
    fun budgetPeriodToString(period: BudgetPeriod): String {
        return period.name
    }
}

@Database(
    entities = [
        Transaction::class,
        Category::class,
        Budget::class,
        ExchangeRate::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun budgetDao(): BudgetDao
    abstract fun currencyDao(): CurrencyDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "billbook_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
