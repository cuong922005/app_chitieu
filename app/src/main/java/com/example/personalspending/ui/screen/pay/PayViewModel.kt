package com.example.personalspending.ui.screen.pay

import androidx.annotation.NonNull
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.PrimaryKey
import com.example.personalspending.data.Account
import com.example.personalspending.data.Area
import com.example.personalspending.data.AutoPay
import com.example.personalspending.data.Spend
import com.example.personalspending.data.User
import com.example.personalspending.repository.SpendingRepository
import com.example.personalspending.ui.screen.login.UserDetails
import com.example.personalspending.ui.screen.login.toUser
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

class PayViewModel(
    savedStateHandle: SavedStateHandle,
    private val spendingRepository: SpendingRepository
): ViewModel() {

    private val userId: Int = checkNotNull(savedStateHandle[PayDestination.userIdArg])

    var payUISate by mutableStateOf(PayUIState())
        private set

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val user = spendingRepository.getUserById(userId)
                .filterNotNull()
                .first()

            val accounts = spendingRepository.getAccountByUser(userId).filterNotNull().first()

            val spends: MutableList<Spend> = mutableListOf()
            val pays = mutableListOf<AutoPay>()
            for (item in accounts) {
                spends.addAll(spendingRepository.getSpend(item.id).filterNotNull().first())
                pays.addAll(spendingRepository.getPayByAccount(item.id).filterNotNull().first())
            }

            val areas = spendingRepository.getAreaByUser(userId).filterNotNull().first()

            payUISate = PayUIState(user = user, accounts = accounts, areas = areas, pays = pays)
        }
    }

    fun updatePayUIState(payDetails: PayDetails) {
        payUISate = PayUIState(
            payDetails = payDetails
        )
    }

    suspend fun insertPay() {
        spendingRepository.insertPay(payUISate.payDetails.toPay())
    }

    suspend fun updatePayStatus(id: Int, status: Boolean) {
        spendingRepository.updatePayStatus(id, status)
    }

    suspend fun updatePay() {
        spendingRepository.updatePay(payUISate.payDetails.toPay())
    }

    suspend fun deletePay(id: Int) {
        spendingRepository.deletePay(id)
    }
}

data class PayUIState(
    val user: User = UserDetails().toUser(),
    val accounts: List<Account> = emptyList(),
    val areas: List<Area> = emptyList(),
    val pays: List<AutoPay> = emptyList(),
    val payDetails: PayDetails = PayDetails(),
)

data class PayDetails(
    val id: Int = 0,
    val accountId: Int = 0,
    val areaId: Int = 0,
    val name: String = "",
    val quantityRemind: Int = 0,
    val auto: Boolean = false,
    val startDate: Date = Calendar.getInstance().time,
    val currentDate: Date = Calendar.getInstance().time,
    val money: Double = 0.0,
    val note: String = "",
)

fun PayDetails.toPay(): AutoPay = AutoPay(
    id = id,
    accountId = accountId,
    areaId = areaId,
    name = name,
    quantityRemind = quantityRemind,
    auto = auto,
    startDate = startDate,
    currentDate = currentDate,
    money = money,
    note = note,
)
