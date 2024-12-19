package com.example.idle_game.ui.views.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import com.example.idle_game.data.database.models.InventoryData
import com.example.idle_game.data.database.models.ShopData
import com.example.idle_game.ui.theme.AppColors
import com.example.idle_game.ui.views.models.InventoryViewModel
import com.example.idle_game.ui.theme.bitcoinBackground
import kotlinx.coroutines.launch

@Composable
fun InventoryView(viewModel: InventoryViewModel = hiltViewModel()) {
    val viewState = viewModel.uiStateFlow.collectAsState().value
    val shopDataList = viewState.shopData.collectAsState(initial = emptyList()).value
    val inventoryData = viewState.inventoryData.collectAsState(initial = InventoryData()).value
    val selectedItem = viewState.selectedItem

    // State for showing the confirmation dialog
    val (showDialog, setShowDialog) = remember { mutableStateOf(false) }
    val (itemToBuy, setItemToBuy) = remember { mutableStateOf<ShopData?>(null) }
    val (dialogMessage, setDialogMessage) = remember { mutableStateOf("") }
    val (dialogTitle, setDialogTitle) = remember { mutableStateOf("") }

    val (quantity, setQuantity) = remember { mutableStateOf("1") }

    val (specialItems, otherItems) = shopDataList.partition {
        it.name.contains("low passive", ignoreCase = true) ||
                it.name.contains("medium passive", ignoreCase = true) ||
                it.name.contains("high passive", ignoreCase = true)
    }

    val sortedShopDataList = otherItems + specialItems

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bitcoinBackground)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Items:", modifier = Modifier.padding(16.dp))

        Row(
            modifier = Modifier.padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Box für den "Kaufen"-Text, der auf der linken Seite zentriert ist (ca. 3/5)
            Box(
                modifier = Modifier
                    .weight(3f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Kaufen",
                    fontSize = 10.sp,
                    color = Color.White
                )
            }

            // Box für den "Anwenden"-Text, der auf der rechten Seite zentriert ist (ca. 2/5)
            Box(
                modifier = Modifier
                    .weight(2f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Anwenden",
                    fontSize = 10.sp,
                    color = Color.White
                )
            }
        }

        sortedShopDataList.forEach { item ->
            val isSelected = item == selectedItem
            val itemAmount = viewModel.gameRepository.getAmountOfItems(item = item, inventoryData = inventoryData)
            ShopItemButtons(
                item = item,
                isSelected = isSelected,
                itemAmount = itemAmount,
                onBuyClick = {
                    setItemToBuy(item)
                    setShowDialog(true)
                    setDialogTitle("Kaufen")
                    setDialogMessage("Willst du wirklich ${item.name} für ${item.cost} kaufen?")
                },
                onApplyClick = {
                    setItemToBuy(item)
                    setShowDialog(true)
                    setDialogTitle("Anwenden")
                    setDialogMessage("Willst du wirklich ${item.name} anwenden?")
                }
            )
        }
    }

    ShowDialog(
        showDialog = showDialog,
        dialogTitle = dialogTitle,
        dialogMessage = dialogMessage,
        itemToBuy = itemToBuy,
        quantity = quantity,
        onQuantityChange = setQuantity,
        onUseItem = { viewModel.viewModelScope.launch {
            viewModel.gameRepository.useItem(itemToBuy!!, it)
            setShowDialog(false)
        } },
        onBuyItem = { viewModel.viewModelScope.launch {
            val amount = quantity.toIntOrNull() ?: 1
            viewModel.gameRepository.buyItem(itemToBuy!!, amount)
            setShowDialog(false)
        } },
        onDismiss = { setShowDialog(false)}
    )
}

