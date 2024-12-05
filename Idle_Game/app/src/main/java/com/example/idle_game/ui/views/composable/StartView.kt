package com.example.idle_game.ui.views.composable

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.idle_game.ui.views.models.StartViewModel

@Composable
fun StartView(viewModel: StartViewModel = hiltViewModel(), modifier: Modifier = Modifier) {
    Text(
        text = "Welcome on the start-page!!!",
        modifier = modifier
    )
}