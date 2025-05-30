package com.example.personalspending.ui.screen.home

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.personalspending.R
import com.example.personalspending.data.Area
import com.example.personalspending.data.Spend
import com.example.personalspending.data.listColorChart
import com.example.personalspending.getCurrentDateTime
import com.example.personalspending.getDayOfMonth
import com.example.personalspending.getDayOfWeek
import com.example.personalspending.getYear
import com.example.personalspending.ui.screen.account.convertMoney
import com.example.personalspending.ui.screen.transaction.toComposeColor
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Calendar
import java.util.Date
import java.util.Locale
import android.graphics.Color as AndroidColor

@Composable
fun LineItem() {
    Box(modifier = Modifier
        .height(2.dp)
        .background(Color(44, 160, 52))
        .fillMaxWidth()
    )
}

@Composable
fun HeaderButton(
    modifier: Modifier = Modifier,
    isExpended: Int,
    onIncome: () -> Unit,
    onSpend: () -> Unit,
) {
    Row() {
        OutlinedButton(
            modifier = Modifier.weight(1f),
            onClick = onSpend,
            colors = ButtonDefaults.buttonColors(Color(0f, 0f, 0f, 0f)),
            border = null,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.expense),
                    fontSize = 20.sp,
                    modifier = Modifier.padding(bottom = 8.dp),
                    color = if(isExpended == 0) {
                        Color.White
                    } else {
                        Color(155, 155, 155)
                    }
                )
                if(isExpended == 0) {
                    Box(modifier = Modifier
                        .height(2.dp)
                        .background(Color.White)
                        .fillMaxWidth()
                    )
                }
            }
        }
        Spacer(modifier = Modifier.size(20.dp))
        OutlinedButton(
            modifier = Modifier.weight(1f),
            onClick = onIncome,
            colors = ButtonDefaults.buttonColors(Color(0f, 0f, 0f, 0f)),
            border = null,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.income),
                    fontSize = 20.sp,
                    modifier = Modifier.padding(bottom = 8.dp),
                    color = if(isExpended == 1) {
                        Color.White
                    } else {
                        Color(155, 155, 155)
                    }
                )
                if(isExpended == 1) {
                    Box(modifier = Modifier
                        .height(2.dp)
                        .background(Color.White)
                        .fillMaxWidth()
                    )
                }
            }

        }
    }
}


// Chart Handle

@SuppressLint("MutableCollectionMutableState")
@Composable
fun PieChart(
    modifier: Modifier = Modifier,
    startDate: Date,
    endDate: Date,
    isExpended: Int,
    homeUIState: HomeUIState,
    total: Double,
    currentAccount: Int,
) {

    val pieEntries by remember { mutableStateOf(ArrayList<PieEntry>()) }
    val colors by remember { mutableStateOf(mutableListOf<Int>()) }
    val areas = homeUIState.areas

    pieEntries.clear()
    colors.clear()
    val mergedSpendList = mergedSpendList(homeUIState, startDate, endDate, currentAccount, isExpended)
    if(mergedSpendList.isNotEmpty()) {
        for (item in mergedSpendList) {
            pieEntries.add(PieEntry((item.spend.money/total).toFloat()*100))
            colors.add(composeToAndroidColor(getColorArea(areas, item.spend.areaId).toComposeColor()))
        }
    }

    Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        if(total > 0) {
            val pieDataSet = PieDataSet(pieEntries, "").apply {
                setColors(colors)
                sliceSpace = 4f
                valueTextSize = 0f
            }
            val pieData = PieData(pieDataSet)
            PieChartView(data = pieData, modifier)
            Text(
                text = convertMoney(total),
                textAlign = TextAlign.Center,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            pieEntries.add(PieEntry(100f))
            colors.add(android.graphics.Color.GRAY)
            val pieDataSet = PieDataSet(pieEntries, "").apply {
                setColors(colors)
                sliceSpace = 4f
                valueTextSize = 0f
            }
            val pieData = PieData(pieDataSet)
            PieChartView(data = pieData, modifier)
            Text(
                text = "Không có giao dịch nào",
                textAlign = TextAlign.Center,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )
        }

    }
}

@Composable
fun PieChartView(data: PieData, modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            com.github.mikephil.charting.charts.PieChart(context).apply {
                setData(data)
                description = null
                legend.isEnabled = false
                isDrawHoleEnabled = true
                holeRadius = 62f
                setDrawEntryLabels(false)
                setUsePercentValues(false)
                legend.isWordWrapEnabled = true

                setHoleColor(android.graphics.Color.TRANSPARENT)
            }
        },
        update = { pieChart ->
            pieChart.data = data
            pieChart.invalidate() // Cập nhật lại PieChart
        }
    )
}

