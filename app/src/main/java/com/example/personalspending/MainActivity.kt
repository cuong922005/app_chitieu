package com.example.personalspending

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.personalspending.ui.screen.login.getUserId
import com.example.personalspending.ui.screen.pay.scheduleWork
import com.example.personalspending.ui.theme.PersonalSpendingTheme
import com.github.mikephil.charting.utils.Utils

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PersonalSpendingTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SpendingApp()
                    Utils.init(this)

                    val userId = getUserId(this) // Lấy userId từ SharedPreferences

                    scheduleWork(this, id = userId)
                }
            }
        }
    }
}



