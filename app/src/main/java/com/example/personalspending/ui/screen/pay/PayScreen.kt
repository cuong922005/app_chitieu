package com.example.personalspending.ui.screen.pay

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.content.Context
import android.icu.util.Calendar
import android.widget.TimePicker
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
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
import com.example.personalspending.data.Account
import com.example.personalspending.data.Area
import com.example.personalspending.getCurrentDateTime
import com.example.personalspending.ui.model.AppViewModelProvider
import com.example.personalspending.ui.navigate.NavigationDestination
import com.example.personalspending.ui.screen.account.convertMoney
import com.example.personalspending.ui.screen.category.NameText
import com.example.personalspending.ui.screen.home.HomeViewModel
import com.example.personalspending.ui.screen.home.dateToString
import com.example.personalspending.ui.screen.home.formatDate
import com.example.personalspending.ui.screen.home.showDatePickerDialog
import com.example.personalspending.ui.screen.home.stringToDate
import com.example.personalspending.ui.screen.transaction.ListCategory
import com.example.personalspending.ui.screen.transaction.MoneyValue
import com.example.personalspending.ui.screen.transaction.Note
import com.example.personalspending.ui.screen.transaction.SpendDetails
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date

object PayDestination: NavigationDestination {
    override val route: String = "pay"
    override val titleRes: Int = R.string.pay
    const val userIdArg = "userId"
    val routeWithArgs = "$route/{$userIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PayScreen(
    onHome: (Int) -> Unit,
    onAccount: (Int) -> Unit,
    onCategory: (Int)-> Unit,
    onChart: (Int) -> Unit,
    onPay: (Int) -> Unit,
    onNotify: (Int) -> Unit,
    onPlan: (Int) -> Unit,
    onLogout: () -> Unit,
    onCreateCategory: (Int) -> Unit,
    viewModel: PayViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    var isAppBar by remember { mutableStateOf(false) }
    var indexScreen by remember { mutableIntStateOf(0) }
    var idCurrent by remember { mutableIntStateOf(0) }
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                HomeTopAppBar(
                    title = if(indexScreen==0) {
                        stringResource(PayDestination.titleRes)
                    } else if(indexScreen==1) {
                           stringResource(id = R.string.create) + " " + stringResource(PayDestination.titleRes).lowercase()
                    } else if(indexScreen==2) {
                        "Chi tiết " + stringResource(PayDestination.titleRes).lowercase()
                    } else {
                        stringResource(id = R.string.update)  + " " + stringResource(PayDestination.titleRes).lowercase()
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
                accounts = viewModel.payUISate.accounts,
                user = viewModel.payUISate.user,
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
            PayContent(
                onCreate = { indexScreen = 1 },
                viewModel = viewModel,
                onPay = onPay,
                onClick = {
                    idCurrent = it
                    indexScreen = 2
                }
            )
        } else if(indexScreen==1) {
            PayForm(
                viewModel = viewModel,
                onCreateCategory = {onCreateCategory(viewModel.payUISate.user.id)},
                onPay = onPay,
            )
        } else if(indexScreen==2) {
            PayDetails(
                id = idCurrent,
                onUpdate = { indexScreen = 3 },
                onPay = { onPay(viewModel.payUISate.user.id) },
                viewModel = viewModel,
            )
        } else {
            PayUpdate(
                id = idCurrent,
                viewModel = viewModel,
                onCreateCategory = {onCreateCategory(viewModel.payUISate.user.id)},
                onPay = onPay,
            )
        }
    }
}

@Composable
fun PayContent(
    onCreate: () -> Unit,
    onPay: (Int) -> Unit,
    onClick: (Int) -> Unit,
    viewModel: PayViewModel,
) {
    var isStatus by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val pays = viewModel.payUISate.pays
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = "Đây là các khoản thanh toán tự động",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )
                }
                Spacer(modifier = Modifier.size(30.dp))
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
            if(pays.isNotEmpty()) {
                items(pays) { item ->
                    PayItem(
                        title = item.name,
                        isOn = item.auto || isStatus,
                        money = item.money,
                        onCheckedChange = {
                            coroutineScope.launch {
                                viewModel.updatePayStatus(item.id, !item.auto)
                            }
                            isStatus = false
                            onPay(viewModel.payUISate.user.id)
                        },
                        onClick = { onClick(item.id) }
                    )
                }
                item{
                    Spacer(modifier = Modifier.size(30.dp))
                }
            }
            if (isStatus && pays.isNotEmpty()) {
                for (item in pays) {
                    coroutineScope.launch {
                        viewModel.updatePayStatus(item.id, true)
                    }
                }
            }
        }
    }
}

