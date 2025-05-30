package com.example.personalspending.ui.screen.details

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.personalspending.HomeTopAppBar
import com.example.personalspending.NotificationUtil
import com.example.personalspending.R
import com.example.personalspending.data.Account
import com.example.personalspending.data.Area
import com.example.personalspending.data.ImageTransaction
import com.example.personalspending.data.Spend
import com.example.personalspending.getCurrentDateTime
import com.example.personalspending.network.ImageController
import com.example.personalspending.ui.model.AppViewModelProvider
import com.example.personalspending.ui.navigate.NavigationDestination
import com.example.personalspending.ui.screen.account.AccountItem
import com.example.personalspending.ui.screen.account.convertMoney
import com.example.personalspending.ui.screen.home.HomeUIState
import com.example.personalspending.ui.screen.home.TitleTotal
import com.example.personalspending.ui.screen.home.dateToString
import com.example.personalspending.ui.screen.home.getColorArea
import com.example.personalspending.ui.screen.home.getIconArea
import com.example.personalspending.ui.screen.home.getNameArea
import com.example.personalspending.ui.screen.home.stringToDate
import com.example.personalspending.ui.screen.pay.AccountItemDetails
import com.example.personalspending.ui.screen.pay.CategoryItemDetail
import com.example.personalspending.ui.screen.transaction.SpendDetails
import com.example.personalspending.ui.screen.transaction.TransactionContent
import com.example.personalspending.ui.screen.transaction.base64ToBitmap
import com.example.personalspending.ui.screen.transaction.imageBitmapToBase64
import com.example.personalspending.ui.screen.transaction.toComposeColor
import kotlinx.coroutines.launch
import java.io.InputStream

