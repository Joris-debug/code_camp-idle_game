package com.example.idle_game.ui.views.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.idle_game.ui.views.models.ScoreBoardViewModel

@Composable
fun ScoreBoardView(viewModel: ScoreBoardViewModel = hiltViewModel()) {

    LazyColumn (modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        item {
            Text(
                text = "Scoreboard: "
            )
        }
    }
}