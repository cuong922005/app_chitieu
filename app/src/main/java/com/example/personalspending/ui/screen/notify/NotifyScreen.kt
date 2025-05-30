package com.example.personalspending.ui.screen.notify

import android.annotation.SuppressLint
import androidx.compose.foundation.background
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
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.personalspending.AppBarContent
import com.example.personalspending.HomeTopAppBar
import com.example.personalspending.R
import com.example.personalspending.getCurrentDateTime
import com.example.personalspending.ui.model.AppViewModelProvider
import com.example.personalspending.ui.navigate.NavigationDestination
import com.example.personalspending.ui.screen.account.convertMoney
import com.example.personalspending.ui.screen.category.NameText
import com.example.personalspending.ui.screen.home.dateToHour
import com.example.personalspending.ui.screen.home.dateToString
import com.example.personalspending.ui.screen.home.getColorArea
import com.example.personalspending.ui.screen.home.getIconArea
import com.example.personalspending.ui.screen.home.getNameArea
import com.example.personalspending.ui.screen.pay.AccountItemDetails
import com.example.personalspending.ui.screen.pay.CategoryItemDetail
import com.example.personalspending.ui.screen.pay.DetailsItem
import com.example.personalspending.ui.screen.pay.ListQuantities
import com.example.personalspending.ui.screen.pay.PayDestination
import com.example.personalspending.ui.screen.pay.PayDetails
import com.example.personalspending.ui.screen.pay.PayItem
import com.example.personalspending.ui.screen.pay.PayViewModel
import com.example.personalspending.ui.screen.pay.ReminderDropdownMenu
import com.example.personalspending.ui.screen.pay.SelectAccount
import com.example.personalspending.ui.screen.pay.SelectDate
import com.example.personalspending.ui.screen.pay.SelectHour
import com.example.personalspending.ui.screen.pay.combineDateAndTime
import com.example.personalspending.ui.screen.pay.dateTimeToString
import com.example.personalspending.ui.screen.pay.getNextDateTimeString
import com.example.personalspending.ui.screen.pay.getNextMonth
import com.example.personalspending.ui.screen.pay.stringToDateTime
import com.example.personalspending.ui.screen.transaction.ListCategory
import com.example.personalspending.ui.screen.transaction.MoneyValue
import com.example.personalspending.ui.screen.transaction.Note
import com.example.personalspending.ui.screen.transaction.toComposeColor
import kotlinx.coroutines.launch
import java.util.Date

object NotifyDestination: NavigationDestination {
    override val route: String = "notify"
    override val titleRes: Int = R.string.notify
    const val userIdArg = "userId"
    val routeWithArgs = "$route/{$userIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotifyScreen(
    onHome: (Int) -> Unit,
    onAccount: (Int) -> Unit,
    onCategory: (Int)-> Unit,
    onChart: (Int) -> Unit,
    onPay: (Int) -> Unit,
    onNotify: (Int) -> Unit,
    onPlan: (Int) -> Unit,
    onLogout: () -> Unit,
    viewModel: NotifyViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    var isAppBar by remember { mutableStateOf(false) }
    var indexScreen by remember { mutableIntStateOf(0) }
    var idCurrent by remember { mutableIntStateOf(0) }
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                HomeTopAppBar(
                    title = if(indexScreen==0) {
                        stringResource(NotifyDestination.titleRes)
                    } else if(indexScreen==1) {
                        stringResource(id = R.string.create) + " " + stringResource(NotifyDestination.titleRes).lowercase()
                    } else if(indexScreen==2) {
                        "Chi tiết " + stringResource(NotifyDestination.titleRes).lowercase()
                    } else {
                        stringResource(id = R.string.update)  + " " + stringResource(NotifyDestination.titleRes).lowercase()
                    },
                    onClick = {
                        when(indexScreen) {
                            0 -> { isAppBar = true }
                            1 -> { indexScreen = 0 }
                            2 -> { indexScreen = 0 }
                            3 -> { indexScreen = 2 }
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
        if(isAppBar) {
            AppBarContent(
                accounts = viewModel.notifyUISate.accounts,
                user = viewModel.notifyUISate.user,
                onClick = { isAppBar = false },
                onHome = onHome,
                onAccount = onAccount,
                onCategory = onCategory,
                onChart = onChart,
                onPay = onPay,
                onRemind = onNotify,
                onPlan = onPlan,
                onLogout = onLogout,
            )
        }
        if(indexScreen==0) {
            NotifyContent(
                viewModel = viewModel,
                onNotify = onNotify,
                onClick = {
                    idCurrent = it
                    indexScreen = 2
                          },
                onCreate = { indexScreen = 1 }
            )
        } else if(indexScreen==1) {
            NotifyForm(
                onNotify = onNotify,
                viewModel = viewModel
            )
        } else if(indexScreen==2) {
            NotifyDetails(
                id = idCurrent,
                onUpdate = { indexScreen = 3 },
                onNotify = { onNotify(viewModel.notifyUISate.user.id) },
                viewModel = viewModel,
            )
        } else {
            NotifyUpdate(
                id = idCurrent,
                onNotify = { onNotify(viewModel.notifyUISate.user.id) },
                viewModel = viewModel
            )
        }
    }
}

@Composable
fun NotifyContent(
    onNotify: (Int) -> Unit,
    onClick: (Int) -> Unit,
    onCreate: () -> Unit,
    viewModel: NotifyViewModel,
) {
    var isStatus by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val notifies = viewModel.notifyUISate.notifies
    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(1f)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 140.dp, start = 20.dp, end = 20.dp),
        ) {
            item {
                IconButton(
                    modifier = Modifier
                        .fillMaxWidth(),
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
                PayItem(
                    title = "Trạng thái",
                    isOn = isStatus,
                    onCheckedChange = { isStatus = it },
                    onClick = {}
                )
            }
            if(notifies.isNotEmpty()) {
                items(notifies) { item ->
                    PayItem(
                        title = item.name,
                        isOn = item.auto || isStatus,
                        date = dateTimeToString(item.currentDate, "HH:mm dd/MM"),
                        onCheckedChange = {
                            coroutineScope.launch {
                                viewModel.updateNotifyStatus(item.id, !item.auto)
                            }
                            isStatus = false
                            onNotify(viewModel.notifyUISate.user.id)
                        },
                        onClick = { onClick(item.id) }
                    )
                }
            }
            if (isStatus && notifies.isNotEmpty()) {
                for (item in notifies) {
                    coroutineScope.launch {
                        viewModel.updateNotifyStatus(item.id, true)
                    }
                }
            }
        }
    }
}

