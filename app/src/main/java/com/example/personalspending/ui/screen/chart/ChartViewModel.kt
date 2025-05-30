package com.example.personalspending.ui.screen.chart

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
import com.example.personalspending.ui.screen.transaction.toSpend
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ChartViewModel(
    savedStateHandle: SavedStateHandle,
    private val spendingRepository: SpendingRepository
): ViewModel() {

    private val userId: Int = checkNotNull(savedStateHandle[ChartDestination.userIdArg])

    var chartUIState by mutableStateOf(ChartUIState())
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

            chartUIState = ChartUIState(id = userId, user = user, spends = spends, areas = areas, accounts = accounts)
        }
    }

}

data class ChartUIState(
    val id: Int = 0,
    val user: User = UserDetails().toUser(),
    val spends: List<Spend> = emptyList(),
    val areas: List<Area> = emptyList(),
    val accounts: List<Account> = emptyList(),
)