package com.example.personalspending.ui.screen.pay

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.compose.ui.res.stringResource
import androidx.core.app.NotificationCompat
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.personalspending.NotificationUtil
import com.example.personalspending.R
import com.example.personalspending.SpendingApplication
import com.example.personalspending.calculateDateBetween
import com.example.personalspending.calculateMonthsBetween
import com.example.personalspending.data.Account
import com.example.personalspending.data.AutoPay
import com.example.personalspending.data.Notify
import com.example.personalspending.data.Planed
import com.example.personalspending.data.Spend
import com.example.personalspending.getCurrentDateTime
import com.example.personalspending.getNextDate
import com.example.personalspending.repository.SpendingRepository
import com.example.personalspending.ui.screen.home.dateToString
import com.example.personalspending.ui.screen.home.getIconArea
import com.example.personalspending.ui.screen.home.getNameArea
import com.example.personalspending.ui.screen.home.stringToDate
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class MyWorker(context: Context, workerParams: WorkerParameters) : CoroutineWorker(context, workerParams) {
    private val spendingRepository: SpendingRepository = (context.applicationContext as SpendingApplication).container.spendingRepository

    override suspend fun doWork(): Result {
        while (true) {
            val userId = inputData.getInt("userId", 0)
            updateSQLiteDatabase(userId)
            delay(59000) // Delay 60 giây trước khi lặp lại công việc
        }

    }

    private suspend fun updateSQLiteDatabase(userId: Int) {
        val accounts = spendingRepository.getAccountByUser(userId).firstOrNull() ?: emptyList()
        val categories = spendingRepository.getAreaByUser(userId).firstOrNull() ?: emptyList()

        val pays = mutableListOf<AutoPay>()
        val plans = mutableListOf<Planed>()
        val spends = mutableListOf<Spend>()
        for (item in accounts) {
            pays.addAll(spendingRepository.getPayByAccount(item.id).firstOrNull() ?: emptyList())
            plans.addAll(spendingRepository.getPlanByAccount(item.id).firstOrNull() ?: emptyList())
            spends.addAll(spendingRepository.getSpend(item.id).firstOrNull() ?: emptyList())
        }

        val notifies = spendingRepository.getNotifyByAccount(userId).firstOrNull() ?: emptyList()

        pays.forEach { pay ->
            var currentDate = stringToDateTime(getCurrentDateTime("HH:mm dd/MM/yyyy", 0))
            if(pay.auto) {
                when(pay.quantityRemind) {
                    0 -> if (pay.currentDate == currentDate) {
                        currentDate = stringToDate(getCurrentDateTime("dd/MM/yyyy", 0))
                        val spend = Spend(
                            id = 0,
                            accountId = pay.accountId,
                            areaId = pay.areaId,
                            money = pay.money,
                            date = currentDate,
                            note = pay.note,
                            type = 0,
                            img = ""
                        )
                        if (getMoney(accounts, pay.accountId) >= pay.money) {
                            spendingRepository.insertSpend(spend)
                            spendingRepository.updateAccountById(
                                pay.accountId,
                                getMoney(accounts, pay.accountId) - pay.money
                            )
                            spendingRepository.deletePay(pay.id)
                            sendNotification(
                                "Thanh toán",
                                "Bạn đã thanh toán ${pay.money} đ cho " + pay.name,
                                getIconArea(categories, pay.areaId)
                            )
                        }
                    }
                    else -> if (pay.currentDate == currentDate) {
                        val spend = Spend(
                            id = 0,
                            accountId = pay.accountId,
                            areaId = pay.areaId,
                            money = pay.money,
                            date = currentDate,
                            note = pay.note,
                            type = 0,
                            img = ""
                        )
                        if (getMoney(accounts, pay.accountId) >= pay.money) {
                            spendingRepository.insertSpend(spend)
                            spendingRepository.updateAccountById(
                                pay.accountId,
                                getMoney(accounts, pay.accountId) - pay.money
                            )
                            sendNotification(
                                "Thanh toán",
                                "Bạn đã thanh toán ${pay.money} đ cho " + pay.name,
                                getIconArea(categories, pay.areaId)
                            )
                        }
                        when(pay.quantityRemind) {
                            1 -> spendingRepository.updatePayDate(pay.id, getNextDateTimeString((dateTimeToString(currentDate)), nextDay = 1))
                            2 -> spendingRepository.updatePayDate(pay.id, getNextDateTimeString((dateTimeToString(currentDate)), nextDay = 7))
                            3 -> spendingRepository.updatePayDate(pay.id, getNextDateTimeString((dateTimeToString(currentDate)), nextDay = 14))
                            4 -> spendingRepository.updatePayDate(pay.id, getNextMonth((dateTimeToString(currentDate)), nextMonth = 1))
                            5 -> spendingRepository.updatePayDate(pay.id, getNextMonth((dateTimeToString(currentDate)), nextMonth = 2))
                            6 -> spendingRepository.updatePayDate(pay.id, getNextMonth((dateTimeToString(currentDate)), nextMonth = 3))
                        }
                    }
                }
            } else  {
                when(pay.quantityRemind) {
                    0 -> if (pay.currentDate == currentDate) {
                        spendingRepository.deletePay(pay.id)
                    }
                    1 -> if (pay.currentDate == currentDate) {
                        spendingRepository.updatePayDate(pay.id, getNextDateTimeString((dateTimeToString(currentDate)), nextDay = 1))
                    }
                    2 -> if (pay.currentDate == currentDate) {
                        spendingRepository.updatePayDate(pay.id, getNextDateTimeString((dateTimeToString(currentDate)), nextDay = 7))
                    }
                    3 -> if (pay.currentDate == currentDate) {
                        spendingRepository.updatePayDate(pay.id, getNextDateTimeString((dateTimeToString(currentDate)), nextDay = 14))
                    }
                    4 -> if (pay.currentDate == currentDate) {
                        spendingRepository.updatePayDate(pay.id, getNextMonth((dateTimeToString(currentDate)), nextMonth = 1))
                    }
                    5 -> if (pay.currentDate == currentDate) {
                        spendingRepository.updatePayDate(pay.id, getNextMonth((dateTimeToString(currentDate)), nextMonth = 2))
                    }
                    6 -> if (pay.currentDate == currentDate) {
                        spendingRepository.updatePayDate(pay.id, getNextMonth((dateTimeToString(currentDate)), nextMonth = 3))
                    }
                }
            }
        }

        notifies.forEach { notify ->
            val currentDate = stringToDateTime(getCurrentDateTime("HH:mm dd/MM/yyyy", 0))
            if(notify.auto) {
                when(notify.quantityRemind) {
                    0 -> if (notify.currentDate == currentDate) {
                        spendingRepository.deleteNotify(notify.id)
                        sendNotification(
                            "Nhắc nhở",
                            notify.name + " - " + notify.note,
                            R.drawable.house_solid,
                        )
                    }
                    else -> if (notify.currentDate == currentDate) {
                        when(notify.quantityRemind) {
                            1 -> spendingRepository.updatePayDate(notify.id, getNextDateTimeString((dateTimeToString(currentDate)), nextDay = 1))
                            2 -> spendingRepository.updatePayDate(notify.id, getNextDateTimeString((dateTimeToString(currentDate)), nextDay = 7))
                            3 -> spendingRepository.updatePayDate(notify.id, getNextDateTimeString((dateTimeToString(currentDate)), nextDay = 14))
                            4 -> spendingRepository.updatePayDate(notify.id, getNextMonth((dateTimeToString(currentDate)), nextMonth = 1))
                            5 -> spendingRepository.updatePayDate(notify.id, getNextMonth((dateTimeToString(currentDate)), nextMonth = 2))
                            6 -> spendingRepository.updatePayDate(notify.id, getNextMonth((dateTimeToString(currentDate)), nextMonth = 3))
                        }
                        sendNotification(
                            "Nhắc nhở",
                            notify.name + " - " + notify.note,
                            R.drawable.house_solid,
                        )
                    }
                }
            } else  {
                when(notify.quantityRemind) {
                    0 -> if (notify.currentDate == currentDate) {
                        spendingRepository.deletePay(notify.id)
                    }
                    1 -> if (notify.currentDate == currentDate) {
                        spendingRepository.updatePayDate(notify.id, getNextDateTimeString((dateTimeToString(currentDate)), nextDay = 1))
                    }
                    2 -> if (notify.currentDate == currentDate) {
                        spendingRepository.updatePayDate(notify.id, getNextDateTimeString((dateTimeToString(currentDate)), nextDay = 7))
                    }
                    3 -> if (notify.currentDate == currentDate) {
                        spendingRepository.updatePayDate(notify.id, getNextDateTimeString((dateTimeToString(currentDate)), nextDay = 14))
                    }
                    4 -> if (notify.currentDate == currentDate) {
                        spendingRepository.updatePayDate(notify.id, getNextMonth((dateTimeToString(currentDate)), nextMonth = 1))
                    }
                    5 -> if (notify.currentDate == currentDate) {
                        spendingRepository.updatePayDate(notify.id, getNextMonth((dateTimeToString(currentDate)), nextMonth = 2))
                    }
                    6 -> if (notify.currentDate == currentDate) {
                        spendingRepository.updatePayDate(notify.id, getNextMonth((dateTimeToString(currentDate)), nextMonth = 3))
                    }
                }
            }
        }

        plans.forEach { plan ->
            val today = LocalDate.now()
            val startOfToday = today.atStartOfDay(ZoneId.systemDefault())
            val currentDate: Date = Date.from(startOfToday.toInstant())
            if(currentDate == stringToDateTime(getCurrentDateTime("HH:mm dd/MM/yyyy", 0))) {
                if(plan.type==0) {
                    val spendSelect = spends.filter { it.areaId==plan.areaId && it.accountId==plan.accountId
                            && it.date == getNextDateTimeString(dateTimeToString(currentDate), -1) }
                    var limitMoney = 0.0
                    if(currentDate >= plan.startDate && currentDate <= plan.endDate) {
                        for(spend in spendSelect) {
                            limitMoney += spend.money
                        }
                    }
                    if(limitMoney > plan.money) {
                        sendNotification(
                            "Chi tiêu trong ngày",
                            "Bạn đã chi nhiều hơn " + (limitMoney-plan.money) + " đ cho mục " + getNameArea(categories, plan.areaId) + " so với kế hoạch của bạn! Hãy cân nhắc chi tiêu để hoàn thành kế hoạch hoàn hảo.",
                            R.drawable.cart_shopping_solid
                        )
                    } else if(limitMoney == plan.money) {
                        sendNotification(
                            "Chi tiêu trong ngày",
                            "Mức chi tiêu cho mục " + getNameArea(categories, plan.areaId) + " trong hôm nay là hoàn hảo. Bạn thật giỏi",
                            R.drawable.cart_shopping_solid
                        )
                    } else {
                        sendNotification(
                            "Chi tiêu trong ngày",
                            "Hôm nay bạn đã tiết kiệm được " + (plan.money-limitMoney) + " đ cho mục " + getNameArea(categories, plan.areaId) + ". Tiếp tục phát huy nhé!!!",
                            R.drawable.cart_shopping_solid
                        )
                    }
                } else if(plan.type==1) {
                    var limitMoney = 0.0
                    if(currentDate >= plan.startDate && currentDate <= plan.endDate) {
                        val quantityDays = calculateDateBetween(plan.startDate, plan.endDate).toInt()
                        val quantityWeeks: Int = quantityDays / 7
                        for(i in 1..quantityWeeks) {
                            if(stringToDate(getNextDate(plan.startDate, i*7)) == stringToDate(getCurrentDateTime("dd/MM/yyyy", -1))) {
                                val spendSelect = spends.filter { it.areaId == plan.areaId && it.accountId == plan.accountId
                                        && it.date >= stringToDate(getNextDate(plan.startDate, (i-1)*7)) && it.date <= stringToDate(getCurrentDateTime("dd/MM/yyyy", -1)) }
                                for(spend in spendSelect) {
                                    limitMoney += spend.money
                                }
                                if(limitMoney > plan.money) {
                                    sendNotification(
                                        "Chi tiêu trong tuần (" + getNextDate(plan.startDate, (i-1)*7) + "-" + getCurrentDateTime("dd/MM/yyyy", -1) + ")",
                                        "Bạn đã chi nhiều hơn " + (limitMoney-plan.money) + " đ cho mục " + getNameArea(categories, plan.areaId) + " so với kế hoạch của bạn! Hãy cân nhắc chi tiêu để hoàn thành kế hoạch hoàn hảo.",
                                        R.drawable.cart_shopping_solid
                                    )
                                } else if(limitMoney == plan.money) {
                                    sendNotification(
                                        "Chi tiêu trong tuần (" + getNextDate(plan.startDate, (i-1)*7) + "-" + getCurrentDateTime("dd/MM/yyyy", -1) + ")",
                                        "Mức chi tiêu cho mục " + getNameArea(categories, plan.areaId) + " quá hoàn hảo. Bạn thật giỏi",
                                        R.drawable.cart_shopping_solid
                                    )
                                } else {
                                    sendNotification(
                                        "Chi tiêu trong tuần (" + getNextDate(plan.startDate, (i-1)*7) + "-" + getCurrentDateTime("dd/MM/yyyy", -1) + ")",
                                        "Bạn đã tiết kiệm được " + (plan.money-limitMoney) + " đ cho mục " + getNameArea(categories, plan.areaId) + ". Tiếp tục phát huy nhé!!!",
                                        R.drawable.cart_shopping_solid
                                    )
                                }
                            }
                        }
                    }
                } else {
                    var limitMoney = 0.0
                    if(currentDate >= plan.startDate && currentDate <= plan.endDate) {
                        val quantityMonths = calculateMonthsBetween(plan.startDate, plan.endDate).toInt()
                        for(i in 1..quantityMonths) {
                            if(getNextMonth(dateToString(plan.startDate), "dd/MM/yyyy", i) == stringToDate(getCurrentDateTime("dd/MM/yyyy", -1))) {
                                val spendSelect = spends.filter { it.areaId == plan.areaId && it.accountId == plan.accountId
                                        && it.date >= getNextMonth(dateToString(plan.startDate), "dd/MM/yyyy", i-1) && it.date <= stringToDate(getCurrentDateTime("dd/MM/yyyy", -1)) }
                                for(spend in spendSelect) {
                                    limitMoney += spend.money
                                }
                                if(limitMoney > plan.money) {
                                    sendNotification(
                                        "Chi tiêu trong tháng(" + dateToString(getNextMonth(dateToString(plan.startDate), "dd/MM", i-1)) + " - " + stringToDate(getCurrentDateTime("dd/MM/yyyy", -1)),
                                        "Bạn đã chi nhiều hơn " + (limitMoney-plan.money) + " đ cho mục " + getNameArea(categories, plan.areaId) + " so với kế hoạch của bạn! Hãy cân nhắc chi tiêu để hoàn thành kế hoạch hoàn hảo.",
                                        R.drawable.cart_shopping_solid
                                    )
                                } else if(limitMoney == plan.money) {
                                    sendNotification(
                                        "Chi tiêu trong tháng(" + dateToString(getNextMonth(dateToString(plan.startDate), "dd/MM", i-1)) + " - " + stringToDate(getCurrentDateTime("dd/MM/yyyy", -1)),
                                        "Mức chi tiêu cho mục " + getNameArea(categories, plan.areaId) + " quá hoàn hảo. Bạn thật giỏi",
                                        R.drawable.cart_shopping_solid
                                    )
                                } else {
                                    sendNotification(
                                        "Chi tiêu trong tháng(" + dateToString(getNextMonth(dateToString(plan.startDate), "dd/MM", i-1)) + " - " + stringToDate(getCurrentDateTime("dd/MM/yyyy", -1)),
                                        "Bạn đã tiết kiệm được " + (plan.money-limitMoney) + " đ cho mục " + getNameArea(categories, plan.areaId) + ". Tiếp tục phát huy nhé!!!",
                                        R.drawable.cart_shopping_solid
                                    )
                                }
                            }
                        }
                    }
                }
                if(plan.endDate == stringToDate(getCurrentDateTime("dd/MM/yyyy", -1))) {
                    var limitMoney = 0.0
                    val spendSelect = spends.filter { it.areaId == plan.areaId && it.accountId == plan.accountId
                            && it.date >= plan.startDate && it.date <= stringToDate(getCurrentDateTime("dd/MM/yyyy", -1)) }
                    for(spend in spendSelect) {
                        limitMoney += spend.money
                    }
                    if(limitMoney > plan.money) {
                        sendNotification(
                            "Chi tiêu từ" + dateToString(plan.startDate) + "-" + dateToString(plan.endDate) + ")",
                            "Bạn đã chi nhiều hơn " + (limitMoney-plan.money) + " đ cho mục " + getNameArea(categories, plan.areaId) + " so với kế hoạch của bạn! Hãy cân nhắc chi tiêu để hoàn thành kế hoạch hoàn hảo.",
                            R.drawable.cart_shopping_solid
                        )
                    } else if(limitMoney == plan.money) {
                        sendNotification(
                            "Chi tiêu từ" + dateToString(plan.startDate) + "-" + dateToString(plan.startDate) + ")",
                            "Mức chi tiêu cho mục " + getNameArea(categories, plan.areaId) + " quá hoàn hảo. Bạn thật giỏi",
                            R.drawable.cart_shopping_solid
                        )
                    } else {
                        sendNotification(
                            "Chi tiêu từ" + dateToString(plan.startDate) + "-" + dateToString(plan.startDate) + ")",
                            "Bạn đã tiết kiệm được " + (plan.money-limitMoney) + " đ cho mục " + getNameArea(categories, plan.areaId) + ". Tiếp tục phát huy nhé!!!",
                            R.drawable.cart_shopping_solid
                        )
                    }
                }
                if(plan.endDate < currentDate) {
                    spendingRepository.deletePlan(plan.id)
                }
            }
        }
    }

    private fun sendNotification(
        title: String,
        content: String,
        icon: Int,
    ) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Tạo kênh thông báo cho Android 8.0 trở lên
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("MY_CHANNEL_ID", "My Channel", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        // Tạo thông báo
        val notification = NotificationCompat.Builder(applicationContext, "MY_CHANNEL_ID")
            .setSmallIcon(icon)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        // Gửi thông báo
        notificationManager.notify(1, notification)
    }
}

