package com.example.idle_game.ui.views.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.idle_game.ui.views.models.ScoreBoardViewModel

@Composable
fun ScoreBoardView(viewModel: ScoreBoardViewModel = hiltViewModel()) {
    val viewState = viewModel.uiStateFlow.collectAsState().value
    val scoreList = viewState.scoreData.collectAsState(initial = emptyList()).value
    val playerObject = viewState.playerData.collectAsState(initial = null).value

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 75.dp)
                .align(Alignment.TopCenter)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Nutzer:",
                    modifier = Modifier,
                    style = TextStyle(fontSize = 20.sp)
                )
                Text(
                    text = "Punktzahl:",
                    modifier = Modifier,
                    style = TextStyle(fontSize = 20.sp)
                )
            }
            scoreList.forEachIndexed  { index, scoreEntity ->
                val backgroundColor = if (scoreEntity.username == playerObject?.username) {
                    MaterialTheme.colorScheme.primary
                } else if (index % 2 == 0) {
                    MaterialTheme.colorScheme.surfaceContainerHigh
                } else {
                    MaterialTheme.colorScheme.surfaceContainer
                }

                val textColor = if (backgroundColor == MaterialTheme.colorScheme.primary) {
                    MaterialTheme.colorScheme.inversePrimary
                } else {
                    MaterialTheme.colorScheme.onSurface
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(backgroundColor)
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = (index + 1).toString() + ". " + scoreEntity.username,
                        modifier = Modifier
                            .weight(1f),
                        style = TextStyle(fontSize = 20.sp, color = textColor),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "${scoreEntity.score}",
                        modifier = Modifier,
                        style = TextStyle(fontSize = 20.sp, color = textColor),
                        maxLines = 1
                    )
                }
            }
        }

        Button(
            colors = ButtonDefaults.buttonColors(
            ),
            onClick = { viewModel.refreshScoreBoard() },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .zIndex(1f),
        ) {
            Text(
                text = "Neu laden",
                style = TextStyle(fontSize = 20.sp)
            )
        }
    }
}
