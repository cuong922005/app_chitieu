package com.example.personalspending.ui.screen.plan

import android.widget.Toast
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
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
import com.example.personalspending.data.Planed
import com.example.personalspending.getCurrentDateTime
import com.example.personalspending.getNextDate
import com.example.personalspending.ui.model.AppViewModelProvider
import com.example.personalspending.ui.navigate.NavigationDestination
import com.example.personalspending.ui.screen.account.convertMoney
import com.example.personalspending.ui.screen.home.dateToString
import com.example.personalspending.ui.screen.home.getColorArea
import com.example.personalspending.ui.screen.home.getFirstDayOfYearsAgo
import com.example.personalspending.ui.screen.home.getIconArea
import com.example.personalspending.ui.screen.home.getNameArea
import com.example.personalspending.ui.screen.home.stringToDate
import com.example.personalspending.ui.screen.login.saveUserId
import com.example.personalspending.ui.screen.pay.AccountItemDetails
import com.example.personalspending.ui.screen.pay.CategoryItemDetail
import com.example.personalspending.ui.screen.pay.CustomSwitch
import com.example.personalspending.ui.screen.pay.DetailsItem
import com.example.personalspending.ui.screen.pay.ListQuantities
import com.example.personalspending.ui.screen.pay.PayDestination
import com.example.personalspending.ui.screen.pay.PayDetails
import com.example.personalspending.ui.screen.pay.ReminderDropdownMenu
import com.example.personalspending.ui.screen.pay.SelectAccount
import com.example.personalspending.ui.screen.pay.SelectDate
import com.example.personalspending.ui.screen.pay.combineDateAndTime
import com.example.personalspending.ui.screen.pay.dateTimeToString
import com.example.personalspending.ui.screen.pay.getNextDateTimeString
import com.example.personalspending.ui.screen.transaction.ListCategory
import com.example.personalspending.ui.screen.transaction.MoneyValue
import com.example.personalspending.ui.screen.transaction.toComposeColor
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date

object PlanDestination: NavigationDestination {
    override val route: String = "plan"
    override val titleRes: Int = R.string.pland
    const val userIdArg = "userId"
    val routeWithArgs = "$route/{$userIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanScreen(
    onHome: (Int) -> Unit,
    onAccount: (Int) -> Unit,
    onCategory: (Int)-> Unit,
    onChart: (Int) -> Unit,
    onPay: (Int) -> Unit,
    onNotify: (Int) -> Unit,
    onPlan: (Int) -> Unit,
    onLogout: () -> Unit,
    viewModel: PlanViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    var isAppBar by remember { mutableStateOf(false) }
    var indexScreen by remember { mutableIntStateOf(0) }
    var idCurrent by remember { mutableIntStateOf(0) }
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                HomeTopAppBar(
                    title = if (indexScreen == 0) {
                        stringResource(PlanDestination.titleRes)
                    } else if (indexScreen == 1) {
                        stringResource(id = R.string.create) + " " + stringResource(PlanDestination.titleRes).lowercase()
                    } else if (indexScreen == 2) {
                        "Chi tiết " + stringResource(PlanDestination.titleRes).lowercase()
                    } else {
                        stringResource(id = R.string.update) + " " + stringResource(PlanDestination.titleRes).lowercase()
                    },
                    onClick = {
                        when (indexScreen) {
                            0 -> isAppBar = true
                            1 -> indexScreen = 0
                            2 -> indexScreen = 0
                            3 -> indexScreen = 2
                        }
                    },
                    backButton = indexScreen != 0
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .background(Color.Black)
                    .fillMaxSize()
                    .padding(top = innerPadding.calculateTopPadding())
            )
        }
        if (isAppBar) {
            AppBarContent(
                accounts = viewModel.planUIState.accounts,
                user = viewModel.planUIState.user,
                onClick = { isAppBar = false },
                onHome = onHome,
                onAccount = onAccount,
                onCategory = onCategory,
                onChart = onChart,
                onPay = onPay,
                onRemind = onNotify,
                onPlan = onPlan,
                onLogout = {
                    onLogout()
                           },
            )
        }
        if(indexScreen==0) {
            PlanContent(
                onCreate = { indexScreen = 1 },
                onDetails = {
                    idCurrent = it
                    indexScreen = 2
                            },
                viewModel = viewModel
            )
        } else if(indexScreen==1) {
            PlanForm(
                onPlan = { onPlan(viewModel.planUIState.user.id) },
                viewModel = viewModel
            )
        } else if(indexScreen==2) {
            PlanDetails(
                id = idCurrent,
                onPlan = { onPlan(viewModel.planUIState.user.id) },
                onUpdate = { indexScreen = 3 },
                viewModel = viewModel
            )
        } else {
            PlanUpdate(
                id = idCurrent,
                onPlan = { onPlan(viewModel.planUIState.user.id) },
                viewModel = viewModel
            )
        }
    }
}

