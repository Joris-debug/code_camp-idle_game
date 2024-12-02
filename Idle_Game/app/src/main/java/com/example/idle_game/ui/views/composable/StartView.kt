package com.example.idle_game.ui.views.composable

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.work.WorkManager
import com.example.idle_game.ui.views.models.StartViewModel
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel


@Composable
fun StartView(
    workManager: WorkManager = WorkManager.getInstance(LocalContext.current),
    viewModel: StartViewModel = viewModel()
) {
    val viewState = viewModel.viewState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        if (viewState.value.isLoading) {
            CircularProgressIndicator()
        } else {
            Text(text = "Counter: ${viewState.value.counter}")
        }

        if (viewState.value.errorMessage != null) {
            Text(text = "Error: ${viewState.value.errorMessage}")
        }

        Button(onClick = { viewModel.incrementCounter(1, 15, workManager) }) {
            Text("Hacker +1")
        }

        Button(onClick = { viewModel.incrementCounter(2, 20, workManager) }) {
            Text("BotNet +2")
        }

        Button(onClick = { viewModel.incrementCounter(3, 30, workManager) }) {
            Text("Miner +3")
        }
    }

}