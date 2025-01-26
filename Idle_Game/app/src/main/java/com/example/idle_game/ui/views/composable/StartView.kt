package com.example.idle_game.ui.views.composable

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.idle_game.ui.views.models.StartViewModel
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.idle_game.R

@Composable
fun StartView(
    viewModel: StartViewModel = hiltViewModel()
) {
    val viewState = viewModel.viewState.collectAsState()
    var isClicked by remember { mutableStateOf(false) }


    // Scale-animation when coin is clicked
    val scale by animateFloatAsState(
        targetValue = if (isClicked) 1.05f else 1f,
        animationSpec = tween(durationMillis = 50)
    )
    LaunchedEffect (isClicked) {
        kotlinx.coroutines.delay(50)
        isClicked = false
    }


    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { viewModel.switchDisplayMode() }
                .background(color = MaterialTheme.colorScheme.surfaceContainerHigh)
                .padding(vertical = 15.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Coins: ${viewState.value.coins}",
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Bitcoins per Second: ${viewState.value.coinsPerSec}",
                    textAlign = TextAlign.Center
                )
            }
        }

        Text(
            text = if (viewState.value.activeBoost > 0) {
                "Boost Aktiv: ${getBoostName(viewState.value.activeBoost)}"
            } else {
                "Kein Boost aktiv"
            },
            style = TextStyle(color = Color.Black),
            textAlign = TextAlign.Center
        )

        Image(
            painter = painterResource(id = R.drawable.bitcoin), "Klicken für Bitcoins",
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = {
                        viewModel.coinClick()
                        isClicked = !isClicked
                    }
                )
                .graphicsLayer(scaleX = scale, scaleY = scale)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
        )
        {
            Row(
                verticalAlignment = Alignment.Bottom, modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                PassiveBox(
                    painterResource(id = R.drawable.hacker),
                    "Hacker",
                    viewState.value.hackerCount
                )
                PassiveBox(
                    painterResource(id = R.drawable.crypto_miner),
                    "Miner",
                    viewState.value.minerCount
                )
                PassiveBox(
                    painterResource(id = R.drawable.botnet),
                    "Botnet",
                    viewState.value.botnetCount
                )
            }
        }
    }
}

private fun getBoostName(boostType: Int): String {
    return when (boostType) {
        1 -> "low boost"
        2 -> "medium boost"
        3 -> "high boost"
        else -> ""
    }
}

@Composable
fun PassiveBox(painter: Painter, description: String, count: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painter,
            description,
            modifier = Modifier,
        )
        Text(": $count", maxLines = 1)
    }
}




