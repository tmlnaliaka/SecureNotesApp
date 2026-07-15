package com.example.securenotesapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * DatabaseHelper class to manage SQLite database operations for Users and Notes.
 */
class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "SecureNotes.db"
        private const val DATABASE_VERSION = 1

        // User table constants
        private const val TABLE_USERS = "users"
        private const val COLUMN_USER_ID = "id"
        private const val COLUMN_USER_USERNAME = "username"
        private const val COLUMN_USER_PASSWORD = "password"

        // Notes table constants
        private const val TABLE_NOTES = "notes"
        private const val COLUMN_NOTE_ID = "id"
        private const val COLUMN_NOTE_TITLE = "title"
        private const val COLUMN_NOTE_SUBTITLE = "subtitle"
        private const val COLUMN_NOTE_CONTENT = "content"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        // Create Users table
        val createUsersTable = ("CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USER_USERNAME + " TEXT,"
                + COLUMN_USER_PASSWORD + " TEXT" + ")")
        db?.execSQL(createUsersTable)

        // Create Notes table
        val createNotesTable = ("CREATE TABLE " + TABLE_NOTES + "("
                + COLUMN_NOTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NOTE_TITLE + " TEXT,"
                + COLUMN_NOTE_SUBTITLE + " TEXT,"
                + COLUMN_NOTE_CONTENT + " TEXT" + ")")
        db?.execSQL(createNotesTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NOTES")
        onCreate(db)
    }

    // --- User Authentication Methods ---

    /**
     * Registers a new user in the database.
     */
    fun addUser(username: String, password: String): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_USER_USERNAME, username)
        values.put(COLUMN_USER_PASSWORD, password)
        val success = db.insert(TABLE_USERS, null, values)
        db.close()
        return success
    }

    /**
     * Checks if a user exists with the given credentials.
     */
    fun checkUser(username: String, password: String): Boolean {
        val db = this.readableDatabase
        val columns = arrayOf(COLUMN_USER_ID)
        val selection = "$COLUMN_USER_USERNAME = ? AND $COLUMN_USER_PASSWORD = ?"
        val selectionArgs = arrayOf(username, password)
        val cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null)
        val count = cursor.count
        cursor.close()
        db.close()
        return count > 0
    }

    // --- Notes Management Methods ---

    /**
     * Adds a new note to the database.
     */
    fun addNote(title: String, subtitle: String, content: String): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_NOTE_TITLE, title)
        values.put(COLUMN_NOTE_SUBTITLE, subtitle)
        values.put(COLUMN_NOTE_CONTENT, content)
        val success = db.insert(TABLE_NOTES, null, values)
        db.close()
        return success
    }

    /**
     * Retrieves all notes from the database.
     */
    fun getAllNotes(): List<Note> {
        val noteList = mutableListOf<Note>()
        val db = this.readableDatabase
        val selectQuery = "SELECT * FROM $TABLE_NOTES ORDER BY $COLUMN_NOTE_ID DESC"
        val cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NOTE_ID))
                val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTE_TITLE))
                val subtitle = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTE_SUBTITLE))
                val content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTE_CONTENT))
                noteList.add(Note(id, title, subtitle, content))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return noteList
    }

    /**
     * Retrieves a single note by its ID.
     */
    fun getNoteById(id: Int): Note? {
        val db = this.readableDatabase
        val selection = "$COLUMN_NOTE_ID = ?"
        val selectionArgs = arrayOf(id.toString())
        val cursor = db.query(TABLE_NOTES, null, selection, selectionArgs, null, null, null)
        
        var note: Note? = null
        if (cursor.moveToFirst()) {
            val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTE_TITLE))
            val subtitle = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTE_SUBTITLE))
            val content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTE_CONTENT))
            note = Note(id, title, subtitle, content)
        }
        cursor.close()
        db.close()
        return note
    }

    /**
     * Updates an existing note.
     */
    fun updateNote(id: Int, title: String, subtitle: String, content: String): Int {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_NOTE_TITLE, title)
        values.put(COLUMN_NOTE_SUBTITLE, subtitle)
        values.put(COLUMN_NOTE_CONTENT, content)
        
        val success = db.update(TABLE_NOTES, values, "$COLUMN_NOTE_ID = ?", arrayOf(id.toString()))
        db.close()
        return success
    }

    /**
     * Deletes a note by its ID.
     */
    fun deleteNote(id: Int): Int {
        val db = this.writableDatabase
        val success = db.delete(TABLE_NOTES, "$COLUMN_NOTE_ID = ?", arrayOf(id.toString()))
        db.close()
        return success
    }

    /**
     * Deletes all notes from the database.
     */
    fun clearAllNotes(): Int {
        val db = this.writableDatabase
        val success = db.delete(TABLE_NOTES, null, null)
        db.close()
        return success
    }
}
