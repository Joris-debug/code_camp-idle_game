package com.example.idle_game.ui.views.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.idle_game.ui.theme.AppColors
import com.example.idle_game.ui.views.models.ScoreBoardViewModel


@Composable
fun ScoreBoardView(viewModel: ScoreBoardViewModel = hiltViewModel()) {

    val viewState = viewModel.uiStateFlow.collectAsState().value
    val scoreList = viewState.scoreData.collectAsState(initial = emptyList()).value

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.tertiary)
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
                    text = "Name:",
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
                val backgroundColor = if (index % 2 == 0) {
                    AppColors.primary
                } else {
                    AppColors.secondary
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(backgroundColor)
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = scoreEntity.username,
                        modifier = Modifier,
                        style = TextStyle(fontSize = 20.sp)
                    )
                    Text(
                        text = "${scoreEntity.score}",
                        modifier = Modifier,
                        style = TextStyle(fontSize = 20.sp)
                    )
                }
            }
        }

        Button(
            colors = ButtonDefaults.buttonColors(
                containerColor = AppColors.buttonColor
            ),
            onClick = { viewModel.refreshScoreBoard() },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .zIndex(1f)
        ) {
            Text(text = "Reload Scoreboard")
        }
    }
}
