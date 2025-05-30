package com.example.personalspending.ui.screen.details

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.personalspending.data.Account
import com.example.personalspending.data.Area
import com.example.personalspending.data.Planed
import com.example.personalspending.data.Spend
import com.example.personalspending.repository.SpendingRepository
import com.example.personalspending.ui.screen.login.AccountDetails
import com.example.personalspending.ui.screen.login.toAccount
import com.example.personalspending.ui.screen.transaction.SpendDetails
import com.example.personalspending.ui.screen.transaction.toSpend
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class DetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val spendingRepository: SpendingRepository
): ViewModel() {

    private val userId: Int = checkNotNull(savedStateHandle[DetailsDestination.userIdArg])
    private val spendId: Int = checkNotNull(savedStateHandle[DetailsDestination.spendIdArg])
    private val backPage: Int = checkNotNull(savedStateHandle[DetailsDestination.backPage])

    var detailsUiState by mutableStateOf(DetailsUIState())
        private set

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            detailsUiState = DetailsUIState(id = userId, idSpend = spendId, backPage = backPage)

            val spend = spendingRepository.getSpendById(spendId)
                .filterNotNull()
                .first()

            val account = spendingRepository.getAccount(spend.accountId)
                .filterNotNull()
                .first()

            val areas = spendingRepository.getAreaByUser(userId)
                .filterNotNull()
                .first()

            val plans = spendingRepository.getPlanByAccount(account.id)
                .filterNotNull()
                .first()

            val spends = spendingRepository.getSpend(account.id)
                .filterNotNull()
                .first()

            detailsUiState = DetailsUIState(id = userId, idSpend = spendId, account = account, spend = spend, areas = areas, plans = plans, spends = spends)
        }
    }

    suspend fun deleteSpend(id: Int) {
        spendingRepository.deleteSpend(id)
    }

    fun updateSpendUIState(spendDetails: SpendDetails) {
        detailsUiState = DetailsUIState(
            spendDetails = spendDetails
        )
    }

    suspend fun updateSpend() {
        spendingRepository.updateSpend(detailsUiState.spendDetails.toSpend())
    }

    suspend fun updateAccountById(id: Int, money: Double) {
        spendingRepository.updateAccountById(id, money)
    }

}

data class DetailsUIState(
    val id: Int = 0,
    val idSpend: Int = 0,
    val backPage: Int = 0,
    val account: Account = AccountDetails().toAccount(),
    val spendDetails: SpendDetails = SpendDetails(),
    val spend: Spend = SpendDetails().toSpend(),
    val areas: List<Area> = emptyList(),
    val plans: List<Planed> = emptyList(),
    val spends: List<Spend> = emptyList(),
)
