package com.example.idle_game.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.example.idle_game.R
import com.example.idle_game.data.workers.NotWorker
import com.example.idle_game.databinding.FragmentHomeBinding
import java.util.concurrent.TimeUnit

class HomeFragment : Fragment() {

    private var counter = 0

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentHomeBinding.inflate(inflater, container, false)

        val buttonHacker = binding.root.findViewById<Button>(R.id.buttonHacker)
        val buttonBotNet = binding.root.findViewById<Button>(R.id.buttonBotnet)
        val buttonMiner = binding.root.findViewById<Button>(R.id.buttonMiner)
        val counterText = binding.counterText

        buttonHacker.setOnClickListener {
            counter++
            updateCounter(counterText)
            scheduleNotWorker(15)
        }

        buttonBotNet.setOnClickListener {
            counter+=2
            updateCounter(counterText)
            scheduleNotWorker(20)
        }

        buttonMiner.setOnClickListener {
            counter+=3
            updateCounter(counterText)
            scheduleNotWorker(30)
        }

        return binding.root
    }

    private fun updateCounter(counterText: TextView) {
        counterText.text = "Counter: $counter"
    }

    private fun scheduleNotWorker(delayMinutes: Long) {
        val workRequest: WorkRequest = OneTimeWorkRequest.Builder(NotWorker::class.java)
            .setInitialDelay(delayMinutes, TimeUnit.MINUTES)
            .build()

        Log.e("Work scheduled", "Notification set.")

        WorkManager.getInstance(requireContext()).enqueue(workRequest)
    }
}