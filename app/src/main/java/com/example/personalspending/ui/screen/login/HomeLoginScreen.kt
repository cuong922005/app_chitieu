package com.example.personalspending.ui.screen.login

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.personalspending.R
import com.example.personalspending.ui.navigate.NavigationDestination
import com.example.personalspending.ui.theme.PersonalSpendingTheme

object LoginHomeDestination: NavigationDestination {
    override val route = "home login"
    override val titleRes = R.string.register
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeLoginScreen(
    onHome: (Int) -> Unit,
    navigateToLogin: () -> Unit,
    navigateToRegister: () -> Unit,
) {
    val userId = getUserId(LocalContext.current)
    if(userId == 0) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = stringResource(id = LoginHomeDestination.titleRes),
                    canNavigateBack = false,
                    modifier = Modifier.background(Color(38, 38, 38))
                )
            }
        ) { contentPadding ->
            Column(
                modifier = Modifier
                    .padding(top = contentPadding.calculateTopPadding())
                    .background(Color(38, 38, 38))
                    .fillMaxSize()
                    .border(width = 0.dp, color = Color.Transparent)
                ,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.hag_tag),
                    color = Color(224, 224, 197),
                    fontSize = 20.sp,
                    modifier = Modifier.padding(top = 80.dp, bottom = 100.dp),
                )
                Button(
                    onClick = navigateToRegister,
                    modifier = Modifier
                        .width(200.dp)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        Color(212, 170, 13)
                    )
                ) {
                    Text(
                        text = stringResource(id = R.string.register),
                        fontSize = 20.sp,
                        color = Color.Black
                    )
                }
                Spacer(modifier = Modifier.height(28.dp))
                Button(
                    onClick = navigateToLogin,
                    modifier = Modifier
                        .width(200.dp)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        Color(38, 38, 38)
                    )
                ) {
                    Text(
                        text = stringResource(id = R.string.login),
                        fontSize = 20.sp,
                        color = Color(77, 164, 101)
                    )
                }
            }
        }
    } else {
        onHome(userId)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    title: String,
    canNavigateBack: Boolean,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    navigateUp: () -> Unit = {}
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        },
        modifier = modifier
            .fillMaxWidth(),
//            .clip(
//                RoundedCornerShape(
//                    topStart = 0.dp,
//                    topEnd = 0.dp,
//                    bottomStart = 40.dp,
//                    bottomEnd = 40.dp
//                )
//            ),
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.smallTopAppBarColors(
            Color(22, 89, 49)
        ),
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(
                    onClick = navigateUp,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = title,
                        tint = Color.White,
                    )
                }
            }
        }
    )
}

fun isValidEmail(email: String): Boolean {
    val pattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}".toRegex()
    return pattern.matches(email)
}

@Preview(showBackground = true)
@Composable
fun TopAppBarPreview() {
    PersonalSpendingTheme {
//        TopAppBar(title = stringResource(id = R.string.login), canNavigateBack = true)
//        HomeLoginScreen()
//        FormLogin()
//        FormRegister()
    }
}