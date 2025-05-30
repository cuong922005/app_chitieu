package com.example.personalspending.ui.screen.plan

import androidx.annotation.NonNull
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.intl.Locale
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.personalspending.data.Account
import com.example.personalspending.data.Area
import com.example.personalspending.data.Planed
import com.example.personalspending.data.Spend
import com.example.personalspending.data.User
import com.example.personalspending.repository.SpendingRepository
import com.example.personalspending.ui.screen.login.UserDetails
import com.example.personalspending.ui.screen.login.toUser
import com.example.personalspending.ui.screen.notify.NotifyDetails
import com.example.personalspending.ui.screen.notify.NotifyUIState
import com.example.personalspending.ui.screen.notify.toNotify
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

class PlanViewModel(
    savedStateHandle: SavedStateHandle,
    private val spendingRepository: SpendingRepository
): ViewModel() {

    private val userId: Int = checkNotNull(savedStateHandle[PlanDestination.userIdArg])

    var planUIState by mutableStateOf(PlanUIState())
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

            val plans: MutableList<Planed> = mutableListOf()
            val spends: MutableList<Spend> = mutableListOf()
            for (item in accounts) {
                plans.addAll(spendingRepository.getPlanByAccount(item.id).filterNotNull().first())
                spends.addAll(spendingRepository.getSpend(item.id).filterNotNull().first())
            }

            val areas = spendingRepository.getAreaByUser(userId).filterNotNull().first()

            planUIState = PlanUIState(user = user, accounts = accounts, areas = areas, plans = plans, spends = spends)
        }
    }

    fun updatePlanUIState(planDetails: PlanDetails) {
        planUIState = PlanUIState(
            planDetails = planDetails
        )
    }

    suspend fun insertPlan() {
        spendingRepository.insertPlan(planUIState.planDetails.toPlan())
    }

    suspend fun updatePlan() {
        spendingRepository.updatePlan(planUIState.planDetails.toPlan())
    }

    suspend fun deletePlan(id: Int) {
        spendingRepository.deletePlan(id)
    }
}

data class PlanUIState(
    val user: User = UserDetails().toUser(),
    val accounts: List<Account> = emptyList(),
    val areas: List<Area> = emptyList(),
    val plans: List<Planed> = emptyList(),
    val spends: List<Spend> = emptyList(),
    val planDetails: PlanDetails = PlanDetails()
)

data class PlanDetails(
    val id: Int = 0,
    val accountId: Int = 0,
    val areaId: Int = 0,
    val money: Double = 0.0,
    val startDate: Date = Calendar.getInstance().time,
    val endDate: Date = Calendar.getInstance().time,
    val type: Int = 0
)

fun PlanDetails.toPlan(): Planed = Planed(
    id = id,
    accountId = accountId,
    areaId = areaId,
    money = money,
    startDate = startDate,
    endDate = endDate,
    type = type,
)