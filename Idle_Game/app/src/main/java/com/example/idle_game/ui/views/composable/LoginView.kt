package com.example.idle_game.ui.views.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.idle_game.ui.views.models.LoginViewModel

@Composable
fun LoginView(viewModel: LoginViewModel = hiltViewModel(), onSignUpSuccess: () -> Unit) {
    viewModel.init { onSignUpSuccess() }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Bitte einen Benutzernamen eingeben: "
        )
        var input by remember { mutableStateOf("") }
        OutlinedTextField(
            value = input,
            onValueChange = { input = it },
            label = { Text("Benutzername") })
        OutlinedButton(
            onClick = { viewModel.buttonSubmit(input, { onSignUpSuccess() }) },
            modifier = Modifier.background(Color.White)
        ) { Text("Submit") }
    }
}