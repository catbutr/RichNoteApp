package com.example.noteapp.repository

import androidx.lifecycle.LiveData
import com.example.noteapp.Model.Database.NoteDAO
import com.example.noteapp.Model.Note

class NoteRepository(private val noteDAO: NoteDAO) {

    val allNotes: LiveData<List<Note>> = noteDAO.getAllNotes()

    fun allNotes(): LiveData<List<Note>> {
        return noteDAO.getAllNotes()
    }
    fun allNotesFromNotebookDesc(notebookName:String): LiveData<List<Note>> {
        return noteDAO.getAllNotesFromNotebookByTimeDesc(notebookName)
    }

    fun allNotesFromNotebookAsc(notebookName:String): LiveData<List<Note>> {
        return noteDAO.getAllNotesFromNotebookByTimeAsc(notebookName)
    }

    fun allFavourite():LiveData<List<Note>>{
        return noteDAO.getAllFavourites()
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