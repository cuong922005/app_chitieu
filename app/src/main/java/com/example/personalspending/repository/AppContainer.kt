package com.example.personalspending.repository

import android.content.Context
import com.example.personalspending.dao.SpendingDatabase

interface AppContainer {
    val spendingRepository: SpendingRepository
}

class AppDataContainer(private val context: Context): AppContainer {
    override val spendingRepository: SpendingRepository by lazy {
        OfflineSpendingRepository(SpendingDatabase.getDatabase(context).spendingDao())
    }
}

interface AppContainerNetwork {
    val imagesRepository: ImageRepository
}