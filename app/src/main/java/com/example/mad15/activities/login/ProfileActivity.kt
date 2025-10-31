package com.example.mad15.activities.login

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mad15.R
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.core.content.ContextCompat
import com.example.mad15.MyApp
import com.example.mad15.activities.login.LoginActivity
import com.example.mad15.databinding.ActivityProfileBinding
import kotlinx.coroutines.launch
import com.example.mad15.activities.dashboard.DashboardActivity


class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private var currentUserEmail: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentUserEmail = intent.getStringExtra("USER_EMAIL")
        Log.d("ProfileActivity", "Received USER_EMAIL = $currentUserEmail")

        // Load user details
        loadUserData()

        // Disable button initially
        setButtonEnabled(false)

        // Add TextWatchers to monitor changes
        setupTextWatchers()

        // Handle Update click
        binding.btnUpdate.setOnClickListener {
            updateUserData()
        }

        // Bottom Navigation Setup
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, DashboardActivity::class.java)
                    intent.putExtra("USER_EMAIL", currentUserEmail)
                    startActivity(intent)
                    finish()
                }
                R.id.nav_messages -> {
                    Toast.makeText(this, "Messages clicked", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_settings -> {
                    Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show()
                }
            }
            true
        }
    }

    private fun loadUserData() {
        lifecycleScope.launch {
            currentUserEmail?.let { email ->
                val user = MyApp.database.userDao().getUserByEmail(email)
                if (user != null) {
                    runOnUiThread {
                        binding.etFullName.setText(user.name)
                        binding.etEmail.setText(user.email)
                        binding.etPassword.setText(user.password)
                    }
                }
            }
        }
    }

    private fun setupTextWatchers() {
        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val name = binding.etFullName.text.toString().trim()
                val email = binding.etEmail.text.toString().trim()
                val password = binding.etPassword.text.toString().trim()
                setButtonEnabled(name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        binding.etFullName.addTextChangedListener(watcher)
        binding.etEmail.addTextChangedListener(watcher)
        binding.etPassword.addTextChangedListener(watcher)
    }

    private fun setButtonEnabled(enabled: Boolean) {
        binding.btnUpdate.isEnabled = enabled
        println("Update button enabled = $enabled")
        val color = if (enabled) {
            ContextCompat.getColor(this, R.color.teal_700)
        } else {
            ContextCompat.getColor(this, R.color.gray_disabled)
        }
        binding.btnUpdate.setBackgroundColor(color)
    }

    private fun updateUserData() {
        val newName = binding.etFullName.text.toString().trim()
        val newEmail = binding.etEmail.text.toString().trim()
        val newPassword = binding.etPassword.text.toString().trim()

        Log.d("ProfileActivity", "Clicked update: $newName / $newEmail / $newPassword")

        lifecycleScope.launch {
            val oldEmail = currentUserEmail
            Log.d("ProfileActivity", "Looking up user for oldEmail=$oldEmail")

            val user = MyApp.database.userDao().getUserByEmail(oldEmail ?: "")
            Log.d("ProfileActivity", "Fetched user = $user")

            if (user != null) {
                val updatedUser = user.copy(
                    id = user.id,
                    name = newName,
                    email = newEmail,
                    password = newPassword
                )
                val rows = MyApp.database.userDao().updateUser(updatedUser)
                Log.d("ProfileActivity", "Rows updated = $rows")

                if (rows > 0) {
                    runOnUiThread {
                        Toast.makeText(
                            this@ProfileActivity,
                            "Profile updated successfully!",
                            Toast.LENGTH_SHORT
                        ).show()

                        // ✅ Redirect to Dashboard with updated email
                        val intent = Intent(this@ProfileActivity, DashboardActivity::class.java)
                        intent.putExtra("USER_EMAIL", newEmail)
                        startActivity(intent)

                        // Optional smooth transition animation
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)

                        // Close ProfileActivity so back button won't return here
                        finishAffinity()
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@ProfileActivity, "Update failed", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Log.d("ProfileActivity", "User not found for $oldEmail")
                runOnUiThread {
                    Toast.makeText(this@ProfileActivity, "User not found", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}