package com.example.idle_game.ui.views.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.idle_game.ui.views.models.TestViewModel

@Composable
fun TestView(viewModel: TestViewModel = hiltViewModel()) {
    val scrollState = rememberScrollState()

    Column(modifier = Modifier.verticalScroll(scrollState)) {
        Text(
            text = "Welcome on the test-page!!!",
            modifier = Modifier.padding(16.dp)
        )

        Button(
            colors = ButtonDefaults.buttonColors(),
            onClick = { viewModel.activateBluetooth() },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Bluetooth aktivieren",
                style = TextStyle(fontSize = 20.sp)
            )
        }
    }
}
