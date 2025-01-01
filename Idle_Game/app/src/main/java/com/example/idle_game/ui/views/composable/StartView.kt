package com.example.idle_game.ui.views.composable

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.work.WorkManager
import com.example.idle_game.ui.views.models.StartViewModel
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.hilt.navigation.compose.hiltViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartView(
    workManager: WorkManager = WorkManager.getInstance(LocalContext.current),
    viewModel: StartViewModel = hiltViewModel()
) {
    val viewState = viewModel.viewState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        if (viewState.value.isLoading) {
            CircularProgressIndicator()
        } else {
            Text(text = "Coins: ${viewState.value.coins}")
        }

        if (viewState.value.errorMessage != null) {
            Text(text = "Error: ${viewState.value.errorMessage}")
        }
        Row {
            Text(text = "Hackers: ${viewState.value.hackers}  ")
            Text(text = "Botnets: ${viewState.value.bots}  ")
            Text(text = "Miners: ${viewState.value.miners}  ")
        }
        Text(text = "Bitcoins per Second: ${viewState.value.coinsPerSec}")
        Button(onClick = { viewModel.coinClick() }) {
            Text("Click for Bitcoins")
        }
    }
}