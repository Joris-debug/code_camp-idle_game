package com.example.idle_game.ui.views.composable

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.idle_game.data.database.models.InventoryData
import com.example.idle_game.data.database.models.ShopData
import com.example.idle_game.ui.theme.AppColors
import com.example.idle_game.ui.views.models.InventoryViewModel
import com.example.idle_game.ui.theme.bitcoinBackground

@Composable
fun InventoryView(viewModel: InventoryViewModel = hiltViewModel()) {
    val viewState = viewModel.uiStateFlow.collectAsState().value
    val shopDataList = viewState.shopData.collectAsState(initial = emptyList()).value
    val inventoryData = viewState.inventoryData.collectAsState(initial = InventoryData()).value
    val selectedItem = viewState.selectedItem

    // State for showing the confirmation dialog
    val (showDialog, setShowDialog) = remember { mutableStateOf(false) }
    val (itemToBuy, setItemToBuy) = remember { mutableStateOf<ShopData?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bitcoinBackground)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Shop Items:", modifier = Modifier.padding(16.dp))

        shopDataList.forEach { item ->
            val isSelected = item == selectedItem
            ShopItemButton(
                item = item,
                isSelected = isSelected,
                inventoryData = inventoryData,
                onClick = {
                    setItemToBuy(item)
                    setShowDialog(true)
                }
            )
        }
    }

    // Show confirmation dialog
    if (showDialog && itemToBuy != null) {
        ConfirmationDialog(
            item = itemToBuy,
            onConfirm = {
                viewModel.buyItem(itemToBuy)
                setShowDialog(false)
            },
            onDismiss = {
                setShowDialog(false)
            }
        )
    }
}

@Composable
fun ShopItemButton(item: ShopData, isSelected: Boolean, inventoryData: InventoryData, onClick: () -> Unit) {
    val backgroundColor = if (isSelected) AppColors.primary else AppColors.secondary

    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        var itemAmount = 0
        when (item.name) {
            "low Boost" -> {
                itemAmount = inventoryData.lowBoosts
            }
            "medium Boost" -> {
                itemAmount = inventoryData.mediumBoosts
            }
            "high Boost" -> {
                itemAmount = inventoryData.highBoosts
            }

            "low passive" -> {
                itemAmount = inventoryData.hackersLvl1
            }
            "medium passive" -> {
                itemAmount = inventoryData.cryptoMinersLvl1
            }
            "high passive" -> {
                itemAmount = inventoryData.botnetsLvl1
            }
            "upgrade lvl 2" -> {
                itemAmount = inventoryData.upgradeLvl2
            }
            "upgrade lvl 3" -> {
                itemAmount = inventoryData.upgradeLvl3
            }
            "upgrade lvl 4" -> {
                itemAmount = inventoryData.upgradeLvl4
            }
            "upgrade lvl 5" -> {
                itemAmount = inventoryData.upgradeLvl5
            }
        }
        Text(text = "Item: ${item.name}, Price: ${item.cost}, Amount: $itemAmount")
    }
}

@Composable
fun ConfirmationDialog(item: ShopData, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Confirm Purchase")
        },
        text = {
            Text(text = "Do you really want to buy the ${item.name} for ${item.cost}?")
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Yes")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("No")
            }
        }
    )
}
