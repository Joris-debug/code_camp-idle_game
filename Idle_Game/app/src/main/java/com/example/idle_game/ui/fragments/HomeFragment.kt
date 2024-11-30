package com.example.idle_game.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.idle_game.R
import com.example.idle_game.databinding.FragmentHomeBinding

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
        }

        buttonBotNet.setOnClickListener {
            counter+=2
            updateCounter(counterText)
        }

        buttonMiner.setOnClickListener {
            counter+=3
            updateCounter(counterText)
        }

        return binding.root
    }

    private fun updateCounter(counterText: TextView) {
        counterText.text = "Counter: $counter"
    }
}