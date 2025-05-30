package com.example.personalspending.ui.screen.transaction

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.personalspending.HomeTopAppBar
import com.example.personalspending.NotificationUtil
import com.example.personalspending.R
import com.example.personalspending.data.Account
import com.example.personalspending.data.Area
import com.example.personalspending.getCurrentDateTime
import com.example.personalspending.ui.model.AppViewModelProvider
import com.example.personalspending.ui.navigate.NavigationDestination
import com.example.personalspending.ui.screen.home.HeaderButton
import com.example.personalspending.ui.screen.home.compareDates
import com.example.personalspending.ui.screen.home.formatDate
import com.example.personalspending.ui.screen.home.getNameArea
import com.example.personalspending.ui.screen.home.showDatePickerDialog
import com.example.personalspending.ui.screen.home.stringToDate
import kotlinx.coroutines.launch
import java.lang.Integer.parseInt
import android.graphics.Color as AndroidColor
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import java.io.InputStream

import androidx.activity.viewModels
import androidx.compose.material.icons.filled.Image
import com.example.personalspending.data.ImageTransaction
import com.example.personalspending.network.DefaultAppContainer
import com.example.personalspending.network.ImageController

object TransactionDestination: NavigationDestination {
    override val route: String = "transaction"
    const val routeDetail: String = "details_transaction"
    override val titleRes: Int = R.string.add_transaction
    const val userIdArg = "userId"
    const val accountIdArg = "accountId"
    val routeWithArgs = "$route/{$userIdArg}"
    val detailWithArgs = "$routeDetail/{$accountIdArg}"
}

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreen(
    onBack: () -> Unit,
    onCreate: (Int) -> Unit,
    onHome: (Int) -> Unit,
    viewModel: TransactionViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val imageController = ImageController()

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                HomeTopAppBar(
                    title = stringResource(R.string.add_transaction),
                    onClick = onBack,
                    backButton = true,
                )
            }
        ) { innerPadding ->
            val context = LocalContext.current
            NotificationUtil.createNotificationChannel(context)
            val listAccount = viewModel.transactionUIState.accounts
            val listArea = viewModel.transactionUIState.areas
            val plans = viewModel.transactionUIState.plans
            val spends = viewModel.transactionUIState.spends

            var isExpended by remember { mutableIntStateOf(0) }
            var moneyValue by remember { mutableIntStateOf(0) }
            var noteValue by remember { mutableStateOf("") }
            var idAccountCurrent by remember { mutableIntStateOf(0) }
            var dayChoosed by remember { mutableStateOf(getCurrentDateTime("dd/MM/yyyy", 0)) }
            var idArea by remember { mutableIntStateOf(0) }
            var selectedImageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
            val isSubmit:Boolean = moneyValue != 0 && idAccountCurrent != 0 && idArea != 0
            var totalMoney: Double = 0.0
            if(idAccountCurrent != 0) {
                for(item in listAccount) {
                    if(item.id == idAccountCurrent) {
                        totalMoney = item.money
                    }
                }
            } else {
                if(listAccount.isNotEmpty()) {
                    idAccountCurrent = listAccount[0].id
                }
            }
            var isHome by remember { mutableStateOf(true) }

            val onValueChangeSpend: (SpendDetails) -> Unit = viewModel::updateSpendUIState
            val coroutineScope = rememberCoroutineScope()

                TransactionContent(
                    innerPadding = innerPadding,
                    isExpended = isExpended,
                    onExpended = { isExpended = it },
                    moneyValue = moneyValue,
                    onMoneyValue = { moneyValue = it },
                    listAccount = listAccount,
                    listArea = listArea,
                    idAccountCurrent = idAccountCurrent,
                    onIdAccountCurrent = { idAccountCurrent = it },
                    idArea = idArea,
                    onIdArea = { idArea = it },
                    dayChoosed = dayChoosed,
                    onDayChoosed = { dayChoosed = it },
                    context = context,
                    noteValue = noteValue,
                    onNote = { noteValue = it },
                    onCreate = { onCreate(viewModel.transactionUIState.userId) },
                    selectedImageBitmap = selectedImageBitmap,
                    onSelectImage = { selectedImageBitmap = it },
                    isSubmit = isSubmit,
                    onSuccess = {
                        var img = ""
//                        if(selectedImageBitmap != null) {
//                            img = getCurrentDateTime("HH:mm:ss:dd:MM:yyyy", 0)
//                            val image = ImageTransaction(id = getCurrentDateTime("HH:mm:ss:dd:MM:yyyy", 0), url = imageBitmapToBase64(selectedImageBitmap!!))
//                            imageController.createImage(image) { createdImage ->
//                                if (createdImage != null) {
//                                    Log.d("MainActivity", "Created image ID: ${createdImage.id}")
//                                } else {
//                                    Log.e("MainActivity", "Failed to create image")
//                                }
//                            }
//                        }
                        coroutineScope.launch {
                            onValueChangeSpend(
                                viewModel.transactionUIState.spendDetails.copy(
                                    accountId = idAccountCurrent,
                                    areaId = idArea,
                                    money = moneyValue.toDouble(),
                                    date = stringToDate(dayChoosed),
                                    note = noteValue,
                                    type = isExpended,
                                    img = img,
                                ))
                            if(isExpended==0) {
                                totalMoney -= moneyValue
                                if(totalMoney >= moneyValue) {
                                    viewModel.insertSpend()
                                    viewModel.updateAccountById(idAccountCurrent, totalMoney)
                                    var limit = 0.0
                                    val planSelect = plans.filter { it.areaId==idArea && it.accountId==idAccountCurrent
                                            && it.startDate <= stringToDate(dayChoosed) && it.endDate >= stringToDate(dayChoosed)}
                                    if(spends.isNotEmpty() && planSelect.isNotEmpty()) {
                                        val plan = planSelect[0]
                                        val spendSelect = if(plan.type==0) {
                                            spends.filter { it.areaId==idArea && it.accountId==idAccountCurrent
                                                    && it.date == stringToDate(dayChoosed) }
                                        } else {
                                            spends.filter { it.areaId==idArea && it.accountId==idAccountCurrent
                                                    && it.date >= plan.startDate && it.date <= plan.endDate }
                                        }
                                        if(spendSelect.isNotEmpty()) {
                                            for(spend in spendSelect) {
                                                limit += spend.money
                                            }
                                        }
                                        limit += moneyValue
                                        if(limit > plan.money) {
                                            NotificationUtil.sendNotification(
                                                context,
                                                "Chi tiêu quá mức",
                                                "Bạn đã chi nhiều hơn " + (limit-plan.money) + " đ cho mục " + getNameArea(listArea, idArea) + " so với kế hoạch của bạn! Hãy cân nhắc chi tiêu để hoàn thành kế hoạch hoàn hảo.",
                                                1
                                            )
                                        } else if(limit == plan.money) {
                                            NotificationUtil.sendNotification(
                                                context,
                                                "Chi tiêu quá mức",
                                                "Mức chi tiêu cho mục " + getNameArea(listArea, idArea) + " theo kế hoạch của bạn đã đạt giới hạn!",
                                                1
                                            )
                                        }
                                    }
                                } else {
                                    Toast.makeText(context, "Số dư của bạn không đủ!!!", Toast.LENGTH_SHORT).show()
                                    isHome = false
                                }
                            } else {
                                totalMoney += moneyValue
                                viewModel.insertSpend()
                                viewModel.updateAccountById(idAccountCurrent, totalMoney)
                            }
                        }
                        if(isHome) {
                            onHome(viewModel.transactionUIState.userId)
                        }
                    }
                )
        }
    }
}