@SuppressLint("SuspiciousIndentation")
@Composable
fun NotifyForm(
    onNotify: (Int) -> Unit,
    viewModel: NotifyViewModel,
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var isFocus by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableIntStateOf(0) }
    var dateStart by remember { mutableStateOf(getCurrentDateTime("dd/MM/yyyy", 0)) }
    var time by remember { mutableStateOf(getCurrentDateTime("HH:mm", 0)) }
    var note by remember { mutableStateOf("") }
    val isSubmit = name!=""

    val onValueChange: (NotifyDetails) -> Unit = viewModel::updateNotifyUIState
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
                NameText(
                    name = name,
                    title = stringResource(id = R.string.name),
                    isFocus = isFocus,
                    onChange = { name = it },
                    onFocus = { isFocus = it}
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
                        expanded = expanded,
                        selectedOption = selectedOption,
                        onExpanded = { expanded = it },
                        onSelect = { selectedOption = it }
                    )
                }
                Spacer(modifier = Modifier.size(20.dp))
                SelectDate(
                    dateStart = dateStart,
                    context = context,
                    onChange = { dateStart = it }
                )
                Spacer(modifier = Modifier.size(20.dp))
                SelectHour(
                    time = time,
                    context = context,
                    onChange = { time = it },
                )
                Spacer(modifier = Modifier.size(20.dp))
                Note(value = note, onValueChange = { note = it }, title = R.string.note)
                Spacer(modifier = Modifier.size(30.dp))
                Box(
                    modifier = Modifier
                        .padding(bottom = 20.dp)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = {
                            val date: Date = combineDateAndTime(dateStart, time)
                            var currentDate = date
                            when(selectedOption) {
                                2 -> currentDate = getNextDateTimeString(dateTimeToString(date), 7)
                                3 -> currentDate = getNextDateTimeString(dateTimeToString(date), 14)
                                4 -> currentDate = getNextMonth(dateTimeToString(date), "HH:mm dd/MM/yyyy", 1)
                                5 -> currentDate = getNextMonth(dateTimeToString(date), "HH:mm dd/MM/yyyy", 2)
                                6 -> currentDate = getNextMonth(dateTimeToString(date), "HH:mm dd/MM/yyyy", 3)
                            }
                            coroutineScope.launch {
                                onValueChange(
                                    viewModel.notifyUISate.notifyDetails.copy(
                                        name = name,
                                        userId = viewModel.notifyUISate.user.id,
                                        startDate = date,
                                        currentDate = currentDate,
                                        quantityRemind = selectedOption,
                                        auto = true,
                                        note = note
                                    )
                                )
                                viewModel.insertNotify()
                            }
                            onNotify(viewModel.notifyUISate.user.id)
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
fun NotifyDetails(
    id: Int,
    onUpdate: () -> Unit,
    onNotify: () -> Unit,
    viewModel: NotifyViewModel,
) {
    var notify = viewModel.notifyUISate.notifies[0]
    for(item in viewModel.notifyUISate.notifies) {
        if(item.id == id) {
            notify = item
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
                DetailsItem(title = stringResource(id = R.string.name), content = notify.name)
                DetailsItem(title = stringResource(id = R.string.quantity), content = ListQuantities.options[notify.quantityRemind].name)
                DetailsItem(title = stringResource(id = R.string.day), content = dateTimeToString(notify.startDate))
                if(notify.note != "") {
                    DetailsItem(title = stringResource(id = R.string.note), content = notify.note)
                }
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
                                    viewModel.deleteNotify(id)
                                }
                                onNotify()
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

@Composable
fun NotifyUpdate(
    id: Int,
    onNotify: (Int) -> Unit,
    viewModel: NotifyViewModel,
) {
    val context = LocalContext.current
    if(viewModel.notifyUISate.notifies.isNotEmpty()) {
        var notify = viewModel.notifyUISate.notifies[0]
        for(item in viewModel.notifyUISate.notifies) {
            if(item.id == id) {
                notify = item
            }
        }
        var name by remember { mutableStateOf(notify.name) }
        var isFocus by remember { mutableStateOf(false) }
        var expanded by remember { mutableStateOf(false) }
        var selectedOption by remember { mutableIntStateOf(notify.quantityRemind) }
        var dateStart by remember { mutableStateOf(dateToString(notify.startDate)) }
        var time by remember { mutableStateOf(dateToHour(notify.startDate)) }
        var note by remember { mutableStateOf(notify.note) }
        val isSubmit = name!=""

        val onValueChangePay: (NotifyDetails) -> Unit = viewModel::updateNotifyUIState
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
                    NameText(
                        name = name,
                        title = stringResource(id = R.string.name),
                        isFocus = isFocus,
                        onChange = { name = it },
                        onFocus = { isFocus = it}
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
                            expanded = expanded,
                            selectedOption = selectedOption,
                            onExpanded = { expanded = it },
                            onSelect = { selectedOption = it }
                        )
                    }
                    Spacer(modifier = Modifier.size(20.dp))
                    SelectDate(
                        dateStart = dateStart,
                        context = context,
                        onChange = { dateStart = it }
                    )
                    Spacer(modifier = Modifier.size(20.dp))
                    SelectHour(
                        time = time,
                        context = context,
                        onChange = { time = it },
                    )
                    Spacer(modifier = Modifier.size(20.dp))
                    Note(value = note, onValueChange = { note = it }, title = R.string.note)
                    Spacer(modifier = Modifier.size(20.dp))
                    Box(
                        modifier = Modifier
                            .padding(bottom = 20.dp)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Button(
                            onClick = {
                                val date: Date = combineDateAndTime(dateStart, time)
                                var currentDate: Date = notify.currentDate
                                if(date > stringToDateTime(getCurrentDateTime("HH:mm dd/MM/yyyy", 0))) {
                                    when(selectedOption) {
                                        1 -> currentDate = date
                                        2 -> currentDate = getNextDateTimeString(dateTimeToString(date), 7)
                                        3 -> currentDate = getNextDateTimeString(dateTimeToString(date), 14)
                                        4 -> currentDate = getNextMonth(dateTimeToString(date), "HH:mm dd/MM/yyyy", 1)
                                        5 -> currentDate = getNextMonth(dateTimeToString(date), "HH:mm dd/MM/yyyy", 2)
                                        6 -> currentDate = getNextMonth(dateTimeToString(date), "HH:mm dd/MM/yyyy", 3)
                                    }
                                }

                                coroutineScope.launch {
                                    onValueChangePay(
                                        viewModel.notifyUISate.notifyDetails.copy(
                                            id = notify.id,
                                            name = name,
                                            userId = notify.userId,
                                            startDate = date,
                                            currentDate = currentDate,
                                            quantityRemind = selectedOption,
                                            auto = true,
                                            note = note
                                        )
                                    )
                                    viewModel.updateNotify()
                                }
                                onNotify(notify.userId)
                            },
                            modifier = Modifier
                                .width(160.dp)
                                .height(50.dp),
                            enabled = isSubmit,
                            colors = ButtonDefaults.buttonColors(Color(247, 177, 12))
                        ) {
                            Text(
                                stringResource(id = R.string.update),
                                fontSize = 20.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.size(40.dp))
                }
            }
        }
    }
}