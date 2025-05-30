package com.example.personalspending.ui.screen.account

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
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
import com.example.personalspending.data.Account
import com.example.personalspending.data.Area
import com.example.personalspending.ui.model.AppViewModelProvider
import com.example.personalspending.ui.navigate.NavigationDestination
import com.example.personalspending.ui.screen.category.CategoryDetails
import com.example.personalspending.ui.screen.home.getColorArea
import com.example.personalspending.ui.screen.home.getIconArea
import com.example.personalspending.ui.screen.home.getNameArea
import com.example.personalspending.ui.screen.login.AccountDetails
import com.example.personalspending.ui.screen.transaction.toComposeColor
import kotlinx.coroutines.launch

object AccountDestination: NavigationDestination {
    override val route: String = "account"
    override val titleRes: Int = R.string.account
    const val userIdArg = "userId"
    val routeWithArgs = "$route/{$userIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    modifier: Modifier = Modifier,
    onHome: (Int) -> Unit,
    onAccount: (Int) -> Unit,
    onCategory: (Int)-> Unit,
    onChart: (Int) -> Unit,
    onPay: (Int) -> Unit,
    onNotify: (Int) -> Unit,
    onPlan: (Int) -> Unit,
    onLogout: () -> Unit,
    viewModel: AccountViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    var isExpanded by remember { mutableStateOf(false) }
    var indexScreen by remember { mutableIntStateOf(0) }
    var accountCurrent by remember { mutableIntStateOf(0) }

    val onAccountUpdate: (AccountDetails) -> Unit = viewModel::updateAccountUiState
    val onCategoryUpdate: (CategoryDetails) -> Unit = viewModel::updateCategoryUiState

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                if(indexScreen == 0) {
                    HomeTopAppBar(
                        title = stringResource(AccountDestination.titleRes),
                        onClick = { isExpanded = true }
                    )
                } else {
                    HomeTopAppBar(
                        title = if(indexScreen==1) {
                            stringResource(R.string.add_account)
                        } else {
                            stringResource(AccountDestination.titleRes)
                        },
                        onClick = { indexScreen = 0 },
                        backButton = true,
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .background(Color.Black)
                    .fillMaxSize()
                    .padding(top = innerPadding.calculateTopPadding())
            )
        }
    }
    if (isExpanded) {
        AppBarContent(
            accounts = viewModel.accountUiState.accounts,
            user = viewModel.accountUiState.user,
            onClick = { isExpanded = false },
            onHome = onHome,
            onCategory = onCategory,
            onAccount = onAccount,
            onChart = onChart,
            onPay = onPay,
            onRemind = onNotify,
            onPlan = onPlan,
            onLogout = onLogout,
        )
    } else if(indexScreen == 0) {
        val listAccount: List<Account> = viewModel.accountUiState.accounts
        val listArea: List<Area> = viewModel.accountUiState.areas
        var total = 0.0
        for (item in listAccount) {
            total += item.money
        }
        AccountContent(
            viewModel = viewModel,
            listAccount = listAccount,
            total = total,
            listArea = listArea,
            onCreate = { indexScreen = 1 },
            onSelect = {
                accountCurrent = it
                indexScreen = 2
            },
            onAccount = { onAccount(viewModel.accountUiState.userId) }
        )
    } else if(indexScreen == 1) {
        AccountFun(
            viewModel = viewModel,
            onCategoryUpdate = onCategoryUpdate,
            onAccountUpdate = onAccountUpdate,
            onAccount = onAccount,
        )
    } else if(indexScreen == 2) {
        AccountFun(
            viewModel = viewModel,
            onCategoryUpdate = onCategoryUpdate,
            onAccountUpdate = onAccountUpdate,
            onAccount = onAccount,
            accountCurrent = accountCurrent,
        )
    }
}