@Composable
fun TransactionContent(
    isUpdate: Int = 0,
    innerPadding: PaddingValues = PaddingValues(0.dp),
    isExpended: Int,
    onExpended: (Int) -> Unit,
    moneyValue: Int,
    onMoneyValue: (Int) -> Unit,
    listAccount: List<Account>,
    idAccountCurrent: Int,
    onIdAccountCurrent: (Int) -> Unit,
    listArea: List<Area>,
    idArea: Int,
    onIdArea: (Int) -> Unit,
    dayChoosed: String,
    onDayChoosed: (String) -> Unit,
    context: Context,
    noteValue: String,
    onNote: (String) -> Unit,
    onCreate: () -> Unit,
    onSuccess: () -> Unit,
    nameAccount: String = "",
    selectedImageBitmap: ImageBitmap? = null,
    onSelectImage: (ImageBitmap?) -> Unit = {},
    isSubmit: Boolean,
) {
    LazyColumn(
        modifier = Modifier
            .background(Color.Black)
            .padding(top = innerPadding.calculateTopPadding())
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        item {
            if(isUpdate==0) {
                HeaderButton(
                    isExpended = isExpended,
                    onIncome = { onExpended(1) },
                    onSpend = { onExpended(0) }
                )
            }

            Spacer(modifier = Modifier.size(20.dp))
            MoneyValue(
                value = moneyValue,
                onValueChange = {
                    onMoneyValue(it)
                }
            )
            Spacer(modifier = Modifier.size(30.dp))
            Column {
                Text(
                    text = stringResource(id = R.string.account),
                    color = Color.Gray,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                LazyRow() {
                    if(isUpdate==0) {
                        items(listAccount) { item ->
                            Button(
                                onClick = { onIdAccountCurrent(item.id) },
                                colors = if(idAccountCurrent == item.id) {
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
                    } else {
                        item {
                            Button(
                                onClick = {},
                                colors = ButtonDefaults.buttonColors( // a@g.co
                                    containerColor = Color.Gray
                                )
                            ) {
                                Text(
                                    text = nameAccount,
                                    color = Color.Green,
                                    fontSize = 16.sp
                                )
                            }
                            Spacer(modifier = Modifier.size(10.dp))
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.size(20.dp))
            ListCategory(
                listArea = listArea,
                isExpended = isExpended,
                onChangeArea = { onIdArea(it) },
                areaValue = idArea,
                onCreate = onCreate,
                isUpdate = isUpdate,
            )
            Spacer(modifier = Modifier.size(20.dp))
            ChooseDate(
                dayChoosed = dayChoosed,
                onChooseDate = { onDayChoosed(it) },
                context = context,
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .padding(10.dp),
            )
            Spacer(modifier = Modifier.size(40.dp))
            Note(
                value = noteValue,
                onValueChange = { onNote(it) },
                title = R.string.note
            )
            Spacer(modifier = Modifier.size(40.dp))
//            Box(
//                modifier = Modifier
//                    .padding(bottom = 20.dp)
//                    .fillMaxWidth()
//            ) {
//                Column {
//                    Text(
//                        text = stringResource(id = R.string.image),
//                        color = Color.Gray,
//                        fontSize = 16.sp,
//                    )
//                    val launcher = rememberLauncherForActivityResult(
//                        contract = ActivityResultContracts.StartActivityForResult()
//                    ) { result ->
//                        if (result.resultCode == Activity.RESULT_OK) {
//                            val uri = result.data?.data
//                            uri?.let {
//                                val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
//                                val bitmap = BitmapFactory.decodeStream(inputStream)
//                                onSelectImage(bitmap.asImageBitmap())
//                            }
//                        }
//                    }
//                    if(selectedImageBitmap==null) {
//                        IconButton(
//                            modifier = Modifier
//                                .size(60.dp)
//                                .background(Color(119, 139, 133)),
//                            onClick = {
//                                val intent = Intent(Intent.ACTION_PICK).apply {
//                                    type = "image/*"
//                                }
//                                launcher.launch(intent)
//                            },
//                        ) {
//                            Icon(
//                                imageVector = Icons.Filled.Image,
//                                contentDescription = null,
//                                modifier = Modifier.size(34.dp),
//                            )
//                        }
//                    } else {
//                        selectedImageBitmap?.let { image ->
//                            Image(
//                                bitmap = image,
//                                contentDescription = null,
//                                modifier = Modifier.fillMaxWidth(0.3f).clickable {
//                                    val intent = Intent(Intent.ACTION_PICK).apply {
//                                        type = "image/*"
//                                    }
//                                    launcher.launch(intent)
//                                }
//                            )
//                        }
//                    }
//                }
//            }
            Box(
                modifier = Modifier
                    .padding(bottom = 20.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = onSuccess,
                    modifier = Modifier
                        .width(220.dp)
                        .height(60.dp),
                    enabled = isSubmit,
                    colors = ButtonDefaults.buttonColors(Color(247, 177, 12))
                ) {
                    Text(
                        text = if(isUpdate==0) {
                            stringResource(id = R.string.add)
                        } else {
                            stringResource(id = R.string.update) },
                        fontSize = 24.sp
                    )
                }
            }
        }
    }
}

@Composable
fun MoneyValue(
    value: Int,
    onValueChange: (Int) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Bottom
        ) {
            TextField(
                value = value.toString(),
                onValueChange = {
                    val intValue = if (it == "" || !isValidInt(it)) {
                        0
                    } else {
                        parseInt(it)
                    }
                    onValueChange(intValue)
                },
                modifier = Modifier
                    .width(160.dp)
                    .height(60.dp)
                    .wrapContentHeight(Alignment.CenterVertically),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.Black,
                    unfocusedContainerColor = Color.Black,
                    disabledContainerColor = Color.Black,
                    focusedTextColor = Color.White,
                ),
                singleLine = true,
                textStyle = TextStyle(
                    fontSize = 26.sp,  // Điều chỉnh kích thước chữ ở đây
                    color = Color.White // Đảm bảo màu chữ vẫn là màu trắng
                )
            )
            Text(
                text = stringResource(id = R.string.vnd),
                modifier = Modifier.padding(start = 10.dp),
                fontSize = 28.sp,
                color = Color(22, 89, 49)
            )
        }
        if(value < 1000) {
            Text(
                text = "Số tiền phải hơn 1,000 đ",
                modifier = Modifier.padding(top = 10.dp, start = 20.dp),
                fontSize = 14.sp,
                color = Color.Red
            )
        }
    }
}

@Composable
fun ChooseDate(
    dayChoosed: String,
    onChooseDate: (String) -> Unit,
    context: Context,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Box(
            modifier = if(compareDates(dayChoosed, getCurrentDateTime("dd/MM/yyyy", 0)) == 0) {
                Modifier
                    .padding(10.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(30, 157, 118))
            } else {
                Modifier
                    .padding(10.dp)
                    .clickable { onChooseDate(getCurrentDateTime("dd/MM/yyyy", 0)) }
                    .background(Color.Transparent)
            }
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(10.dp)
            ) {
                Text(
                    text = getCurrentDateTime("dd/MM", 0),
                    fontSize = 18.sp,
                    color = Color.White
                )
                Text(
                    text = stringResource(id = R.string.now_day),
                    fontSize = 18.sp,
                    color = Color.White
                )
            }
        }
        if(compareDates(dayChoosed, getCurrentDateTime("dd/MM/yyyy", 0)) != 0
            && compareDates(dayChoosed, getCurrentDateTime("dd/MM/yyyy", -1)) != 0) {
            Box(
                modifier = Modifier
                    .padding(10.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(30, 157, 118))
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(10.dp)
                ) {
                    Text(
                        text = dayChoosed,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                    Text(
                        text = stringResource(id = R.string.choosed),
                        fontSize = 18.sp,
                        color = Color.White
                    )
                }
            }
        } else {
            Box(
                modifier = if(compareDates(dayChoosed, getCurrentDateTime("dd/MM/yyyy", -1)) == 0) {
                    Modifier
                        .padding(10.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(30, 157, 118))
                } else {
                    Modifier
                        .clickable { onChooseDate(getCurrentDateTime("dd/MM/yyyy", -1)) }
                        .padding(10.dp)
                        .background(Color.Transparent)
                }
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(10.dp)
                ) {
                    Text(
                        text = getCurrentDateTime("dd/MM", -1),
                        fontSize = 18.sp,
                        color = Color.White
                    )
                    Text(
                        text = stringResource(id = R.string.last_day),
                        fontSize = 18.sp,
                        color = Color.White
                    )
                }
            }
        }
        IconButton(onClick = {
            showDatePickerDialog(
                context,
                "Chọn ngày"
            ) { selectedDate ->
                onChooseDate(formatDate(selectedDate))
            }
        }) {
            Icon(
                imageVector = Icons.Filled.CalendarMonth,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = Color.Green
            )
        }
    }
}

@Composable
fun Note(
    value: String,
    onValueChange: (String) -> Unit,
    title: Int,
) {
    Column {
        Text(
            text = stringResource(id = title),
            color = Color.Gray,
            fontSize = 16.sp,
        )
        TextField(
            value = value,
            onValueChange = { onValueChange(it) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.Black,
                unfocusedContainerColor = Color.Black,
                disabledContainerColor = Color.Black,
                focusedTextColor = Color.White,
            ),
            singleLine = true,
            textStyle = TextStyle(
                fontSize = 18.sp,  // Điều chỉnh kích thước chữ ở đây
                color = Color.White // Đảm bảo màu chữ vẫn là màu trắng
            )
        )
    }
}

@SuppressLint("ResourceType")
@Composable
fun ListCategory(
    listArea: List<Area>,
    isExpended: Int,
    onChangeArea: (Int) -> Unit,
    areaValue: Int,
    onCreate: () -> Unit,
    isUpdate: Int = 0,
) {
    Column {
        Text(
            text = stringResource(id = R.string.category),
            color = Color.Gray,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        val filterListArea = listArea.filter { it.type == isExpended }
        val sizeOfList = filterListArea.size
        val quantityRow = sizeOfList / 3
        val overRow = sizeOfList % 3
        var countAdd = 0
        Column(modifier = Modifier.fillMaxWidth()) {
            for (indexRow in 0..quantityRow) {
                Row(
                    modifier = Modifier
                        .padding(top = 20.dp, start = 10.dp, end = 10.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom,
                ) {
                    for (index in indexRow*3..indexRow*3+2) {
                        if(index < sizeOfList) {
                            CategoryItem(
                                item = filterListArea[index],
                                areaValue = areaValue,
                                onChangeArea = onChangeArea
                            )
                        } else if(overRow!=0 && countAdd==0 && isUpdate==0) {
                            AddButton(
                                onClick = onCreate,
                                title = R.string.over_view,
                                backgroundColor = Color.Gray,
                                size = 50.dp,
                                colorIcon = Color.White
                            )
                            countAdd=1
                        } else if(index == indexRow*3+2) {
                            Box(modifier = Modifier.size(50.dp)) {
                            }
                        }
                    }
                }
            }
            if(overRow==0 && isUpdate==0) {
                Row(
                    modifier = Modifier.padding(top = 20.dp, start = 20.dp),
                    horizontalArrangement = Arrangement.Start,
                ) {
                    AddButton(
                        onClick = onCreate,
                        title = R.string.over_view,
                        backgroundColor = Color.Gray,
                        size = 50.dp,
                        colorIcon = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryItem(
    item: Area,
    areaValue: Int,
    onChangeArea: (Int) -> Unit = {},
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val shapeModifier = if (item.id == areaValue) {
            Modifier.background(color = item.color.toComposeColor())
        } else {
            Modifier.background(color = item.color.toComposeColor(), CircleShape)
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .width(60.dp)
                .height(60.dp)
                .then(shapeModifier)
                .clickable { onChangeArea(item.id) },
        ) {
            Image(
                painter = painterResource(id = item.icon),
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                colorFilter = ColorFilter.tint(Color.White)
            )
        }
        Text(
            text = item.name,
            color = Color.White,
            fontSize = 16.sp,
            modifier = Modifier.padding(top = 6.dp),
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
    }
}

@Composable
fun AddButton(
    onClick: () -> Unit,
    title: Int,
    backgroundColor: Color,
    size: Dp,
    colorIcon: Color,
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(
            modifier = Modifier
                .size(size)
                .background(backgroundColor, CircleShape),
            onClick = onClick,
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = null,
                modifier = Modifier.size(34.dp),
            )
        }
        Text(
            text = stringResource(title),
            color = Color.White,
            fontSize = 16.sp,
            modifier = Modifier.padding(top = 6.dp),
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
    }
}

fun isValidInt(input: String): Boolean {
    return input.toIntOrNull() != null
}

fun Int.toComposeColor(): Color {
    return Color(
        red = AndroidColor.red(this) / 255f,
        green = AndroidColor.green(this) / 255f,
        blue = AndroidColor.blue(this) / 255f,
        alpha = AndroidColor.alpha(this) / 255f
    )
}

@Preview(showBackground = true)
@Composable
fun TransactionScreenPreview() {
    MoneyValue(value = 0, onValueChange = {})
//    TransactionScreen(onBack = {}, onHome = {})
}