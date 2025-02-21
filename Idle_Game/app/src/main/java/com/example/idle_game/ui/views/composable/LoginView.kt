package com.example.idle_game.ui.views.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.idle_game.ui.views.models.LoginViewModel
import com.example.idle_game.util.SoundManager

@Composable
fun LoginView(
    viewModel: LoginViewModel = hiltViewModel(),
    onSignUpSuccess: () -> Unit
) {
    val viewState = viewModel.viewState.collectAsState()
    var inputUsername by remember { mutableStateOf("") }
    var inputPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = viewState.value.errorMessage,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Bitte Benutzernamen und Passwort eingeben: ",
            color = MaterialTheme.colorScheme.onBackground
        )
        OutlinedTextField(
            value = inputUsername,
            onValueChange = { input -> inputUsername = viewModel.checkInput(input, true) },
            singleLine = true,

            label = { Text("Benutzername") })

        OutlinedTextField(
            value = inputPassword,
            singleLine = true,
            onValueChange = { input -> inputPassword = viewModel.checkInput(input, false) },
            label = { Text("Passwort") },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (passwordVisible) Icons.Default.Check else Icons.Default.Search
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = "Passwort anzeigen/verstecken")
                }
            }
        )

        OutlinedButton(
            onClick = {
                viewModel.soundManager.playSound(SoundManager.CURSOR_SOUND_RESOURCE_ID)
                viewModel.buttonSubmit(inputUsername, inputPassword, { onSignUpSuccess() })
            },
            modifier = Modifier
        ) { Text("Submit") }
    }
}