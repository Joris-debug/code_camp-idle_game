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
                }
            )
        }
    }

    // Show confirmation dialog
    if (showDialog && itemToBuy != null) {
        val itemAmount = getAmountOfItems(item = itemToBuy, inventoryData = inventoryData)
        var title = ""

        //TODO: Wenn itemAmount == 0 & genug Geld
        if (itemAmount == 0){
            title = "Kaufen"
            setDialogMessage("Willst du wirklich ${itemToBuy.name} für ${itemToBuy.cost} kaufen?")

            //TODO: Sonst, wenn itemAmount ist > 0
        } else if (itemAmount > 0){
            title = "Anwenden"
            setDialogMessage("Wende erst die gekauften Items an.")
            //TODO: Sonst, wenn itemAmount ist == 0 und kein Geld
        } else {
            title = "Nicht genug Geld"
            setDialogMessage("")
        }

        ConfirmationDialog(
            message = dialogMessage,
            title = title,
            onConfirm = {
                when(title){
                    "Kaufen" -> {
                        viewModel.buyItem(itemToBuy)
                        setShowDialog(false)
                    }
                    "Anwenden" -> {
                        viewModel.useItem(itemToBuy)
                        setShowDialog(false)
                    }
                    "Nicht genug Geld" -> setShowDialog(false)
                }
            },
            onDismiss = {
                setShowDialog(false)
            }
        )
    }
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


fun getAmountOfItems(item: ShopData, inventoryData: InventoryData): Int{
    when (item.name) {
        "low Boost" -> {
            return inventoryData.lowBoosts
        }
        "medium Boost" -> {
            return inventoryData.mediumBoosts
        }
        "high Boost" -> {
            return inventoryData.highBoosts
        }
        "low passive" -> {
            return inventoryData.hackersLvl1
        }
        "medium passive" -> {
            return inventoryData.cryptoMinersLvl1
        }
        "high passive" -> {
            return inventoryData.botnetsLvl1
        }
        "upgrade lvl 2" -> {
            return inventoryData.upgradeLvl2
        }
        "upgrade lvl 3" -> {
            return inventoryData.upgradeLvl3
        }
        "upgrade lvl 4" -> {
            return inventoryData.upgradeLvl4
        }
        "upgrade lvl 5" -> {
            return inventoryData.upgradeLvl5
        }
    }
    return 0
}

//TODO: Randfälle abdecken (Kaufen)
//TODO: Geldmenge überprüfen & Geldmenge aktualisieren

//TODO: Randfälle abdecken (Anwendung)
//TODO: Boosts wirken sich nur auf aktive Erzeuger aus und es kann nur 1 aktiv sein (Direkte Anwendung auf den Main BTC Button) und jeweiligen Boost 1 runterzählen
//TODO: passive(Erzeuger) können gekauft werden und werden automatisch direkt angewendet -> LVL 1 & jeweiligen Erzeuger um 1 hochzählen
//TODO: Upgrades können für jeden passiven Erzeuger gekauft werden -> Für Anwendung wir düberprüft ob ein erzeuger mit ein lvl weniger exisitiert oder nicht. Jede Art mit lvl -= 1 wird angezeigt. User kann auswählen. Datenbank muss aktualisiert werden.

//TODO: Randfälle abdecken (Nicht genug Geld)
//TODO: Einfach Meldung, dass nichts geht



