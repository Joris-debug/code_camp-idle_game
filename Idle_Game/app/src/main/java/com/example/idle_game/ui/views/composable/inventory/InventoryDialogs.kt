// UpgradesInputDialog.kt
package com.example.idle_game.ui.views.composable.inventory

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.idle_game.data.database.models.InventoryData
import com.example.idle_game.data.database.models.ShopData
import com.example.idle_game.ui.views.models.InventoryViewModel
import com.example.idle_game.ui.views.states.InventoryViewState

//For showing InputDialog for custom Amount of Upgrades
@Composable
fun ShowUpgradesInputDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
    useOn: String,
    viewState: InventoryViewState,
    itemToBuy: ShopData?
) {
    if (showDialog) {
        var inputText by remember { mutableStateOf("1") }

        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(text = "$useOn upgraden")
            },
            text = {
                Column {
                    Text(text = "Menge der Upgrades, die auf $useOn angewendet werden sollen:")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = inputText,
                        label = { Text("Menge") },
                        onValueChange = { newValue ->
                            var maxQuantityProducer = 0
                            var maxQuantityUpgrades = 0
                            when (itemToBuy!!.name) {
                                "upgrade lvl 2" -> {
                                    maxQuantityUpgrades = viewState.inventoryData?.upgradeLvl2 ?: 0
                                    when (useOn) {
                                        "Hacker" -> maxQuantityProducer =
                                            viewState.inventoryData?.hackersLvl1 ?: 0

                                        "Miner" -> maxQuantityProducer =
                                            viewState.inventoryData?.cryptoMinersLvl1 ?: 0

                                        "BotNet" -> maxQuantityProducer =
                                            viewState.inventoryData?.botnetsLvl1 ?: 0
                                    }
                                }

                                "upgrade lvl 3" -> {
                                    maxQuantityUpgrades = viewState.inventoryData?.upgradeLvl3 ?: 0
                                    when (useOn) {
                                        "Hacker" -> maxQuantityProducer =
                                            viewState.inventoryData?.hackersLvl2 ?: 0

                                        "Miner" -> maxQuantityProducer =
                                            viewState.inventoryData?.cryptoMinersLvl2 ?: 0

                                        "BotNet" -> maxQuantityProducer =
                                            viewState.inventoryData?.botnetsLvl2 ?: 0
                                    }
                                }

                                "upgrade lvl 4" -> {
                                    maxQuantityUpgrades = viewState.inventoryData?.upgradeLvl4 ?: 0
                                    when (useOn) {
                                        "Hacker" -> maxQuantityProducer =
                                            viewState.inventoryData?.hackersLvl3 ?: 0

                                        "Miner" -> maxQuantityProducer =
                                            viewState.inventoryData?.cryptoMinersLvl3 ?: 0

                                        "BotNet" -> maxQuantityProducer =
                                            viewState.inventoryData?.botnetsLvl3 ?: 0
                                    }
                                }

                                "upgrade lvl 5" -> {
                                    maxQuantityUpgrades = viewState.inventoryData?.upgradeLvl5 ?: 0
                                    when (useOn) {
                                        "Hacker" -> maxQuantityProducer =
                                            viewState.inventoryData?.hackersLvl4 ?: 0

                                        "Miner" -> maxQuantityProducer =
                                            viewState.inventoryData?.cryptoMinersLvl4 ?: 0

                                        "BotNet" -> maxQuantityProducer =
                                            viewState.inventoryData?.botnetsLvl4 ?: 0
                                    }
                                }
                            }
                            val maxUpgrades: Int = if (maxQuantityUpgrades <= maxQuantityProducer) {
                                maxQuantityUpgrades
                            } else {
                                maxQuantityProducer
                            }

                            if (newValue.all { it.isDigit() } && (newValue.toIntOrNull()
                                    ?: 0) <= maxUpgrades) {
                                inputText = newValue
                            }
                        },
                        placeholder = { Text("Zahl eingeben") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    onConfirm(inputText)
                    onDismiss()
                }) {
                    Text("Anwenden")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Abbrechen")
                }
            }
        )
    }
}

