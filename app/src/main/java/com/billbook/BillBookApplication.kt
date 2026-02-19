package com.billbook

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.billbook.data.database.DatabaseSeeder
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class BillBookApplication : Application() {
    
    private val applicationScope = CoroutineScope(Dispatchers.Default)
    
    override fun onCreate() {
        super.onCreate()
        
        // 初始化数据库数据
        applicationScope.launch {
            DatabaseSeeder.seedDatabase(this@BillBookApplication)
        }
    }
}