@Composable
fun AccountContent(
    viewModel: AccountViewModel,
    listAccount: List<Account>,
    total: Double,
    listArea: List<Area>,
    onCreate: () -> Unit,
    onSelect: (Int) -> Unit,
    onAccount: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 120.dp, bottom = 20.dp)
            .zIndex(1f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.total),
                    color = Color(175, 175, 175),
                    fontSize = 26.sp,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
                Text(
                    text = "${convertMoney(money = total)} đ",
                    color = Color.White,
                    fontSize = 30.sp,
                    modifier = Modifier.padding(bottom = 10.dp),
                    fontWeight = FontWeight.Bold
                )
            }
            LazyColumn {
                items(listAccount) { item ->
                    Spacer(modifier = Modifier.size(16.dp))
                    AccountItem(
                        modifier = Modifier.clip(RoundedCornerShape(20.dp)),
                        icon = getIconArea(listArea, item.areaId),
                        name = getNameArea(listArea, item.areaId),
                        color = getColorArea(listArea, item.areaId).toComposeColor(),
                        money = item.money,
                        isDelete = true,
                        onDelete = {
                                   coroutineScope.launch {
                                       val listSpends = viewModel.accountUiState.spends.filter { it.accountId == item.id }
                                       if(listSpends.isNotEmpty()) {
                                           for (spend in listSpends) {
                                               viewModel.deleteSpend(spend.id)
                                           }
                                       }
                                       val listPays = viewModel.accountUiState.pays.filter { it.accountId == item.id }
                                       if(listPays.isNotEmpty()) {
                                           for (pay in listPays) {
                                               viewModel.deletePay(pay.id)
                                           }
                                       }
                                       val listPlans = viewModel.accountUiState.plans.filter { it.accountId == item.id }
                                       if(listPlans.isNotEmpty()) {
                                           for (plan in listPlans) {
                                               viewModel.deletePlan(plan.id)
                                           }
                                       }
                                       viewModel.deleteAccount(item.id)
                                   }
                            onAccount()
                        },
                        onSelect = { onSelect(item.id) }
                    )
                }
            }
            Spacer(modifier = Modifier.size(30.dp))
        }
        IconButton(
            onClick = onCreate,
            modifier = Modifier
                .padding(bottom = 30.dp)
                .size(60.dp)
                .background(Color(247, 177, 12), CircleShape)
                .align(Alignment.BottomCenter),
        ) {
            Icon(
                imageVector = Icons.Rounded.Add,
                contentDescription = null,
                modifier = Modifier.size(26.dp),
                tint = Color.Black
            )
        }
    }
}

@Composable
fun AccountItem(
    modifier: Modifier = Modifier,
    icon: Int,
    name: String,
    color: Color,
    money: Double,
    isDelete: Boolean = false,
    onSelect: () -> Unit,
    onDelete: () -> Unit = {},
    nameAccount: String = "",
) {
    if(isDelete) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Box(
                modifier = modifier
                    .fillMaxWidth(0.9f)
                    .background(Color(38, 38, 38))
                    .padding(8.dp)
                    .clickable { onSelect() }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(color, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = icon),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                colorFilter = ColorFilter.tint(Color.White)
                            )
                        }
                        Column(
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = name,
                                modifier = Modifier.padding(start = 16.dp),
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if(nameAccount != "") {
                                Text(
                                    text = nameAccount,
                                    modifier = Modifier.padding(start = 16.dp, top = 4.dp),
                                    color = Color(193, 193, 193),
                                    fontSize = 12.sp
                                )
                            }
                        }

                    }
                    Text(
                        text = convertMoney(money = money) + " đ",
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }
            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .padding(start = 10.dp)
                    .size(30.dp)
                    .background(Color.Transparent, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Delete,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp),
                    tint = Color.White
                )
            }
        }
    } else {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .background(Color(38, 38, 38))
                .padding(8.dp)
                .clickable { onSelect() }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(color, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = icon),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            colorFilter = ColorFilter.tint(Color.White)
                        )
                    }
                    Column(
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = name,
                            modifier = Modifier.padding(start = 16.dp),
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if(nameAccount != "") {
                            Text(
                                text = nameAccount,
                                modifier = Modifier.padding(start = 16.dp, top = 4.dp),
                                color = Color(193, 193, 193),
                                fontSize = 12.sp
                            )
                        }
                    }

                }
                Text(
                    text = convertMoney(money = money) + " đ",
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun AccountScreenPreview() {
   // AccountScreen()
}