//Dialog that pops up when you want to buy or use an item
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
    inventoryData: InventoryData,
    setQuantity: (String) -> Unit,
    viewState: InventoryViewState

) {
    if (showDialog && itemToBuy != null) {
        when (dialogTitle) {
            "Anwenden" -> {
                if (itemToBuy.name.startsWith("upgrade lvl")) {
                    ApplyOnDialog(
                        title = dialogTitle,
                        onHackerClick = {
                            onUseItem("Hacker")
                            onDismiss()
                        },
                        onBotNetClick = {
                            onUseItem("BotNet")
                            onDismiss()
                        },
                        onMinerClick = {
                            onUseItem("Miner")
                            onDismiss()
                        },
                        onDismiss = onDismiss,
                        itemToBuy = itemToBuy,
                        viewState = viewState
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
                val amount: Long = quantity.toLongOrNull() ?: 1
                val cost: Long = itemToBuy.cost * amount
                if (cost <= inventoryData.bitcoins) {
                    QuantityDialog(
                        message = dialogMessage,
                        title = dialogTitle,
                        quantity = quantity,
                        onQuantityChange = onQuantityChange,
                        onConfirm = onBuyItem,
                        onDismiss = {
                            onDismiss()
                        },
                        itemToBuy = itemToBuy,
                        inventoryData = inventoryData,
                        setQuantity = setQuantity,
                    )
                } else {
                    InsufficientFundsDialog(
                        onDismiss = onDismiss,
                        setQuantity = setQuantity
                    )
                }
            }
        }
    }
}

//Dialog that appears if user has not enough BTC
@Composable
fun InsufficientFundsDialog(
    onDismiss: () -> Unit,
    setQuantity: (String) -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Nicht genug BTC") },
        text = {
            Text("Du hast nicht genug BTC, um dieses Item zu kaufen. Bitte versuche es später nochmal.")
        },
        confirmButton = {
            TextButton(onClick = {
                setQuantity("1")
                onDismiss()
            }) {
                Text("OK")
            }
        }
    )
}

//Shows Dialog when user want to use an upgrade
@Composable
fun ApplyOnDialog(
    title: String,
    onHackerClick: () -> Unit,
    onBotNetClick: () -> Unit,
    onMinerClick: () -> Unit,
    onDismiss: () -> Unit,
    itemToBuy: ShopData?,
    viewState: InventoryViewState
) {
    var enoughHacker = false
    var enoughMiner = false
    var enoughBotNets = false
    var availableHackers = 0
    var availableMiner = 0
    var availableBotNets = 0

    when (itemToBuy?.name) {
        "upgrade lvl 2" -> {
            availableHackers = viewState.inventoryData?.hackersLvl1 ?: 0
            availableMiner = viewState.inventoryData?.cryptoMinersLvl1 ?: 0
            availableBotNets = viewState.inventoryData?.botnetsLvl1 ?: 0

            enoughHacker = availableHackers > 0
            enoughMiner = availableMiner > 0
            enoughBotNets = availableBotNets > 0
        }

        "upgrade lvl 3" -> {
            availableHackers = viewState.inventoryData?.hackersLvl2 ?: 0
            availableMiner = viewState.inventoryData?.cryptoMinersLvl2 ?: 0
            availableBotNets = viewState.inventoryData?.botnetsLvl2 ?: 0

            enoughHacker = availableHackers > 0
            enoughMiner = availableMiner > 0
            enoughBotNets = availableBotNets > 0
        }

        "upgrade lvl 4" -> {
            availableHackers = viewState.inventoryData?.hackersLvl3 ?: 0
            availableMiner = viewState.inventoryData?.cryptoMinersLvl3 ?: 0
            availableBotNets = viewState.inventoryData?.botnetsLvl3 ?: 0

            enoughHacker = availableHackers > 0
            enoughMiner = availableMiner > 0
            enoughBotNets = availableBotNets > 0
        }

        "upgrade lvl 5" -> {
            availableHackers = viewState.inventoryData?.hackersLvl4 ?: 0
            availableMiner = viewState.inventoryData?.cryptoMinersLvl4 ?: 0
            availableBotNets = viewState.inventoryData?.botnetsLvl4 ?: 0

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
                Button(
                    onClick = onHackerClick,
                    modifier = Modifier.padding(vertical = 4.dp),
                    enabled = enoughHacker
                ) {
                    Text(text = "Hacker, Verfügbar: $availableHackers")
                }
                Button(
                    onClick = onMinerClick,
                    modifier = Modifier.padding(vertical = 4.dp),
                    enabled = enoughMiner
                ) {
                    Text(text = "Miner, Verfügbar: $availableMiner")
                }
                Button(
                    onClick = onBotNetClick,
                    modifier = Modifier.padding(vertical = 4.dp),
                    enabled = enoughBotNets
                ) {
                    Text(text = "BotNet, Verfügbar: $availableBotNets")
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Abbrechen")
            }
        }
    )
}

//Dialog for input text field
@Composable
fun QuantityDialog(
    message: String,
    title: String,
    quantity: String,
    onQuantityChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    itemToBuy: ShopData?,
    inventoryData: InventoryData,
    setQuantity: (String) -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title) },
        text = {
            Column {
                Text(text = message)
                OutlinedTextField(
                    value = quantity,
                    onValueChange = { newValue ->
                        if (newValue.all { it.isDigit() }) {
                            onQuantityChange(newValue)
                        }
                    },
                    label = { Text("Menge") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Kaufen")
            }

            val amount = quantity.toIntOrNull() ?: 1
            val cost = itemToBuy!!.cost * amount
            if (cost > inventoryData.bitcoins) {
                InsufficientFundsDialog(
                    onDismiss = onDismiss,
                    setQuantity = setQuantity
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Abbrechen")
            }
        }
    )
}

//Dialog when user want to use an item that is not an upgrade
@Composable
fun ApplyDialog(
    message: String,
    title: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    viewModel: InventoryViewModel
) {
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
            if (!isBoostActive) {
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
            if (!isBoostActive) {
                TextButton(onClick = onDismiss) {
                    Text("No")
                }
            }
        }
    )
}