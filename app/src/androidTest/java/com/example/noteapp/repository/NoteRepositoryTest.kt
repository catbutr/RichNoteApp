package com.example.noteapp.repository

import android.content.Context
import android.graphics.Color
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.map
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.noteapp.Model.Database.NoteDAO
import com.example.noteapp.Model.Database.NoteDatabase
import com.example.noteapp.Model.Note
import com.jraska.livedata.test
import junit.framework.TestCase
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.rules.TestRule
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class NoteRepositoryTest : TestCase() {
    private lateinit var db: NoteDatabase
    private lateinit var dao: NoteDAO
    private lateinit var repo:NoteRepository
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
    fun getNotesFromNotebook() = runBlocking<Unit>{
        val expectedNotebook1 = "TES1T"
        val expectedNotebook2 = "TEST2"
        val testNote1 = Note("Test1","Test","32", Color.GREEN,expectedNotebook1)
        val testNote2 = Note("Test2","Test","32", Color.GREEN,expectedNotebook2)
        repo.insert(testNote1)
        repo.insert(testNote2)
        val notesTest1 = repo.allNotesFromNotebookAsc(expectedNotebook1)
        val notesTest2 = repo.allNotesFromNotebookDesc(expectedNotebook2)
        notesTest1.map { it.contains(testNote1) }
            .test().assertValue(true)
        notesTest2.map { it.contains(testNote2) }
            .test().assertValue(true)
    }

    @Test
    fun getFavouriteNotes() = runBlocking<Unit> {
        val testNote1 = Note("Test1","Test","32", Color.GREEN,"Test1")
        testNote1.isFavourite = true
        val testNote2 = Note("Test2","Test","32", Color.GREEN,"Test2")
        testNote2.isFavourite = true
        repo.insert(testNote1)
        repo.insert(testNote2)
        val notesTest1 = repo.allFavourite()
        notesTest1.map { it.contains(testNote1) && it.contains(testNote2) }
            .test().assertValue(true)
    }

    @Test
    fun deleteAllNotesFromNotebook() = runBlocking<Unit>{
        val testNote = Note("Test","Test","32", Color.GREEN,"TEST")
        val notes = repo.allNotes()
        repo.deleteAllNotesFromNotebook("TEST")
        notes.map { it.contains(testNote) }
            .test().assertValue(false)
    }

    @Test
    fun searchDatabaseByTitle() = runBlocking<Unit>{
        val testNote = Note("Test","Test","32", Color.GREEN,"TEST")
        val notes = repo.allNotes()
        repo.insert(testNote)
        val searchResultTrue = repo.searchDatabaseByTitle("TEST","dasfsddfs")
        searchResultTrue.map { it.isEmpty() }
            .test().assertValue(true)
        val searchResultFalse = repo.searchDatabaseByTitle("TEST","Test")
        searchResultFalse.map { it.isEmpty() }
            .test().assertValue(false)

    }

    @Test
    fun replaceNotebookTitle() = runBlocking<Unit> {
        val testNote = Note("Test","Test","32", Color.GREEN,"TEST")
        val notes = repo.allNotes()
        repo.insert(testNote)
        repo.replaceNotebookTitle("TEST","TEST2")
        notes.map { it.last().noteBook }
            .test().assertValue("TEST2")
    }

    @Test
    fun insert() = runBlocking<Unit> {
        val testNote = Note("Test","Test","32", Color.GREEN,"TEST")
        val notes = repo.allNotes()
        repo.insert(testNote)
        notes.map { it.last() }
            .test().assertValue(testNote)
    }


    @Test
    fun delete() = runBlocking<Unit> {
        val testNote = Note("Test","Test","32", Color.GREEN,"TEST")
        val notes = repo.allNotes()
        repo.delete(testNote)
        notes.map { it.contains(testNote) }
            .test().assertValue(false)
    }

    @Test
    fun update() = runBlocking<Unit>{
        val testNote1 = Note("Test1","Test","32", Color.GREEN,"TEST")
        testNote1.id = 25
        val testNote2 = Note("Test2","Test","32", Color.BLUE,"TEST")
        repo.insert(testNote1)
        testNote2.isFavourite = true
        testNote2.id = testNote1.id
        repo.update(testNote2)
        val notes = repo.allNotes()
        notes.map { it[0].isFavourite }
            .test().assertValue(true)
    }
}