package com.fantalbala.doublechrono

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.*
import android.os.VibrationEffect.createOneShot
import android.view.*
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity


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

    private val LONG_INTERVAL = 2000

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
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;

        redButton = findViewById<View>(R.id.redButton) as Button
        blueButton = findViewById<View>(R.id.blueButton) as Button

        redButton?.text = "00:00"
        blueButton?.text = "00:00"

        redButton?.setOnClickListener {
            if (!redIsActif) {
                startTime = System.currentTimeMillis()
                redIsActif = true
                blueIsActif = false
                blueSeconds = currentSeconds

                if (!isRunning) {
                    timerHandler.post(timerRunnable)
                }
            }
        }

        blueButton?.setOnClickListener {
            if (!blueIsActif) {
                startTime = System.currentTimeMillis()
                redIsActif = false
                blueIsActif = true
                redSeconds = currentSeconds

                if (!isRunning) {
                    timerHandler.post(timerRunnable)
                }
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUI()
    }

    private fun Activity.hideSystemUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.let {
                // Default behavior is that if navigation bar is hidden, the system will "steal" touches
                // and show it again upon user's touch. We just want the user to be able to show the
                // navigation bar by swipe, touches are handled by custom code -> change system bar behavior.
                // Alternative to deprecated SYSTEM_UI_FLAG_IMMERSIVE.
                it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_BARS_BY_SWIPE
                // Finally, hide the system bars, alternative to View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                // and SYSTEM_UI_FLAG_FULLSCREEN.
                it.hide(WindowInsets.Type.systemBars())
            }
        } else {
            // Enables regular immersive mode.
            // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
            // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                    // Do not let system steal touches for showing the navigation bar
                    View.SYSTEM_UI_FLAG_IMMERSIVE
                            // Hide the nav bar and status bar
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_FULLSCREEN
                            // Keep the app content behind the bars even if user swipes them up
                            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
            // make navbar translucent - do this already in hideSystemUI() so that the bar
            // is translucent if user swipes it up
            @Suppress("DEPRECATION")
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
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
        isRunning = false
        timerHandler.removeCallbacks(timerRunnable)
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