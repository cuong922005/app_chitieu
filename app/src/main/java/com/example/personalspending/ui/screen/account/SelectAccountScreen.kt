package com.example.personalspending.ui.screen.account

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.zIndex
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.personalspending.R
import com.example.personalspending.data.Account
import java.text.NumberFormat
import java.util.Locale

@Composable
fun SelectAccountScreen(
    accounts: List<Account>,
    onDestroy:() -> Unit,
    currentAccount: Int,
    onSelect: (Int) -> Unit,
) {

    var idCurrentAccount by remember { mutableIntStateOf(currentAccount) }
    var total = 0.0
    for (item in accounts) {
        total += item.money
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0f, 0f, 0f, alpha = 0.4f))
            .zIndex(4f),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clickable { onDestroy() },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .width(350.dp)
                    .height(420.dp)
                    .background(Color(38, 38, 38))
                    .pointerInput(Unit) {
                        detectTapGestures {
                        }
                    }
            ) {
                Column(
                    modifier = Modifier.padding(30.dp).padding(bottom = 80.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.select_acc),
                        color = Color(225, 225, 225),
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )
                    LazyColumn(
                        modifier = Modifier.padding( 20.dp, top = 40.dp)
                    ) {
                        item {
                            Row(
                                modifier = Modifier.padding(bottom = 24.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                RadioButton(selected = idCurrentAccount==0, onClick = { idCurrentAccount = 0 })
                                Column {
                                    Text(
                                        text = stringResource(id = R.string.total),
                                        modifier = Modifier.padding(bottom = 10.dp),
                                        color = Color(225, 225, 225),
                                        fontSize = 22.sp
                                    )
                                    Text(
                                        text = convertMoney(money = total) + " đ",
                                        color = Color(175, 175, 175),
                                        fontSize = 18.sp
                                    )
                                }
                            }
                        }
                        items(accounts) {item ->
                            Row(
                                modifier = Modifier.padding(bottom = 24.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                RadioButton(
                                    selected = idCurrentAccount==item.id,
                                    onClick = {
                                        idCurrentAccount = item.id
                                    }
                                )
                                Column {
                                    Text(
                                        text = item.name,
                                        modifier = Modifier.padding(bottom = 10.dp),
                                        color = Color(225, 225, 225),
                                        fontSize = 22.sp
                                    )
                                    Text(
                                        text = convertMoney(money = item.money) + " đ",
                                        color = Color(175, 175, 175),
                                        fontSize = 18.sp
                                    )
                                }
                            }
                        }
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomEnd)
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    Button(
                        onClick = {
                                  onDestroy()
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color.Transparent,
                            contentColor = Color.Green
                        ),
                        elevation = ButtonDefaults.elevation(
                            defaultElevation = 0.dp,
                            pressedElevation = 0.dp,
                            disabledElevation = 0.dp,
                            hoveredElevation = 0.dp,
                            focusedElevation = 0.dp
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = stringResource(id = R.string.destroy),
                            fontSize = 22.sp
                        )
                    }
                    Button(
                        onClick = {
                                  onDestroy()
                            onSelect(idCurrentAccount)
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color.Transparent,
                            contentColor = Color.Green
                        ),
                        elevation = ButtonDefaults.elevation(
                            defaultElevation = 0.dp,
                            pressedElevation = 0.dp,
                            disabledElevation = 0.dp,
                            hoveredElevation = 0.dp,
                            focusedElevation = 0.dp
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = stringResource(id = R.string.select),
                            fontSize = 22.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun convertMoney(money: Double): String {
    val numberFormat = NumberFormat.getNumberInstance(Locale.US)
    if(money >= 1000000) {
        val formattedNumber = numberFormat.format(money/1000000)
        return "${formattedNumber}Tr"
    } else {
        return numberFormat.format(money)
    }
}

@Preview(showBackground = true)
@Composable
fun SelectAccountScreenPreview() {
//    SelectAccountScreen(onDestroy = {})
}