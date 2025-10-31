package com.example.mad15.activities.login.SignupActivity.kt

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.mad15.MyApp
import com.example.mad15.activities.login.LoginActivity
import com.example.mad15.data.db.entities.User
import com.example.mad15.databinding.ActivitySignupBinding
import kotlinx.coroutines.launch

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSignup.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val userDao = MyApp.database.userDao()
                val existingUser = userDao.getUserByEmail(email)

                if (existingUser != null) {
                    runOnUiThread {
                        Toast.makeText(this@SignupActivity, "User already exists", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val newUser = User(name = username, email = email, password = password)
                    userDao.insertUser(newUser)
                    runOnUiThread {
                        Toast.makeText(this@SignupActivity, "Signup successful!", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            }
        }

        binding.tvLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}