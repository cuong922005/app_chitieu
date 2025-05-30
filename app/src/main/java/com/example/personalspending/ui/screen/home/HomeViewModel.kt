package com.example.personalspending.ui.screen.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.personalspending.data.Account
import com.example.personalspending.data.Area
import com.example.personalspending.data.Spend
import com.example.personalspending.data.User
import com.example.personalspending.repository.SpendingRepository
import com.example.personalspending.ui.screen.login.UserDetails
import com.example.personalspending.ui.screen.login.toUser
import com.example.personalspending.ui.screen.transaction.SpendDetails
import com.example.personalspending.ui.screen.transaction.TransactionUIState
import com.example.personalspending.ui.screen.transaction.toSpend
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class HomeViewModel(
    savedStateHandle: SavedStateHandle,
    private val spendingRepository: SpendingRepository
): ViewModel() {

    private val userId: Int = checkNotNull(savedStateHandle[HomeDestination.userIdArg])
//    private val userId = 1

    var homeUiState by mutableStateOf(HomeUIState())
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
            for (item in accounts) {
                spends.addAll(spendingRepository.getSpend(item.id).filterNotNull().first())
            }

            val areas = spendingRepository.getAreaByUser(userId).filterNotNull().first()

            homeUiState = HomeUIState(user = user, accounts = accounts, id = userId, spends = spends, areas = areas)
        }
    }
}

data class HomeUIState(
    val user: User = UserDetails().toUser(),
    val accounts: List<Account> = emptyList(),
    val id: Int = 0,
    val spendDetails: SpendDetails = SpendDetails(),
    val spends: List<Spend> = emptyList(),
    val areas: List<Area> = emptyList(),
)
