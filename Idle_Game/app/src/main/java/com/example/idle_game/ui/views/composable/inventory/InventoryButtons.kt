package com.example.idle_game.ui.views.composable.inventory

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.idle_game.R
import com.example.idle_game.data.database.models.ShopData
import com.example.idle_game.ui.views.states.InventoryViewState
import kotlinx.coroutines.delay

//Logic for visualising items
@SuppressLint("DefaultLocale")
@Composable
fun ShopItemButtons(
    item: ShopData,
    itemAmount: Int,
    onBuyClick: () -> Unit,
    onApplyClick: () -> Unit,
    viewState: InventoryViewState
) {
    val isSpecialItem = item.name.contains("low passive", ignoreCase = true) ||
            item.name.contains("medium passive", ignoreCase = true) ||
            item.name.contains("high passive", ignoreCase = true)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Button(
                onClick = onBuyClick,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceBright),
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier
                    .size(150.dp)
                    .padding(0.dp)
                    .border(2.dp, MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(50.dp))
                    .background(Color.Transparent)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxSize()
                ) {
                    getIcon(item)?.let {
                        Image(
                            painter = it,
                            contentDescription = "Icon",
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 4.dp)
                    ) {

                        Text(
                            text = "${item.cost}",
                            fontSize = 16.sp,
                            color = Color.Black
                        )

                        Image(
                            painter = painterResource(id = R.drawable.bitcoin),
                            contentDescription = "BTC Icon",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            if (isSpecialItem) {
                Column(
                    modifier = Modifier
                        .padding(start = 16.dp)
                ) {
                    Text(text = "Lvl1: ${getQuantities(item,1,viewState)}")
                    Text(text = "Lvl2: ${getQuantities(item,2,viewState)}")
                    Text(text = "Lvl3: ${getQuantities(item,3,viewState)}")
                    Text(text = "Lvl4: ${getQuantities(item,4,viewState)}")
                    Text(text = "Lvl5: ${getQuantities(item,5,viewState)}")
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                if (!isSpecialItem) {
                    Button(
                        onClick = onApplyClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (itemAmount > 0) MaterialTheme.colorScheme.inversePrimary else MaterialTheme.colorScheme.surfaceVariant
                        ),
                        shape = RoundedCornerShape(5.dp),
                        modifier = Modifier
                            .height(30.dp)
                            .border(2.dp, if (itemAmount > 0) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.outline, RoundedCornerShape(5.dp))
                            .padding(0.dp),
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
                            fontSize = 10.sp,
                            color = if (itemAmount > 0) Color.Black else Color.DarkGray
                        )
                    }

                    val remainingTime = remember { mutableStateOf("") }

                    // Calculate the remaining time and update the state
                    LaunchedEffect(viewState.inventoryData?.boostActiveUntil) {
                        val endTime = viewState.inventoryData?.boostActiveUntil ?: 0L
                        while (System.currentTimeMillis() < endTime) {
                            val timeLeft = endTime - System.currentTimeMillis()
                            val hours = (timeLeft / (1000 * 60 * 60)) % 24
                            val minutes = (timeLeft / (1000 * 60)) % 60
                            val seconds = (timeLeft / 1000) % 60
                            remainingTime.value = String.format("%02d:%02d:%02d", hours, minutes, seconds)
                            delay(1000L)
                        }
                        remainingTime.value = "Abgelaufen"
                    }

                    val boostMap = mapOf(
                        "low Boost" to 1,
                        "medium Boost" to 2,
                        "high Boost" to 3
                    )

                    boostMap[item.name]?.let { boostType ->
                        if ((viewState.inventoryData?.activeBoostType ?: 0) == boostType) {
                            Text(
                                text = "Aktiv ${remainingTime.value}",
                                fontSize = 16.sp,
                                color = Color.Red,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun getIcon(item: ShopData): Painter? {
    when(item.name) {
        "low passive" -> return painterResource(id = R.drawable.hacker)
        "medium passive" -> return painterResource(id = R.drawable.crypto_miner)
        "high passive" -> return painterResource(id = R.drawable.botnet)
        "upgrade lvl 2" -> return painterResource(id = R.drawable.upgrade_lvl_2)
        "upgrade lvl 3" -> return painterResource(id = R.drawable.upgrade_lvl_3)
        "upgrade lvl 4" -> return painterResource(id = R.drawable.upgrade_lvl_4)
        "upgrade lvl 5" -> return painterResource(id = R.drawable.upgrade_lvl_5)
        "low Boost" -> return painterResource(id = R.drawable.low_boost)
        "medium Boost" -> return painterResource(id = R.drawable.medium_boost)
        "high Boost" -> return painterResource(id = R.drawable.high_boost)
    }
    return null
}

private fun getQuantities(item: ShopData, lvl: Int, viewState: InventoryViewState): Int {
    when (item.name) {
        "low passive" -> {
            when (lvl){
                1 -> return viewState.inventoryData?.hackersLvl1 ?: 0
                2 -> return viewState.inventoryData?.hackersLvl2 ?: 0
                3 -> return viewState.inventoryData?.hackersLvl3 ?: 0
                4 -> return viewState.inventoryData?.hackersLvl4 ?: 0
                5 -> return viewState.inventoryData?.hackersLvl5 ?: 0
            }
        }

        "medium passive" -> {
            when (lvl){
                1 -> return viewState.inventoryData?.cryptoMinersLvl1 ?: 0
                2 -> return viewState.inventoryData?.cryptoMinersLvl2 ?: 0
                3 -> return viewState.inventoryData?.cryptoMinersLvl3 ?: 0
                4 -> return viewState.inventoryData?.cryptoMinersLvl4 ?: 0
                5 -> return viewState.inventoryData?.cryptoMinersLvl5 ?: 0
            }
        }

        "high passive" -> {
            when (lvl){
                1 -> return viewState.inventoryData?.botnetsLvl1 ?: 0
                2 -> return viewState.inventoryData?.botnetsLvl2 ?: 0
                3 -> return viewState.inventoryData?.botnetsLvl3 ?: 0
                4 -> return viewState.inventoryData?.botnetsLvl4 ?: 0
                5 -> return viewState.inventoryData?.botnetsLvl5 ?: 0
            }
        }
    }
    return 0
}