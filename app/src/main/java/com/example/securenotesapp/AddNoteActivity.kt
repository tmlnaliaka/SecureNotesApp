package com.example.securenotesapp

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

/**
 * Activity to add a new note or edit an existing one.
 */
class AddNoteActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private var noteId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        val sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val mode = sharedPreferences.getInt("ThemeMode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        
        if (AppCompatDelegate.getDefaultNightMode() != mode) {
            AppCompatDelegate.setDefaultNightMode(mode)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)

        dbHelper = DatabaseHelper(this)

        val etTitle = findViewById<EditText>(R.id.etNoteTitle)
        val etSubtitle = findViewById<EditText>(R.id.etNoteSubtitle)
        val etContent = findViewById<EditText>(R.id.etNoteContent)
        val btnSave = findViewById<Button>(R.id.btnSaveNote)
        val btnDelete = findViewById<Button>(R.id.btnDeleteNote)
        val btnClose = findViewById<ImageButton>(R.id.btnClose)
        val tvActivityTitle = findViewById<TextView>(R.id.tvActivityTitle)

        noteId = intent.getIntExtra("NOTE_ID", -1)
        if (noteId != -1) {
            loadNoteData(etTitle, etSubtitle, etContent)
            btnSave.text = getString(R.string.title_edit_note)
            tvActivityTitle.text = getString(R.string.title_edit_note)
            btnDelete.visibility = android.view.View.VISIBLE
        }

        btnClose.setOnClickListener { finish() }

        btnDelete.setOnClickListener {
            val result = dbHelper.deleteNote(noteId)
            if (result > 0) {
                Toast.makeText(this, getString(R.string.note_deleted), Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        btnSave.setOnClickListener {
            val title = etTitle.text.toString().trim()
            val subtitle = etSubtitle.text.toString().trim()
            val content = etContent.text.toString().trim()

            if (title.isEmpty()) {
                Toast.makeText(this, getString(R.string.error_title_empty), Toast.LENGTH_SHORT).show()
            } else {
                if (noteId == -1) {
                    val result = dbHelper.addNote(title, subtitle, content)
                    if (result != -1L) {
                        Toast.makeText(this, getString(R.string.note_saved), Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this, getString(R.string.error_save), Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val result = dbHelper.updateNote(noteId, title, subtitle, content)
                    if (result > 0) {
                        Toast.makeText(this, getString(R.string.note_updated), Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this, getString(R.string.error_save), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun loadNoteData(etTitle: EditText, etSubtitle: EditText, etContent: EditText) {
        val note = dbHelper.getNoteById(noteId)
        note?.let {
            etTitle.setText(it.title)
            etSubtitle.setText(it.subtitle)
            etContent.setText(it.content)
        }
    }
}
