package com.example.personalspending.ui.screen.chart

import android.annotation.SuppressLint
import android.content.Context
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.personalspending.AppBarContent
import com.example.personalspending.HomeTopAppBar
import com.example.personalspending.R
import com.example.personalspending.calculateMonthsBetween
import com.example.personalspending.calculateYearsBetween
import com.example.personalspending.data.Area
import com.example.personalspending.data.Spend
import com.example.personalspending.getCurrentDateTime
import com.example.personalspending.getDayOfMonth
import com.example.personalspending.getDayOfWeek
import com.example.personalspending.getFirstDayOfMonth
import com.example.personalspending.getFirstDayOfWeek
import com.example.personalspending.getFirstDayOfYear
import com.example.personalspending.getYear
import com.example.personalspending.ui.model.AppViewModelProvider
import com.example.personalspending.ui.navigate.NavigationDestination
import com.example.personalspending.ui.screen.account.AccountItem
import com.example.personalspending.ui.screen.account.convertMoney
import com.example.personalspending.ui.screen.home.LineItem
import com.example.personalspending.ui.screen.home.ListTime
import com.example.personalspending.ui.screen.home.composeToAndroidColor
import com.example.personalspending.ui.screen.home.dateToString
import com.example.personalspending.ui.screen.home.getColorArea
import com.example.personalspending.ui.screen.home.getEndDayOfYearsAgo
import com.example.personalspending.ui.screen.home.getFirstDayOfYearsAgo
import com.example.personalspending.ui.screen.home.getIconArea
import com.example.personalspending.ui.screen.home.getNameArea
import com.example.personalspending.ui.screen.home.stringToDate
import com.example.personalspending.ui.screen.transaction.toComposeColor
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.Calendar
import java.util.Locale

object ChartDestination: NavigationDestination {
    override val route: String = "chart"
    override val titleRes: Int = R.string.chart
    const val userIdArg = "userId"
    val routeWithArgs = "$route/{$userIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChartScreen(
    modifier: Modifier = Modifier,
    onAccount: (Int) -> Unit,
    onCategory: (Int)-> Unit,
    onHome: (Int) -> Unit,
    onChart: (Int) -> Unit,
    onPay: (Int) -> Unit,
    onNotify: (Int) -> Unit,
    onPlan: (Int) -> Unit,
    onLogout: () -> Unit,
    onTransactionDetails: (Int, Int) -> Unit,
    viewModel: ChartViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    var isCategory by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                HomeTopAppBar(
                    title = stringResource(R.string.chart),
                    onClick = {
                        isCategory = true
                    },
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
        if(isCategory) {
            AppBarContent(
                accounts = viewModel.chartUIState.accounts,
                user = viewModel.chartUIState.user,
                onClick = { isCategory = false },
                onHome = onHome,
                onAccount = onAccount,
                onCategory = onCategory,
                onChart = onChart,
                onPay = onPay,
                onRemind = onNotify,
                onPlan = onPlan,
                onLogout = onLogout,
            )
        } else {
            ChartContent(
                spends = viewModel.chartUIState.spends,
                listArea = viewModel.chartUIState.areas,
                id = viewModel.chartUIState.id,
                onTransactionDetails = onTransactionDetails,
            )
        }
    }
}

