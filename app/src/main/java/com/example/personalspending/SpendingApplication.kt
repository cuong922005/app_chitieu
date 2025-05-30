package com.example.personalspending

import android.app.Application
import com.example.personalspending.network.DefaultAppContainer
import com.example.personalspending.repository.AppContainer
import com.example.personalspending.repository.AppContainerNetwork
import com.example.personalspending.repository.AppDataContainer

class SpendingApplication: Application() {

    lateinit var container: AppContainer
    lateinit var containerNetwork: AppContainerNetwork

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
        containerNetwork = DefaultAppContainer()
    }

}