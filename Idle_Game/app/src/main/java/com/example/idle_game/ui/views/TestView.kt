package com.example.idle_game.ui.views

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.idle_game.ui.views.models.TestViewModel


@Composable
fun TestView(
    modifier: Modifier = Modifier,
    viewModel: TestViewModel = hiltViewModel()
) {
    Text(
        text = "Welcome on the test-page!!!",
        modifier = modifier
    )
}