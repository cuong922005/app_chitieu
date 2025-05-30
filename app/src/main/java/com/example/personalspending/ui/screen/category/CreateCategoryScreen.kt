package com.example.personalspending.ui.screen.category

import android.annotation.SuppressLint
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Dialpad
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.personalspending.HomeTopAppBar
import com.example.personalspending.R
import com.example.personalspending.data.Area
import com.example.personalspending.data.AreaList
import com.example.personalspending.data.ItemIcon
import com.example.personalspending.data.listColor
import com.example.personalspending.ui.model.AppViewModelProvider
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCategoryScreen(
    modifier: Modifier = Modifier,
    onCategory: (Int)-> Unit,
    onBack: () -> Unit,
    viewModel: CategoryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                HomeTopAppBar(
                    title = stringResource(R.string.add_category),
                    onClick = onBack,
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
        CreateContent(
            viewModel = viewModel,
            onCategory = onCategory
        )
    }
}

@SuppressLint("SuspiciousIndentation")
@Composable
fun CreateContent(
    modifier: Modifier = Modifier,
    onCategory: (Int) -> Unit,
    viewModel: CategoryViewModel,
) {
    var name by remember { mutableStateOf("") }
    var isFocus by remember { mutableStateOf(false) }
    var isSpend by remember { mutableIntStateOf(0) }
    var categoryValue by remember { mutableIntStateOf(0) }
    var colorValue by remember { mutableIntStateOf(0) }
    val isSubmit = name!="" && categoryValue!=0 && colorValue!=0

    val categoryUIState = viewModel.categoryUiState
    val onCategoryUpdate = viewModel::updateCategoryUIState
    val coroutineScope = rememberCoroutineScope()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .padding(top = 100.dp),
    ) {
        item {
            NameText(
                name = name,
                title = stringResource(id = R.string.name_category),
                isFocus = isFocus,
                onChange = { name = it},
                onFocus = { isFocus = it}
            )
            Row(
                modifier = Modifier.padding(top = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(selected = isSpend==0, onClick = { isSpend=0 })
                Text(
                    text = "Chi phí",
                    color = Color(213, 215, 194),
                    fontSize = 22.sp
                )
                Spacer(modifier = Modifier.size(40.dp))
                RadioButton(selected = isSpend==1, onClick = { isSpend=1 })
                Text(
                    text = "Thu nhập",
                    color = Color(213, 215, 194),
                    fontSize = 22.sp
                )
            }
            SelectCategory(
                categoryValue = categoryValue,
                onSelect = {categoryValue = it},
                listArea = categoryUIState.areas,
                isSpend = isSpend,
            )
            SelectColor(
                colorValue = colorValue,
                onSelect = {colorValue = it}
            )
            Box(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = {
                        val userId = categoryUIState.id
                              coroutineScope.launch {
                                  onCategoryUpdate(viewModel.categoryUiState.categoryDetails.copy(
                                      userId = userId,
                                      name = name,
                                      icon = categoryValue,
                                      color = colorValue,
                                      type = isSpend,
                                  ))
                                  viewModel.insertCategory()
                              }
                        onCategory(userId)
                    },
                    modifier = Modifier
                        .width(220.dp)
                        .height(60.dp),
                    enabled = isSubmit,
                    colors = ButtonDefaults.buttonColors(Color(247, 177, 12))
                ) {
                    Text(
                        text = stringResource(id = R.string.create),
                        fontSize = 24.sp
                    )
                }
            }
        }
    }
}

@Composable
fun NameText(
    name: String,
    title: String,
    isFocus: Boolean,
    onChange: (String) -> Unit,
    onFocus: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.Center
    ) {
        IconButton(
            onClick = { /*TODO*/ },
            modifier = Modifier
                .background(Color(119, 139, 133), CircleShape)
                .size(40.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.EditNote,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(30.dp)
            )
        }
        TextField(
            value = name,
            onValueChange = { onChange(it) },
            label = { if (!isFocus) Text(
                text = title,
                fontSize = 20.sp,
                color = Color.Gray
            ) else null },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 14.dp)
                .height(54.dp)
                .onFocusChanged { focusState ->
                    onFocus(focusState.isFocused)
                },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.Black,
                unfocusedContainerColor = Color.Black,
                disabledContainerColor = Color.Black,
                focusedTextColor = Color.White,
            ),
            singleLine = true,
            textStyle = TextStyle(
                fontSize = 22.sp,  // Điều chỉnh kích thước chữ ở đây
                color = Color.White // Đảm bảo màu chữ vẫn là màu trắng
            )
        )
    }
}

