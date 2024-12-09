package com.example.idle_game.ui.views.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.idle_game.ui.views.models.ScoreBoardViewModel

@Composable
fun ScoreBoardView(viewModel: ScoreBoardViewModel = hiltViewModel()) {

    val viewState = viewModel.uiStateFlow.collectAsState().value
    val scoreList = viewState.scoreData.collectAsState(initial = emptyList()).value

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Scoreboard: "
        )
        scoreList.forEach { scoreEntity ->
            Text(
                text = "Player: ${scoreEntity.username}, Score: ${scoreEntity.score}"
            )
        }
    }
}