package com.example.securenotesapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

/**
 * LoginActivity handles user authentication.
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        // Apply saved theme
        val sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val mode = sharedPreferences.getInt("ThemeMode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        
        if (AppCompatDelegate.getDefaultNightMode() != mode) {
            AppCompatDelegate.setDefaultNightMode(mode)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        dbHelper = DatabaseHelper(this)

        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvSignupLink = findViewById<TextView>(R.id.tvSignupLink)

        // Login button click listener
        btnLogin.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            // Validate inputs
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, getString(R.string.empty_fields_error), Toast.LENGTH_SHORT).show()
            } else {
                // Check credentials in SQLite
                if (dbHelper.checkUser(username, password)) {
                    // Save username for settings
                    sharedPreferences.edit().putString("CurrentUsername", username).apply()

                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, getString(R.string.invalid_credentials), Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Redirect to Signup screen
        tvSignupLink.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
    }
}
