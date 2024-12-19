package com.example.mad_3

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.mad_3.databinding.ActivityTimerFragmentBinding

class TimerFragment : Fragment() {

    private lateinit var binding: ActivityTimerFragmentBinding

    private var isRunning = false
    private var startTime = 0L
    private var timeInMillis = 0L

    private val handler = Handler(Looper.getMainLooper())
    private val runnable: Runnable = object : Runnable {
        override fun run() {
            if (isRunning) {
                val currentTime = System.currentTimeMillis()
                val elapsedTime = currentTime - startTime + timeInMillis
                val seconds = (elapsedTime / 1000) % 60
                val minutes = (elapsedTime / 1000) / 60
                val millis = (elapsedTime % 1000) / 10
                binding.timerText.text = String.format("%02d:%02d:%02d", minutes, seconds, millis)

                handler.postDelayed(this, 50) // update every 50ms for smoother animation
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ActivityTimerFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Start/Pause button functionality
        binding.btnStartPause.setOnClickListener {
            if (isRunning) {
                pauseTimer()
            } else {
                startTimer()
            }
        }

        // Reset button functionality
        binding.btnReset.setOnClickListener {
            resetTimer()
        }

        // Show Animation
        binding.timerAnimation.visibility = View.VISIBLE
        binding.timerAnimation.setBackgroundColor(Color.TRANSPARENT)
        binding.timerAnimation.playAnimation()
    }

    private fun startTimer() {
        startTime = System.currentTimeMillis()
        handler.post(runnable)
        binding.btnStartPause.text = "Pause"
        isRunning = true
    }

    private fun pauseTimer() {
        timeInMillis += System.currentTimeMillis() - startTime
        handler.removeCallbacks(runnable)
        binding.btnStartPause.text = "Start"
        isRunning = false
    }

    private fun resetTimer() {
        handler.removeCallbacks(runnable)
        binding.timerText.text = "00:00:00"
        timeInMillis = 0L
        isRunning = false
        binding.btnStartPause.text = "Start"
    }
}