@Composable
fun PlanContent(
    onCreate: () -> Unit,
    onDetails: (Int) -> Unit,
    viewModel: PlanViewModel
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(1f)
    ) {
        val listArea = viewModel.planUIState.areas
        val plans = viewModel.planUIState.plans

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 140.dp, end = 10.dp, bottom = 40.dp),
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = if(plans.isEmpty()) {
                            "Không có kế hoạch nào được tạo gần đây"
                        } else {
                            "Danh sách các kế hoạch hoạt động"
                               },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(190, 190, 190),
                    )
                }
                Spacer(modifier = Modifier.size(30.dp))
                IconButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp),
                    onClick = onCreate,
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "",
                            tint = Color(60, 179, 113),
                            modifier = Modifier.size(34.dp)
                        )
                        Text(
                            text = stringResource(id = R.string.create).uppercase(),
                            fontSize = 22.sp,
                            color = Color(60, 179, 113),
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.size(20.dp))
            }
            if(plans.isNotEmpty()) {
                items(plans) {plan ->
                    PlanItem(
                        modifier = Modifier.clip(RoundedCornerShape(10.dp)),
                        title = getNameArea(listArea, plan.areaId),
                        money = plan.money,
                        color = getColorArea(listArea, plan.areaId).toComposeColor(),
                        icon = getIconArea(listArea, plan.areaId),
                        startDate = dateToString(plan.startDate),
                        endDate = dateToString(plan.endDate),
                        onClick = { onDetails(plan.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun PlanForm(
    onPlan: () -> Unit,
    viewModel: PlanViewModel
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(1f)
    ) {
        val context = LocalContext.current
        val listAccount = viewModel.planUIState.accounts
        val listArea = viewModel.planUIState.areas
        val plans = viewModel.planUIState.plans
        val onValueChange: (PlanDetails) -> Unit = viewModel::updatePlanUIState
        val coroutineScope = rememberCoroutineScope()

        var moneyValue by remember { mutableIntStateOf(0) }
        var dateStart by remember { mutableStateOf(getCurrentDateTime("dd/MM/yyyy", 0)) }
        var dateEnd by remember { mutableStateOf(getNextDate(stringToDate(dateStart))) }
        var idCategory by remember { mutableIntStateOf(0) }
        var idAccount by remember { mutableIntStateOf(0) }
        var selectedOption by remember { mutableIntStateOf(0) }

        if(idAccount==0 && listAccount.isNotEmpty()) {
            idAccount = listAccount[0].id
        }
        var expanded by remember { mutableStateOf(false) }
        val isSubmit:Boolean = moneyValue != 0 && idCategory != 0 && idAccount != 0

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 140.dp, start = 20.dp, end = 20.dp),
        ) {
            item {
                MoneyValue(value = moneyValue, onValueChange = { moneyValue = it })
                Spacer(modifier = Modifier.size(20.dp))
                SelectDate(
                    title = "Ngày bắt đầu",
                    dateStart = dateStart,
                    context = context,
                    onChange = { dateStart = it }
                )
                Spacer(modifier = Modifier.size(20.dp))
                SelectDate(
                    title = "Ngày kết thúc",
                    begin = stringToDate(dateStart),
                    dateStart = dateEnd,
                    context = context,
                    onChange = { dateEnd = it }
                )
                Spacer(modifier = Modifier.size(20.dp))
                Column {
                    Text(
                        text = stringResource(id = R.string.quantity),
                        color = Color.Gray,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    ReminderDropdownMenu(
                        isPay = false,
                        expanded = expanded,
                        selectedOption = selectedOption,
                        onExpanded = { expanded = it },
                        onSelect = { selectedOption = it }
                    )
                }
                Spacer(modifier = Modifier.size(20.dp))
                SelectAccount(
                    listAccount = listAccount,
                    accountId = idAccount,
                    onChange = { idAccount = it }
                )
                Spacer(modifier = Modifier.size(20.dp))
                ListCategory(
                    listArea = listArea,
                    isExpended = 0,
                    onChangeArea = { idCategory = it },
                    areaValue = idCategory,
                    onCreate = {},
                    isUpdate = 0,
                )
                Spacer(modifier = Modifier.size(20.dp))
                Box(
                    modifier = Modifier
                        .padding(bottom = 20.dp)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = {
                            val planCheck = plans.filter { it.areaId==idCategory && it.accountId==idAccount }
                            coroutineScope.launch {
                                if(!checkPlan(dateStart, dateEnd, planCheck)) {
                                    onValueChange(
                                        viewModel.planUIState.planDetails.copy(
                                            accountId = idAccount,
                                            areaId = idCategory,
                                            money = moneyValue.toDouble(),
                                            startDate = stringToDate(dateStart),
                                            endDate = stringToDate(dateEnd),
                                            type = selectedOption,
                                        )
                                    )
                                    viewModel.insertPlan()
                                } else {
                                    Toast.makeText(context, "Không thể tạo vì thời gian trùng với kế hoạch khác!!!", Toast.LENGTH_SHORT).show()
                                }
                            }
                            if(!checkPlan(dateStart, dateEnd, planCheck)) {
                                onPlan()
                            }
                        },
                        modifier = Modifier
                            .width(160.dp)
                            .height(40.dp),
                        enabled = isSubmit,
                        colors = ButtonDefaults.buttonColors(Color(247, 177, 12))
                    ) {
                        Text(
                            stringResource(id = R.string.create),
                            fontSize = 20.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.size(30.dp))
            }
        }
    }
}

@Composable
fun PlanItem(
    title: String,
    money: Double = 0.0,
    color: Color,
    icon: Int,
    startDate: String,
    endDate: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Spacer(modifier = Modifier.size(10.dp))
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.size(16.dp))
        Box(
            modifier = modifier
                .fillMaxWidth()
                .background(Color(38, 38, 38))
                .padding(8.dp)
                .clickable { onClick() }
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
                            modifier = Modifier.size(24.dp),
                            colorFilter = ColorFilter.tint(Color.White)
                        )
                    }
                    Column {
                        Text(
                            text = title,
                            modifier = Modifier.padding(start = 10.dp),
                            color = Color.White,
                            fontSize = 16.sp
                        )
                        var dateBegin = startDate
                        var dateFinish = endDate
                        if(stringToDate(startDate) >= getFirstDayOfYearsAgo(0)) {
                            dateBegin = dateToString(stringToDate(startDate), "dd/MM")
                            dateFinish = dateToString(stringToDate(endDate), "dd/MM")
                        }
                        Text(
                            text = "$dateBegin-$dateFinish",
                            modifier = Modifier.padding(start = 10.dp, top = 8.dp),
                            color = Color(179, 179, 179),
                            fontSize = 10.sp
                        )
                    }
                }
                Text(
                    text = convertMoney(money) + " đ",
                    color = Color.Green,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun PlanDetails(
    id: Int,
    onPlan: () -> Unit,
    onUpdate: () -> Unit,
    viewModel: PlanViewModel,
) {
    val listAccount = viewModel.planUIState.accounts
    if(listAccount.isNotEmpty()) {
        var account = listAccount[0]
        val listArea = viewModel.planUIState.areas
        var plan = viewModel.planUIState.plans[0]
        for(item in viewModel.planUIState.plans) {
            if(item.id == id) {
                plan = item
            }
        }
        for (item in listAccount) {
            if(item.id == plan.accountId) {
                account = item
            }
        }
        val spends = viewModel.planUIState.spends
        var moneyUsed = 0.0
        if(spends.isNotEmpty()) {
            for (item in spends) {
                if(item.areaId==plan.areaId && item.date >= plan.startDate && item.date <= plan.endDate) {
                    moneyUsed += item.money
                }
            }
        }
        val coroutineScope = rememberCoroutineScope()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(1f)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 120.dp, start = 20.dp, end = 20.dp),
            ) {
                item {
                    CategoryItemDetail(
                        colorCategory = getColorArea(listArea, plan.areaId).toComposeColor(),
                        iconCategory = getIconArea(listArea, plan.areaId),
                        nameCategory = getNameArea(listArea, plan.areaId)
                    )
                    DetailsItem(title = stringResource(id = R.string.money_value), content = convertMoney(money = plan.money) + " đ")
                    if(moneyUsed > 0.0) {
                        Column(
                            modifier = Modifier
                                .padding(top = 20.dp)
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = stringResource(id = R.string.money_value) + " đã chi",
                                color = Color.Gray,
                                fontSize = 18.sp,
                            )
                            Spacer(modifier = Modifier.size(10.dp))
                            Text(
                                text = convertMoney(moneyUsed) + " đ",
                                color = if(moneyUsed <= plan.money) {
                                    Color(60, 179, 113)
                                } else { Color.Red },
                                fontSize = 20.sp,
                            )
                        }
                    }
                    AccountItemDetails(listCategory = listArea, account = account)
                    DetailsItem(title = stringResource(id = R.string.quantity), content = ListQuantities.optionsPlan[plan.type].name)
                    DetailsItem(title = "Thời gian", content = dateToString(plan.startDate) + " - " + dateToString(plan.endDate))
                    Box(
                        modifier = Modifier
                            .padding(top = 30.dp, bottom = 20.dp)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Button(
                                onClick = onUpdate,
                                modifier = Modifier
                                    .width(100.dp)
                                    .height(40.dp),
                                colors = ButtonDefaults.buttonColors(Color(7, 212, 15))
                            ) {
                                Text(
                                    text = stringResource(id = R.string.update),
                                    fontSize = 20.sp
                                )
                            }
                            Spacer(modifier = Modifier.size(40.dp))
                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        viewModel.deletePlan(plan.id)
                                    }
                                    onPlan()
                                },
                                modifier = Modifier
                                    .width(100.dp)
                                    .height(40.dp),
                                colors = ButtonDefaults.buttonColors(Color(242, 76, 76))
                            ) {
                                Text(
                                    text = stringResource(id = R.string.delete),
                                    fontSize = 20.sp
                                )
                            }
                        }

                    }
                    Spacer(modifier = Modifier.size(30.dp))
                }
            }
        }
    }
}

