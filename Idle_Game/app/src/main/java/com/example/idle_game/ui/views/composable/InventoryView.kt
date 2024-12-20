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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.idle_game.ui.views.models.InventoryViewModel
import kotlinx.coroutines.launch

@Composable
fun InventoryView(viewModel: InventoryViewModel = hiltViewModel()) {
    val viewState = viewModel.uiStateFlow.collectAsState().value
    val shopDataList = viewState.shopData.collectAsState(initial = emptyList()).value
    val inventoryData = viewState.inventoryData.collectAsState(initial = InventoryData()).value
    val selectedItem = viewState.selectedItem

    //State for showing the confirmation dialog
    val (showDialog, setShowDialog) = remember { mutableStateOf(false) }
    val (itemToBuy, setItemToBuy) = remember { mutableStateOf<ShopData?>(null) }
    val (dialogMessage, setDialogMessage) = remember { mutableStateOf("") }
    val (dialogTitle, setDialogTitle) = remember { mutableStateOf("") }

    //State for quantity input
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
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Items:", modifier = Modifier.padding(16.dp))

        Row(
            modifier = Modifier.padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
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

        //Creating buttons for buying and using items
        sortedShopDataList.forEach { item ->
            val isSelected = item == selectedItem
            val itemAmount = viewModel.getAmountOfItems(item, inventoryData)
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

    //Dialog that pops up when you want to buy or use an item
    ShowDialog(
        showDialog = showDialog,
        dialogTitle = dialogTitle,
        dialogMessage = dialogMessage,
        itemToBuy = itemToBuy,
        quantity = quantity,
        onQuantityChange = setQuantity,
        onUseItem = {
            viewModel.useItem(itemToBuy!!, it)
            setShowDialog(false)
        },
        //TODO: Kaufen nur möglich wenn genügend BTC vorhanden ist.
        //TODO: Wenn nicht genug BTC vorhanden ist, soll der Button ausgegraut werden und nicht klickbar sein.
        //TODO: Nach erfolgreichem Kauf BTC Stand aktualisieren.
        onBuyItem = {
            val amount = quantity.toIntOrNull() ?: 1
            viewModel.buyItem(itemToBuy!!, amount)
            setShowDialog(false)
        },
        onDismiss = { setShowDialog(false)},
        viewModel = viewModel,
        inventoryData = inventoryData

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
    onDismiss: () -> Unit,
    viewModel: InventoryViewModel,
    inventoryData: InventoryData
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
                        onDismiss = onDismiss,
                        inventoryData = inventoryData,
                        itemToBuy = itemToBuy
                    )
                } else {
                    ApplyDialog(
                        message = dialogMessage,
                        title = dialogTitle,
                        onConfirm = { onUseItem("") },
                        onDismiss = onDismiss,
                        viewModel = viewModel,
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

//Dialog when u want to use an upgrade
@Composable
fun ApplyOnDialog(
    title: String,
    onHackerClick: () -> Unit,
    onBotNetClick: () -> Unit,
    onMinerClick: () -> Unit,
    onDismiss: () -> Unit,
    inventoryData: InventoryData,
    itemToBuy: ShopData?
) {
    var enoughHacker = false
    var enoughMiner = false
    var enoughBotNets = false
    var availableHackers = 0
    var availableMiner = 0
    var availableBotNets = 0


    when(itemToBuy?.name) {
        "upgrade lvl 2" -> {
            availableHackers = inventoryData.hackersLvl1
            availableMiner = inventoryData.cryptoMinersLvl1
            availableBotNets = inventoryData.botnetsLvl1

            enoughHacker = availableHackers > 0
            enoughMiner = availableMiner > 0
            enoughBotNets = availableBotNets > 0
        }
        "upgrade lvl 3" -> {
            availableHackers = inventoryData.hackersLvl2
            availableMiner = inventoryData.cryptoMinersLvl2
            availableBotNets = inventoryData.botnetsLvl2

            enoughHacker = availableHackers > 0
            enoughMiner = availableMiner > 0
            enoughBotNets = availableBotNets > 0
        }
        "upgrade lvl 4" -> {
            availableHackers = inventoryData.hackersLvl3
            availableMiner = inventoryData.cryptoMinersLvl3
            availableBotNets = inventoryData.botnetsLvl3

            enoughHacker = availableHackers > 0
            enoughMiner = availableMiner > 0
            enoughBotNets = availableBotNets > 0
        }
        "upgrade lvl 5" -> {
            availableHackers = inventoryData.hackersLvl4
            availableMiner = inventoryData.cryptoMinersLvl4
            availableBotNets = inventoryData.botnetsLvl4

            enoughHacker = availableHackers > 0
            enoughMiner = availableMiner > 0
            enoughBotNets = availableBotNets > 0
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title) },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Button(onClick = onHackerClick, modifier = Modifier.padding(vertical = 4.dp), enabled = enoughHacker) {
                    Text(text = "Hacker, Verfügbar: $availableHackers")
                }
                Button(onClick = onMinerClick, modifier = Modifier.padding(vertical = 4.dp), enabled = enoughMiner) {
                    Text(text = "Miner, Verfügbar: $availableMiner")
                }
                Button(onClick = onBotNetClick, modifier = Modifier.padding(vertical = 4.dp), enabled = enoughBotNets) {
                    Text(text = "BotNet, Verfügbar: $availableBotNets")
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

    val buttonBackground = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary

    val isSpecialItem = item.name.contains("low passive", ignoreCase = true) ||
            item.name.contains("medium passive", ignoreCase = true) ||
            item.name.contains("high passive", ignoreCase = true)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Button(
            onClick = onBuyClick,
            colors = ButtonDefaults.buttonColors(containerColor = buttonBackground),
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

        if (!isSpecialItem) {
            Button(
                onClick = onApplyClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (itemAmount > 0) buttonBackground else Color.Gray
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

//Dialog for input text field
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

//Dialog when you want to use an item that is not an upgrade
@Composable
fun ApplyDialog(message: String, title: String, onConfirm: () -> Unit, onDismiss: () -> Unit, viewModel: InventoryViewModel) {

    var isBoostActive by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isBoostActive = viewModel.gameRepository.isBoostActive()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = if (!isBoostActive) title else "Achtung")
        },
        text = {
            Text(text = if (!isBoostActive) message else "Warte bis dein anderer Boost abgelaufen ist.")
        },
        confirmButton = {
            if(!isBoostActive) {
                TextButton(onClick = onConfirm) {
                    Text("Yes")
                }
            } else {
                TextButton(onClick = onConfirm) {
                    Text("Back")
                }
            }
        },
        dismissButton = {
            if(!isBoostActive) {
                TextButton(onClick = onDismiss) {
                    Text("No")
                }
            }
        }
    )
}