@SuppressLint("MutableCollectionMutableState")
@Composable
fun ChartContent(
    spends: List<Spend>,
    listArea: List<Area>,
    id: Int,
    onTransactionDetails: (Int, Int) -> Unit,
) {
    var isExpended by remember { mutableIntStateOf(0) }
    var idListTime by remember { mutableIntStateOf(0) }
    var itemCurrent by remember { mutableIntStateOf(0) }
    val listItem by remember { mutableStateOf(mutableListOf<ListItem>()) }
    var isItem by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(1f)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 140.dp, start = 10.dp, end = 10.dp, bottom = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                HeaderButton(
                    isExpended = isExpended,
                    onSpend = {
                        isExpended = it
                        isItem = false
                        itemCurrent = 0
                        listItem.clear()
                    }
                )
                Spacer(modifier = Modifier.size(12.dp))
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(38, 38, 38))
                            .padding(bottom = 16.dp, end = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth()
                        ) {
                            LazyRow {
                                items(ListTimeChart.listTimes) { listTime ->
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .width(IntrinsicSize.Max)
                                            .clickable {
                                                listItem.clear()
                                                idListTime = listTime.value
                                                itemCurrent = 0
                                                       },
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = stringResource(id = listTime.title),
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(12.dp),
                                                color = if (idListTime == listTime.value) {
                                                    Color(44, 160, 52)
                                                } else {
                                                    Color(155, 155, 155)
                                                }
                                            )
                                            if (idListTime == listTime.value) {
                                                LineItem()
                                            }
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.size(60.dp))
                            if(isExpended == 2) {
                                ChartMulColumn(
                                    spends = spends,
                                    idListTime = idListTime,
                                )
                            } else {
                                ChartStack(
                                    spends = spends,
                                    idListTime = idListTime,
                                    isExpended = isExpended,
                                    listArea = listArea,
                                    onItemCurrent = {
                                        itemCurrent = it
                                        isItem = true
                                                    },
                                    onListItem = {
                                        listItem.clear()
                                        listItem.addAll(it)
                                    }
                                )
                            }
                        }
                    }
                    if(listItem.isNotEmpty() && isItem && itemCurrent != 0) {
                        for (item in listItem) {
                            for (spend in spends) {
                                if(item.id == spend.id && item.x == itemCurrent) {
                                    Spacer(modifier = Modifier.size(12.dp))
                                    AccountItem(
                                        modifier = Modifier.clip(RoundedCornerShape(10.dp)),
                                        icon = getIconArea(listArea, spend.areaId),
                                        name = getNameArea(listArea, spend.areaId),
                                        color = getColorArea(listArea, spend.areaId).toComposeColor(),
                                        money = spend.money,
                                        onSelect = {
                                                   onTransactionDetails(id, item.id)
                                        },
                                        nameAccount = dateToString(spend.date)
                                    )
                                }
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
fun ChartMulColumn(
    spends: List<Spend>,
    idListTime: Int,
) {
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            ChartItem(
                context = context,
                spends = spends,
                idListTime = idListTime,
            )
            CustomLegend()
        }
    }
}

@Composable
fun CustomLegend() {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 26.dp)
    ) {
        LegendItem(ColorTemplate.COLORFUL_COLORS[0], "- thu nhập")
        Spacer(modifier = Modifier.width(8.dp))
        LegendItem(ColorTemplate.COLORFUL_COLORS[1], "- chi phí")
        Spacer(modifier = Modifier.width(8.dp))
        LegendItem(ColorTemplate.COLORFUL_COLORS[3], "- lợi nhuận")
        Spacer(modifier = Modifier.width(8.dp))
        LegendItem(ColorTemplate.COLORFUL_COLORS[2], "- lỗ")
    }
}

@Composable
fun LegendItem(color: Int, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(Color(color))
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = text, fontSize = 11.sp, color = Color.White)
    }
}

