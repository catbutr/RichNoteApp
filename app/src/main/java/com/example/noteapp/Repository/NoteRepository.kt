package com.example.noteapp.Repository

import androidx.lifecycle.LiveData
import com.example.noteapp.Model.Database.NoteDAO
import com.example.noteapp.Model.Note

class NoteRepository(private val noteDAO: NoteDAO) {

    val allNotes: LiveData<List<Note>> = noteDAO.getAllNotes()
    fun allNotesFromNotebook(notebookName:String): LiveData<List<Note>> {
        return noteDAO.getAllNotesFromNotebook(notebookName)
    }

    fun searchDatabaseByTitle(notebookName:String,searchQuery: String):LiveData<List<Note>>{
        return noteDAO.searchDatabaseByTitle(notebookName,searchQuery)

    }

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