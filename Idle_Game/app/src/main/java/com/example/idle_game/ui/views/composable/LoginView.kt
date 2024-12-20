package com.example.idle_game.ui.views.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.test.espresso.util.filter
import com.example.idle_game.ui.views.models.LoginViewModel
import com.example.idle_game.ui.views.states.StartViewState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun LoginView(viewModel: LoginViewModel = hiltViewModel(), onSignUpSuccess: () -> Unit) {
    val viewState = viewModel.viewState.collectAsState()
    var inputUsername by remember { mutableStateOf("") }
    var inputPassword by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = viewState.value.errorMessage,
            color = Color.Red
        )
        Text(
            text = "Bitte Benutzernamen und Passwort eingeben: "
        )
        OutlinedTextField(
            value = inputUsername,
            onValueChange = { input -> inputUsername = viewModel.checkInput(input, true) },
            singleLine = true,
//            onValueChange = { inputUsername = it },
            label = { Text("Benutzername") })

        OutlinedTextField(
            value = inputPassword,
            singleLine = true,
            onValueChange = { input -> inputPassword = viewModel.checkInput(input, false) },
//            onValueChange = { inputPassword = it },
            label = { Text("Passwort") })

        OutlinedButton(
            onClick = { viewModel.buttonSubmit(inputUsername, inputPassword, { onSignUpSuccess() }) },
            modifier = Modifier.background(Color.White)
        ) { Text("Submit") }
    }
}