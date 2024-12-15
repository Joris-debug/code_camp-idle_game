package com.example.idle_game.ui.views.composable

import android.content.Context
import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.idle_game.R
import com.example.idle_game.ui.views.models.LoadingSceenViewModel

@Composable
fun LoadingScreenView(
    viewModel: LoadingSceenViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit,
    onLoginFailure: () -> Unit,
    context: Context,
    onWifiOK: () -> Unit
) {
    val viewState = viewModel.viewState.collectAsState()

    viewModel.init(
        onLoginSuccess = { onLoginSuccess() },
        onLoginFailure = { onLoginFailure() },
        context,
        { onWifiOK() })
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.White),
            contentAlignment = Alignment.Center
        ) {
            Image(
                modifier = Modifier
                    .fillMaxSize(0.8f),
                painter = painterResource(id = R.drawable.bitcoin),
                contentDescription = "Bitcoin",
                contentScale = let {
                    if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT) {
                        ContentScale.FillWidth
                    } else {
                        ContentScale.FillHeight
                    }
                })
        }
        Column(
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = viewState.value.connectionString,
                fontSize = 30.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(all = 40.dp)
            )
        }

    }
}
