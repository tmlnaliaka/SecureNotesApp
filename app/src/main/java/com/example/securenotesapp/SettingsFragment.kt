package com.example.securenotesapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment

/**
 * Fragment to manage application settings, profile, and data.
 */
class SettingsFragment : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        sharedPreferences = requireActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE)
        dbHelper = DatabaseHelper(requireContext())

        val tvUsername = view.findViewById<TextView>(R.id.tvUsernameDisplay)
        val rgTheme = view.findViewById<RadioGroup>(R.id.rgTheme)
        val rbLight = view.findViewById<RadioButton>(R.id.rbLight)
        val rbDark = view.findViewById<RadioButton>(R.id.rbDark)
        val rbSystem = view.findViewById<RadioButton>(R.id.rbSystem)
        val btnClearData = view.findViewById<Button>(R.id.btnClearData)
        val btnLogout = view.findViewById<Button>(R.id.btnLogout)

        // 1. Display Current User
        val username = sharedPreferences.getString("CurrentUsername", "User")
        tvUsername.text = getString(R.string.settings_logged_in_as, username)

        // 2. Theme Management
        val savedTheme = sharedPreferences.getInt("ThemeMode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        when (savedTheme) {
            AppCompatDelegate.MODE_NIGHT_NO -> rbLight.isChecked = true
            AppCompatDelegate.MODE_NIGHT_YES -> rbDark.isChecked = true
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> rbSystem.isChecked = true
        }

        rgTheme.setOnCheckedChangeListener { _, checkedId ->
            val mode = when (checkedId) {
                R.id.rbLight -> AppCompatDelegate.MODE_NIGHT_NO
                R.id.rbDark -> AppCompatDelegate.MODE_NIGHT_YES
                else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }
            sharedPreferences.edit().putInt("ThemeMode", mode).apply()
            AppCompatDelegate.setDefaultNightMode(mode)
        }

        // 3. Data Management (Clear All)
        btnClearData.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.dialog_clear_title))
                .setMessage(getString(R.string.dialog_clear_msg))
                .setPositiveButton(getString(R.string.dialog_clear_pos)) { _, _ ->
                    dbHelper.clearAllNotes()
                    Toast.makeText(context, getString(R.string.settings_clear_all), Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton(getString(R.string.dialog_cancel), null)
                .show()
        }

        // 4. Logout Feature
        btnLogout.setOnClickListener {
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        return view
    }
}
