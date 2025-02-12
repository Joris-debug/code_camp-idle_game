package com.example.idle_game.ui.views.composable.inventory

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.idle_game.data.database.models.InventoryData
import com.example.idle_game.data.database.models.ShopData
import com.example.idle_game.ui.views.models.InventoryViewModel
import com.example.idle_game.ui.views.states.InventoryViewState

//Logic for different screens for different item categories
@Composable
fun CategoryScreen(
    items: List<ShopData>,
    title: String,
    onBuyClick: (ShopData) -> Unit,
    onApplyClick: (ShopData) -> Unit,
    viewModel: InventoryViewModel,
    inventoryData: InventoryData,
    page: Int,
    viewState: InventoryViewState
) {

    val bitcoinBalance = inventoryData.bitcoins
    val itemAmounts = remember(items, inventoryData) {
        items.map { mutableIntStateOf(viewModel.getAmountOfItems(it, inventoryData)) }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 120.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title)

            items.forEachIndexed { index, item ->
                val itemAmount = itemAmounts[index].intValue

                ShopItemButtons(
                    item = item,
                    itemAmount = itemAmount,
                    onBuyClick = { onBuyClick(item) },
                    onApplyClick = { onApplyClick(item) },
                    viewState = viewState
                )
            }
        }
        if (page > 0) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Swipe left",
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
                    .size(40.dp)
            )
        }

        if (page < 2) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Swipe right",
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .size(40.dp)
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        ) {
            Text(
                text = "BTC: $bitcoinBalance",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(horizontal = 32.dp)
                    .align(Alignment.CenterVertically)
            )
        }
    }
}