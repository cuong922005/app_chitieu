package com.example.personalspending.ui.screen.account

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.personalspending.data.Account
import com.example.personalspending.data.Area
import com.example.personalspending.data.AutoPay
import com.example.personalspending.data.Planed
import com.example.personalspending.data.Spend
import com.example.personalspending.data.User
import com.example.personalspending.repository.SpendingRepository
import com.example.personalspending.ui.screen.category.CategoryDetails
import com.example.personalspending.ui.screen.category.toCategory
import com.example.personalspending.ui.screen.login.AccountDetails
import com.example.personalspending.ui.screen.login.ItemUIState
import com.example.personalspending.ui.screen.login.UserDetails
import com.example.personalspending.ui.screen.login.toAccount
import com.example.personalspending.ui.screen.login.toUser
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AccountViewModel(
    savedStateHandle: SavedStateHandle,
    private val spendingRepository: SpendingRepository
): ViewModel() {
    private val userId: Int = checkNotNull(savedStateHandle[AccountDestination.userIdArg])

    var accountUiState by mutableStateOf(AccountUIState())
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
            val plans: MutableList<Planed> = mutableListOf()
            for (item in accounts) {
                spends.addAll(spendingRepository.getSpend(item.id).filterNotNull().first())
                pays.addAll(spendingRepository.getPayByAccount(item.id).filterNotNull().first())
                plans.addAll(spendingRepository.getPlanByAccount(item.id).filterNotNull().first())
            }

            val areas = spendingRepository.getAreaByUser(userId).filterNotNull().first()

            accountUiState = AccountUIState(user = user, accounts = accounts, userId = userId, spends = spends, areas = areas, pays = pays, plans = plans)
        }
    }

    fun updateAccountUiState(accountDetails: AccountDetails) {
        accountUiState = AccountUIState(accountDetails = accountDetails)
    }

    fun updateCategoryUiState(categoryDetails: CategoryDetails) {
        accountUiState = AccountUIState(categoryDetails = categoryDetails)
    }

    suspend fun insertAccount() {
        spendingRepository.insertAccount(accountUiState.accountDetails.toAccount())
    }

    suspend fun insertCategory() {
        spendingRepository.insertArea(accountUiState.categoryDetails.toCategory())
    }

    suspend fun updateAccount() {
        spendingRepository.updateAccount(accountUiState.accountDetails.toAccount())
    }

    suspend fun deleteAccount(id: Int) {
        spendingRepository.deleteAccount(id)
    }

    suspend fun deleteSpend(id: Int) {
        spendingRepository.deleteSpend(id)
    }

    suspend fun deletePay(id: Int) {
        spendingRepository.deletePay(id)
    }

    suspend fun deletePlan(id: Int) {
        spendingRepository.deletePlan(id)
    }

    suspend fun getCategoryId(icon: Int, name: String, color: Int): Int {
        val areas = spendingRepository.getAreaByUser(userId).filterNotNull().first()
        for (item in areas) {
            if(item.icon==icon && item.name==name && color==item.color && item.type==2) {
                return item.id
            }
        }
        return 0
    }
}

class AccountUIState(
    val user: User = UserDetails().toUser(),
    val accounts: List<Account> = emptyList(),
    val areas: List<Area> = emptyList(),
    val userId: Int = 0,
    val spends: List<Spend> = emptyList(),
    val pays: List<AutoPay> = emptyList(),
    val plans: List<Planed> = emptyList(),
    val accountDetails: AccountDetails = AccountDetails(),
    val categoryDetails: CategoryDetails = CategoryDetails()
)
