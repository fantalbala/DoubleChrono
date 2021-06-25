package com.fantalbala.doublechrono

import android.content.Context
import android.os.*
import android.os.VibrationEffect.createOneShot
import android.view.*
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    companion object {
        private const val LONG_INTERVAL = 2000
        private const val TIME_INITIAL_TEXT = "00:00:00"
        private const val TIME_STRING_FORMAT = "%s%d:%s%d:%02d"
    }

    var redButton: Button? = null
    var blueButton: Button? = null

    private var redIsActive = false
    private var blueIsActive = false

    private var isRunning = false

    private var startTime: Long = 0
    private var currentSeconds: Int = 0
    private var redSeconds: Int = 0
    private var blueSeconds: Int = 0

    val timerHandler = Handler(Looper.getMainLooper())
    private val timerRunnable: Runnable = object : Runnable {
        override fun run() {
            isRunning = true
            updateText()
            timerHandler.post(this)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAsFullScreen()

        setContentView(R.layout.activity_main)

        initRedButton()
        initBlueButton()
    }

    override fun onPause() {
        super.onPause()
        isRunning = false
        timerHandler.removeCallbacks(timerRunnable)
    }

    private fun setAsFullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }

    private fun initRedButton() {
        redButton = findViewById<View>(R.id.redButton) as Button

        redButton?.apply {
            text = TIME_INITIAL_TEXT
            setOnClickListener {
                if (!redIsActive) {
                    startTime = System.currentTimeMillis()
                    redIsActive = true
                    blueIsActive = false
                    blueSeconds = currentSeconds

                    if (!isRunning) {
                        timerHandler.post(timerRunnable)
                    }
                }
            }
        }
    }

    private fun initBlueButton() {
        blueButton = findViewById<View>(R.id.blueButton) as Button

        blueButton?.apply {
            text = TIME_INITIAL_TEXT
            setOnClickListener {
                if (!blueIsActive) {
                    startTime = System.currentTimeMillis()
                    redIsActive = false
                    blueIsActive = true
                    redSeconds = currentSeconds

                    if (!isRunning) {
                        timerHandler.post(timerRunnable)
                    }
                }
            }
        }
    }

    private fun updateText() {
        val millis = System.currentTimeMillis() - startTime
        val secondsToAdd = if (redIsActive) redSeconds else blueSeconds

        currentSeconds = (millis / 1000).toInt() + secondsToAdd
        val hours = currentSeconds / (60 * 60)
        val minutes = currentSeconds / 60 % 60
        val second = currentSeconds % 60

        val prefixM = if (minutes < 10) "0" else ""
        val prefixH = if (hours < 10) "0" else ""

        if (redIsActive) {
            redButton?.text =
                String.format(TIME_STRING_FORMAT, prefixH, hours, prefixM, minutes, second)
        } else if (blueIsActive) {
            blueButton?.text =
                String.format(TIME_STRING_FORMAT, prefixH, hours, prefixM, minutes, second)
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            if (event.eventTime - event.downTime > LONG_INTERVAL) {
                val vb = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                vb.vibrate(createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE))
                onPause()
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}
