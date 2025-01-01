package com.example.idle_game.ui.views.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.work.WorkManager
import com.example.idle_game.ui.views.models.StartViewModel
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.material3.Button
import androidx.compose.runtime.ComposableTarget
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.idle_game.R

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

        Row(verticalAlignment = Alignment.Bottom) {
//            PassiveBox(
//                painterResource(id = R.drawable.hacker),
//                "Hacker",
//                viewState.value.hackerLvl1,
//                viewState.value.hackerLvl2,
//                viewState.value.hackerLvl3,
//                viewState.value.hackerLvl4,
//                viewState.value.hackerLvl5
//            )
//            PassiveBox(
//                painterResource(id = R.drawable.crypto_miner),
//                "Miner",
//                viewState.value.minerLvl1,
//                viewState.value.minerLvl2,
//                viewState.value.minerLvl3,
//                viewState.value.minerLvl4,
//                viewState.value.minerLvl5
//            )
//            PassiveBox(
//                painterResource(id = R.drawable.botnet),
//                "Botnet",
//                viewState.value.botnetLvl1,
//                viewState.value.botnetLvl2,
//                viewState.value.botnetLvl3,
//                viewState.value.botnetLvl4,
//                viewState.value.botnetLvl5
//            )
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

@Composable
fun PassiveBox(
    painter: Painter,
    description: String,
    lvl1: Int,
    lvl2: Int,
    lvl3: Int,
    lvl4: Int,
    lvl5: Int,
) {
    Row () {
        Image(painter = painter, description, modifier = Modifier.fillMaxWidth(0.50f).fillMaxHeight())
        Column {
            Text("Level 1: {$lvl1}")
            Text("Level 2: {$lvl2}")
            Text("Level 3: {$lvl3}")
            Text("Level 4: {$lvl4}")
            Text("Level 5: {$lvl5}")
        }
    }
}