object DetailsDestination: NavigationDestination {
    override val route: String = "details"
    override val titleRes: Int = R.string.detail_transaction
    const val userIdArg = "userId"
    const val spendIdArg = "spendId"
    const val backPage = "backPage"
    val routeWithArgs = "$route/{$userIdArg}/{$spendIdArg}/{$backPage}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    id: Int,
    backPage: Int,
    onHome: (Int) -> Unit,
    onBack: (Int) -> Unit,
    viewModel: DetailsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    var indexScreen by remember { mutableIntStateOf(0) }
    println(viewModel.detailsUiState.id)

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                HomeTopAppBar(
                    title = when (indexScreen) {
                        0 -> stringResource(id = R.string.detail_transaction)
                        1 -> stringResource(id = R.string.update_transaction)
                        else -> stringResource(R.string.detail_transaction)
                    },
                    onClick = if(indexScreen == 0) {
                        {
                            if(backPage==0) {
                                onHome(id)
                            } else {
                                onBack(id)
                            }
                        }
                    } else {
                        { indexScreen = 0 }
                           },
                    backButton = true
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
        if(viewModel.detailsUiState.spend.id != 0) {
            if(indexScreen==0) {
                DetailsTransaction(
                    viewModel = viewModel,
                    onBack = {
                        if(viewModel.detailsUiState.backPage==0) {
                            onHome(viewModel.detailsUiState.id)
                        } else {
                            onBack(viewModel.detailsUiState.id)
                        }
                    },
                    onTransactionDetails = { indexScreen = 1 }
                )
            } else {
                UpdateTransaction(
                    viewModel = viewModel,
                    onBack = {
                        if(viewModel.detailsUiState.backPage==0) {
                            onHome(viewModel.detailsUiState.id)
                        } else {
                            onBack(viewModel.detailsUiState.id)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun ListTransaction(
    modifier: Modifier = Modifier,
    listId: List<Int>,
    homeUIState: HomeUIState,
    isExpend: Int,
    onSelect: (Int) -> Unit,
) {
    val listCategory = homeUIState.areas
    val listAccount = homeUIState.accounts
    val spends = homeUIState.spends
    val listSpend = mutableListOf<Spend>()
    var icon = 0
    var color = Color.White

    var name = ""
    var total = 0.0
    for (item in spends) {
        if(checkDetailsById(listId, item.id) && item.type == isExpend) {
            total += item.money
            name = getNameByCategory(listCategory, item.areaId)
            icon = getIconArea(listCategory, item.areaId)
            color = getColorArea(listCategory, item.areaId).toComposeColor()
            listSpend.add(item)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(1f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 120.dp, start = 20.dp, end = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            TitleTotal(
                onShowAccount = { /*TODO*/ },
                totalMoney = total,
                nameAccount = name,
                isIcon = false,
            )
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(listSpend) { item ->
                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp)
                    ) {
                        Text(
                            text = dateToString(item.date),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(176, 178, 154),
                            modifier = Modifier.padding(bottom = 8.dp, start = 10.dp)
                        )
                        AccountItem(
                            icon = icon,
                            name = name,
                            color = color,
                            money = item.money,
                            onSelect = { onSelect(item.id) },
                            modifier = Modifier.clip(RoundedCornerShape(10.dp)),
                            nameAccount = getNameAccount(listAccount, item.accountId)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DetailsTransaction(
    modifier: Modifier = Modifier,
    viewModel: DetailsViewModel,
    onBack: () -> Unit,
    onTransactionDetails: () -> Unit,
) {
    val imageController = ImageController()

    val detailsUiState = viewModel.detailsUiState
    val id = detailsUiState.idSpend
    val listCategory = detailsUiState.areas
    val spend = detailsUiState.spend
    val account = detailsUiState.account
    var iconCategory = 0
    var colorCategory = Color.White
    var nameCategory = ""
    var date = getCurrentDateTime("dd/MM/yyyy", 0)
    var total = 0.0
    date = dateToString(spend.date)
    total = spend.money
    nameCategory = getNameByCategory(listCategory, spend.areaId)
    iconCategory = getIconArea(listCategory, spend.areaId)
    colorCategory = getColorArea(listCategory, spend.areaId).toComposeColor()

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
                Spacer(modifier = Modifier.size(20.dp))
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(id = R.string.money_value),
                        color = Color.Gray,
                        fontSize = 18.sp,
                    )
                    Spacer(modifier = Modifier.size(10.dp))
                    Text(
                        text = "${convertMoney(total)} đ",
                        color = Color.White,
                        fontSize = 20.sp,
                    )
                }
                AccountItemDetails(listCategory = listCategory, account = account)
                CategoryItemDetail(colorCategory = colorCategory, iconCategory = iconCategory, nameCategory = nameCategory)
                Spacer(modifier = Modifier.size(20.dp))
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(id = R.string.day),
                        color = Color.Gray,
                        fontSize = 18.sp,
                    )
                    Spacer(modifier = Modifier.size(10.dp))
                    Text(
                        text = date,
                        color = Color.White,
                        fontSize = 20.sp,
                    )
                }
//                var selectedImageBitmap by remember { mutableStateOf<Bitmap?>(null) }
//                var idImage by remember { mutableStateOf("") }
//                imageController.fetchImages { images ->
//                    if (images != null) {
//                        for (image in images) {
//                            val bitmap = base64ToBitmap(image.url)
//                            if (bitmap != null && image.id == spend.img) {
//                                selectedImageBitmap = bitmap
//                                idImage = image.id
//                                break
//                            }
//                        }
//                    } else {
//                        Log.e("MainActivity", "Failed to fetch images")
//                    }
//                }
//                Spacer(modifier = Modifier.size(20.dp))
//                if(selectedImageBitmap!=null) {
//                    selectedImageBitmap?.let { image ->
//                        Image(
//                            bitmap = image.asImageBitmap(),
//                            contentDescription = null,
//                            modifier = Modifier.fillMaxWidth(0.3f)
//                        )
//                    }
//                }
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
                            onClick = onTransactionDetails,
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
                        val deleteResult = remember { mutableStateOf<Boolean?>(null) }
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    viewModel.deleteSpend(id)
//                                    if(idImage != "") {
//                                        imageController.deleteImage(idImage) { result ->
//                                            deleteResult.value = result
//                                            if (result) {
//                                                Log.d("MainActivity", "Image deleted successfully")
//                                            } else {
//                                                Log.e("MainActivity", "Failed to delete image")
//                                            }
//                                        }
//                                    }
                                    if(spend.type==0) {
                                        viewModel.updateAccountById(account.id, account.money + total)
                                    } else {
                                        viewModel.updateAccountById(account.id, account.money - total)
                                    }
                                }
                                onBack()
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
            }
        }
    }
}

@Composable
fun UpdateTransaction(
    viewModel: DetailsViewModel,
    onBack: () -> Unit,
) {
    val imageController = ImageController()
    val context = LocalContext.current
    val account = viewModel.detailsUiState.account
    val listArea = viewModel.detailsUiState.areas
    val spend = viewModel.detailsUiState.spend
    val plans = viewModel.detailsUiState.plans
    val spends = viewModel.detailsUiState.spends
    val listAccount = listOf(account)

    var isExpended by remember { mutableIntStateOf(spend.type) }
    var moneyValue by remember { mutableIntStateOf(spend.money.toInt()) }
    var noteValue by remember { mutableStateOf(spend.note) }
    val idAccountCurrent by remember { mutableIntStateOf(spend.accountId) }
    var dayChoosed by remember { mutableStateOf(dateToString(spend.date)) }
    var idArea by remember { mutableIntStateOf(spend.areaId) }
//    var onImage by remember { mutableStateOf(false) }
//    var selectedImageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
//    var idImage by remember { mutableStateOf(spend.img) }
//    imageController.fetchImages { images ->
//        if (images != null && !onImage) {
//            for (image in images) {
//                val bitmap = base64ToBitmap(image.url)
//                if (bitmap != null && image.id == idImage) {
//                    selectedImageBitmap = bitmap.asImageBitmap()
//                    break
//                }
//            }
//        } else {
//            Log.e("MainActivity", "Failed to fetch images")
//        }
//    }
//    val updateResult = remember { mutableStateOf<ImageTransaction?>(null) }

    val isSubmit:Boolean = moneyValue != 0 && idAccountCurrent != 0 && idArea != 0
    var totalMoney: Double = account.money

    val onValueChangeSpend: (SpendDetails) -> Unit = viewModel::updateSpendUIState
    val coroutineScope = rememberCoroutineScope()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(1f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 120.dp),
        ) {
            TransactionContent(
                isExpended = isExpended,
                onExpended = { isExpended = it },
                moneyValue = moneyValue,
                onMoneyValue = { moneyValue = it },
                listAccount = listAccount,
                listArea = listArea,
                idAccountCurrent = idAccountCurrent,
                onIdAccountCurrent = {  },
                idArea = idArea,
                onIdArea = { idArea = it },
                dayChoosed = dayChoosed,
                onDayChoosed = { dayChoosed = it },
                context = context,
                noteValue = noteValue,
                onNote = { noteValue = it },
                onCreate = {  },
                isUpdate = 1,
                nameAccount = getNameAccount(listAccount, idAccountCurrent),
                isSubmit = isSubmit,
//                selectedImageBitmap = selectedImageBitmap,
//                onSelectImage = {
//                    selectedImageBitmap = it
//                    onImage = true
//                                },
                onSuccess = {
                    totalMoney = if(isExpended==0) {
                        totalMoney - spend.money + moneyValue.toDouble()
                    } else {
                        totalMoney + spend.money - moneyValue.toDouble()
                    }
                    var limit = 0.0
                    val planSelect = plans.filter { it.areaId==idArea && it.accountId==idAccountCurrent
                            && it.startDate <= stringToDate(dayChoosed) && it.endDate >= stringToDate(dayChoosed)}
//                    if(idImage == "" && selectedImageBitmap != null) {
//                        idImage = getCurrentDateTime("HH:mm:ss:dd:MM:yyyy", 0)
//                        val image = ImageTransaction(id = idImage, url = imageBitmapToBase64(selectedImageBitmap!!))
//                        imageController.createImage(image) { createdImage ->
//                            if (createdImage != null) {
//                                Log.d("MainActivity", "Created image ID: ${createdImage.id}")
//                            } else {
//                                Log.e("MainActivity", "Failed to create image")
//                            }
//                        }
//                    } else if(idImage != "") {
//                        val image = ImageTransaction(id = idImage, url = imageBitmapToBase64(selectedImageBitmap!!)) // Replace with actual data
//                        imageController.updateImage(image) { updatedImage ->
//                            updateResult.value = updatedImage
//                            if (updatedImage != null) {
//                                Log.d("MainActivity", "Image updated successfully: ${updatedImage.id}")
//                            } else {
//                                Log.e("MainActivity", "Failed to update image")
//                            }
//                        }
//                    }
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
                        limit += totalMoney
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
                    coroutineScope.launch {
                        onValueChangeSpend(
                            viewModel.detailsUiState.spendDetails.copy(
                                id = spend.id,
                                accountId = idAccountCurrent,
                                areaId = idArea,
                                money = moneyValue.toDouble(),
                                date = stringToDate(dayChoosed),
                                note = noteValue,
                                type = isExpended,
                                img = "",
                            ))
                        viewModel.updateSpend()
                        viewModel.updateAccountById(idAccountCurrent, totalMoney)
                    }
                    onBack()
                }
            )
        }
    }
}

fun checkDetailsById(listId: List<Int>, id: Int): Boolean {
    for (item in listId) {
        if(item == id) {
            return true
        }
    }
    return false
}

fun getNameByCategory(listCategory: List<Area>, id: Int): String {
    for (item in listCategory) {
        if(item.id == id) {
            return item.name
        }
    }
    return ""
}

fun getNameAccount(listAccount: List<Account>, id: Int): String {
    for (item in listAccount) {
        if(item.id == id) {
            return item.name
        }
    }
    return ""
}

@Preview
@Composable
fun HomeDetailsPreview() {
//    DetailsTransaction()
//    ListTransaction()
}
