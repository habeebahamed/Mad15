package com.example.mad15.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mad15.R
import com.example.mad15.activities.login.LoginActivity
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.mad15.activities.dashboard.DashboardActivity
import com.example.mad15.databinding.ActivityMainBinding
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // If you have startup work that may take time, use a coroutine or background thread.
    // We'll simulate startup with a small delay below.
    private val startupScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        // 1) Install the splash screen. Must be BEFORE super.onCreate to show correctly on some devices.
        val splashScreen = installSplashScreen()

        // Optional: keep the splash on-screen while some condition is true
        var keepSplashOn = true
        splashScreen.setKeepOnScreenCondition { keepSplashOn }

        super.onCreate(savedInstanceState)

        // 2) Inflate view binding and set contentView
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 3) Edge-to-edge or UI setup if needed (optional)
        // WindowCompat.setDecorFitsSystemWindows(window, false)

        // 4) Start startup tasks (load prefs, DB init, check session, remote config, etc.)
        startupScope.launch {
            try {
                delay(800)
                val loggedIn = isUserLoggedIn()
                keepSplashOn = false

                if (loggedIn) {
                    goToDashboard()
                } else {
                    goToLogin()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                keepSplashOn = false
            }
        }

        // Optional: customize exit animation for the splash icon
        splashScreen.setOnExitAnimationListener { splashView ->
            // Fade out the splash icon and remove the splash view
            splashView.iconView.animate()
                .alpha(0f)
                .setDuration(200L)
                .withEndAction { splashView.remove() }
                .start()
        }
    }

    private fun isUserLoggedIn(): Boolean {
        // TODO: Replace with DataStore / SharedPreferences or repository check
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        return prefs.getBoolean("isLoggedIn", false)
    }

    private fun goToLogin() {
        startActivity(Intent(this@MainActivity, LoginActivity::class.java))
        finish()
    }

    private fun goToDashboard() {
        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        startupScope.cancel()
    }
}