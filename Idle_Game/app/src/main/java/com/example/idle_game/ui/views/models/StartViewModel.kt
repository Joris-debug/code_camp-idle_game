package com.example.idle_game.ui.views.models

import androidx.lifecycle.ViewModel
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.example.idle_game.data.workers.NotWorker
import com.example.idle_game.ui.views.states.StartViewState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.concurrent.TimeUnit


class StartViewModel : ViewModel() {

    private val _viewState = MutableStateFlow(StartViewState())
    val viewState: StateFlow<StartViewState> get() = _viewState

    fun incrementCounter(increment: Int, delayMinutes: Long, workManager: WorkManager) {
        _viewState.value = _viewState.value.copy(
            counter = _viewState.value.counter + increment,
            isLoading = true,
            errorMessage = null
        )
        scheduleNotWorker(delayMinutes, workManager)
    }


    private fun scheduleNotWorker(delayMinutes: Long, workManager: WorkManager) {
        val workRequest: WorkRequest = OneTimeWorkRequest.Builder(NotWorker::class.java)
            .setInitialDelay(delayMinutes, TimeUnit.MINUTES)
            .build()

        workManager.enqueue(workRequest)

        _viewState.value = _viewState.value.copy(isLoading = false)
    }
}
