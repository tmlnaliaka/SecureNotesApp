package com.example.securenotesapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment

/**
 * Fragment to display the list of notes.
 */
class NotesFragment : Fragment() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var lvNotes: ListView
    private lateinit var emptyState: View
    private var noteList: List<Note> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notes, container, false)
        lvNotes = view.findViewById(R.id.lvNotes)
        emptyState = view.findViewById(R.id.emptyState)
        dbHelper = DatabaseHelper(requireContext())

        // Handle clicking on a note to open it
        lvNotes.setOnItemClickListener { _, _, position, _ ->
            val selectedNote = noteList[position]
            val intent = Intent(requireContext(), AddNoteActivity::class.java)
            intent.putExtra("NOTE_ID", selectedNote.id)
            startActivity(intent)
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        loadNotes()
    }

    private fun loadNotes() {
        noteList = dbHelper.getAllNotes()
        if (noteList.isEmpty()) {
            emptyState.visibility = View.VISIBLE
            lvNotes.visibility = View.GONE
        } else {
            emptyState.visibility = View.GONE
            lvNotes.visibility = View.VISIBLE
            lvNotes.adapter = NoteAdapter(noteList)
        }
    }

    /**
     * Custom adapter for the ListView to show title and subtitle.
     */
    inner class NoteAdapter(private val notes: List<Note>) : BaseAdapter() {
        override fun getCount(): Int = notes.size
        override fun getItem(position: Int): Any = notes[position]
        override fun getItemId(position: Int): Long = notes[position].id.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_note, parent, false)
            val tvTitle = view.findViewById<TextView>(R.id.tvNoteTitle)
            val tvSubtitle = view.findViewById<TextView>(R.id.tvNoteSubtitle)

            val note = notes[position]
            tvTitle.text = note.title
            tvSubtitle.text = note.subtitle

            return view
        }
    }
}
