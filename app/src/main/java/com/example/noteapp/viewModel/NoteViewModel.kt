package com.example.noteapp.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.noteapp.Model.Database.NoteDatabase
import com.example.noteapp.Model.Note
import com.example.noteapp.repository.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NoteViewModel(application: Application) : AndroidViewModel(application) {
    // on below line we are creating a variable
    // for our all notes list and repository
    private val allNotes : LiveData<List<Note>>
    private val repository : NoteRepository

    // on below line we are initializing
    // our dao, repository and all notes
    init {
        val dao = NoteDatabase.getDatabase(application).getNotesDao()
        repository = NoteRepository(dao)
        allNotes = repository.allNotes
    }

    // on below line we are creating a new method for deleting a note. In this we are
    // calling a delete method from our repository to delete our note.
    fun deleteNote (note: Note) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(note)
    }

    fun deleteAllNotesFromNotebook(notebookName: String)= viewModelScope.launch(Dispatchers.IO){
        repository.deleteAllNotesFromNotebook(notebookName)
    }

    // on below line we are creating a new method for updating a note. In this we are
    // calling a update method from our repository to update our note.
    fun updateNote(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(note)
    }

    fun replaceNotebookTitle(notebookName:String,newName:String) =
        viewModelScope.launch(Dispatchers.IO){
            repository.replaceNotebookTitle(notebookName,newName)
        }


    // on below line we are creating a new method for adding a new note to our database
    // we are calling a method from our repository to add a new note.
    fun addNote(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(note)
    }

    fun getAllNotesFromNotebookDesc(notebookName: String): LiveData<List<Note>> {
        return repository.allNotesFromNotebookDesc(notebookName)
    }

    fun getAllNotesFromNotebookAsc(notebookName: String): LiveData<List<Note>> {
        return repository.allNotesFromNotebookAsc(notebookName)
    }

    fun getAllFavourite():LiveData<List<Note>>{
        return repository.allFavourite()
    }

    fun searchDatabaseByTitle(notebookName: String,searchQuery: String):LiveData<List<Note>>{
        return repository.searchDatabaseByTitle(notebookName,searchQuery)
    }
}