fun scheduleWork(context: Context, id: Int) {
    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    val data = Data.Builder()
        .putInt("userId", id) // Truyền userId vào Worker
        .build()

    val workRequest = PeriodicWorkRequestBuilder<MyWorker>(1, TimeUnit.SECONDS) // Đặt khoảng thời gian cần thiết cho công việc chạy liên tục
        .setConstraints(constraints)
        .setInputData(data)
        .build()

    WorkManager.getInstance(context).enqueueUniquePeriodicWork("MyPeriodicWork", ExistingPeriodicWorkPolicy.REPLACE, workRequest)
}

fun getMoney(accounts: List<Account>, id: Int): Double {
    for (item in accounts) {
        return item.money
    }
    return  0.0
}

fun getNextMonth(dateString: String, format: String = "HH:mm dd/MM/yyyy", nextMonth: Int): Date {
    val formatter = DateTimeFormatter.ofPattern(format)
    val localDateTime = LocalDateTime.parse(dateString, formatter)
    val month = localDateTime.plusMonths(nextMonth.toLong())
    return Date.from(month.atZone(ZoneId.systemDefault()).toInstant())
}

fun getNextDateTimeString(dateTimeString: String, nextDay: Int, format: String = "HH:mm dd/MM/yyyy"): Date {
    val formatter = DateTimeFormatter.ofPattern(format)
    val dateTime = LocalDateTime.parse(dateTimeString, formatter)
    val nextDateTime = dateTime.plusDays(nextDay.toLong())
    return Date.from(nextDateTime.atZone(ZoneId.systemDefault()).toInstant())
}

fun stringToDateTime(date: String): Date {
    val dateFormat = SimpleDateFormat("HH:mm dd/MM/yyyy")
    return dateFormat.parse(date)
}

fun dateTimeToString(date: Date, format: String = "HH:mm dd/MM/yyyy"): String {
    val dateFormat = SimpleDateFormat(format, Locale.getDefault())
    return dateFormat.format(date)
}