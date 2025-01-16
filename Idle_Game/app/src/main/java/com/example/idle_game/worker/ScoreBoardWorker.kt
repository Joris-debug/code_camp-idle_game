package com.example.idle_game.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.idle_game.data.repositories.GameRepository
import kotlinx.coroutines.coroutineScope

@HiltWorker
class ScoreBoardWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val gameRepository: GameRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        coroutineScope {
            gameRepository.updateScoreBoard()
            gameRepository.fetchScoreBoard()
        }
        return Result.success()
    }

}