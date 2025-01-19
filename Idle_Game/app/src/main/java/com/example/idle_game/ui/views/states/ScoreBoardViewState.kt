package com.example.idle_game.ui.views.states

import com.example.idle_game.data.database.models.PlayerData
import com.example.idle_game.data.database.models.ScoreBoardData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

data class ScoreBoardViewState(
    val scoreData: Flow<List<ScoreBoardData>> = emptyFlow(),
    val playerData: Flow<PlayerData> = emptyFlow()
)