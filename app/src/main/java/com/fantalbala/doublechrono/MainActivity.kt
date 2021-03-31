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

    private var redTime: Long = 0
    private var blueTime: Long = 0

    private var redIsActif = false
    private var blueIsActif = false

    private var isRunning = false

    //runs without a timer by reposting this handler at the end of the runnable
    var timerHandler: Handler = Handler();
    var timerRunnable: Runnable = object : Runnable {
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

        redButton!!.text = "00:00"
        blueButton!!.text = "00:00"

        redButton!!.setOnClickListener {
            if (!isRunning) {
                timerHandler.post(timerRunnable)
            }
            redIsActif = true
            blueIsActif = false
        }

        blueButton!!.setOnClickListener {
            if (!isRunning) {
                timerHandler.post(timerRunnable)
            }
            redIsActif = false
            blueIsActif = true
        }
    }

    fun updateText() {
        var prefix = ""
        if (redIsActif){
            redTime ++
            var seconds = (redTime / 100).toInt()
            var minutes = seconds / 60
            seconds %= 60

            if (minutes < 10) {
                prefix = "0"
            }

            redButton!!.text = String.format("%s%d:%02d", prefix, minutes, seconds)
        } else if (blueIsActif) {
            blueTime++
            var seconds = (blueTime / 100).toInt()
            var minutes = seconds / 60
            seconds %= 60

            if (minutes < 10) {
                prefix = "0"
            }

            blueButton!!.text = String.format("%s%d:%02d", prefix, minutes, seconds)
        }
    }

    override fun onPause() {
        super.onPause()
        timerHandler.removeCallbacks(timerRunnable)
    }
}