package com.example.personalspending.ui.screen.category

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.personalspending.data.Account
import com.example.personalspending.data.Area
import com.example.personalspending.data.User
import com.example.personalspending.repository.SpendingRepository
import com.example.personalspending.ui.screen.login.UserDetails
import com.example.personalspending.ui.screen.login.toUser
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class CategoryViewModel(
    savedStateHandle: SavedStateHandle,
    private val spendingRepository: SpendingRepository
): ViewModel() {

    private val userId: Int = checkNotNull(savedStateHandle[CategoryDestination.userIdArg])
//    private val userId: Int = 1
//    val createHandle: String = checkNotNull(savedStateHandle[CategoryDestination.create])

    var categoryUiState by mutableStateOf(CategoryUIState())
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

            val areas = spendingRepository.getAreaByUser(userId).filterNotNull().first()

            categoryUiState = CategoryUIState(user = user, accounts = accounts, id = userId, areas = areas)
        }
    }

    fun updateCategoryUIState(categoryDetails: CategoryDetails) {
        categoryUiState = CategoryUIState(
            categoryDetails = categoryDetails
        )
    }

    suspend fun insertCategory() {
        spendingRepository.insertArea(categoryUiState.categoryDetails.toCategory())
    }
}
// a@g.co
data class CategoryUIState(
    val user: User = UserDetails().toUser(),
    val accounts: List<Account> = emptyList(),
    val id: Int = 0,
    val areas: List<Area> = emptyList(),
    val categoryDetails: CategoryDetails = CategoryDetails(),
)

data class CategoryDetails(
    val id: Int = 0,
    val userId: Int = 0,
    val name: String = "",
    val icon: Int = 0,
    val color: Int = 0,
    val type: Int = 0,
)

fun CategoryDetails.toCategory(): Area = Area(
    id = id,
    userId = userId,
    name = name,
    icon = icon,
    color = color,
    type = type,
)