@Composable
fun PayItem(
    title: String,
    money: Double = 0.0,
    date: String = "",
    isOn: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onClick: () -> Unit,
) {
    Spacer(modifier = Modifier.size(16.dp))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp)
            .clickable { onClick() },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if(money > 0.0) {
            Column {
                Text(
                    text = title,
                    fontSize = 20.sp,
                    color = Color.White,
                )
                Text(
                    text = convertMoney(money) + " đ",
                    fontSize = 16.sp,
                    color = Color(180, 180, 180),
                    modifier = Modifier
                        .padding(top = 6.dp),
                )
            }
        } else if(date!="") {
            Column {
                Text(
                    text = title,
                    fontSize = 20.sp,
                    color = Color.White,
                )
                Text(
                    text = date,
                    fontSize = 12.sp,
                    color = Color(180, 180, 180),
                    modifier = Modifier
                        .padding(top = 6.dp),
                )
            }
        } else {
            Text(
                text = title,
                fontSize = 20.sp,
                color = Color.White,
            )
        }
        CustomSwitch(
            checked = isOn,
            onCheckedChange = { onCheckedChange(!isOn) },
            width = 40,
            height = 20
        )
    }
}

@SuppressLint("SuspiciousIndentation")
@Composable
fun PayForm(
    onPay: (Int) -> Unit,
    onCreateCategory: () -> Unit,
    viewModel: PayViewModel,
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var isFocus by remember { mutableStateOf(false) }
    var money by remember { mutableStateOf(0.0) }
    var accountId by remember { mutableIntStateOf(0) }
    var idCategory by remember { mutableIntStateOf(0) }
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf(0) }
    var dateStart by remember { mutableStateOf(getCurrentDateTime("dd/MM/yyyy", 1)) }
    var time by remember { mutableStateOf(getCurrentDateTime("HH:mm", 0)) }
    var note by remember { mutableStateOf("") }
    val isSubmit = name!="" && money!=0.0 &&
            accountId!=0 && idCategory!=0

    val listAccount = viewModel.payUISate.accounts
    val listArea = viewModel.payUISate.areas
    val onValueChangePay: (PayDetails) -> Unit = viewModel::updatePayUIState
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
                MoneyValue(value = money.toInt(), onValueChange = { money = it.toDouble() })
                Spacer(modifier = Modifier.size(20.dp))
                NameText(
                    name = name,
                    title = stringResource(id = R.string.name),
                    isFocus = isFocus,
                    onChange = { name = it },
                    onFocus = { isFocus = it}
                )
                Spacer(modifier = Modifier.size(30.dp))
                SelectAccount(
                    listAccount = listAccount,
                    accountId = accountId,
                    onChange = { accountId = it }
                )
                Spacer(modifier = Modifier.size(30.dp))
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
                Spacer(modifier = Modifier.size(30.dp))
                SelectDate(
                    dateStart = dateStart,
                    context = context,
                    onChange = { dateStart = it }
                )
                Spacer(modifier = Modifier.size(30.dp))
                SelectHour(
                    time = time,
                    context = context,
                    onChange = { time = it },
                )
                Spacer(modifier = Modifier.size(30.dp))
                ListCategory(
                    listArea = listArea,
                    isExpended = 0,
                    onChangeArea = { idCategory = it },
                    areaValue = idCategory,
                    onCreate = onCreateCategory,
                    isUpdate = 0,
                )
                Spacer(modifier = Modifier.size(30.dp))
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
                                onValueChangePay(
                                    viewModel.payUISate.payDetails.copy(
                                          name = name,
                                          money = money,
                                          areaId = idCategory,
                                          accountId = accountId,
                                          startDate = date,
                                          currentDate = currentDate,
                                          quantityRemind = selectedOption,
                                          auto = true,
                                          note = note
                                    )
                                )
                                viewModel.insertPay()
                              }
                            onPay(viewModel.payUISate.user.id)
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
fun CustomSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    width: Int,
    height: Int
) {
    Box(
        modifier = Modifier
            .width(width.dp)
            .height(height.dp)
            .clip(CircleShape)
            .background(if (checked) Color.Green else Color.Gray)
            .clickable { onCheckedChange(!checked) }
            .padding(2.dp)
    ) {
        Box(
            modifier = Modifier
                .size(height.dp - 4.dp)
                .clip(CircleShape)
                .background(Color.White)
                .align(if (checked) Alignment.CenterEnd else Alignment.CenterStart)
        )
    }
}

