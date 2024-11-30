package com.example.idle_game.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.idle_game.R
import com.example.idle_game.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentHomeBinding.inflate(inflater, container, false)

        val buttonHacker = binding.root.findViewById<Button>(R.id.buttonHacker)
        val buttonBotNet = binding.root.findViewById<Button>(R.id.buttonBotnet)
        val buttonCryptoMiner = binding.root.findViewById<Button>(R.id.buttonCryptoMiner)

        buttonHacker.setOnClickListener {
            Log.e("Hacker", "Hacker in use")
        }

        buttonBotNet.setOnClickListener {
            Log.e("Bot-Net", "Bot-Net in use")
        }

        buttonCryptoMiner.setOnClickListener {
            Log.e("Crypto Miner", "Miner in use")
        }

        return binding.root
    }
}