@Composable
fun ShowDialog(
    showDialog: Boolean,
    dialogTitle: String,
    dialogMessage: String,
    itemToBuy: ShopData?,
    quantity: String,
    onQuantityChange: (String) -> Unit,
    onUseItem: (String) -> Unit,
    onBuyItem: () -> Unit,
    onDismiss: () -> Unit
) {
    if (showDialog && itemToBuy != null) {
        when (dialogTitle) {
            "Anwenden" -> {
                if (itemToBuy.name.startsWith("upgrade lvl")) {
                    ApplyOnDialog(
                        title = dialogTitle,
                        onHackerClick = { onUseItem("Hacker") },
                        onBotNetClick = { onUseItem("BotNet") },
                        onMinerClick = { onUseItem("Miner") },
                        onDismiss = onDismiss
                    )
                } else {
                    ApplyDialog(
                        message = dialogMessage,
                        title = dialogTitle,
                        onConfirm = { onUseItem("") },
                        onDismiss = onDismiss
                    )
                }
            }
            "Kaufen" -> {
                QuantityDialog(
                    message = dialogMessage,
                    title = dialogTitle,
                    quantity = quantity,
                    onQuantityChange = onQuantityChange,
                    onConfirm = onBuyItem,
                    onDismiss = onDismiss
                )
            }
        }
    }
}


@Composable
fun ApplyOnDialog(
    title: String,
    onHackerClick: () -> Unit,
    onBotNetClick: () -> Unit,
    onMinerClick: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title) },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Button(onClick = onHackerClick, modifier = Modifier.padding(vertical = 4.dp)) {
                    Text(text = "Hacker")
                }
                Button(onClick = onBotNetClick, modifier = Modifier.padding(vertical = 4.dp)) {
                    Text(text = "BotNet")
                }
                Button(onClick = onMinerClick, modifier = Modifier.padding(vertical = 4.dp)) {
                    Text(text = "Miner")
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}


@Composable
fun ShopItemButtons(item: ShopData,
                    isSelected: Boolean,
                    itemAmount: Int, onBuyClick: () -> Unit,
                    onApplyClick: () -> Unit) {

    val buyBackgroundColor = if (isSelected) AppColors.primary else AppColors.secondary
    val applyBackgroundColor = if (isSelected) AppColors.primary else AppColors.secondary

    // Überprüfen, ob der Item-Name zu den "special items" gehört
    val isSpecialItem = item.name.contains("low passive", ignoreCase = true) ||
            item.name.contains("medium passive", ignoreCase = true) ||
            item.name.contains("high passive", ignoreCase = true)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // Button für Kaufen
        Button(
            onClick = onBuyClick,
            colors = ButtonDefaults.buttonColors(containerColor = buyBackgroundColor),
            modifier = Modifier
                .weight(2f)
        ) {
            if(!isSpecialItem) {
                Text(
                    text = "${item.name}, ${item.cost}BTC",
                    fontSize = 10.sp
                )
            } else {
                Text(
                    text = "${item.name}, ${item.cost}BTC, ${itemAmount}Stk. in Einsatz",
                    fontSize = 14.sp
                )
            }
        }

        // Button für Anwenden
        if (!isSpecialItem) {
            Button(
                onClick = onApplyClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (itemAmount > 0) applyBackgroundColor else Color.Gray
                ),
                modifier = Modifier
                    .weight(1f),
                enabled = itemAmount > 0
            ) {
                Text(
                    text = "$itemAmount verfügbar",
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
fun QuantityDialog(
    message: String,
    title: String,
    quantity: String,
    onQuantityChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title) },
        text = {
            Column {
                Text(text = message)
                OutlinedTextField(
                    value = quantity,
                    onValueChange = onQuantityChange,
                    label = { Text("Menge") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Bestätigen")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Abbrechen")
            }
        }
    )
}

@Composable
fun ApplyDialog(message: String, title: String, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = title)
        },
        text = {
            Text(text = message)
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

//TODO: Randfälle abdecken (Kaufen)
//TODO: Geldmenge überprüfen & Geldmenge aktualisieren
//TODO: Falls nicht genug Geld, einfach ausblenden
//TODO: Beliebige Menge kaufen.




