package com.example.personalspending

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Dehaze
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.NoteAlt
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.personalspending.ui.navigate.SpendingNavGraph
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar
import java.util.Locale
import com.example.personalspending.data.Account
import com.example.personalspending.data.User
import com.example.personalspending.ui.screen.account.convertMoney
import com.example.personalspending.ui.screen.login.saveUserId
import java.time.DayOfWeek
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters
import java.util.Date

@Composable
fun SpendingApp(
    navController: NavHostController = rememberNavController()
) {
    SpendingNavGraph(navController = navController)
}


@Composable
fun AppBarContent(
    accounts: List<Account>,
    user: User,
    onAccount: (Int) -> Unit = {},
    onHome: (Int) -> Unit = {},
    onCategory: (Int) -> Unit = {},
    onChart: (Int) -> Unit = {},
    onRemind: (Int) -> Unit = {},
    onPay: (Int) -> Unit = {},
    onPlan: (Int) -> Unit = {},
    onLogout: () -> Unit = {},
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0f, 0f, 0f, alpha = 0.4f))
            .zIndex(100f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .width(330.dp)
                .background(Color(22, 89, 49))
        ) {
            UserInfo(
                accounts = accounts,
                user = user,
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(Color(162, 162, 162))
            )
            Spacer(modifier = Modifier.size(20.dp))
            ListCategory(
                onBack = onClick,
                onAccount = { onAccount(user.id) },
                onHome = { onHome(user.id) },
                onCategory = { onCategory(user.id) },
                onChart = { onChart(user.id) },
                onRemind = { onRemind(user.id) },
                onPay = { onPay(user.id) },
                onPlan = { onPlan(user.id) },
                onLogout = { onLogout() },
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize(1f)
                .clickable { onClick() }
        )
    }
}

@Composable
fun UserInfo(
    accounts: List<Account>,
    user: User,
    modifier: Modifier = Modifier
) {
    var total = 0.0
    for (item in accounts) {
        total += item.money
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .aspectRatio(1f)
                .background(Color(255, 132, 0), shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = user.name.first().uppercase(),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 30.sp
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = user.name,
                fontSize = 20.sp,
                color = Color.White
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = "Số dư: " + convertMoney(money = total) + "đ",
                fontSize = 16.sp,
                color = Color.White
            )
        }
    }
}

@Composable
fun ListCategory(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    onAccount: () -> Unit,
    onHome: () -> Unit,
    onCategory: () -> Unit,
    onChart: () -> Unit,
    onRemind: () -> Unit = {},
    onPay: () -> Unit = {},
    onPlan: () -> Unit = {},
    onLogout: () -> Unit,
) {
    val context = LocalContext.current
    Column {
        TextCategoryItem(
            modifier = modifier,
            title = R.string.home,
            onNext = onHome,
            imageVector = Icons.Filled.Home,
        )
        TextCategoryItem(
            modifier = modifier,
            title = R.string.account,
            onNext = onAccount,
            imageVector = Icons.Filled.ManageAccounts,
        )
        TextCategoryItem(
            modifier = modifier,
            title = R.string.chart,
            onNext = onChart,
            imageVector = Icons.Filled.BarChart,
        )
        TextCategoryItem(
            modifier = modifier,
            title = R.string.category,
            onNext = onCategory,
            imageVector = Icons.Filled.Category,
        )
        TextCategoryItem(
            modifier = modifier,
            title = R.string.notify,
            onNext = onRemind,
            imageVector = Icons.Filled.ManageAccounts,
        )
        TextCategoryItem(
            modifier = modifier,
            title = R.string.pay,
            onNext = onPay,
            imageVector = Icons.Filled.BarChart,
        )
        TextCategoryItem(
            modifier = modifier,
            title = R.string.pland,
            onNext = onPlan,
            imageVector = Icons.Filled.NoteAlt,
        )
        TextCategoryItem(
            modifier = modifier,
            title = R.string.logout,
            onNext = {
                onLogout()
                saveUserId(context, 0)
            },
            imageVector = Icons.AutoMirrored.Filled.Logout,
        )
        Spacer(modifier = Modifier.weight(1f))
        TimeBottom(onBack = onBack)
    }
}

@Composable
fun TextCategoryItem(
    modifier: Modifier = Modifier,
    title: Int,
    onNext: () -> Unit,
    imageVector: ImageVector,
) {
    OutlinedButton(
        onClick = { onNext() },
        modifier = modifier
            .background(Color(22, 89, 49))
            .fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(Color(22, 89, 49)),
        border = null,
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = imageVector,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = stringResource(title),
                textAlign = TextAlign.Left,
                fontSize = 18.sp,
                color = Color.White
            )
            Spacer(modifier = Modifier.weight(1f))

        }
    }
}