@Composable
fun ChartItem(
    context: Context,
    spends: List<Spend>,
    idListTime: Int,
) {
    val yVals1 = ArrayList<BarEntry>()
    val yVals2 = ArrayList<BarEntry>()
    val yVals3 = ArrayList<BarEntry>()
    val yVals4 = ArrayList<BarEntry>()
    val listSpend = spends.sortedBy { it.date }.groupBy { it.date }
    var countWeek = -3
    var dateFirst = if(idListTime==0) {
        stringToDate(getCurrentDateTime("dd/MM/yyyy", -3))
    } else if(idListTime==1) {
        stringToDate(getDayOfWeek("dd/MM/yyyy", countWeek, Calendar.MONDAY))
    } else if(idListTime==2) {
        stringToDate(getDayOfMonth("dd/MM/yyyy", countWeek).first)
    } else {
        getFirstDayOfYearsAgo(countWeek)
    }
    val currentDate = stringToDate(getCurrentDateTime("dd/MM/yyyy", 0))
    if(spends.isNotEmpty()) {
        if(dateFirst > spends.first().date) {
            if(idListTime==0) {
                dateFirst = spends.first().date
            } else if(idListTime==1) {
                dateFirst = getFirstDayOfWeek(spends.first().date)
                countWeek = -ChronoUnit.DAYS.between(dateFirst.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                    currentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()).toInt()/7
            } else if(idListTime==2) {
                dateFirst = getFirstDayOfMonth(spends.first().date)
                countWeek = - calculateMonthsBetween(dateFirst, currentDate).toInt()
            } else if(idListTime==3) {
                dateFirst = getFirstDayOfYear(spends.first().date)
                countWeek = - calculateYearsBetween(dateFirst, currentDate).toInt()
            }
        }
    }

    val calendar = Calendar.getInstance()
    calendar.time = dateFirst

    var count = 0
    val labels: MutableList<String> = mutableListOf()

    if(idListTime==0) {
        labels.clear()
        while (calendar.time <= currentDate) {
            var isDate = false
            for ((date, spendsOfDay) in listSpend) {
                if(calendar.time == date) {
                    var totalSpend = 0.0
                    var totalIncome = 0.0
                    for (spend in spendsOfDay) {
                        if (spend.type == 0) {
                            totalSpend+=spend.money
                        } else {
                            totalIncome+=spend.money
                        }
                    }
                    count++
                    yVals1.add(BarEntry(count.toFloat(), totalIncome.toFloat()))
                    yVals2.add(BarEntry(count.toFloat(), totalSpend.toFloat()))
                    if(totalIncome-totalSpend > 0) {
                        yVals3.add(BarEntry(count.toFloat(), (totalIncome-totalSpend).toFloat()))
                    } else {
                        yVals3.add(BarEntry(count.toFloat(), 0f))
                    }
                    if(totalSpend-totalIncome > 0) {
                        yVals4.add(BarEntry(count.toFloat(), (totalSpend-totalIncome).toFloat()))
                    } else {
                        yVals4.add(BarEntry(count.toFloat(), 0f))
                    }
                    labels.add(SimpleDateFormat("dd/MM", Locale.getDefault()).format(date))
                    isDate = true
                }
            }
            if (!isDate) {
                count++
                yVals1.add(BarEntry(count.toFloat(), 0f))
                yVals2.add(BarEntry(count.toFloat(), 0f))
                yVals3.add(BarEntry(count.toFloat(), 0f))
                yVals4.add(BarEntry(count.toFloat(), 0f))
                labels.add(SimpleDateFormat("dd/MM", Locale.getDefault()).format(calendar.time))
            }
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
    } else {
        labels.clear()
        while (countWeek<=0) {
            var isDate = false
            var totalSpend = 0.0
            var totalIncome = 0.0
            var dateStart = stringToDate(getDayOfWeek("dd/MM/yyyy", countWeek, Calendar.MONDAY))
            var dateEnd = stringToDate(getDayOfWeek("dd/MM/yyyy", countWeek, Calendar.SUNDAY))
            if(idListTime==2) {
                dateStart = stringToDate(getDayOfMonth("dd/MM/yyyy", countWeek).first)
                dateEnd = stringToDate(getDayOfMonth("dd/MM/yyyy", countWeek).second)
            } else if(idListTime==3) {
                dateStart = getFirstDayOfYearsAgo(countWeek)
                dateEnd = getEndDayOfYearsAgo(countWeek)
            }
            for ((date, spendsOfDay) in listSpend) {
                if(date in dateStart..dateEnd) {
                    for (spend in spendsOfDay) {
                        if (spend.type == 0) {
                            totalSpend+=spend.money
                        } else {
                            totalIncome+=spend.money
                        }
                    }
                    isDate = true
                }
            }
            if(isDate) {
                count++
                yVals1.add(BarEntry(count.toFloat(), totalIncome.toFloat()))
                yVals2.add(BarEntry(count.toFloat(), totalSpend.toFloat()))
                if(totalIncome-totalSpend > 0) {
                    yVals3.add(BarEntry(count.toFloat(), (totalIncome-totalSpend).toFloat()))
                } else {
                    yVals3.add(BarEntry(count.toFloat(), 0f))
                }
                if(totalSpend-totalIncome > 0) {
                    yVals4.add(BarEntry(count.toFloat(), (totalSpend-totalIncome).toFloat()))
                } else {
                    yVals4.add(BarEntry(count.toFloat(), 0f))
                }
            } else {
                count++
                yVals1.add(BarEntry(count.toFloat(), 0f))
                yVals2.add(BarEntry(count.toFloat(), 0f))
                yVals3.add(BarEntry(count.toFloat(), 0f))
                yVals4.add(BarEntry(count.toFloat(), 0f))
            }
            if(idListTime==1) {
                labels.add(getDayOfWeek("dd/MM", countWeek, Calendar.MONDAY))
            } else if(idListTime==2) {
                labels.add("Thg " + getDayOfMonth("MM", countWeek).first)
            } else if(idListTime==3) {
                labels.add(getYear(countWeek))
            }
            countWeek++
        }
    }

    val set1 = BarDataSet(yVals1, " - thu nhập  ").apply {
        color = ColorTemplate.COLORFUL_COLORS[0]
    }
    val set2 = BarDataSet(yVals2, "- chi phí ").apply {
        color = ColorTemplate.COLORFUL_COLORS[1]
    }
    val set3 = BarDataSet(yVals3, " - lợi nhuận ").apply {
        color = ColorTemplate.COLORFUL_COLORS[3]
    }
    val set4 = BarDataSet(yVals4, " - lỗ ").apply {
        color = ColorTemplate.COLORFUL_COLORS[2]
    }

    val data = BarData(set1, set2, set3, set4).apply {
        barWidth = 0.2f // Thiết lập độ rộng cột
        setDrawValues(false)
    }

    val barWidth = 0.2f

    data.barWidth = barWidth
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        reverseLayout = true,
    ) {
        item {
            Box(
                modifier = Modifier
                    .height(240.dp)
                    .width((count * 80).dp)
            ) {
                AndroidView(
                    factory = {
                        BarChart(context).apply {
                            layoutParams = RelativeLayout.LayoutParams(
                                RelativeLayout.LayoutParams.MATCH_PARENT,
                                RelativeLayout.LayoutParams.MATCH_PARENT
                            )

                            this.data = data

                            xAxis.apply {
                                position = XAxis.XAxisPosition.BOTTOM
                                axisLineWidth = 2f // Độ dày của đường trục X
                                setDrawGridLines(false)
                                setDrawAxisLine(true)
                                setCenterAxisLabels(true)
                                valueFormatter = IndexAxisValueFormatter(labels)
                                granularity = 1f
                            }

                            axisLeft.apply {
                                setDrawGridLines(false)
                                setDrawLabels(false)
                                setDrawAxisLine(false)
                                axisMinimum = 0f // Đảm bảo trục Y bắt đầu từ 0
                                axisLineWidth = 2f // Độ dày của đường trục Y
                            }

                            axisRight.isEnabled = false

                            setDrawGridBackground(false)
                            setDrawBorders(false)

                            val groupSpace = 0.1f
                            val barSpace = 0.02f

                            data.barWidth = barWidth
                            xAxis.axisMinimum = 0f
                            xAxis.axisMaximum = data.xMax
                            groupBars(0f, groupSpace, barSpace)

                            description.isEnabled = false
                            legend.apply {
                                verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                                horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                                orientation = Legend.LegendOrientation.HORIZONTAL
                                legend.textColor = android.graphics.Color.WHITE
                                legend.isEnabled = false
                                setDrawInside(false)
                                form = Legend.LegendForm.SQUARE
                                formSize = 9f
                                textSize = 11f
                            }
                            setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                                override fun onValueSelected(e: Entry?, h: Highlight?) {
                                    e?.let { its ->
                                        val y = its.y
                                        Toast.makeText(context, "Số tiền: $y đ", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                override fun onNothingSelected() {}
                            })
                            invalidate()
                        }
                    },
                    update = {
                        it.data = data
                        it.xAxis.apply {
                            position = XAxis.XAxisPosition.BOTTOM
                            setDrawGridLines(false)
                            setDrawAxisLine(true)
                            setCenterAxisLabels(true)
                            valueFormatter = IndexAxisValueFormatter(labels)
                            textColor = android.graphics.Color.WHITE
                            granularity = 1f
                        }
                        it.axisLeft.apply {
                            setDrawGridLines(false)
                            setDrawLabels(false)
                            axisLineWidth = 2f // Độ dày của đường trục X
                            setDrawAxisLine(false)
                        }
                        it.axisRight.isEnabled = false
                        it.setDrawGridBackground(false)
                        it.setDrawBorders(false)
                        it.setFitBars(true)

                        val groupSpace = 0.1f
                        val barSpace = 0.02f

                        data.barWidth = barWidth
                        it.xAxis.axisMinimum = 0f
                        it.xAxis.axisMaximum = data.xMax
                        it.groupBars(0f, groupSpace, barSpace)

                        it.invalidate()
                    }
                )
            }
        }
    }
}

@Composable
fun ChartStack(
    spends: List<Spend>,
    idListTime: Int,
    isExpended: Int,
    listArea: List<Area>,
    onItemCurrent: (Int) -> Unit,
    onListItem: (MutableList<ListItem>) -> Unit,
) {
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            ChartStackItem(
                context = context,
                spends = spends.filter { it.type==isExpended },
                idListTime = idListTime,
                listArea = listArea,
                onItemCurrent = onItemCurrent,
                onListItem = onListItem
            )
        }
    }

}

