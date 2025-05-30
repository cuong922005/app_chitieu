package com.example.personalspending.ui.screen.login

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.personalspending.R
import com.example.personalspending.data.Account
import com.example.personalspending.data.Area
import com.example.personalspending.data.AreaList
import com.example.personalspending.data.User
import com.example.personalspending.data.listColor
import com.example.personalspending.repository.SpendingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Date

class LoginViewModel(
    private val spendingRepository: SpendingRepository
): ViewModel() {

    var userId: Int = 0
    val loginUiState: StateFlow<LoginUIState> = spendingRepository.getAllUser().map { LoginUIState(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = LoginUIState()
        )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    var itemUiState by mutableStateOf(ItemUIState())
        private set

    fun updateUserUiState(userDetails: UserDetails) {
        itemUiState =
            ItemUIState(userDetails = userDetails)
    }

    private fun updateAccountUiState(accountDetails: AccountDetails): AccountDetails {
        return accountDetails.copy()
    }

    suspend fun saveUser() {
        withContext(Dispatchers.IO) {
            spendingRepository.insertUser(itemUiState.userDetails.toUser())
            spendingRepository.getUserByEmail(itemUiState.userDetails.email).collect { user ->
                userId = user.id
                spendingRepository.insertArea(
                    Area(
                        userId = user.id,
                        icon = R.drawable.money_bill_1_regular,
                        name = "Chính",
                        color = listColor[4].color.toArgb(),
                        type = 2
                    )
                )
                saveAreasDefault(user.id)
                spendingRepository.getAreaByType(user.id, 2).collect{ area ->
                    val updatedAccountDetails = updateAccountUiState(itemUiState.accountDetails.copy(userId = userId, areaId = area.id))
                    saveAccount(updatedAccountDetails)
                }
            }
        }
    }

    private suspend fun saveAccount(updatedAccountDetails: AccountDetails) {
        spendingRepository.insertAccount(updatedAccountDetails.toAccount())
    }

    suspend fun saveAreasDefault(userId: Int) {
        for(item in AreaList.listDefaultIcon) {
            spendingRepository.insertArea(
                Area(
                    userId = userId,
                    icon = item.icon,
                    name = item.name,
                    color = item.color,
                    type = 0
                )
            )
        }
        for(item in AreaList.listDefaultIconIncome) {
            spendingRepository.insertArea(
                Area(
                    userId = userId,
                    icon = item.icon,
                    name = item.name,
                    color = item.color,
                    type = 1
                )
            )
        }
    }
}

data class LoginUIState(val userList: List<User> = listOf())

data class ItemUIState(
    val userDetails: UserDetails = UserDetails(),
    val accountDetails: AccountDetails = AccountDetails()
)

data class UserDetails(
    val id: Int = 0,
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val date: Date = Calendar.getInstance().time,
)

fun UserDetails.toUser(): User = User(
    id = id,
    name = name,
    email = email,
    password = password,
    date = date
)

data class AccountDetails(
    val id: Int = 0,
    val userId: Int = 0,
    val name: String = "Chính",
    val money: Double = 0.0,
    val areaId: Int = 0
)

fun AccountDetails.toAccount(): Account = Account(
    id = id,
    userId = userId,
    name = name,
    money = money,
    areaId = areaId
)

fun saveUserId(context: Context, userId: Int) {
    val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putInt("userId", userId)
    editor.apply()
}

fun getUserId(context: Context): Int {
    val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    return sharedPreferences.getInt("userId", 0)
}