@Composable
fun HomeContentChart(
    modifier: Modifier = Modifier,
    onAddTransaction: () -> Unit,
    isExpended: Int,
    homeUIState: HomeUIState,
    currentAccount: Int,
    onListDetails: (List<Int>) -> Unit,
) {
    val context = LocalContext.current

    var idListTime by remember { mutableIntStateOf(0) }
    var currentDateOnClick by remember { mutableIntStateOf(0) }
    var currentDateTime by remember { mutableStateOf(getCurrentDateTime("dd/MM/yyyy", currentDateOnClick)) }
    var mondayOfWeek by remember { mutableStateOf(getDayOfWeek("dd/MM/yyyy", currentDateOnClick, Calendar.MONDAY)) }
    var dayOfMonth by remember { mutableStateOf(getDayOfMonth("dd/MM/yyyy", currentDateOnClick)) }
    var currentYear by remember { mutableStateOf(getYear(currentDateOnClick)) }
    var isChooseTime by remember { mutableStateOf(false) }
    var startTime by remember { mutableStateOf(getCurrentDateTime("dd/MM/yyyy", currentDateOnClick)) }
    var endTime by remember { mutableStateOf(getCurrentDateTime("dd/MM/yyyy", currentDateOnClick)) }

    var startDateForChart: Date by remember { mutableStateOf(stringToDate(currentDateTime)) }
    var endDateForChart: Date by remember { mutableStateOf(stringToDate(getCurrentDateTime("dd/MM/yyyy", 1))) }

    var total = 0.0
    if(currentAccount == 0) {
        for(item in homeUIState.spends) {
            if(item.date >= startDateForChart && item.date < endDateForChart && item.type==isExpended) {
                total += item.money
            }
        }
    } else {
        for(item in homeUIState.spends) {
            if(item.date >= startDateForChart && item.date < endDateForChart && item.type==isExpended && item.accountId == currentAccount) {
                total += item.money
            }
        }
    }

    when(idListTime) {
        0 -> {
            startDateForChart = stringToDate(currentDateTime)
            endDateForChart = stringToDate(getCurrentDateTime("dd/MM/yyyy", currentDateOnClick+1))
        }
        1 -> {
            startDateForChart = stringToDate(mondayOfWeek)
            endDateForChart = stringToDate(getDayOfWeek("dd/MM/yyyy", currentDateOnClick+1, Calendar.SUNDAY))
        }
        2 -> {
           startDateForChart = stringToDate(dayOfMonth.first)
            endDateForChart = stringToDate(dayOfMonth.second)
        }
        3 -> {
            startDateForChart = getFirstDayOfYearsAgo(currentDateOnClick)
            endDateForChart = getEndDayOfYearsAgo(currentDateOnClick)
        }
        4 -> {
            startDateForChart = stringToDate(startTime)
            endDateForChart = stringToDate(endTime)
        }
    }

    LazyColumn {
        item {
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .background(Color(38, 38, 38))
                    .padding(bottom = 16.dp, end = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(8.dp)
                ) {
                    LazyRow {
                        items(ListTimeContent.listTimes) { listTime ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .width(IntrinsicSize.Max)
                                    .clickable {
                                        isChooseTime = false
                                        idListTime = listTime.value
                                        currentDateOnClick = 0
                                        currentDateTime = getCurrentDateTime("dd/MM/yyyy", 0)
                                        mondayOfWeek =
                                            getDayOfWeek("dd/MM/yyyy", 0, Calendar.MONDAY)
                                        dayOfMonth = getDayOfMonth("dd/MM/yyyy", 0)
                                        currentYear = getYear(0)
                                        if (idListTime == 4) {
                                            isChooseTime = true
                                            showDatePickerDialog(
                                                context,
                                                "Ngày bắt đầu"
                                            ) { selectedDate ->
                                                if (compareDates(
                                                        formatDate(selectedDate),
                                                        endTime
                                                    ) < 0
                                                ) {
                                                    startTime = formatDate(selectedDate)
                                                    showDatePickerDialog(
                                                        context,
                                                        "Ngày kết thúc"
                                                    ) { selectedDateEnd ->
                                                        if (compareDates(
                                                                startTime,
                                                                formatDate(selectedDateEnd)
                                                            ) < 0
                                                        ) {
                                                            endTime = formatDate(selectedDateEnd)
                                                        }
                                                    }
                                                }

                                            }
                                        }
                                    },
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = stringResource(id = listTime.title),
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(8.dp),
                                        color = if(idListTime == listTime.value) {
                                            Color(44, 160, 52)
                                        } else {
                                            Color(155, 155, 155)
                                        }
                                    )
                                    if(idListTime == listTime.value) {
                                        LineItem()
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.size(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        if(!isChooseTime) {
                            IconButton(
                                onClick = {
                                    currentDateOnClick--
                                    when(idListTime) {
                                        0 -> {
                                            currentDateTime = getCurrentDateTime("dd/MM/yyyy", currentDateOnClick)
                                        }
                                        1 -> {
                                            mondayOfWeek = getDayOfWeek("dd/MM/yyyy", currentDateOnClick, Calendar.MONDAY)
                                        }
                                        2 -> {
                                            dayOfMonth = getDayOfMonth("dd/MM/yyyy", currentDateOnClick)
                                        }
                                        3 -> {
                                            currentYear = getYear(currentDateOnClick)
                                        }
                                    }
                                },
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.ArrowBackIosNew,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                            }
                        } else {
                            IconButton(onClick = {}) {}
                        }

                        Text(
                            text = when(idListTime) {
                                0 -> currentDateTime
                                1 -> mondayOfWeek + " - " + getDayOfWeek("dd/MM/yyyy", currentDateOnClick+1, Calendar.SUNDAY)
                                2 -> dayOfMonth.first + " - " + dayOfMonth.second
                                3 -> currentYear
                                4 -> "$startTime - $endTime"
                                else -> currentDateTime
                            },
                            fontSize = 16.sp,
                            color = Color.White,
                            fontStyle = FontStyle.Italic
                        )

                        if(currentDateOnClick < 0 && !isChooseTime) {
                            IconButton(
                                onClick = {
                                    currentDateOnClick++
                                    when(idListTime) {
                                        0 -> currentDateTime = getCurrentDateTime("dd/MM/yyyy", currentDateOnClick)
                                        1 -> mondayOfWeek = getDayOfWeek("dd/MM/yyyy", currentDateOnClick, Calendar.MONDAY)
                                        2 -> dayOfMonth = getDayOfMonth("dd/MM/yyyy", currentDateOnClick)
                                        3 -> currentYear = getYear(currentDateOnClick)
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.ArrowForwardIos,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                            }
                        } else {
                            IconButton(onClick = {}) {}
                        }
                    }
                    Spacer(modifier = Modifier.size(16.dp))
                    PieChart(
                        Modifier
                            .fillMaxWidth()
                            .height(220.dp),
                        startDate = startDateForChart,
                        endDate = endDateForChart,
                        isExpended = isExpended,
                        homeUIState = homeUIState,
                        total = total,
                        currentAccount = currentAccount,
                    )
                }

                // Add icon
                IconButton(
                    onClick = onAddTransaction,
                    modifier = Modifier
                        .size(60.dp)
                        .background(Color(247, 177, 12), CircleShape)
                        .align(Alignment.BottomEnd),
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
        val listArea = homeUIState.areas
        val mergedSpendList = mergedSpendList(homeUIState, startDateForChart, endDateForChart, currentAccount, isExpended)
        if(mergedSpendList.isNotEmpty()) {
            items(mergedSpendList) { item ->
                SpendItem(
                    modifier = modifier,
                    icon = getIconArea(listArea, item.spend.areaId),
                    title = getNameArea(listArea, item.spend.areaId),
                    color = getColorArea(listArea, item.spend.areaId).toComposeColor(),
                    percent = String.format("%.1f", item.spend.money / total * 100),
                    moneyTotal = item.spend.money,
                    onClick = { onListDetails(item.listId) }
                )
            }
        }
        item {
            Spacer(modifier = Modifier.size(60.dp))
        }
    }
}

fun composeToAndroidColor(composeColor: Color): Int {
    return AndroidColor.argb(
        (composeColor.alpha * 255).toInt(),
        (composeColor.red * 255).toInt(),
        (composeColor.green * 255).toInt(),
        (composeColor.blue * 255).toInt()
    )
}


// Date Handle

fun showDatePickerDialog(context: Context, title: String, onDateSelected: (Calendar) -> Unit) {
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = DatePickerDialog(
        context,
        { _, selectedYear, selectedMonth, selectedDayOfMonth ->
            // Handle when user selects a date
            val selectedDate = Calendar.getInstance()
            selectedDate.set(selectedYear, selectedMonth, selectedDayOfMonth)
            onDateSelected(selectedDate)
        },
        year,
        month,
        dayOfMonth
    )

    datePickerDialog.setTitle(title)
    datePickerDialog.show()
}

fun formatDate(calendar: Calendar): String {
    val dateFormat = "dd/MM/yyyy"
    val simpleDateFormat = java.text.SimpleDateFormat(dateFormat, Locale.getDefault())
    return simpleDateFormat.format(calendar.time)
}

fun compareDates(dateString1: String, dateString2: String): Int {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy")
    val date1 = dateFormat.parse(dateString1)
    val date2 = dateFormat.parse(dateString2)
    return date1.compareTo(date2)
}

fun getFirstDayOfYearsAgo(agoYear: Int): Date {
    val calendar = Calendar.getInstance()

    calendar.add(Calendar.YEAR, agoYear)

    calendar.set(Calendar.MONTH, Calendar.JANUARY)

    calendar.set(Calendar.DAY_OF_MONTH, 1)

    return calendar.time
}

fun getEndDayOfYearsAgo(agoYear: Int): Date {
    val calendar = Calendar.getInstance()

    calendar.add(Calendar.YEAR, agoYear)

    calendar.set(Calendar.MONTH, Calendar.DECEMBER)

    calendar.set(Calendar.DAY_OF_MONTH, 31)

    return calendar.time
}

fun stringToDate(date: String): Date {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy")
    return dateFormat.parse(date)
}

fun dateToString(date: Date, format: String = "dd/MM/yyyy"): String {
    val dateFormat = SimpleDateFormat(format, Locale.getDefault())
    return dateFormat.format(date)
}

fun dateToHour(date: Date): String {
    val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    return dateFormat.format(date)
}

fun getIconArea(listArea: List<Area>, id: Int): Int {
    for (item in listArea) {
        if(item.id == id) {
            return item.icon
        }
    }
    return 0
}

fun getNameArea(listArea: List<Area>, id: Int): String {
    for (item in listArea) {
        if(item.id == id) {
            return item.name
        }
    }
    return ""
}

fun getColorArea(listArea: List<Area>, id: Int): Int {
    for (item in listArea) {
        if(item.id == id) {
            return item.color
        }
    }
    return 0
}

fun mergedSpendList(
    homeUIState: HomeUIState,
    startDate: Date,
    endDate: Date,
    currentAccount: Int,
    isExpended: Int,
): List<SpendWithListId> {
    val listSpend = homeUIState.spends

    return listSpend
        .groupBy { it.areaId }
        .mapNotNull { (areaId, spends) ->
            val filteredSpends = spends.filter { spend ->
                if (currentAccount == 0) {
                    spend.date >= startDate && spend.date < endDate && isExpended==spend.type
                } else {
                    spend.date >= startDate && spend.date < endDate && isExpended==spend.type && spend.accountId == currentAccount
                }
            }

            if (filteredSpends.isNotEmpty()) {
                val initialSpend = filteredSpends.first()
                val initialListId = mutableListOf(initialSpend.id)
                val reducedSpend = filteredSpends.drop(1).fold(initialSpend) { acc, spend ->
                    initialListId.add(spend.id)
                    Spend(
                        id = acc.id, // Giữ nguyên id của Spend đầu tiên
                        areaId = areaId,
                        accountId = acc.accountId,
                        date = acc.date,
                        note = acc.note,
                        img = acc.img,
                        type = acc.type,
                        money = acc.money + spend.money // Tính tổng money
                    )
                }
                SpendWithListId(
                    spend = reducedSpend,
                    listId = initialListId
                )
            } else {
                null
            }
        }
}

@Composable
fun SpendItem(
    modifier: Modifier = Modifier,
    icon: Int = 0,
    title: String = "",
    color: Color,
    percent: String = "",
    moneyTotal: Double,
    onClick: () -> Unit
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
                Text(
                    text = title,
                    modifier = Modifier.padding(start = 10.dp),
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = if(percent!="") { "${percent}%" } else { "" },
                    modifier = Modifier.padding(end = 6.dp),
                    color = Color(155, 155, 155),
                    fontSize = 16.sp
                )
                Text(
                    text = convertMoney(money = moneyTotal) + " đ",
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
        }
    }
}

data class SpendWithListId(
    val spend: Spend,
    val listId: List<Int>
)

