package com.example.noteapp.viewModel


import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.noteapp.Model.Database.NoteDAO
import com.example.noteapp.Model.Database.NoteDatabase
import com.example.noteapp.repository.NoteRepository
import junit.framework.TestCase
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NoteViewModelTest : TestCase() {
    private lateinit var db: NoteDatabase
    private lateinit var dao: NoteDAO
    private lateinit var repo: NoteRepository
    private lateinit var viewModel:NoteViewModel
    @JvmField
    @Rule
    var rule: TestRule = InstantTaskExecutorRule()
    @Before
    public override fun setUp() {
        // get context -- since this is an instrumental test it requires
        // context from the running application
        val context = ApplicationProvider.getApplicationContext<Context>()
        // initialize the db and dao variable
        db = Room.inMemoryDatabaseBuilder(context, NoteDatabase::class.java).build()
        dao = db.getNotesDao()
        repo = NoteRepository(dao)
    }
    // Override function closeDb() and annotate it with @After
    // this function will be called at last when this test class is called
    @After
    fun closeDb() {
        db.close()
    }
    @Test
    fun deleteNote() {
    }

    @Test
    fun deleteAllNotesFromNotebook() {
    }

    @Test
    fun updateNote() {
    }

    @Test
    fun replaceNotebookTitle() {
    }

    @Test
    fun addNote() {
    }

    @Test
    fun getAllNotesFromNotebook() {
    }

    @Test
    fun searchDatabaseByTitle() {
    }
}