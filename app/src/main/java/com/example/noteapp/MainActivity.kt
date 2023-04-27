package com.example.noteapp

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.noteapp.Model.Note
import com.example.noteapp.ViewModel.NoteViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView

//TODO:Найти сопосб переключения категорий на лету+++
//TODO:Динамическое добавление категорий
//TODO:Найти способ добавления в активную категорию
class MainActivity : AppCompatActivity(), NoteClickInterface, NoteClickDeleteInterface,
    NavigationView.OnNavigationItemSelectedListener {

    //on below line we are creating a variable for our recycler view, exit text, button and viewmodal.
    lateinit var viewModel: NoteViewModel
    lateinit var notesRV: RecyclerView
    lateinit var addFAB: FloatingActionButton
    lateinit var drawerLayout:DrawerLayout
    lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    lateinit var selectedNotebook:String
    var isNotebookSelected = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // drawer layout instance to toggle the menu icon to open
        // drawer and back button to close drawer
        drawerLayout = findViewById<DrawerLayout>(R.id.nav_view)
        actionBarDrawerToggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open_nav, R.string.close_nav)
        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        // to make the Navigation drawer icon always appear on the action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val navView = findViewById<NavigationView>(R.id.nav_view2)
        navView.setNavigationItemSelectedListener(this)
        //on below line we are initializing all our variables.
        notesRV = findViewById(R.id.notesRV)
        addFAB = findViewById(R.id.idFAB)
        //on below line we are setting layout manager to our recycler view.
        notesRV.layoutManager = LinearLayoutManager(this)
        //on below line we are initializing our adapter class.
        val noteRVAdapter = NoteRVAdapter(this, this)
        //on below line we are setting adapter to our recycler view.
        notesRV.adapter = noteRVAdapter
        //on below line we are initializing our view modal.
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(NoteViewModel::class.java)
        //on below line we are calling all notes method from our view modal class to observer the changes on list.
        viewModel.getAllNotesFromNotebook("My Notes").observe(this) { list ->
            list?.let {
                //on below line we are updating our list.
                noteRVAdapter.updateList(it)
            }
        }
        addFAB.setOnClickListener {
            //adding a click listner for fab button and opening a new intent to add a new note.
            val intent = Intent(this@MainActivity, AddEditNoteActivity::class.java)
            startActivity(intent)
            this.finish()
        }
    }

    override fun onNoteClick(note: Note) {
        //opening a new intent and passing a data to it.
        val intent = Intent(this@MainActivity, AddEditNoteActivity::class.java)
        intent.putExtra("noteType", "Edit")
        intent.putExtra("noteTitle", note.noteTitle)
        intent.putExtra("noteDescription", note.text)
        intent.putExtra("noteId", note.id)
        startActivity(intent)
        this.finish()
    }

    override fun onDeleteIconClick(note: Note) {
        //in on note click method we are calling delete method from our viw modal to delete our not.
        viewModel.deleteNote(note)
        //displaying a toast message
        Toast.makeText(this, "${note.noteTitle} Deleted", Toast.LENGTH_LONG).show()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val noteRVAdapter = NoteRVAdapter(this, this)
        notesRV.adapter = noteRVAdapter
        when (item.itemId)
        {
            R.id.nav_my_notes-> {
                viewModel.getAllNotesFromNotebook("My Notes").observe(this) { list ->
                    list?.let {
                        //on below line we are updating our list.
                        noteRVAdapter.updateList(it)
                        Toast.makeText(this, "Deleted", Toast.LENGTH_LONG).show()
                    }
                }
            }
            R.id.nav_new_notes-> {
                viewModel.getAllNotesFromNotebook("My notes").observe(this) { list ->
                    list?.let {
                        //on below line we are updating our list.
                        noteRVAdapter.updateList(it)
                        Toast.makeText(this, "Deleted", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
        return true
    }
}