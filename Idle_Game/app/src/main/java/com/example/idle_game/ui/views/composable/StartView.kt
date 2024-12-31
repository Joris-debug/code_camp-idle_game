package com.example.idle_game.ui.views.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.work.WorkManager
import com.example.idle_game.ui.views.models.StartViewModel
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun StartView(
    workManager: WorkManager = WorkManager.getInstance(LocalContext.current),
    viewModel: StartViewModel = hiltViewModel()
) {
    val viewState = viewModel.viewState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier.fillMaxWidth(),
//            MaterialTheme.colorScheme.surfaceContainerHigh
        ) {
            Text(
                text = "Coins: ${viewState.value.coins}",
                Modifier.padding(horizontal = 5.dp),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Bitcoins per Second: ${viewState.value.coinsPerSec}",
                textAlign = TextAlign.Center
            )
        }
        Button(onClick = { viewModel.coinClick() }) {
            Text("Click for Bitcoins")
        }

        Row {
            Text(text = "Hackers: ${viewState.value.hackers}  ")
            Text(text = "Botnets: ${viewState.value.bots}  ")
            Text(text = "Miners: ${viewState.value.miners}  ")
        }


        /*   Debug code start ------------------------------------------------------------------------------------------------*/
        Button(onClick = { viewModel.addHacker() }) { Text("Add new Hacker") }
        Button(onClick = { viewModel.addBot() }) { Text("Add new Bot") }
        Button(onClick = { viewModel.addMiner() }) { Text("Add new Miner") }
        Button(onClick = { viewModel.addBooster(1) }) { Text("Add new Booster lvl 1") }
        Button(onClick = { viewModel.addBooster(2) }) { Text("Add new Booster lvl 2") }
        Button(onClick = { viewModel.addBooster(3) }) { Text("Abmelden") }
        /*   Debug code end   ------------------------------------------------------------------------------------------------*/
    }

}