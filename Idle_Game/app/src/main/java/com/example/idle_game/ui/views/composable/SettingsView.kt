package com.example.idle_game.ui.views.composable

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.idle_game.ui.views.models.SettingsViewModel
import com.example.idle_game.util.checkAndRequestNotificationPermission
import kotlin.system.exitProcess

@Composable
fun SettingsView(viewModel: SettingsViewModel = hiltViewModel()) {
    val viewState by viewModel.viewState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var checkPermission by remember { mutableStateOf(false) }
    var inputCheatCode by remember { mutableStateOf("") }

    Column {

        SettingsSwitch(
            text = "Benachrichtigungen",
            onCheckedChange = {
                viewModel.saveOption(it, 0)
                checkPermission = it

            },
            checked = viewState.switchState[0]

        )

        SettingsSwitch(
            text = "Große Nummern abkürzen",
            onCheckedChange = { viewModel.saveOption(it, 1) },
            checked = viewState.switchState[1]
        )

        SettingsButton(text = "Username: ${viewState.username}", action = { showDialog = true })

        SettingsSwitch(
            text = "Dunkelmodus",
            onCheckedChange = {
                viewModel.saveOption(it, 2)
            },
            checked = viewState.switchState[2]
        )

        SettingsDropDown(
            text = "Kontrast",
            onValueChanged = {
                viewModel.saveContrast(it)
            },
            value = viewState.contrast,
            options = listOf("Niedrieger Kontrast", "Mittler Kontrast", "Hoher Kontrast")
        )

        if (showDialog) {
            WarningDialog(
                onConfirm = { viewModel.logout(); showDialog = false; exitProcess(0) },
                onDismiss = { showDialog = false })
        }
        if (checkPermission) {
            val context = LocalContext.current
            val activity = context as? Activity
            LaunchedEffect(Unit) {
                activity?.let {
                    checkAndRequestNotificationPermission(it)
                }
            }
            viewModel.notification(true)
        }
        Box(modifier = Modifier.fillMaxSize()) {
            TextField(
                value = inputCheatCode,
                onValueChange = { newText -> inputCheatCode = viewModel.checkCheatCode(newText) },
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(0f)
                    .align(Alignment.BottomEnd)
                    .width(5.dp)
                    .height(5.dp)
            )
        }

    }
}

/**
 * Settings Body is an Outlined-Box with a Description on the left and an actor (e.g. Button) on the right side
 */
@Composable
fun SettingsBody(
    text: String = "Option",
    actor: @Composable () -> Unit
) {
    Spacer(
        modifier = Modifier
            .height(6.dp)
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                2.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(8.dp)
            )
            .background(
                color = MaterialTheme.colorScheme.surfaceContainerHigh
            )
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = text,
                modifier = Modifier.weight(1f),
                fontSize = 16.sp
            )
            actor()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun SettingsDropDown(
    options: List<String>,
    text: String,
    onValueChanged: (Int) -> Unit,
    value: Int
) {
    var expanded by remember { mutableStateOf(false) }

    SettingsBody(text = text)
    {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            TextField(
                value = options[value],
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(0.5f),
                singleLine = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        content = { Text(option) },
                        onClick = {
                            onValueChanged(options.indexOf(option))
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsButton(
    text: String,
    enabled: Boolean = true,
    action: () -> Unit
) {

    SettingsBody(
        text = text,
        actor = {
            Button(onClick = action, enabled = enabled) { Text(text = "Abmelden") }
        }
    )
}

@Composable
fun SettingsSwitch(
    text: String = "Option",
    checked: Boolean = false,
    onCheckedChange: (Boolean) -> Unit
) {
    SettingsBody(
        text = text,
        actor = {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    )
}

@Composable
fun WarningDialog(onDismiss: () -> Unit = {}, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Hinweis") },
        text = {
            Text(text = "Wenn du mit einem anderen Benutzernamen einloggst, wird dein vorheriger Spielstand gelöscht!")
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Abbrechen")
            }
        }
    )
}