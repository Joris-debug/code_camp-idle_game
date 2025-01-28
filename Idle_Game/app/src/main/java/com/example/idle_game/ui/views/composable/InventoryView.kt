package com.example.idle_game.ui.views.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.idle_game.data.database.models.InventoryData
import com.example.idle_game.data.database.models.ShopData
import com.example.idle_game.ui.views.models.InventoryViewModel
import com.example.idle_game.ui.views.states.InventoryViewState
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.*

@OptIn(ExperimentalPagerApi::class)
@Composable
fun InventoryView(viewModel: InventoryViewModel = hiltViewModel()) {
    val viewState = viewModel.viewState.collectAsState().value
    val shopDataList = viewState.shopData.collectAsState(initial = emptyList()).value
    val inventoryData = viewState.inventoryData.collectAsState(initial = InventoryData()).value
    val selectedItem = viewState.selectedItem

    //State for showing the confirmation dialog
    val (showDialog, setShowDialog) = remember { mutableStateOf(false) }
    val (itemToBuy, setItemToBuy) = remember { mutableStateOf<ShopData?>(null) }
    val (dialogMessage, setDialogMessage) = remember { mutableStateOf("") }
    val (dialogTitle, setDialogTitle) = remember { mutableStateOf("") }
    val (showInputDialog, setShowInputDialog) = remember { mutableStateOf(false) }
    val (useOn, setUseOn) = remember { mutableStateOf("") }

    //State for quantity input
    val (quantity, setQuantity) = remember { mutableStateOf("1") }

    // Partition the shop data into categories (e.g., Upgrade, Boost, Passive)
    val upgradeItems = shopDataList.filter { it.name.contains("upgrade", ignoreCase = true) }
    val boostItems = shopDataList.filter { it.name.contains("boost", ignoreCase = true) }
    val passiveItems = shopDataList.filter { it.name.contains("passive", ignoreCase = true) }


    // Set up the HorizontalPager for swiping between screens
    HorizontalPager(
        count = 3, // 3 screens
        modifier = Modifier.fillMaxSize()
    ) { page ->
        when (page) {
            0 -> {
                // Passive Items Screen
                CategoryScreen(
                    items = passiveItems,
                    title = "Erzeuger (Werden automatisch angewendet)",
                    selectedItem = selectedItem,
                    onBuyClick = { item ->
                        setItemToBuy(item)
                        setShowDialog(true)
                        setDialogTitle("Kaufen")
                        setDialogMessage("Willst du wirklich ${item.name} für ${item.cost} kaufen?")
                    },
                    onApplyClick = { item ->
                        setItemToBuy(item)
                        setShowDialog(true)
                        setDialogTitle("Anwenden")
                        setDialogMessage("Willst du wirklich ${item.name} anwenden?")
                    },
                    viewModel = viewModel,
                    inventoryData = inventoryData,
                    page = page,
                    viewState = viewState
                )
            }

            1 -> {
                // Upgrade Items Screen
                CategoryScreen(
                    items = upgradeItems,
                    title = "Upgrades",
                    selectedItem = selectedItem,
                    onBuyClick = { item ->
                        setItemToBuy(item)
                        setShowDialog(true)
                        setDialogTitle("Kaufen")
                        setDialogMessage("Willst du wirklich ${item.name} für ${item.cost} kaufen?")
                    },
                    onApplyClick = { item ->
                        setItemToBuy(item)
                        setShowDialog(true)
                        setDialogTitle("Anwenden")
                        setDialogMessage("Willst du wirklich ${item.name} anwenden?")
                    },
                    viewModel = viewModel,
                    inventoryData = inventoryData,
                    page = page,
                    viewState = viewState
                )
            }

            2 -> {
                // Boost Items Screen
                val title =
                    if (viewState.activeBoost > 0) "Boosts (${getBoostName(viewState.activeBoost)} aktiv)" else "Boosts (Inaktiv)"
                CategoryScreen(
                    items = boostItems,
                    title = title,
                    selectedItem = selectedItem,
                    onBuyClick = { item ->
                        setItemToBuy(item)
                        setShowDialog(true)
                        setDialogTitle("Kaufen")
                        setDialogMessage("Willst du wirklich ${item.name} für ${item.cost} kaufen?")
                    },
                    onApplyClick = { item ->
                        setItemToBuy(item)
                        setShowDialog(true)
                        setDialogTitle("Anwenden")
                        setDialogMessage("Willst du wirklich ${item.name} anwenden?")
                    },
                    viewModel = viewModel,
                    inventoryData = inventoryData,
                    page = page,
                    viewState = viewState
                )
            }
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
            if (it == "") {
                viewModel.useItem(itemToBuy!!, it, 0)
                setShowDialog(false)
            } else {
                setUseOn(it)
                setShowInputDialog(true)
            }
        },
        onBuyItem = {
            val amount = quantity.toIntOrNull() ?: 1
            val cost = itemToBuy!!.cost * amount

            if (cost <= inventoryData.bitcoins) {
                viewModel.buyItem(itemToBuy, amount)
                viewModel.updateBitcoinBalance(cost.toLong())
                setShowDialog(false)
            }
        },
        onDismiss = { setShowDialog(false) },
        viewModel = viewModel,
        inventoryData = inventoryData,
        setQuantity = setQuantity,
        viewState = viewState,
    )

    ShowUpgradesInputDialog(
        showDialog = showInputDialog,
        onDismiss = { setShowInputDialog(false) },
        onConfirm = { input ->
            val quantity = input.toIntOrNull()
            if (quantity != null && itemToBuy != null) {
                viewModel.useItem(itemToBuy, useOn, quantity)
            }
            setShowInputDialog(false)
        },
        useOn = useOn,
        viewState = viewState,
        itemToBuy = itemToBuy
    )
}

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
                                    maxQuantityUpgrades = viewState.amountUpgradeLvl2
                                    when (useOn) {
                                        "Hacker" -> maxQuantityProducer = viewState.amountHackerLvl1
                                        "Miner" -> maxQuantityProducer = viewState.amountMinerLvl1
                                        "BotNet" -> maxQuantityProducer = viewState.amountBotNetLvl1
                                    }
                                }

                                "upgrade lvl 3" -> {
                                    maxQuantityUpgrades = viewState.amountUpgradeLvl3
                                    when (useOn) {
                                        "Hacker" -> maxQuantityProducer = viewState.amountHackerLvl2
                                        "Miner" -> maxQuantityProducer = viewState.amountMinerLvl2
                                        "BotNet" -> maxQuantityProducer = viewState.amountBotNetLvl2
                                    }
                                }

                                "upgrade lvl 4" -> {
                                    maxQuantityUpgrades = viewState.amountUpgradeLvl4
                                    when (useOn) {
                                        "Hacker" -> maxQuantityProducer = viewState.amountHackerLvl3
                                        "Miner" -> maxQuantityProducer = viewState.amountMinerLvl3
                                        "BotNet" -> maxQuantityProducer = viewState.amountBotNetLvl3
                                    }
                                }

                                "upgrade lvl 5" -> {
                                    maxQuantityUpgrades = viewState.amountUpgradeLvl5
                                    when (useOn) {
                                        "Hacker" -> maxQuantityProducer = viewState.amountHackerLvl4
                                        "Miner" -> maxQuantityProducer = viewState.amountMinerLvl4
                                        "BotNet" -> maxQuantityProducer = viewState.amountBotNetLvl4
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

@Composable
fun CategoryScreen(
    items: List<ShopData>,
    title: String,
    selectedItem: ShopData?,
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
                val isSelected = item == selectedItem
                val itemAmount = itemAmounts[index].intValue

                ShopItemButtons(
                    item = item,
                    isSelected = isSelected,
                    onBuyClick = { onBuyClick(item) },
                    onApplyClick = { onApplyClick(item) },
                    itemAmount = itemAmount,
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

//Shows a dialog that about purchase or application event
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
                val amount = quantity.toIntOrNull() ?: 1
                val cost = itemToBuy.cost * amount

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
            availableHackers = viewState.amountHackerLvl1
            availableMiner = viewState.amountMinerLvl1
            availableBotNets = viewState.amountBotNetLvl1

            enoughHacker = availableHackers > 0
            enoughMiner = availableMiner > 0
            enoughBotNets = availableBotNets > 0
        }

        "upgrade lvl 3" -> {
            availableHackers = viewState.amountHackerLvl2
            availableMiner = viewState.amountMinerLvl2
            availableBotNets = viewState.amountBotNetLvl2

            enoughHacker = availableHackers > 0
            enoughMiner = availableMiner > 0
            enoughBotNets = availableBotNets > 0
        }

        "upgrade lvl 4" -> {
            availableHackers = viewState.amountHackerLvl3
            availableMiner = viewState.amountMinerLvl3
            availableBotNets = viewState.amountBotNetLvl3

            enoughHacker = availableHackers > 0
            enoughMiner = availableMiner > 0
            enoughBotNets = availableBotNets > 0
        }

        "upgrade lvl 5" -> {
            availableHackers = viewState.amountHackerLvl4
            availableMiner = viewState.amountMinerLvl4
            availableBotNets = viewState.amountBotNetLvl4

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

//Logic for visualising items
@Composable
fun ShopItemButtons(
    item: ShopData,
    isSelected: Boolean,
    itemAmount: Int,
    onBuyClick: () -> Unit,
    onApplyClick: () -> Unit,
    viewState: InventoryViewState
) {

    val buttonBackground =
        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary

    val isSpecialItem = item.name.contains("low passive", ignoreCase = true) ||
            item.name.contains("medium passive", ignoreCase = true) ||
            item.name.contains("high passive", ignoreCase = true)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Button(
            onClick = onBuyClick,
            colors = ButtonDefaults.buttonColors(containerColor = buttonBackground),
            shape = RoundedCornerShape(5.dp),
            modifier = Modifier
                .size(150.dp)
        ) {
            if (!isSpecialItem) {
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
        var quantityLvl1 = 0
        var quantityLvl2 = 0
        var quantityLvl3 = 0
        var quantityLvl4 = 0
        var quantityLvl5 = 0

        when(item.name){
            "low passive" -> {
                quantityLvl1 = viewState.amountHackerLvl1
                quantityLvl2 = viewState.amountHackerLvl2
                quantityLvl3 = viewState.amountHackerLvl3
                quantityLvl4 = viewState.amountHackerLvl4
                quantityLvl5 = viewState.amountHackerLvl5
            }
            "medium passive" -> {
                quantityLvl1 = viewState.amountMinerLvl1
                quantityLvl2 = viewState.amountMinerLvl2
                quantityLvl3 = viewState.amountMinerLvl3
                quantityLvl4 = viewState.amountMinerLvl4
                quantityLvl5 = viewState.amountMinerLvl5
            }
            "high passive" -> {
                quantityLvl1 = viewState.amountBotNetLvl1
                quantityLvl2 = viewState.amountBotNetLvl2
                quantityLvl3 = viewState.amountBotNetLvl3
                quantityLvl4 = viewState.amountBotNetLvl4
                quantityLvl5 = viewState.amountBotNetLvl5
            }
        }

        if (isSpecialItem) {
            Column(
                modifier = Modifier
                    .padding(start = 16.dp)
            ) {
                Text(text = "Lvl1 $quantityLvl1")
                Text(text = "Lvl2 $quantityLvl2")
                Text(text = "Lvl3 $quantityLvl3")
                Text(text = "Lvl4 $quantityLvl4")
                Text(text = "Lvl5 $quantityLvl5")
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        if (!isSpecialItem) {
            Button(
                onClick = onApplyClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (itemAmount > 0) buttonBackground else Color.Gray
                ),
                shape = RoundedCornerShape(5.dp),
                modifier = Modifier
                    .height(30.dp),

                enabled = itemAmount > 0
            ) {
                val text = when (item.name) {
                    "upgrade lvl 2" -> "Anwenden (${viewState.amountUpgradeLvl2})"
                    "upgrade lvl 3" -> "Anwenden (${viewState.amountUpgradeLvl3})"
                    "upgrade lvl 4" -> "Anwenden (${viewState.amountUpgradeLvl4})"
                    "upgrade lvl 5" -> "Anwenden (${viewState.amountUpgradeLvl5})"
                    else -> "Anwenden(${itemAmount})"

                }
                Text(
                    text = text,
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

private fun getBoostName(boostType: Int): String {
    return when (boostType) {
        1 -> "low boost"
        2 -> "medium boost"
        3 -> "high boost"
        else -> ""
    }
}

