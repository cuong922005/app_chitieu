package com.example.personalspending.ui.screen.pay

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.personalspending.R
import com.example.personalspending.data.Account
import com.example.personalspending.data.Area
import com.example.personalspending.getCurrentDateTime
import com.example.personalspending.ui.screen.account.convertMoney
import com.example.personalspending.ui.screen.category.NameText
import com.example.personalspending.ui.screen.home.dateToHour
import com.example.personalspending.ui.screen.home.dateToString
import com.example.personalspending.ui.screen.home.getColorArea
import com.example.personalspending.ui.screen.home.getIconArea
import com.example.personalspending.ui.screen.home.getNameArea
import com.example.personalspending.ui.screen.transaction.ListCategory
import com.example.personalspending.ui.screen.transaction.MoneyValue
import com.example.personalspending.ui.screen.transaction.Note
import com.example.personalspending.ui.screen.transaction.toComposeColor
import kotlinx.coroutines.launch
import java.util.Date

@Composable
fun PayDetails(
    id: Int,
    onUpdate: () -> Unit,
    onPay: () -> Unit,
    viewModel: PayViewModel,
) {
    val listAccount = viewModel.payUISate.accounts
    var account = listAccount[0]
    val listArea = viewModel.payUISate.areas
    var pay = viewModel.payUISate.pays[0]
        for(item in viewModel.payUISate.pays) {
        if(item.id == id) {
            pay = item
        }
    }
    for (item in listAccount) {
        if(item.id == pay.accountId) {
            account = item
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
                DetailsItem(title = stringResource(id = R.string.name), content = pay.name)
                DetailsItem(title = stringResource(id = R.string.money_value), content = convertMoney(money = pay.money) + " đ")
                AccountItemDetails(listCategory = listArea, account = account)
                DetailsItem(title = stringResource(id = R.string.quantity), content = ListQuantities.options[pay.quantityRemind].name)
                DetailsItem(title = stringResource(id = R.string.day), content = dateTimeToString(pay.startDate))
                CategoryItemDetail(
                    colorCategory = getColorArea(listArea, pay.areaId).toComposeColor(),
                    iconCategory = getIconArea(listArea, pay.areaId),
                    nameCategory = getNameArea(listArea, pay.areaId)
                )
                if(pay.note != "") {
                    DetailsItem(title = stringResource(id = R.string.note), content = pay.note)
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
                                          viewModel.deletePay(id)
                                      }
                                onPay()
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
fun PayUpdate(
    id: Int,
    onPay: (Int) -> Unit,
    onCreateCategory: () -> Unit,
    viewModel: PayViewModel,
) {
    val context = LocalContext.current
    if(viewModel.payUISate.pays.isNotEmpty()) {
        var pay = viewModel.payUISate.pays[0]
        for(item in viewModel.payUISate.pays) {
            if(item.id == id) {
                pay = item
            }
        }
        var name by remember { mutableStateOf(pay.name) }
        var isFocus by remember { mutableStateOf(false) }
        var money by remember { mutableDoubleStateOf(pay.money) }
        var accountId by remember { mutableIntStateOf(pay.accountId) }
        var idCategory by remember { mutableIntStateOf(pay.areaId) }
        var expanded by remember { mutableStateOf(false) }
        var selectedOption by remember { mutableIntStateOf(pay.quantityRemind) }
        var dateStart by remember { mutableStateOf(dateToString(pay.startDate)) }
        var time by remember { mutableStateOf(dateToHour(pay.startDate)) }
        var note by remember { mutableStateOf(pay.note) }
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
                                var currentDate: Date = pay.currentDate
                                if(date > stringToDateTime(getCurrentDateTime("HH:mm dd/MM/yyyy", 0))) {
                                    when(selectedOption) {
                                        0 -> currentDate = date
                                        1 -> currentDate = date
                                        2 -> currentDate = getNextDateTimeString(dateTimeToString(date),7)
                                        3 -> currentDate = getNextDateTimeString(dateTimeToString(date),14)
                                        4 -> currentDate = getNextMonth(dateTimeToString(date), "HH:mm dd/MM/yyyy", 1)
                                        5 -> currentDate = getNextMonth(dateTimeToString(date), "HH:mm dd/MM/yyyy", 2)
                                        6 -> currentDate = getNextMonth(dateTimeToString(date), "HH:mm dd/MM/yyyy", 3)
                                    }
                                }
                                coroutineScope.launch {
                                    onValueChangePay(
                                        viewModel.payUISate.payDetails.copy(
                                            id = pay.id,
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
                                    viewModel.updatePay()
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

@Composable
fun DetailsItem(
    title: String,
    content: String
) {
    Column(
        modifier = Modifier
            .padding(top = 20.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = title,
            color = Color.Gray,
            fontSize = 18.sp,
        )
        Spacer(modifier = Modifier.size(10.dp))
        Text(
            text = content,
            color = Color.White,
            fontSize = 20.sp,
        )
    }
}

@Composable
fun AccountItemDetails(
    listCategory: List<Area>,
    account: Account,
) {
    Column(
        modifier = Modifier.padding(top = 20.dp)
    ) {
        Text(
            text = stringResource(id = R.string.account),
            color = Color.Gray,
            fontSize = 18.sp,
        )
        Spacer(modifier = Modifier.size(10.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        getColorArea(listCategory, account.areaId).toComposeColor(),
                        CircleShape
                    )
                    .align(Alignment.CenterVertically)
            ) {
                Icon(
                    painter = painterResource(getIconArea(listCategory, account.areaId)),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .size(26.dp)
                        .align(Alignment.Center)
                )
            }
            Spacer(modifier = Modifier.size(20.dp))
            Text(
                text = account.name,
                color = Color.White,
                fontSize = 20.sp,
            )
        }
    }
}

@Composable
fun CategoryItemDetail(
    colorCategory: Color,
    iconCategory: Int,
    nameCategory: String,
) {
    Column(
        modifier = Modifier.padding(top = 20.dp)
    ) {
        Text(
            text = stringResource(id = R.string.category),
            color = Color.Gray,
            fontSize = 18.sp,
        )
        Spacer(modifier = Modifier.size(10.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(colorCategory, CircleShape)
                    .align(Alignment.CenterVertically)
            ) {
                Icon(
                    painter = painterResource(iconCategory),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .size(26.dp)
                        .align(Alignment.Center)
                )
            }
            Spacer(modifier = Modifier.size(20.dp))
            Text(
                text = nameCategory,
                color = Color.White,
                fontSize = 20.sp,
            )
        }

    }
}