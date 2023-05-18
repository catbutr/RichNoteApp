package com.example.noteapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.noteapp.Model.Note
import com.example.noteapp.ViewModel.NoteViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView


//TODO:Возвращение на активную категорию после выхода с экрана заметки
//TODO:Динамическое добавление категорий
//TODO:Поиск и сортировка
class MainActivity : AppCompatActivity(), NoteClickInterface, SearchView.OnQueryTextListener,
    NoteClickDeleteInterface, NavigationView.OnNavigationItemSelectedListener {

    //ViewModel
    lateinit var viewModel: NoteViewModel
    //UI elements
    lateinit var notesRV: RecyclerView
    lateinit var addFAB: FloatingActionButton
    lateinit var drawerLayout:DrawerLayout
    lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    //Working variables
    private var selectedNotebook:String = "My Notes"
    var addedDrawerMenuItems = mutableListOf<String>()
    val noteRVAdapter = NoteRVAdapter(this, this)
    private fun addNewNotebook(title:String)
    {
        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        var myEdit: SharedPreferences.Editor = sharedPref.edit()
        var navigationView = findViewById<NavigationView>(R.id.nav_view2)
        addedDrawerMenuItems.add(title)
        myEdit.putStringSet("notebooks",HashSet(addedDrawerMenuItems)).apply()
        var existingMenu = navigationView.menu
        existingMenu.add(R.id.nav_notebooks, Menu.NONE,0,title)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        var navigationView = findViewById<NavigationView>(R.id.nav_view2)
        outState.putString("selectedNotebook", selectedNotebook)
        outState.putStringArrayList("newItems",java.util.ArrayList(addedDrawerMenuItems))
        super.onSaveInstanceState(outState)
    }
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        var navigationView = findViewById<NavigationView>(R.id.nav_view2)
        super.onRestoreInstanceState(savedInstanceState)
        selectedNotebook = savedInstanceState.getString("selectedNotebook").toString()
        var newItems = savedInstanceState.getStringArrayList("newItems")
        if (newItems!!.isNotEmpty()) {
            for (i in newItems.indices) {
                navigationView.menu.add(R.id.nav_notebooks, Menu.NONE, 0, newItems!![i])
            }
        }
        addedDrawerMenuItems=newItems
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        //Data store
        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        var myEdit: SharedPreferences.Editor = sharedPref.edit()
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
        val savedNotebooks = sharedPref.getStringSet("notebooks",null)
        val savedNotebooksList = savedNotebooks!!.toMutableList()
        addedDrawerMenuItems.addAll(savedNotebooksList)
        if (savedNotebooks.isNotEmpty()) {
            for (i in savedNotebooks.indices) {
                navView.menu.add(R.id.nav_notebooks, Menu.NONE, 0, savedNotebooksList[i])
            }
        }
        //on below line we are initializing all our variables.
        notesRV = findViewById(R.id.notesRV)
        addFAB = findViewById(R.id.idFAB)
        //on below line we are setting layout manager to our recycler view.
        notesRV.layoutManager = LinearLayoutManager(this)
        //on below line we are setting adapter to our recycler view.
        notesRV.adapter = noteRVAdapter
        //on below line we are initializing our view modal.
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(NoteViewModel::class.java)
        Toast.makeText(this, intent.getStringExtra("lastNotebook")
            .toString() + " in main",Toast.LENGTH_SHORT).show()
        if (intent.getBooleanExtra("viewFlag",false)) {
            selectedNotebook = intent.getStringExtra("previousNotebook").toString()
            Toast.makeText(this, "$selectedNotebook is current notebook in main", Toast.LENGTH_LONG).show()
        }
        //on below line we are calling all notes method from our view modal class to observer the changes on list.
        viewModel.getAllNotesFromNotebook(selectedNotebook).observe(this) { list ->
            list?.let {
                //on below line we are updating our list.
                noteRVAdapter.updateList(it)
            }
        }
        addFAB.setOnClickListener {
            //adding a click listner for fab button and opening a new intent to add a new note.
            val intent = Intent(this@MainActivity, AddEditNoteActivity::class.java)
            intent.putExtra("selectedNotebook",selectedNotebook)
            startActivity(intent)
            this.finish()
        }
    }

    override fun onNoteClick(note: Note) {
        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        var myEdit: SharedPreferences.Editor = sharedPref.edit()
        myEdit.putStringSet("notebooks",HashSet(addedDrawerMenuItems)).apply()
        //opening a new intent and passing a data to it.
        val intent = Intent(this@MainActivity, AddEditNoteActivity::class.java)
        intent.putExtra("noteType", "Edit")
        intent.putExtra("noteTitle", note.noteTitle)
        intent.putExtra("noteDescription", note.noteText)
        intent.putExtra("noteId", note.id)
        intent.putExtra("noteColor",note.backgroundColor)
        intent.putExtra("selectedNotebook",selectedNotebook)
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
            R.id.nav_settings-> Toast.makeText(this,
                "Settings not implemented yet",Toast.LENGTH_SHORT).show()
            R.id.nav_new_notebook-> {
                val currentLayout = LayoutInflater.from(this)
                val newLayout = currentLayout.inflate(R.layout.edit_dialogue, null)
                val dialogBuilder = AlertDialog.Builder(this)
                dialogBuilder.setView(newLayout)
                val userInput = newLayout.findViewById<EditText>(R.id.input_text)
                dialogBuilder.setCancelable(false)
                dialogBuilder.setPositiveButton("OK")
                { dialog, which -> addNewNotebook(userInput.text.toString()) }
                dialogBuilder.setNegativeButton("Cancel")
                {dialog,which->dialog.cancel()}
                dialogBuilder.show()
            }
            else->{
                viewModel.getAllNotesFromNotebook(item.title.toString()).observe(this) { list ->
                    list?.let {
                        //on below line we are updating our list.
                        noteRVAdapter.updateList(it)
                        selectedNotebook = item.title.toString()
                    }
                }
            }
        }
        return true
    }

    // method to inflate the options menu when
    // the user opens the menu for the first time
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_main_action_bar, menu)
        val search = menu.findItem(R.id.menu_search)
        val searchView = search?.actionView as? SearchView
        searchView?.setOnQueryTextListener(this)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(query: String?): Boolean {
        if(query != null){
            searchDatabase(selectedNotebook,query)
        }
        return true
    }

    // We have just created this function for searching our database
    private fun searchDatabase(notebook:String, query: String) {
        // %" "% because our custom sql query will require that
        val searchQuery = "%$query%"
        notesRV.adapter = noteRVAdapter

        viewModel.searchDatabaseByTitle(notebook,searchQuery).observe(this) { list ->
            list.let {
                noteRVAdapter.updateList(it)
            }
        }
    }

}