package com.example.personalspending.ui.screen.login

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.personalspending.R
import com.example.personalspending.data.User
import com.example.personalspending.ui.model.AppViewModelProvider
import com.example.personalspending.ui.navigate.NavigationDestination
import kotlinx.coroutines.launch


object LoginDestination: NavigationDestination {
    override val route = "login"
    override val titleRes = R.string.login
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormLogin(
    navigateUp: () -> Unit,
    navigateToHome: (Int) -> Unit,
    viewModel: LoginViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = stringResource(id = R.string.login),
                canNavigateBack = true,
                navigateUp = navigateUp,
                modifier = Modifier.background(Color(38, 38, 38))
            )
        }
    ) { contentPadding ->

        var valueEmail by remember { mutableStateOf("") }
        var isEmailValid by remember { mutableStateOf(false) }
        var valuePassword by remember { mutableStateOf("") }
        var isPasswordVisible by remember { mutableStateOf(false) }
        var isUserWarn by remember { mutableStateOf(true) }
        val isSubmit: Boolean = isEmailValid && valuePassword!="" && valuePassword.length>=5

        val loginUIState by viewModel.loginUiState.collectAsState()
        val coroutineScope = rememberCoroutineScope()

        Column(
            modifier = Modifier
                .padding(top = contentPadding.calculateTopPadding())
                .background(Color(38, 38, 38))
                .fillMaxSize()
                .border(width = 0.dp, color = Color.Transparent)
            ,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if(isUserWarn) {
                Text(
                    text =  stringResource(id = R.string.hag_tag_register),
                    color = Color(224, 224, 197),
                    fontSize = 20.sp,
                    modifier = Modifier.padding(top = 80.dp, bottom = 60.dp),
                )
            } else {
                Text(
                    text = stringResource(id = R.string.check_login),
                    color = Color(223, 206, 6),
                    fontSize = 20.sp,
                    modifier = Modifier.padding(top = 80.dp, bottom = 60.dp),
                )
            }
            OutlinedTextField(
                value = valueEmail,
                onValueChange = {
                    valueEmail = it
                    isEmailValid = isValidEmail(it)
                },
                label = { Text(
                    text = stringResource(id = R.string.email),
                    color = if(valueEmail=="" || !isEmailValid) Color.Red else Color.White
                ) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                ),
                modifier = Modifier
                    .height(60.dp)
                    .background(Color(38, 38, 38, 38)),
                enabled = true,
                singleLine = true
            )
            Spacer(modifier = Modifier.height(14.dp))
            OutlinedTextField(
                value = valuePassword,
                onValueChange = {
                    valuePassword = it
                },
                label = { Text(
                    text = stringResource(id = R.string.password),
                    color = if(valuePassword=="" || valuePassword.length <= 5) Color.Red else Color.White
                ) },
                visualTransformation = if (isPasswordVisible) VisualTransformation.None
                else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(
                        onClick = { isPasswordVisible = !isPasswordVisible }
                    ) {
                        Icon(
                            imageVector =  if (isPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = if (isPasswordVisible) "Hide password" else "Show password"
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                ),
                modifier = Modifier
                    .height(60.dp)
                    .background(Color(38, 38, 38, 38)),
                enabled = true,
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Password
                )
            )
            Spacer(modifier = Modifier.height(40.dp))
            Button(
                onClick = {
                    coroutineScope.launch {
                        if(isCheckUser(loginUIState.userList, valueEmail, valuePassword) != 0) {
                            navigateToHome(isCheckUser(loginUIState.userList, valueEmail, valuePassword))
                        } else {
                            isUserWarn = false
                        }
                    }
                },
                enabled = isSubmit,
                modifier = Modifier
                    .width(200.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    Color(212, 170, 13)
                )
            ) {
                Text(
                    text = stringResource(id = R.string.login),
                    fontSize = 20.sp,
                    color = if(isSubmit) Color.Black else Color(212, 170, 13)
                )
            }
        }
    }
}


fun isCheckUser(userList: List<User>, email: String, password: String): Int {
    if(userList == null) {
        return 0
    } else {
        for (users in userList) {
            if(users.email.trim() == email.trim()) {
                if(users.password.trim() == password.trim()) {
                    return users.id
                }
            }
        }
        return 0
    }
}
