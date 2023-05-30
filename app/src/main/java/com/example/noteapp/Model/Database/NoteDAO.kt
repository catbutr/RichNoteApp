package com.example.noteapp.Model.Database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.noteapp.Model.Note
@Dao
interface NoteDAO {

    @Query("SELECT * FROM notes ORDER BY date_time DESC")
    fun getAllNotes(): LiveData<List<Note>>

    @Query("SELECT * FROM notes WHERE notebook = :notebookName")
    fun getAllNotesFromNotebook(notebookName:String):LiveData<List<Note>>

    @Query("DELETE FROM notes WHERE notebook = :notebookName")
    fun deleteAllNotesFromNotebook(notebookName:String)

    @Query("SELECT * FROM notes WHERE notebook = :notebookName AND title LIKE :searchQuery")
    fun searchDatabaseByTitle(notebookName:String, searchQuery: String):LiveData<List<Note>>

    @Query("UPDATE notes SET notebook = replace(notebook,:notebookName,:newName)")
    fun replaceNotebookTitle(notebookName:String,newName:String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: Note)

    @Delete
    suspend fun delete(note: Note)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(note: Note)
}