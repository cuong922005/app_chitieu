package com.example.personalspending.ui.screen.notify

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.personalspending.data.Account
import com.example.personalspending.data.Area
import com.example.personalspending.data.AutoPay
import com.example.personalspending.data.Notify
import com.example.personalspending.data.Spend
import com.example.personalspending.data.User
import com.example.personalspending.repository.SpendingRepository
import com.example.personalspending.ui.screen.login.UserDetails
import com.example.personalspending.ui.screen.login.toUser
import com.example.personalspending.ui.screen.pay.PayDestination
import com.example.personalspending.ui.screen.pay.toPay
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

class NotifyViewModel(
    savedStateHandle: SavedStateHandle,
    private val spendingRepository: SpendingRepository
): ViewModel() {

    private val userId: Int = checkNotNull(savedStateHandle[NotifyDestination.userIdArg])

    var notifyUISate by mutableStateOf(NotifyUIState())
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

            val notifies = spendingRepository.getNotifyByAccount(userId).filterNotNull().first()

            notifyUISate = NotifyUIState(user = user, accounts = accounts, notifies = notifies)
        }
    }

    fun updateNotifyUIState(notifyDetails: NotifyDetails) {
        notifyUISate = NotifyUIState(
            notifyDetails = notifyDetails
        )
    }

    suspend fun insertNotify() {
        spendingRepository.insertNotify(notifyUISate.notifyDetails.toNotify())
    }

    suspend fun updateNotifyStatus(id: Int, status: Boolean) {
        spendingRepository.updateNotifyStatus(id, status)
    }

    suspend fun updateNotify() {
        spendingRepository.updateNotify(notifyUISate.notifyDetails.toNotify())
    }

    suspend fun deleteNotify(id: Int) {
        spendingRepository.deleteNotify(id)
    }
}

data class NotifyUIState(
    val user: User = UserDetails().toUser(),
    val accounts: List<Account> = emptyList(),
    val notifies: List<Notify> = emptyList(),
    val notifyDetails: NotifyDetails = NotifyDetails(),
)

data class NotifyDetails(
    val id: Int = 0,
    val userId: Int = 0,
    val name: String = "",
    val quantityRemind: Int = 0,
    val auto: Boolean = false,
    val startDate: Date = Calendar.getInstance().time,
    val currentDate: Date = Calendar.getInstance().time,
    val note: String = "",
)

fun NotifyDetails.toNotify(): Notify = Notify(
    id = id,
    userId = userId,
    name = name,
    quantityRemind = quantityRemind,
    auto = auto,
    startDate = startDate,
    currentDate = currentDate,
    note = note,
)