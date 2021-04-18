package com.fantalbala.doublechrono

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.view.View
import android.widget.Button

class MainActivity : AppCompatActivity() {

    var redButton: Button? = null
    var blueButton: Button? = null

    private var redIsActif = false
    private var blueIsActif = false

    private var isRunning = false

    private var startTime: Long = 0
    private var currentSeconds: Int = 0
    private var redSeconds: Int = 0
    private var blueSeconds: Int = 0

    //runs without a timer by reposting this handler at the end of the runnable
    var timerHandler: Handler = Handler();
    private var timerRunnable: Runnable = object : Runnable {
        override fun run() {
            isRunning = true
            updateText()
            timerHandler.post(this)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        redButton = findViewById<View>(R.id.redButton) as Button
        blueButton = findViewById<View>(R.id.blueButton) as Button

        redButton?.text = "00:00"
        blueButton?.text = "00:00"

        redButton?.setOnClickListener {
            startTime = System.currentTimeMillis()
            redIsActif = true
            blueIsActif = false
            blueSeconds = currentSeconds

            if (!isRunning) {
                timerHandler.post(timerRunnable)
            }
        }

        blueButton?.setOnClickListener {
            startTime = System.currentTimeMillis()
            redIsActif = false
            blueIsActif = true
            redSeconds = currentSeconds

            if (!isRunning) {
                timerHandler.post(timerRunnable)
            }
        }
    }

    fun updateText() {
        var prefix = ""
        val millis = System.currentTimeMillis() - startTime
        val secondsToAdd = if (redIsActif) redSeconds else blueSeconds

        currentSeconds = (millis / 1000).toInt() + secondsToAdd
        val minutes = currentSeconds / 60
        val second = currentSeconds % 60

        if (minutes < 10) {
            prefix = "0"
        }

        if (redIsActif) {
            redButton?.text = String.format("%s%d:%02d", prefix, minutes, second)
        } else if (blueIsActif) {
            blueButton?.text = String.format("%s%d:%02d", prefix, minutes, second)
        }
    }

    override fun onPause() {
        super.onPause()
        timerHandler.removeCallbacks(timerRunnable)
    }
}