@Composable
fun ReminderDropdownMenu(
    isPay: Boolean = true,
    selectedOption: Int,
    expanded: Boolean,
    onExpanded: (Boolean) -> Unit,
    onSelect: (Int) -> Unit,
) {
    val options = if(isPay)  ListQuantities.options else ListQuantities.optionsPlan

    Box() {
        Column {
            Text(
                text = options[selectedOption].name,
                fontSize = 20.sp,
                modifier = Modifier
                    .padding(start = 10.dp)
                    .clickable { onExpanded(true) },
                color = Color(60, 179, 113)
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { onExpanded(false) },
                modifier = Modifier.background(Color.Black)
            ) {
                options.forEach { option ->
                    DropdownMenuItem(onClick = {
                        onSelect(option.id)
                        onExpanded(false)
                    }) {
                        Text(text = option.name, color = Color.White, fontSize = 18.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun SelectAccount(
    listAccount: List<Account>,
    accountId: Int,
    onChange: (Int) -> Unit,
) {
    Column {
        Text(
            text = stringResource(id = R.string.account),
            color = Color.Gray,
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        LazyRow() {
            items(listAccount) { item ->
                Button(
                    onClick = { onChange(item.id) },
                    colors = if(accountId == item.id) {
                        ButtonDefaults.buttonColors( // a@g.co
                            containerColor = Color.Gray
                        )
                    } else {
                        ButtonDefaults.buttonColors( // Define the background color
                            containerColor = Color.Black
                        )
                    }
                ) {
                    Text(
                        text = item.name,
                        color = Color.Green,
                        fontSize = 16.sp
                    )
                }
                Spacer(modifier = Modifier.size(10.dp))
            }
        }
    }
}

@Composable
fun SelectDate(
    title: String = stringResource(id = R.string.day),
    begin: Date = stringToDate(getCurrentDateTime("dd/MM/yyyy", 0)),
    dateStart: String,
    context: Context,
    onChange: (String) -> Unit,
) {
    Column {
        Text(
            text = title,
            color = Color.Gray,
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Text(
            text = dateStart,
            fontSize = 20.sp,
            modifier = Modifier
                .padding(start = 10.dp)
                .clickable {
                    showDatePickerDialog(
                        context,
                        "Chọn ngày"
                    ) { selectedDate ->
                        if (selectedDate.time >= begin) {
                            onChange(dateToString(selectedDate.time))
                        }
                    }
                },
            color = Color(60, 179, 113)
        )
    }
}

@Composable
fun SelectHour(
    time: String,
    context: Context,
    onChange: (String) -> Unit,
) {
    Column {
        Text(
            text = stringResource(id = R.string.hour),
            color = Color.Gray,
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Text(
            text = time,
            fontSize = 20.sp,
            modifier = Modifier
                .padding(start = 10.dp)
                .clickable {
                    TimePickerExample(context = context, onTime = { onChange(it) })
                },
            color = Color(60, 179, 113)
        )
    }
}

fun TimePickerExample(
    context: Context,
    onTime: (String) -> Unit,
) {
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)

    val timePickerDialog = TimePickerDialog(
        context,
        { _: TimePicker, selectedHour: Int, selectedMinute: Int ->
            onTime(String.format("%02d:%02d", selectedHour, selectedMinute))
        }, hour, minute, true
    )
    timePickerDialog.show()
}


fun combineDateAndTime(date: String, time: String): Date {
    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    val localDate = LocalDate.parse(date, dateFormatter)
    val localTime = LocalTime.parse(time, timeFormatter)

    val localDateTime = LocalDateTime.of(localDate, localTime)

    // Chuyển đổi LocalDateTime sang Date
    val instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant()
    return Date.from(instant)
}

class ListQuantity(
    val id: Int,
    val name: String,
)

object ListQuantities {
    val options = listOf(
        ListQuantity(0, "Một lần"),
        ListQuantity(1, "Hàng ngày"),
        ListQuantity(2, "Hàng tuần"),
        ListQuantity(3, "Mỗi 2 tuần"),
        ListQuantity(4, "Hàng tháng"),
        ListQuantity(5, "Mỗi 2 tháng"),
        ListQuantity(6, "Hàng quý"),
    )
    val optionsPlan = listOf(
        ListQuantity(0, "Mỗi ngày"),
        ListQuantity(1, "Mỗi tuần"),
        ListQuantity(2, "Mỗi tháng"),
    )
}