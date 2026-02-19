package com.billbook.data.dao

import androidx.room.*
import com.billbook.data.model.Currency
import com.billbook.data.model.ExchangeRate
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrencyDao {
    @Query("SELECT * FROM currencies")
    fun getAllRates(): Flow<List<ExchangeRate>>
    
    @Query("SELECT * FROM currencies WHERE currency = :currency")
    suspend fun getRate(currency: Currency): ExchangeRate?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(rate: ExchangeRate)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(rates: List<ExchangeRate>)
    
    @Query("SELECT rateToCNY FROM currencies WHERE currency = :currency")
    suspend fun getRateToCNY(currency: Currency): Double?
}