@Composable
fun ChartStackItem(
    context: Context,
    spends: List<Spend>,
    idListTime: Int,
    listArea: List<Area>,
    onItemCurrent: (Int) -> Unit,
    onListItem: (MutableList<ListItem>) -> Unit,
) {
    val yVals1 = mutableListOf<BarEntry>()
    val listSpend = spends.sortedBy { it.date }.groupBy { it.date }
    var countWeek = -4
    var dateFirst = if(idListTime==0) {
        stringToDate(getCurrentDateTime("dd/MM/yyyy", -4))
    } else if(idListTime==1) {
        stringToDate(getDayOfWeek("dd/MM/yyyy", countWeek, Calendar.MONDAY))
    } else if(idListTime==2) {
        stringToDate(getDayOfMonth("dd/MM/yyyy", countWeek).first)
    } else {
        getFirstDayOfYearsAgo(countWeek)
    }
    val currentDate = stringToDate(getCurrentDateTime("dd/MM/yyyy", 0))
    if(spends.isNotEmpty()) {
        if(dateFirst > spends.first().date) {
            if(idListTime==0) {
                dateFirst = spends.first().date
            } else if(idListTime==1) {
                dateFirst = getFirstDayOfWeek(spends.first().date)
                countWeek = -ChronoUnit.DAYS.between(dateFirst.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                    currentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()).toInt()/7
            } else if(idListTime==2) {
                dateFirst = getFirstDayOfMonth(spends.first().date)
                countWeek = - calculateMonthsBetween(dateFirst, currentDate).toInt()
            } else if(idListTime==3) {
                dateFirst = getFirstDayOfYear(spends.first().date)
                countWeek = - calculateYearsBetween(dateFirst, currentDate).toInt()
            }
        }
    }

    val calendar = Calendar.getInstance()
    calendar.time = dateFirst

    var count = 0
    val labels: MutableList<String> = mutableListOf()
    val listItem: MutableList<ListItem> = mutableListOf()

    val listColor = mutableListOf<Int>()

    if(idListTime==0) {
        labels.clear()
        listColor.clear()
        labels.add("")
        while (calendar.time <= currentDate) {
            var isDate = false
            val floatList = mutableListOf<Float>()
            for ((date, spendsOfDay) in listSpend) {
                if(calendar.time == date) {
                    for (spend in spendsOfDay) {
                        floatList.add(spend.money.toFloat())
                        listColor.add(composeToAndroidColor(getColorArea(listArea, spend.areaId).toComposeColor()))
                        listItem.add(ListItem(count+1, spend.id))
                    }
                    count++
                    isDate = true
                }
            }
            if(isDate) {
                labels.add(SimpleDateFormat("dd/MM", Locale.getDefault()).format(calendar.time))
                yVals1.add(BarEntry(count.toFloat(), floatList.toFloatArray()))
            } else {
                count++
                yVals1.add(BarEntry(count.toFloat(), 0f))
                labels.add(SimpleDateFormat("dd/MM", Locale.getDefault()).format(calendar.time))
                listColor.add(android.graphics.Color.TRANSPARENT)
            }
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
    }  else {
        labels.clear()
        listColor.clear()
        labels.add("")
        while (countWeek<=0) {
            var isDate = false
            val floatList = mutableListOf<Float>()
            var dateStart = stringToDate(getDayOfWeek("dd/MM/yyyy", countWeek, Calendar.MONDAY))
            var dateEnd = stringToDate(getDayOfWeek("dd/MM/yyyy", countWeek, Calendar.SUNDAY))
            if(idListTime==2) {
                dateStart = stringToDate(getDayOfMonth("dd/MM/yyyy", countWeek).first)
                dateEnd = stringToDate(getDayOfMonth("dd/MM/yyyy", countWeek).second)
            } else if(idListTime==3) {
                dateStart = getFirstDayOfYearsAgo(countWeek)
                dateEnd = getEndDayOfYearsAgo(countWeek)
            }
            for ((date, spendsOfDay) in listSpend) {
                if(date in dateStart..dateEnd) {
                    for (spend in spendsOfDay) {
                        floatList.add(spend.money.toFloat())
                        listColor.add(composeToAndroidColor(getColorArea(listArea, spend.areaId).toComposeColor()))
                        listItem.add(ListItem(count+1, spend.id))
                    }
                    isDate = true
                }
            }
            if(isDate) {
                count++
                yVals1.add(BarEntry(count.toFloat(), floatList.toFloatArray()))
            } else {
                count++
                yVals1.add(BarEntry(count.toFloat(), 0f))
                listColor.add(android.graphics.Color.TRANSPARENT)
            }
            if(idListTime==1) {
                labels.add(getDayOfWeek("dd/MM", countWeek, Calendar.MONDAY))
            } else if(idListTime==2) {
                labels.add("Thg " + getDayOfMonth("MM", countWeek).first)
            } else if(idListTime==3) {
                labels.add(getYear(countWeek))
            }
            countWeek++
        }
    }
    onListItem(listItem)

    val set1 = BarDataSet(yVals1, "Stacked Data").apply {
        colors = listColor
    }

    val data = BarData(set1).apply {
        barWidth = 0.6f
        setDrawValues(false)
    }
    var isHighLight by remember { mutableStateOf(false) }

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        reverseLayout = true,
    ) {
        item {
            Box(
                modifier = Modifier
                    .height(240.dp)
                    .width((count * 60).dp)
                    .padding(8.dp)
            ) {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = {
                        BarChart(context).apply {
                            layoutParams = RelativeLayout.LayoutParams(
                                RelativeLayout.LayoutParams.MATCH_PARENT,
                                RelativeLayout.LayoutParams.MATCH_PARENT
                            )

                            this.data = data

                            xAxis.apply {
                                position = XAxis.XAxisPosition.BOTTOM
                                legend.textColor = android.graphics.Color.WHITE
                                axisLineWidth = 2f
                                setDrawGridLines(false)
                                setDrawAxisLine(true)
                                setCenterAxisLabels(false)
                                valueFormatter = IndexAxisValueFormatter(labels)
                                granularity = 1f
                                spaceMin = 0.5f
                                textColor = android.graphics.Color.WHITE
                            }

                            axisLeft.apply {
                                setDrawGridLines(false)
                                setDrawLabels(true)
                                setDrawAxisLine(true)
                                axisMinimum = 0f // Đảm bảo trục Y bắt đầu từ 0
                            }

                            axisRight.isEnabled = false

                            setDrawGridBackground(false)
                            setDrawBorders(false)
                            legend.isEnabled = false // Ẩn chú thích

                            description.isEnabled = false
                            legend.apply {
                                verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                                horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                                orientation = Legend.LegendOrientation.HORIZONTAL
                                legend.textColor = android.graphics.Color.WHITE
                                setDrawInside(false)
                            }

                            setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                                override fun onValueSelected(e: Entry?, h: Highlight?) {
                                    e?.let { its ->
                                        isHighLight = true
                                        val x = its.x
                                        onItemCurrent(x.toInt())
                                    }
                                }
                                override fun onNothingSelected() {}
                            })
                            invalidate()
                        }
                    },
                    update = {
                        if(isHighLight) {
                            it.highlightValues(null) // Reset highlight
                        }
                        it.data = data
                        it.xAxis.apply {
                            position = XAxis.XAxisPosition.BOTTOM
                            textColor = android.graphics.Color.WHITE
                            setDrawGridLines(false)
                            setDrawAxisLine(true)
                            setCenterAxisLabels(false)
                            valueFormatter = IndexAxisValueFormatter(labels)
                            granularity = 1f
                            spaceMin = 0.5f
                        }
                        it.axisLeft.apply {
                            setDrawGridLines(false)
                            setDrawLabels(false)
                            setDrawAxisLine(false)
                            axisMinimum = 0f
                        }

                        it.axisRight.isEnabled = false
                        it.setDrawGridBackground(false)
                        it.setDrawBorders(false)
                        it.setFitBars(true)

                        it.invalidate()
                    }
                )
            }
        }
    }
}

