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


object RegisterDestination: NavigationDestination {
    override val route = "register"
    override val titleRes = R.string.register
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormRegister(
    navigateUp: () -> Unit,
    navigateToHome: (Int) -> Unit,
    viewModel: LoginViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = stringResource(id = R.string.register),
                canNavigateBack = true,
                navigateUp = navigateUp,
                modifier = Modifier.background(Color(38, 38, 38))
            )
        }
    ) { contentPadding ->

        val loginUIState by viewModel.loginUiState.collectAsState()
        var valueName by remember { mutableStateOf("") }
        var valueEmail by remember { mutableStateOf("") }
        var isEmailValid by remember { mutableStateOf(false) }
        var valuePassword by remember { mutableStateOf("") }
        var isPasswordVisible by remember { mutableStateOf(false) }
        var valueRePassword by remember { mutableStateOf("") }
        val isSubmit: Boolean =
            (valueName!="") &&
                    isEmailValid &&
                    valuePassword!="" &&
                    valuePassword.length>=5 &&
                    valuePassword==valueRePassword &&
                    isCheckEmail(loginUIState.userList, valueEmail)

        val onValueChange: (UserDetails) -> Unit = viewModel::updateUserUiState
        val coroutineScope = rememberCoroutineScope()

        if(viewModel.userId != 0) {
            navigateToHome(viewModel.userId)
        }

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
                text = stringResource(id = R.string.hag_tag_register),
                color = Color(224, 224, 197),
                fontSize = 20.sp,
                modifier = Modifier.padding(top = 80.dp, bottom = 60.dp),
            )
            OutlinedTextField(
                value = valueName,
                onValueChange = {
                    valueName = it
                    onValueChange(viewModel.itemUiState.userDetails.copy(name = it))
                },
                label = { Text(
                    text = stringResource(id = R.string.name),
                    color = if(valueName=="") Color.Red else Color.White
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
                    .padding(0.dp)
                    .background(Color(38, 38, 38, 38)),
                enabled = true,
                singleLine = true
            )
            Spacer(modifier = Modifier.height(14.dp))
            OutlinedTextField(
                value = valueEmail,
                onValueChange = {
                    valueEmail = it
                    isEmailValid = isValidEmail(it)
                    onValueChange(viewModel.itemUiState.userDetails.copy(email = it))
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
            Spacer(modifier = Modifier.height(7.dp))
            if(!isCheckEmail(loginUIState.userList, valueEmail)) {
                isEmailValid = false
                Text(
                    text = stringResource(id = R.string.check_email),
                    color = Color.Red
                )
            }

            Spacer(modifier = Modifier.height(7.dp))
            OutlinedTextField(
                value = valuePassword,
                onValueChange = {
                    valuePassword = it
                    onValueChange(viewModel.itemUiState.userDetails.copy(password = it))
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
            Spacer(modifier = Modifier.height(14.dp))
            OutlinedTextField(
                value = valueRePassword,
                onValueChange = {
                    valueRePassword = it
                },
                label = { Text(
                    text = stringResource(id = R.string.re_password),
                    color = if(valueRePassword=="" || valuePassword != valueRePassword) Color.Red else Color.White
                ) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                ),
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
                modifier = Modifier
                    .height(60.dp)
                    .background(Color(38, 38, 38, 38)),
                enabled = true,
                singleLine = true
            )
            Spacer(modifier = Modifier.height(40.dp))
            Button(
                onClick = {
                    coroutineScope.launch {
                        viewModel.saveUser()
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
                    text = stringResource(id = R.string.register),
                    fontSize = 20.sp,
                    color = if(isSubmit) Color.Black else Color(212, 170, 13)
                )
            }
        }
    }
}


fun isCheckEmail(userList: List<User>, email: String): Boolean {
    if(userList == null) {
        return true
    } else {
        for (users in userList) {
            if(users.email.trim() == email.trim()) {
                return false
            }
        }
        return true
    }
}
