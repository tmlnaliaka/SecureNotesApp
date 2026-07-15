package com.example.securenotesapp

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

/**
 * SignupActivity handles new user registration.
 */
class SignupActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        // Apply saved theme
        val sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val mode = sharedPreferences.getInt("ThemeMode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        
        if (AppCompatDelegate.getDefaultNightMode() != mode) {
            AppCompatDelegate.setDefaultNightMode(mode)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        dbHelper = DatabaseHelper(this)

        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnSignup = findViewById<Button>(R.id.btnSignup)
        val tvLoginLink = findViewById<TextView>(R.id.tvLoginLink)

        // Signup button click listener
        btnSignup.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            // Validate inputs
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, getString(R.string.empty_fields_error), Toast.LENGTH_SHORT).show()
            } else {
                // Save user credentials in SQLite
                val result = dbHelper.addUser(username, password)
                if (result != -1L) {
                    Toast.makeText(this, getString(R.string.registration_success), Toast.LENGTH_SHORT).show()
                    finish() // Back to login
                } else {
                    Toast.makeText(this, getString(R.string.registration_failed), Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Redirect back to Login
        tvLoginLink.setOnClickListener {
            finish()
        }
    }
}
