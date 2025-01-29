package com.example.idle_game.ui.views.composable.inventory

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.idle_game.R
import com.example.idle_game.data.database.models.ShopData
import com.example.idle_game.ui.views.composable.PassiveBox
import com.example.idle_game.ui.views.models.InventoryViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.delay

@SuppressLint("DefaultLocale")
@OptIn(ExperimentalPagerApi::class)
@Composable
fun InventoryView(viewModel: InventoryViewModel = hiltViewModel()) {
    val viewState = viewModel.viewState.collectAsState().value
    val shopDataList = viewState.shopData.collectAsState(initial = emptyList()).value
    val inventoryData = viewState.inventoryData

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

    var page by remember { mutableIntStateOf(0) }

    // Set up the HorizontalPager for swiping between screens
    HorizontalPager(
        count = 3, // 3 screens
        modifier = Modifier.fillMaxSize(),
        state = rememberPagerState(initialPage = page)
    ) { pageIndex ->
        page = pageIndex
        when (pageIndex) {
            0 -> {
                // Passive Items Screen
                if (inventoryData != null) {
                    //Logic for different screens for different item categories
                    CategoryScreen(
                        items = passiveItems,
                        title = "Erzeuger (Werden automatisch angewendet)",
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
                if (inventoryData != null) {
                    CategoryScreen(
                        items = boostItems,
                        title = "Boosts",
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

@Composable
private fun getBoostIcon(boostType: Int): Painter? {
    return when (boostType) {
        1 -> return painterResource(id = R.drawable.low_boost)
        2 -> return painterResource(id = R.drawable.medium_boost)
        3 -> return painterResource(id = R.drawable.high_boost)
        else -> null
    }
}

