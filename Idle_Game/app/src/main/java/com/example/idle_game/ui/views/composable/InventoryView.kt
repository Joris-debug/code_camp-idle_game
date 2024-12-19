package com.example.idle_game.ui.views.composable

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
    val (dialogMessage, setDialogMessage) = remember { mutableStateOf("") }
    val (dialogTitle, setDialogTitle) = remember { mutableStateOf("") }

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
            val itemAmount = getAmountOfItems(item = item, inventoryData = inventoryData)
            ShopItemButton(
                item = item,
                isSelected = isSelected,
                itemAmount = itemAmount,
                onClick = {
                    setItemToBuy(item)
                    setShowDialog(true)
                    setDialogTitle(if (itemAmount > 0 && (item.name != "low passive" &&
                            item.name != "medium passive" && item.name != "high passive")) "Anwenden" else "Kaufen")
                    setDialogMessage(if (itemAmount > 0 && (item.name == "low Boost" ||
                                item.name == "medium Boost" || item.name == "high Boost")
                        ) "Wende das gekaufte Item an." else "Willst du wirklich ${item.name} für ${item.cost} kaufen?")
                }
            )
        }
    }

    // Show confirmation dialog
    if (showDialog && itemToBuy != null) {
        if (dialogTitle == "Anwenden" && (itemToBuy.name == "upgrade lvl 2" || itemToBuy.name == "upgrade lvl 3"
                    || itemToBuy.name == "upgrade lvl 4" || itemToBuy.name == "upgrade lvl 5")) {
            ApplyOnDialog(
                title = dialogTitle,
                onHackerClick = {
                    viewModel.useItem(itemToBuy, "Hacker")
                    setShowDialog(false)
                },
                onBotNetClick = {
                    viewModel.useItem(itemToBuy, "BotNet")
                    setShowDialog(false)
                },
                onMinerClick = {
                    viewModel.useItem(itemToBuy, "Miner")
                    setShowDialog(false)
                },
                onDismiss = { setShowDialog(false) }
            )
        } else if (dialogTitle == "Anwenden" && (itemToBuy.name == "low Boost" ||
                itemToBuy.name == "medium Boost" || itemToBuy.name == "high Boost")) {
            ApplyDialog(
                message = dialogMessage,
                title = dialogTitle,
                onConfirm = {
                    viewModel.useItem(itemToBuy, "")
                    setShowDialog(false)
                },
                onDismiss = { setShowDialog(false) }
            )
        } else {
            ConfirmationDialog(
                message = dialogMessage,
                title = dialogTitle,
                onConfirm = {
                    viewModel.buyItem(itemToBuy)
                    setShowDialog(false)
                },
                onDismiss = { setShowDialog(false) }
            )
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
fun ShopItemButton(item: ShopData, isSelected: Boolean, itemAmount: Int, onClick: () -> Unit) {
    val backgroundColor = if (isSelected) AppColors.primary else AppColors.secondary

    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(text = "Item: ${item.name}, Price: ${item.cost}, Amount: $itemAmount")
    }
}

@Composable
fun ConfirmationDialog(message: String, title: String, onConfirm: () -> Unit, onDismiss: () -> Unit) {
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

fun getAmountOfItems(item: ShopData, inventoryData: InventoryData): Int {
    return when (item.name) {
        "low Boost" -> inventoryData.lowBoosts
        "medium Boost" -> inventoryData.mediumBoosts
        "high Boost" -> inventoryData.highBoosts
        "low passive" -> inventoryData.hackersLvl1 + inventoryData.hackersLvl2 + inventoryData.hackersLvl3 + inventoryData.hackersLvl4 + inventoryData.hackersLvl5
        "medium passive" -> inventoryData.cryptoMinersLvl1 + inventoryData.cryptoMinersLvl2 + inventoryData.cryptoMinersLvl3 + inventoryData.cryptoMinersLvl4 + inventoryData.cryptoMinersLvl5
        "high passive" -> inventoryData.botnetsLvl1 + inventoryData.botnetsLvl2 + inventoryData.botnetsLvl3 + inventoryData.botnetsLvl4 + inventoryData.botnetsLvl5
        "upgrade lvl 2" -> inventoryData.upgradeLvl2
        "upgrade lvl 3" -> inventoryData.upgradeLvl3
        "upgrade lvl 4" -> inventoryData.upgradeLvl4
        "upgrade lvl 5" -> inventoryData.upgradeLvl5
        else -> 0
    }
}

//TODO: Randfälle abdecken (Kaufen)
//TODO: Geldmenge überprüfen & Geldmenge aktualisieren

//TODO: Randfälle abdecken (Anwendung)
//TODO: Boosts wirken sich nur auf aktive Erzeuger aus und es kann nur 1 aktiv sein (Direkte Anwendung auf den Main BTC Button) und jeweiligen Boost 1 runterzählen
//TODO: passive(Erzeuger) können gekauft werden und werden automatisch direkt angewendet -> LVL 1 & jeweiligen Erzeuger um 1 hochzählen
//TODO: Upgrades können für jeden passiven Erzeuger gekauft werden -> Für Anwendung wir düberprüft ob ein erzeuger mit ein lvl weniger exisitiert oder nicht. Jede Art mit lvl -= 1 wird angezeigt. User kann auswählen. Datenbank muss aktualisiert werden.

//TODO: Randfälle abdecken (Nicht genug Geld)
//TODO: Einfach Meldung, dass nichts geht



