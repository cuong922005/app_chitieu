package com.example.personalspending.ui.screen.transaction

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.personalspending.data.Account
import com.example.personalspending.data.Area
import com.example.personalspending.data.ImageTransaction
import com.example.personalspending.data.Planed
import com.example.personalspending.data.Spend
import com.example.personalspending.repository.SpendingRepository
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import java.io.ByteArrayOutputStream
import android.util.Base64
import com.example.personalspending.network.DefaultAppContainer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TransactionViewModel (
    savedStateHandle: SavedStateHandle,
    private val spendingRepository: SpendingRepository
): ViewModel() {

    private val userId: Int = checkNotNull(savedStateHandle[TransactionDestination.userIdArg])

    var transactionUIState by mutableStateOf(TransactionUIState())
        private set

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            val accounts = spendingRepository.getAccountByUser(userId).filterNotNull().first()
            val areas = spendingRepository.getAreaByUser(userId).filterNotNull().first()

            val plans: MutableList<Planed> = mutableListOf()
            val spends: MutableList<Spend> = mutableListOf()
            for (item in accounts) {
                plans.addAll(spendingRepository.getPlanByAccount(item.id).filterNotNull().first())
                spends.addAll(spendingRepository.getSpend(item.id).filterNotNull().first())
            }

            transactionUIState = TransactionUIState(accounts = accounts, areas = areas, userId = userId, plans = plans, spends = spends)
        }
    }

    fun updateSpendUIState(spendDetails: SpendDetails) {
        transactionUIState = TransactionUIState(
            spendDetails = spendDetails
        )
    }

    suspend fun insertSpend() {
        spendingRepository.insertSpend(transactionUIState.spendDetails.toSpend())
    }

    suspend fun updateAccountById(id: Int, money: Double) {
        spendingRepository.updateAccountById(id, money)
    }
}

class TransactionUIState(
    val accounts: List<Account> = emptyList(),
    val areas: List<Area> = emptyList(),
    val userId: Int = 0,
    val plans: List<Planed> = emptyList(),
    val spends: List<Spend> = emptyList(),
    val spendDetails: SpendDetails = SpendDetails(),
)

data class SpendDetails(
    val id: Int = 0,
    val accountId: Int = 0,
    val areaId: Int = 0,
    val money: Double = 0.0,
    val date: Date = Calendar.getInstance().time,
    val note: String = "",
    val type: Int = 0,
    val img: String = "",
)

fun SpendDetails.toSpend(): Spend = Spend(
    id = id,
    accountId = accountId,
    areaId = areaId,
    money = money,
    date = date,
    note = note,
    type = type,
    img = img
)

fun imageBitmapToBase64(imageBitmap: ImageBitmap): String {
    val bitmap = imageBitmap.asAndroidBitmap()
    val outputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
    val byteArray = outputStream.toByteArray()
    return Base64.encodeToString(byteArray, Base64.DEFAULT)
}

fun base64ToBitmap(base64String: String): Bitmap? {
    val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
    return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
}


