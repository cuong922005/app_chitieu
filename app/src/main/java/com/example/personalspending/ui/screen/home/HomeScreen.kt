package com.example.personalspending.ui.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.personalspending.AppBarContent
import com.example.personalspending.HomeTopAppBar
import com.example.personalspending.R
import com.example.personalspending.ui.model.AppViewModelProvider
import com.example.personalspending.ui.navigate.NavigationDestination
import com.example.personalspending.ui.screen.account.SelectAccountScreen
import com.example.personalspending.ui.screen.account.convertMoney
import com.example.personalspending.ui.screen.details.ListTransaction
import com.example.personalspending.ui.screen.login.saveUserId

object HomeDestination: NavigationDestination {
    override val route: String = "home"
    override val titleRes: Int = R.string.home
    const val userIdArg = "userId"
    val routeWithArgs = "$route/{$userIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onAddTransaction: (Int) -> Unit,
    onHome: (Int) -> Unit,
    onAccount: (Int) -> Unit,
    onCategory: (Int)-> Unit,
    onChart: (Int) -> Unit,
    onPay: (Int) -> Unit,
    onNotify: (Int) -> Unit,
    onPlan: (Int) -> Unit,
    onLogout: () -> Unit,
    onTransactionDetails: (Int, Int) -> Unit,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    saveUserId(LocalContext.current, viewModel.homeUiState.id)

    var isExpanded by remember { mutableStateOf(false) }
    var isShowAccount by remember { mutableStateOf(false) }
    var isExpended by remember { mutableIntStateOf(0) }
    val valueCategory by remember { mutableIntStateOf(R.string.home) }
    var accountCurrent by remember { mutableIntStateOf(0) }
    var indexScreen by remember { mutableIntStateOf(0) }
    var listId by remember { mutableStateOf(emptyList<Int>()) }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                HomeTopAppBar(
                    title = when(indexScreen) {
                        0 -> stringResource(valueCategory)
                        1 -> stringResource(id = R.string.list_transaction)
                        else -> stringResource(valueCategory)
                    },
                    onClick = {
                        if(indexScreen==0) {
                            isExpanded = true
                        } else {
                            indexScreen = 0
                        }
                              },
                    backButton = indexScreen!=0
                )
            }
        ) {  innerPadding ->
            Box(
                modifier = Modifier
                    .background(Color.Black)
                    .fillMaxSize()
                    .padding(top = innerPadding.calculateTopPadding())
            )
        }
        if (isExpanded) {
            AppBarContent(
                accounts = viewModel.homeUiState.accounts,
                user = viewModel.homeUiState.user,
                onClick = { isExpanded = false },
                onHome = onHome,
                onAccount = onAccount,
                onCategory = onCategory,
                onChart = onChart,
                onPay = onPay,
                onRemind = onNotify,
                onPlan = onPlan,
                onLogout = onLogout,
            )
        } else if(isShowAccount) {
            SelectAccountScreen(
                accounts = viewModel.homeUiState.accounts,
                onDestroy = { isShowAccount = false },
                currentAccount = accountCurrent,
                onSelect = { accountCurrent = it }
            )
        } else if(indexScreen == 0) {
            HomeContent(
                onShowAccount = {
                    isShowAccount = true
                },
                onAddTransaction = { onAddTransaction(viewModel.homeUiState.user.id) },
                homeUIState = viewModel.homeUiState,
                currentAccount = accountCurrent,
                onListDetails = {
                    listId = it
                    indexScreen = 1
                },
                isExpended = isExpended,
                onExpended = { isExpended = it }
            )
        } else if(indexScreen == 1) {
            ListTransaction(
                listId = listId,
                homeUIState = viewModel.homeUiState,
                isExpend = isExpended,
                onSelect = {
                    onTransactionDetails(viewModel.homeUiState.id, it)
                }
            )
        }
    }
}

@Composable
fun HomeContent(
    onShowAccount: () -> Unit,
    onAddTransaction:() -> Unit,
    homeUIState: HomeUIState,
    currentAccount: Int,
    onListDetails: (List<Int>) -> Unit,
    isExpended: Int,
    onExpended: (Int) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(1f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 120.dp, start = 20.dp, end = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var total = 0.0
            var nameAccount = stringResource(id = R.string.total)
            if(currentAccount == 0) {
                for (item in homeUIState.accounts) {
                    total += item.money
                }
            } else {
                for (item in homeUIState.accounts) {
                    if(item.id == currentAccount) {
                        total = item.money
                        nameAccount = item.name
                    }
                }
            }
            TitleTotal(
                onShowAccount = onShowAccount,
                totalMoney = total,
                nameAccount = nameAccount
            )
            Spacer(modifier = Modifier.size(12.dp))
            HomeContentBody(
                onAddTransaction = onAddTransaction,
                homeUIState = homeUIState,
                currentAccount = currentAccount,
                onListDetails = onListDetails,
                isExpended = isExpended,
                onExpended = onExpended
            )
        }
    }
}

@Composable
fun TitleTotal(
    onShowAccount: () -> Unit,
    totalMoney: Double,
    nameAccount: String,
    isIcon: Boolean = true,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = onShowAccount,
            colors = ButtonDefaults.buttonColors(Color(0f, 0f, 0f, 0f)),
            border = null,
        ) {
            if(isIcon) {
                Icon(
                    imageVector = Icons.Filled.CurrencyExchange,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = Color(60, 179, 113)
                )
                Spacer(modifier = Modifier.size(8.dp))
            }
            Text(
                text = nameAccount,
                fontSize = 18.sp,
                color = Color(60, 179, 113)
            )
            if (isIcon) {
                Spacer(modifier = Modifier.size(8.dp))
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = Color(60, 179, 113)
                )
            }
        }
        Text(
            text = convertMoney(totalMoney) + " đ",
            fontSize = 22.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun HomeContentBody(
    onAddTransaction: () -> Unit,
    homeUIState: HomeUIState,
    currentAccount: Int,
    onListDetails: (List<Int>) -> Unit,
    isExpended: Int,
    onExpended: (Int) -> Unit,
) {
    HeaderButton(
        isExpended = isExpended,
        onIncome = { onExpended(1) },
        onSpend = { onExpended(0) }
    )
    HomeContentChart(
        modifier = Modifier.clip(
            RoundedCornerShape(20.dp)
        ),
        onAddTransaction = onAddTransaction,
        isExpended = isExpended,
        homeUIState = homeUIState,
        currentAccount = currentAccount,
        onListDetails = onListDetails,
    )
}

class ListTime(
    val title: Int,
    val value: Int
)

object ListTimeContent {
    val listTimes = listOf<ListTime>(
        ListTime(
            title = R.string.day,
            value = 0
        ),
        ListTime(
            title = R.string.week,
            value = 1
        ),
        ListTime(
            title = R.string.month,
            value = 2
        ),
        ListTime(
            title = R.string.year,
            value = 3
        ),
        ListTime(
            title = R.string.period,
            value = 4
        ),
    )
}

@Preview(showBackground = true)
@Composable
fun TopAppBarPreview() {
//    HomeScreen()
}