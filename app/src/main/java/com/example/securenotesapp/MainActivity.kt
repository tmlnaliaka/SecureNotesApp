package com.example.securenotesapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * MainActivity serves as the host for Notes and Settings fragments.
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Apply saved theme before calling super.onCreate
        val sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val mode = sharedPreferences.getInt("ThemeMode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        
        if (AppCompatDelegate.getDefaultNightMode() != mode) {
            AppCompatDelegate.setDefaultNightMode(mode)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val fab = findViewById<FloatingActionButton>(R.id.fab_add_note)

        // Set default fragment
        loadFragment(NotesFragment())

        // Bottom navigation listener
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_notes -> {
                    loadFragment(NotesFragment())
                    fab.show()
                    true
                }
                R.id.nav_settings -> {
                    loadFragment(SettingsFragment())
                    fab.hide()
                    true
                }
                else -> false
            }
        }

        // Floating Action Button to add notes
        fab.setOnClickListener {
            val intent = Intent(this, AddNoteActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
