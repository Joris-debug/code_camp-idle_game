package com.example.idle_game.ui.views.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.idle_game.ui.theme.AppColors
import com.example.idle_game.ui.views.models.InventoryViewModel

@Composable
fun InventoryView(viewModel: InventoryViewModel = hiltViewModel()) {
    val viewState = viewModel.uiStateFlow.collectAsState().value
    val shopDataList = viewState.shopData.collectAsState(initial = emptyList()).value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.tertiary)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Shop Items:", modifier = Modifier.padding(16.dp))
        shopDataList.forEach { item ->
            Text(
                text = "Item: ${item.name}, Price: ${item.cost}",
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AppColors.primary)
                    .padding(8.dp)
            )
        }
    }

}