@Composable
fun HeaderButton(
    modifier: Modifier = Modifier,
    isExpended: Int,
    onSpend: (Int) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(start = 10.dp, end = 10.dp)
                .clickable { onSpend(2) },
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "CHUNG",
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 8.dp),
                    color = if(isExpended == 2) {
                        Color.White
                    } else {
                        Color(155, 155, 155)
                    }
                )
                Box(modifier =
                    if(isExpended == 2) {
                        Modifier
                            .height(2.dp)
                            .fillMaxWidth()
                            .background(Color.White)
                    } else {
                        Modifier
                            .height(2.dp)
                            .fillMaxWidth()
                            .background(Color.Transparent)
                    }
                )
            }
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(start = 10.dp, end = 10.dp)
                .clickable { onSpend(0) },
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.expense),
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 8.dp),
                    color = if(isExpended == 0) {
                        Color.White
                    } else {
                        Color(155, 155, 155)
                    }
                )
                Box(modifier =
                    if(isExpended == 0) {
                        Modifier
                            .height(2.dp)
                            .fillMaxWidth()
                            .background(Color.White)
                    } else {
                        Modifier
                            .height(2.dp)
                            .fillMaxWidth()
                            .background(Color.Transparent)
                    }
                )
            }
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(start = 10.dp, end = 10.dp)
                .clickable { onSpend(1) },
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.income),
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 8.dp),
                    color = if(isExpended == 1) {
                        Color.White
                    } else {
                        Color(155, 155, 155)
                    }
                )
                Box(modifier =
                    if(isExpended == 1) {
                        Modifier
                            .height(2.dp)
                            .fillMaxWidth()
                            .background(Color.White)
                    } else {
                        Modifier
                            .height(2.dp)
                            .fillMaxWidth()
                            .background(Color.Transparent)
                    }
                )
            }

        }
    }
}

object ListTimeChart {
    val listTimes = listOf<ListTime>(
        ListTime(
            title = R.string.day_chart,
            value = 0
        ),
        ListTime(
            title = R.string.week_chart,
            value = 1
        ),
        ListTime(
            title = R.string.month_chart,
            value = 2
        ),
        ListTime(
            title = R.string.year_chart,
            value = 3
        ),
    )
}
class ListItem(
    val x: Int,
    val id: Int,
)

@Preview(showBackground = true)
@Composable
fun ChartPreview() {
//    ChartStack()
//    ChartScreen()
}