@Composable
fun TimeBottom(
    onBack: () -> Unit
) {
    var currentDateTime by remember { mutableStateOf(getCurrentDateTime("dd/MM/yyyy     HH:mm", 0)) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            currentDateTime = getCurrentDateTime("dd/MM/yyyy     HH:mm", 0)
        }
    }

    Row(
        modifier = Modifier.padding(16.dp).padding(bottom = 50.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(Color(0f, 0f, 0f, 0.2f), CircleShape)
                .padding(12.dp)
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Text(
            text = currentDateTime,
            color = Color.White,
            fontSize = 16.sp,
            modifier = Modifier.padding(start = 20.dp)
        )
    }
}

object NotificationUtil {
    private const val CHANNEL_ID = "example_channel_id"
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Example Channel"
            val descriptionText = "This is an example notification channel"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun sendNotification(context: Context, title: String, text: String, notificationId: Int) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.cart_shopping_solid)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            notify(notificationId, builder.build())
        }
    }
}

fun getCurrentDateTime(displayType: String, nextDay: Int): String {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DAY_OF_YEAR, nextDay)

    val dateFormat = SimpleDateFormat(displayType, Locale.getDefault())
    return dateFormat.format(calendar.time)
}

fun getNextDate(date: Date, nextDay: Int = 1): String {
    val calendar = Calendar.getInstance()
    calendar.time = date
    calendar.add(Calendar.DAY_OF_YEAR, nextDay)

    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return dateFormat.format(calendar.time)
}

fun getDayOfWeek(displayType: String, nextWeek: Int, day: Int): String {
    val calendar = Calendar.getInstance()

    calendar.set(Calendar.DAY_OF_WEEK, day)

    calendar.add(Calendar.DAY_OF_YEAR, nextWeek*7)

    val dateFormat = SimpleDateFormat(displayType, Locale.getDefault())
    return dateFormat.format(calendar.time)
}

fun getFirstDayOfWeek(date: Date): Date {
    val localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
    val firstDayOfWeek = localDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    return Date.from(firstDayOfWeek.atStartOfDay(ZoneId.systemDefault()).toInstant())
}

fun getFirstDayOfMonth(date: Date): Date {
    val localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
    val firstDayOfMonth = localDate.with(TemporalAdjusters.firstDayOfMonth())
    return Date.from(firstDayOfMonth.atStartOfDay(ZoneId.systemDefault()).toInstant())
}

fun getDayOfMonth(displayType: String, nextMonth: Int): Pair<String, String> {
    val calendar = Calendar.getInstance()

    calendar.set(Calendar.DAY_OF_MONTH, 1)

    calendar.add(Calendar.MONTH, nextMonth)

    val firstDayOfMonth = calendar.time

    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
    val lastDayOfMonth = calendar.time

    val dateFormat = SimpleDateFormat(displayType, Locale.getDefault())
    val firstDayString = dateFormat.format(firstDayOfMonth)
    val lastDayString = dateFormat.format(lastDayOfMonth)

    return Pair(firstDayString, lastDayString)
}

fun getFirstDayOfYear(date: Date): Date {
    val localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
    val firstDayOfYear = localDate.with(TemporalAdjusters.firstDayOfYear())
    return Date.from(firstDayOfYear.atStartOfDay(ZoneId.systemDefault()).toInstant())
}

fun getYear(nextYear: Int): String {
    return LocalDate.now().plusYears(nextYear.toLong()).year.toString()
}

fun calculateDateBetween(startDate: Date, endDate: Date): Long {
    // Chuyển đổi Date sang LocalDate
    val startLocalDate = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
    val endLocalDate = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()

    // Tính số tháng giữa hai ngày
    return ChronoUnit.DAYS.between(startLocalDate, endLocalDate)
}

fun calculateMonthsBetween(startDate: Date, endDate: Date): Long {
    // Chuyển đổi Date sang LocalDate
    val startLocalDate = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
    val endLocalDate = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()

    // Tính số tháng giữa hai ngày
    return ChronoUnit.MONTHS.between(startLocalDate, endLocalDate)
}

fun calculateYearsBetween(startDate: Date, endDate: Date): Long {
    // Chuyển đổi Date sang LocalDate
    val startLocalDate = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
    val endLocalDate = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()

    // Tính số năm giữa hai ngày
    return ChronoUnit.YEARS.between(startLocalDate, endLocalDate)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBar(
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    title: String,
    onClick: () -> Unit,
    backButton: Boolean = false,
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                fontSize = 20.sp,
                color = Color.White
            )
        },
        modifier = modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(
                    topStart = 0.dp,
                    topEnd = 0.dp,
                    bottomStart = 20.dp,
                    bottomEnd = 20.dp
                )
            ),
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.smallTopAppBarColors(
            Color(22, 89, 49)
        ),
        navigationIcon = {
            if(!backButton) {
                IconButton(
                    onClick = onClick ,
                    modifier = Modifier.size(100.dp),
                ) {
                    Icon(
                        imageVector = Icons.Filled.Dehaze,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
            } else {
                IconButton(
                    onClick = onClick ,
                    modifier = Modifier.size(100.dp),
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun TopAppBarPreview() {
//    HomeTopAppBar(onClick = {})
}