@Composable
fun SelectCategory(
    modifier: Modifier = Modifier,
    categoryValue: Int,
    onSelect: (Int) -> Unit,
    listArea: List<Area>,
    isSpend: Int,
) {
    var quantityItem by remember { mutableIntStateOf(8) }
    var isSelectType by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier.padding(top = 20.dp),
    ) {
        Text(
            text = stringResource(id = R.string.icon),
            color = Color(177, 179, 155),
            fontSize = 20.sp
        )
        val listCategorySave = listArea.filter { it.type == isSpend }
        val filterListArea = if(isSpend==1 || isSpend==0) {
            AreaList.listAllIcon
        } else {
            AreaList.listAccountIcon
        }

        val iconsInCategorySave = listCategorySave.map { it.icon }.toSet()

        val filteredListArea = filterListArea.filter { it.icon !in iconsInCategorySave }

        val sizeOfList = filteredListArea.size
        val quantityRow = sizeOfList / 3
        if(sizeOfList <= quantityItem) {
            quantityItem = sizeOfList
        }
        Column(
            modifier = Modifier
                .padding(start = 20.dp, end = 20.dp)
                .fillMaxWidth(),
        ) {
            for (indexRow in 0..quantityRow) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (index in indexRow*3..indexRow*3+2) {
                        if(sizeOfList > quantityItem) {
                            if(index < quantityItem) {
                                ItemIconCategory(
                                    categoryValue = categoryValue,
                                    itemIcon = filteredListArea[index],
                                    onSelect = onSelect,
                                )
                            } else if(index < sizeOfList && !isSelectType) {
                                ItemIconCategory(
                                    categoryValue = categoryValue,
                                    itemIcon = filteredListArea[index],
                                    onSelect = onSelect,
                                )
                            } else if(index == quantityItem && isSelectType) {
                                IconButton(
                                    modifier = Modifier
                                        .padding(top = 20.dp)
                                        .size(50.dp)
                                        .background(Color(253, 194, 42), CircleShape),
                                    onClick = {
                                        isSelectType = false
                                        quantityItem = sizeOfList
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Dialpad,
                                        contentDescription = null,
                                        modifier = Modifier.size(40.dp),
                                        tint = Color.White
                                    )
                                }
                            } else if(index == indexRow*3+2 && !isSelectType && quantityRow*3 != sizeOfList) {
                                Box(modifier = Modifier.size(60.dp)) {
                                }
                            }
                        } else {
                            if(index < quantityItem) {
                                ItemIconCategory(
                                    categoryValue = categoryValue,
                                    itemIcon = filteredListArea[index],
                                    onSelect = onSelect,
                                )
                            } else if(index == indexRow*3+2) {
                                Box(modifier = Modifier.size(60.dp)) {
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ItemIconCategory(
    categoryValue: Int,
    itemIcon: ItemIcon,
    onSelect: (Int) -> Unit,
) {
    val shapeModifier = if(categoryValue==itemIcon.icon) {
        Modifier.background(Color(119, 139, 133))
    } else {
        Modifier.background(Color(119, 139, 133), CircleShape)
    }
    Box(
        modifier = Modifier
            .padding(top = 20.dp)
            .size(60.dp)
            .then(shapeModifier)
            .clickable { onSelect(itemIcon.icon) },
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(itemIcon.icon),
            contentDescription = null,
            modifier = Modifier.size(40.dp),
            colorFilter = ColorFilter.tint(Color.White)
        )
    }
}

@Composable
fun SelectColor(
    modifier: Modifier = Modifier,
    colorValue: Int,
    onSelect: (Int) -> Unit,
) {
    var quantityItem by remember { mutableIntStateOf(6) }
    Column(
        modifier = Modifier.padding(top = 20.dp),
    ) {
        Text(
            text = stringResource(id = R.string.color),
            color = Color(177, 179, 155),
            fontSize = 20.sp,
        )
        val listColor = listColor
        val sizeOfList = listColor.size
        val quantityRow = sizeOfList / 7
        Column() {
            for (indexRow in 0..quantityRow) {
                Row(
                    modifier = Modifier
                        .padding(start = 10.dp, end = 10.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (index in indexRow*7..indexRow*7+6) {
                        if(index < quantityItem) {
                            Box(modifier = Modifier.padding(top = 20.dp)) {
                                Box(modifier = Modifier
                                    .background(listColor[index].color, CircleShape)
                                    .size(30.dp)
                                    .clickable { onSelect(listColor[index].color.toArgb()) }) {
                                    if(colorValue==listColor[index].color.toArgb()) {
                                        Icon(
                                            imageVector = Icons.Filled.Check,
                                            contentDescription = null,
                                            modifier = Modifier.size(24.dp).align(Alignment.Center),
                                            tint = Color.White
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.size(14.dp))
                        } else if(index == quantityItem && quantityItem != sizeOfList) {
                            Box(modifier = Modifier.padding(top = 20.dp)) {
                                IconButton(modifier = Modifier
                                    .background(Color(119, 139, 133), CircleShape)
                                    .size(30.dp),
                                    onClick = { quantityItem = sizeOfList}
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Add,
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp),
                                        tint = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreateCategoryPreview() {
//    CreateCategoryScreen()
}