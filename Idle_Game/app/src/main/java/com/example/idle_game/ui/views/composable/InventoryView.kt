package com.example.idle_game.ui.views.composable

import android.annotation.SuppressLint
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.idle_game.data.database.models.InventoryData
import com.example.idle_game.data.database.models.ShopData
import com.example.idle_game.ui.views.models.InventoryViewModel
import com.example.idle_game.ui.views.states.InventoryViewState
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import kotlinx.coroutines.delay

@SuppressLint("DefaultLocale")
@OptIn(ExperimentalPagerApi::class)
@Composable
fun InventoryView(viewModel: InventoryViewModel = hiltViewModel()) {
    val viewState = viewModel.viewState.collectAsState().value
    val shopDataList = viewState.shopData.collectAsState(initial = emptyList()).value
    val inventoryData = viewState.inventoryData
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
                if (inventoryData != null) {
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
            }

            1 -> {
                // Upgrade Items Screen
                if (inventoryData != null) {
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
            }

            2 -> {
                // Boost Items Screen
                val title = if ((viewState.inventoryData?.activeBoostType ?: 0) > 0) {
                    val boostName = viewState.inventoryData?.let { getBoostName(it.activeBoostType) }
                    val remainingTime = remember { mutableStateOf("") }

                    // Calculate the remaining time and update the state
                    LaunchedEffect(viewState.inventoryData?.boostActiveUntil) {
                        val endTime = viewState.inventoryData?.boostActiveUntil ?: 0L
                        while (System.currentTimeMillis() < endTime) {
                            val timeLeft = endTime - System.currentTimeMillis()
                            val minutes = (timeLeft / 60000) % 60
                            val seconds = (timeLeft / 1000) % 60
                            remainingTime.value = String.format("%02d:%02d", minutes, seconds)
                            delay(1000L) // Update every second
                        }
                        remainingTime.value = "Abgelaufen"
                    }

                    "Boosts ($boostName aktiv - ${remainingTime.value})"
                } else {
                    "Boosts (Inaktiv)"
                }



                if (inventoryData != null) {
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
    }

    //Dialog that pops up when you want to buy or use an item
    if (inventoryData != null) {
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
    }

    //For showing InputDialog for custom Amount of Upgrades
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

//Logic for different screens for different item categories
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
                quantityLvl1 = viewState.inventoryData?.hackersLvl1 ?: 0
                quantityLvl2 = viewState.inventoryData?.hackersLvl2 ?: 0
                quantityLvl3 = viewState.inventoryData?.hackersLvl3 ?: 0
                quantityLvl4 = viewState.inventoryData?.hackersLvl4 ?: 0
                quantityLvl5 = viewState.inventoryData?.hackersLvl5 ?: 0
            }
            "medium passive" -> {
                quantityLvl1 = viewState.inventoryData?.cryptoMinersLvl1 ?: 0
                quantityLvl2 = viewState.inventoryData?.cryptoMinersLvl2 ?: 0
                quantityLvl3 = viewState.inventoryData?.cryptoMinersLvl3 ?: 0
                quantityLvl4 = viewState.inventoryData?.cryptoMinersLvl4 ?: 0
                quantityLvl5 = viewState.inventoryData?.cryptoMinersLvl5 ?: 0
            }
            "high passive" -> {
                quantityLvl1 = viewState.inventoryData?.botnetsLvl1 ?: 0
                quantityLvl2 = viewState.inventoryData?.botnetsLvl2 ?: 0
                quantityLvl3 = viewState.inventoryData?.botnetsLvl3 ?: 0
                quantityLvl4 = viewState.inventoryData?.botnetsLvl4 ?: 0
                quantityLvl5 = viewState.inventoryData?.botnetsLvl5 ?: 0
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
                    "upgrade lvl 2" -> "Anwenden (${viewState.inventoryData?.upgradeLvl2})"
                    "upgrade lvl 3" -> "Anwenden (${viewState.inventoryData?.upgradeLvl3})"
                    "upgrade lvl 4" -> "Anwenden (${viewState.inventoryData?.upgradeLvl4})"
                    "upgrade lvl 5" -> "Anwenden (${viewState.inventoryData?.upgradeLvl5})"
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

private fun getBoostName(boostType: Int): String {
    return when (boostType) {
        1 -> "low boost"
        2 -> "medium boost"
        3 -> "high boost"
        else -> ""
    }
}

