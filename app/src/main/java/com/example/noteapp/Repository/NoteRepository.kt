package com.example.noteapp.Repository

import androidx.lifecycle.LiveData
import com.example.noteapp.Model.Note
import com.example.noteapp.Model.Database.NoteDAO

class NoteRepository(private val noteDAO: NoteDAO) {

    val allNotes: LiveData<List<Note>> = noteDAO.getAllNotes()

    suspend fun insert(note: Note) {
        noteDAO.insert(note)
    }

    suspend fun delete(note: Note) {
        noteDAO.delete(note)
    }

    suspend fun update(note: Note) {
        noteDAO.update(note)
    }

}