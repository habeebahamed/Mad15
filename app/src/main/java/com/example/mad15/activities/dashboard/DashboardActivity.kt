package com.example.mad15.activities.dashboard


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.mad15.MyApp
import com.example.mad15.R
import com.example.mad15.activities.login.LoginActivity
import com.example.mad15.activities.login.ProfileActivity
import com.example.mad15.databinding.ActivityDashboardBinding
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.launch
import androidx.core.content.edit

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup Toolbar
        setSupportActionBar(binding.topAppBar)
        binding.topAppBar.setNavigationOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        // Profile icon dropdown menu
        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            if (menuItem.itemId == R.id.action_profile) {
                showProfileDropdown()
                true
            } else false
        }

        // Drawer item click
        binding.navView.setNavigationItemSelectedListener { menuItem ->
            handleDrawerClick(menuItem)
            true
        }

        // Bottom navigation click
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.nav_home -> loadFragment(HomeFragment())
                R.id.nav_messages -> loadFragment(MessagesFragment())
                R.id.nav_settings -> loadFragment(SettingsFragment())
            }
            true
        }

        // Load default fragment
        loadFragment(HomeFragment())

        //  Load logged-in user info to nav header
        loadUserHeader()
    }

    private fun loadUserHeader() {
        val userEmail = intent.getStringExtra("USER_EMAIL")
        Log.d("DashboardActivity", "Dashboard started with email = $userEmail")
        if (userEmail != null) {
            lifecycleScope.launch {
                val user = MyApp.database.userDao().getUserByEmail(userEmail)
                if (user != null) {
                    val headerView = binding.navView.getHeaderView(0)
                    val tvUserName = headerView.findViewById<TextView>(R.id.FtvUserNameHeader)
                    val tvUserEmail = headerView.findViewById<TextView>(R.id.tvEmailHeader)
                    tvUserName.text = user.name
                    tvUserEmail.text = user.email
                }
            }
        }
    }

    private fun showProfileDropdown() {
        val popup = PopupMenu(this, findViewById(R.id.action_profile))
        popup.menuInflater.inflate(R.menu.drawer_menu, popup.menu)
        popup.setOnMenuItemClickListener { item ->
            when(item.itemId) {
                R.id.nav_profile -> {
                    val userEmail = intent.getStringExtra("USER_EMAIL")
                    val intent = Intent(this, com.example.mad15.activities.login.ProfileActivity::class.java)
                    intent.putExtra("USER_EMAIL", userEmail)
                    startActivity(intent)
                    Log.d("DashboardActivity", "➡️ Opening profile for $userEmail")
                    true
                }
                R.id.nav_logout -> {
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    private fun handleDrawerClick(item: MenuItem) {
        when(item.itemId) {
            R.id.nav_profile -> {
                val userEmail = intent.getStringExtra("USER_EMAIL")
                val intent = Intent(this, ProfileActivity::class.java)
                intent.putExtra("USER_EMAIL", userEmail)
                startActivity(intent)
            }
            R.id.nav_logout -> {
                showLogoutDialog()
                true
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
    }

    private fun showLogoutDialog() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("Logout")
        builder.setMessage("Are you sure you want to log out?")
        builder.setCancelable(true)

        builder.setPositiveButton("Yes") { dialog, _ ->
            dialog.dismiss()

            // Clear session if you have preferences
            val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
            prefs.edit { putBoolean("isLoggedIn", false) }

            // Redirect to LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()

        // Optional: style the buttons for better visibility
        dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE)
            ?.setTextColor(ContextCompat.getColor(this, R.color.teal_700))
        dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE)
            ?.setTextColor(ContextCompat.getColor(this, R.color.gray_disabled))
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    override fun onResume() {
        super.onResume()
        val userEmail = intent.getStringExtra("USER_EMAIL")
        if (userEmail != null) {
            lifecycleScope.launch {
                val user = MyApp.database.userDao().getUserByEmail(userEmail)
                if (user != null) {
                    val headerView = binding.navView.getHeaderView(0)
                    val tvUserName = headerView.findViewById<TextView>(R.id.FtvUserNameHeader)
                    val tvUserEmail = headerView.findViewById<TextView>(R.id.tvEmailHeader)
                    runOnUiThread {
                        tvUserName.text = user.name
                        tvUserEmail.text = user.email
                    }
                }
            }
        }
    }
}