@Composable
fun PlanUpdate(
    id: Int,
    onPlan: () -> Unit,
    viewModel: PlanViewModel
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(1f)
    ) {
        val context = LocalContext.current
        val listAccount = viewModel.planUIState.accounts
        val listArea = viewModel.planUIState.areas
        val plans = viewModel.planUIState.plans
        if (plans.isNotEmpty()) {
            var plan = plans[0]
            for (item in plans) {
                if(item.id == id) {
                    plan = item
                }
            }
            val onValueChange: (PlanDetails) -> Unit = viewModel::updatePlanUIState
            val coroutineScope = rememberCoroutineScope()

            var moneyValue by remember { mutableIntStateOf(plan.money.toInt()) }
            var dateStart by remember { mutableStateOf(dateToString(plan.startDate)) }
            var dateEnd by remember { mutableStateOf(dateToString(plan.endDate)) }
            var idCategory by remember { mutableIntStateOf(plan.areaId) }
            var idAccount by remember { mutableIntStateOf(plan.accountId) }
            var selectedOption by remember { mutableIntStateOf(plan.type) }

            var expanded by remember { mutableStateOf(false) }
            val isSubmit:Boolean = moneyValue != 0 && idCategory != 0 && idAccount != 0

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 140.dp, start = 20.dp, end = 20.dp),
            ) {
                item {
                    MoneyValue(value = moneyValue, onValueChange = { moneyValue = it })
                    Spacer(modifier = Modifier.size(20.dp))
                    SelectDate(
                        title = "Ngày bắt đầu",
                        dateStart = dateStart,
                        context = context,
                        onChange = { dateStart = it }
                    )
                    Spacer(modifier = Modifier.size(20.dp))
                    SelectDate(
                        title = "Ngày kết thúc",
                        begin = stringToDate(dateStart),
                        dateStart = dateEnd,
                        context = context,
                        onChange = { dateEnd = it }
                    )
                    Spacer(modifier = Modifier.size(20.dp))
                    Column {
                        Text(
                            text = stringResource(id = R.string.quantity),
                            color = Color.Gray,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        ReminderDropdownMenu(
                            isPay = false,
                            expanded = expanded,
                            selectedOption = selectedOption,
                            onExpanded = { expanded = it },
                            onSelect = { selectedOption = it }
                        )
                    }
                    Spacer(modifier = Modifier.size(20.dp))
                    SelectAccount(
                        listAccount = listAccount,
                        accountId = idAccount,
                        onChange = { idAccount = it }
                    )
                    Spacer(modifier = Modifier.size(20.dp))
                    ListCategory(
                        listArea = listArea,
                        isExpended = 0,
                        onChangeArea = { idCategory = it },
                        areaValue = idCategory,
                        onCreate = {},
                        isUpdate = 0,
                    )
                    Spacer(modifier = Modifier.size(20.dp))
                    Box(
                        modifier = Modifier
                            .padding(bottom = 20.dp)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    onValueChange(
                                        viewModel.planUIState.planDetails.copy(
                                            id = plan.id,
                                            accountId = idAccount,
                                            areaId = idCategory,
                                            money = moneyValue.toDouble(),
                                            startDate = stringToDate(dateStart),
                                            endDate = stringToDate(dateEnd),
                                            type = selectedOption,
                                        )
                                    )
                                    viewModel.updatePlan()
                                }
                                onPlan()
                            },
                            modifier = Modifier
                                .width(160.dp)
                                .height(40.dp),
                            enabled = isSubmit,
                            colors = ButtonDefaults.buttonColors(Color(247, 177, 12))
                        ) {
                            Text(
                                stringResource(id = R.string.update),
                                fontSize = 20.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.size(30.dp))
                }
            }
        }
    }
}

fun checkMulTime(start1: String, end1: String, start2: String, end2: String): Boolean {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    val startDate1 = LocalDate.parse(start1, formatter)
    val endDate1 = LocalDate.parse(end1, formatter)
    val startDate2 = LocalDate.parse(start2, formatter)
    val endDate2 = LocalDate.parse(end2, formatter)

    val latestStart = if (startDate1.isAfter(startDate2)) startDate1 else startDate2
    val earliestEnd = if (endDate1.isBefore(endDate2)) endDate1 else endDate2

    return !latestStart.isAfter(earliestEnd)
}

fun checkPlan(startDate: String, endDate: String, planCheck: List<Planed>): Boolean {
    for (plan in planCheck) {
        return checkMulTime(startDate, endDate, dateToString(plan.startDate), dateToString(plan.endDate))
    }
    return false
}

@Preview
@Composable
fun PlanPreview() {
//    PlanScreen()
}