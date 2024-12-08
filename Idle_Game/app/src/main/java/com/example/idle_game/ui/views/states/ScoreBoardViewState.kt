package com.example.idle_game.ui.views.states

import com.example.idle_game.data.database.models.ScoreBoardData
import kotlinx.coroutines.flow.Flow

data class ScoreBoardViewState (
    val scoreData: Flow<List<ScoreBoardData>>? = null
)