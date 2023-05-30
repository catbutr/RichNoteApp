package com.example.noteapp.Repository

import androidx.lifecycle.LiveData
import com.example.noteapp.Model.Database.NoteDAO
import com.example.noteapp.Model.Note

class NoteRepository(private val noteDAO: NoteDAO) {

    val allNotes: LiveData<List<Note>> = noteDAO.getAllNotes()
    fun allNotesFromNotebook(notebookName:String): LiveData<List<Note>> {
        return noteDAO.getAllNotesFromNotebook(notebookName)
    }

    fun deleteAllNotesFromNotebook(notebookName:String){
        noteDAO.deleteAllNotesFromNotebook(notebookName)
    }

    fun searchDatabaseByTitle(notebookName:String,searchQuery: String):LiveData<List<Note>>{
        return noteDAO.searchDatabaseByTitle(notebookName,searchQuery)
    }

    fun replaceNotebookTitle(notebookName:String,newName:String){
        noteDAO.replaceNotebookTitle(notebookName,newName)
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