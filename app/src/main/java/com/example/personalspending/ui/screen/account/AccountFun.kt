package com.example.personalspending.ui.screen.account

import android.annotation.SuppressLint
import androidx.compose.foundation.background
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.personalspending.R
import com.example.personalspending.ui.screen.category.CategoryDetails
import com.example.personalspending.ui.screen.category.SelectCategory
import com.example.personalspending.ui.screen.category.SelectColor
import com.example.personalspending.ui.screen.home.getColorArea
import com.example.personalspending.ui.screen.home.getIconArea
import com.example.personalspending.ui.screen.login.AccountDetails
import com.example.personalspending.ui.screen.transaction.MoneyValue
import com.example.personalspending.ui.screen.transaction.Note
import kotlinx.coroutines.launch

@SuppressLint("SuspiciousIndentation")
@Composable
fun AccountFun(
    modifier: Modifier = Modifier,
    viewModel: AccountViewModel,
    onCategoryUpdate: (CategoryDetails) -> Unit,
    onAccountUpdate: (AccountDetails) -> Unit,
    onAccount: (Int) -> Unit,
    accountCurrent: Int = 0,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 120.dp, start = 20.dp, end = 20.dp)
            .zIndex(1f)
            .background(Color.Black)
    ) {

        var moneyValue by remember { mutableIntStateOf(0) }
        var nameValue by remember { mutableStateOf("") }
        var categoryValue by remember { mutableIntStateOf(0) }
        var colorValue by remember { mutableIntStateOf(0) }
        var isUpdate by remember { mutableStateOf(true) }
        var isValueUpdate by remember { mutableStateOf(false) }

        val listArea = viewModel.accountUiState.areas

        if(accountCurrent != 0 && isUpdate && !isValueUpdate) {
            for(item in viewModel.accountUiState.accounts) {
                if(item.id == accountCurrent) {
                    moneyValue = item.money.toInt()
                    nameValue = item.name
                    categoryValue = getIconArea(listArea, item.areaId)
                    colorValue = getColorArea(listArea, item.areaId)
                    isValueUpdate = true
                }
            }
        }

        val isSubmit = moneyValue > 1000 && nameValue != "" && categoryValue != 0 && colorValue != 0

        val coroutineScope = rememberCoroutineScope()

        LazyColumn() {
            item {
                MoneyValue(
                    value = moneyValue,
                    onValueChange = {
                        moneyValue = it
                        isUpdate = false
                    }
                )
                Spacer(modifier = Modifier.size(40.dp))
                Note(
                    value = nameValue,
                    onValueChange = {
                        nameValue = it
                        isUpdate = false
                                    },
                    title = R.string.name_account
                )
                SelectCategory(
                    categoryValue = categoryValue,
                    onSelect = {
                        categoryValue = it
                        isUpdate = false
                               },
                    listArea = viewModel.accountUiState.areas,
                    isSpend = if(accountCurrent==0) 2 else -1
                )
                SelectColor(
                    colorValue = colorValue,
                    onSelect = {
                        colorValue = it
                        isUpdate = false
                    }
                )
                if(isSubmit) {
                    Box(
                        modifier = Modifier
                            .padding(top = 30.dp, bottom = 20.dp)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Button(
                            onClick = {
                                val userId = viewModel.accountUiState.userId
                                coroutineScope.launch {
                                      if (accountCurrent==0) {
                                          onCategoryUpdate(viewModel.accountUiState.categoryDetails.copy(
                                              name = nameValue,
                                              userId = userId,
                                              icon = categoryValue,
                                              color = colorValue,
                                              type = 2,
                                          ))
                                          viewModel.insertCategory()
                                          val areaId = viewModel.getCategoryId(categoryValue, nameValue, colorValue)
                                          onAccountUpdate(viewModel.accountUiState.accountDetails.copy(
                                              name = nameValue,
                                              userId = userId,
                                              money = moneyValue.toDouble(),
                                              areaId = areaId
                                          ))
                                          viewModel.insertAccount()
                                      } else {
                                          var areaId = viewModel.getCategoryId(categoryValue, nameValue, colorValue)
                                          if(areaId == 0) {
                                              onCategoryUpdate(viewModel.accountUiState.categoryDetails.copy(
                                                  name = nameValue,
                                                  userId = userId,
                                                  icon = categoryValue,
                                                  color = colorValue,
                                                  type = 2,
                                              ))
                                              viewModel.insertCategory()
                                              areaId = viewModel.getCategoryId(categoryValue, nameValue, colorValue)
                                          }
                                          onAccountUpdate(viewModel.accountUiState.accountDetails.copy(
                                              id = accountCurrent,
                                              name = nameValue,
                                              userId = userId,
                                              money = moneyValue.toDouble(),
                                              areaId = areaId
                                          ))
                                          viewModel.updateAccount()
                                      }
                                  }
                                onAccount(userId)
                            },
                            modifier = Modifier
                                .width(220.dp)
                                .height(60.dp),
                            colors = ButtonDefaults.buttonColors(Color(247, 177, 12))
                        ) {
                            Text(
                                text = if(accountCurrent == 0) {
                                    stringResource(id = R.string.create)
                                } else {
                                    stringResource(id = R.string.update)
                                       },
                                fontSize = 24.sp
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.size(60.dp))
            }
        }
    }
}

@Preview
@Composable
fun AccountFunPreview() {
//    AccountFun()
}