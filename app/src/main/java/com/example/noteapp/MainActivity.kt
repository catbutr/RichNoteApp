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
//TODO:Найти причину почему динамически созданные элементы отказывают делать элемент
// изменения видимым
@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity(), NoteClickInterface, SearchView.OnQueryTextListener,
    NoteClickDeleteInterface, NavigationView.OnNavigationItemSelectedListener {

    //ViewModel
    private lateinit var viewModel: NoteViewModel
    //UI elements
    private lateinit var notesRV: RecyclerView
    private lateinit var addFAB: FloatingActionButton
    private lateinit var drawerLayout:DrawerLayout
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    //Working variables
    private var selectedTheme:Int = R.style.Theme_NoteApp
    private var selectedNotebook:String = "My Notes"
    private var addedDrawerMenuItems = mutableListOf<String>()
    private var selectedFont = R.style.Roboto
    private lateinit var noteRVAdapter:NoteRVAdapter
    private var latestSelectedNotebookID = R.id.nav_my_notes
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("selectedNotebook", selectedNotebook)
        outState.putStringArrayList("newItems",java.util.ArrayList(addedDrawerMenuItems))
        outState.putInt("selectedFont",selectedFont)
        super.onSaveInstanceState(outState)
    }
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        val navigationView = findViewById<NavigationView>(R.id.nav_view2)
        super.onRestoreInstanceState(savedInstanceState)
        selectedNotebook = savedInstanceState.getString("selectedNotebook").toString()
        selectedFont = savedInstanceState.getInt("selectedFont")
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        //Data store
        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        val chosenTheme = sharedPref.getInt("chosenTheme", MODE_PRIVATE)
        selectedFont = sharedPref.getInt("selectedFont", MODE_PRIVATE)
        noteRVAdapter = NoteRVAdapter(this, this,
            selectedFont)
        setTheme(chosenTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val currentSpace = this
        // drawer layout instance to toggle the menu icon to open
        // drawer and back button to close drawer
        drawerLayout = findViewById(R.id.nav_view)
        actionBarDrawerToggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open_nav, R.string.close_nav)
        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        // to make the Navigation drawer icon always appear on the action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val navView = findViewById<NavigationView>(R.id.nav_view2)
        navView.setItemTextAppearance(selectedFont)
        navView.setNavigationItemSelectedListener(this)
        navView.menu.findItem(R.id.nav_change_notebook).isVisible = false
        val savedNotebooks = sharedPref.getStringSet("notebooks",null)
        val savedNotebooksList = savedNotebooks!!.toMutableList()
        addedDrawerMenuItems.addAll(savedNotebooksList)
        if (savedNotebooks.isNotEmpty()) {
            for (i in savedNotebooks.indices) {
                navView.menu.add(R.id.nav_notebooks, (0..10000000).random(), 1, savedNotebooksList[i])
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
        )[NoteViewModel::class.java]
        Toast.makeText(this, intent.getStringExtra("lastNotebook")
            .toString() + " in main",Toast.LENGTH_SHORT).show()
        if (intent.getBooleanExtra("viewFlag",false)) {
            selectedNotebook = intent.getStringExtra("previousNotebook").toString()
            Toast.makeText(this, "$selectedNotebook is current notebook in main", Toast.LENGTH_LONG).show()
        }
        //on below line we are calling all notes method from our view modal class to observer the changes on list.
        uploadNotesToRecyclerView(noteRVAdapter,viewModel,selectedNotebook)
        addFAB.setOnClickListener {
            //adding a click listener for fab button and opening a new intent to add a new note.
            val intent = Intent(this@MainActivity, AddEditNoteActivity::class.java)
            intent.putExtra("selectedNotebook",selectedNotebook)
            startActivity(intent)
            this.finish()
        }
    }

    override fun onNoteClick(note: Note) {
        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        val myEdit: SharedPreferences.Editor = sharedPref.edit()
        myEdit.putStringSet("notebooks",HashSet(addedDrawerMenuItems)).apply()
        //opening a new intent and passing a data to it.
        val intent = Intent(this@MainActivity, AddEditNoteActivity::class.java)
        intent.putExtra("noteType", "Edit")
        intent.putExtra("noteTitle", note.noteTitle)
        intent.putExtra("noteDescription", note.noteText)
        intent.putExtra("noteId", note.id)
        intent.putExtra("noteColor",note.backgroundColor)
        intent.putExtra("selectedNotebook",selectedNotebook)
        intent.putExtra("selectedTheme", sharedPref.getInt("chosenTheme", MODE_PRIVATE))
        intent.putExtra("selectedFont",selectedFont)
        Toast.makeText(this, selectedTheme.toString(), Toast.LENGTH_SHORT).show()
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
        val noteRVAdapter = NoteRVAdapter(this, this,selectedFont)
        notesRV.adapter = noteRVAdapter
        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        val myEdit: SharedPreferences.Editor = sharedPref.edit()
        val themeChoice = arrayOf<CharSequence>("Standard", "Red","Blue",
            "Green","Yellow","Orange",
        "Purple")
        val fontChoice = arrayOf<CharSequence>("Roboto","Aldrich","Garamond","PTSans","Stencil")
        val navView = findViewById<NavigationView>(R.id.nav_view2)
//        val viewModel = ViewModelProvider(
//            this,
//            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
//        )[NoteViewModel::class.java]
        when (item.itemId)
        {
            R.id.nav_theme-> {
                val myAlertDialog: AlertDialog.Builder = AlertDialog.Builder(this)
                myAlertDialog.setTitle("Select Theme")
                myAlertDialog.setItems(themeChoice) { _, item ->
                    when {
                        themeChoice[item] == "Standard" -> {
                            myEdit.putInt("chosenTheme", R.style.Theme_NoteApp).apply()
                            setTheme(R.style.Theme_NoteApp)
                            recreate()
                        }
                        themeChoice[item] == "Red" -> {
                            myEdit.putInt("chosenTheme", R.style.Theme_NoteApp_Red).apply()
                            setTheme(R.style.Theme_NoteApp)
                            recreate()
                        }
                        themeChoice[item] == "Blue" -> {
                            myEdit.putInt("chosenTheme", R.style.Theme_NoteApp_Blue).apply()
                            setTheme(R.style.Theme_NoteApp)
                            recreate()
                        }
                        themeChoice[item] == "Green" -> {
                            myEdit.putInt("chosenTheme", R.style.Theme_NoteApp_Green).apply()
                            setTheme(R.style.Theme_NoteApp)
                            recreate()
                        }
                        themeChoice[item] == "Yellow" -> {
                            myEdit.putInt("chosenTheme", R.style.Theme_NoteApp_Yellow).apply()
                            setTheme(R.style.Theme_NoteApp)
                            recreate()
                        }
                        themeChoice[item] == "Orange" -> {
                            myEdit.putInt("chosenTheme", R.style.Theme_NoteApp_Orange).apply()
                            setTheme(R.style.Theme_NoteApp)
                            recreate()
                        }
                        themeChoice[item] == "Purple" -> {
                            myEdit.putInt("chosenTheme", R.style.Theme_NoteApp_Purple).apply()
                            setTheme(R.style.Theme_NoteApp)
                            recreate()
                        }
                    }
                }
                myAlertDialog.show()
            }
            R.id.nav_new_notebook-> {
                val currentLayout = LayoutInflater.from(this)
                val newLayout = currentLayout.inflate(R.layout.edit_dialogue, null)
                val dialogBuilder = AlertDialog.Builder(this)
                dialogBuilder.setView(newLayout)
                val userInput = newLayout.findViewById<EditText>(R.id.input_text)
                dialogBuilder.setCancelable(false)
                dialogBuilder.setPositiveButton("OK") { _, _ ->
                    addNewNotebook(userInput.text.toString()) }
                dialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
                    dialog.cancel()}
                dialogBuilder.show()
            }
            R.id.nav_my_notes->{
                navView.menu.findItem(R.id.nav_change_notebook).isVisible = false
                updateRecyclerView(item,noteRVAdapter)
                Toast.makeText(this,"Nav_My_Notes",Toast.LENGTH_SHORT).show()
            }
            R.id.nav_settings->{
                val myAlertDialog: AlertDialog.Builder = AlertDialog.Builder(this)
                myAlertDialog.setTitle("Do you really want to delete the notebook" +
                        "and all the notes?")
                myAlertDialog.setPositiveButton("Accept") {_, _ ->
                    removeNotebook(selectedNotebook)
                    viewModel.deleteAllNotesFromNotebook(selectedNotebook)
                    selectedNotebook = "My Notes"
                    uploadNotesToRecyclerView(noteRVAdapter,viewModel,selectedNotebook)
                }
                myAlertDialog.setNegativeButton("Cancel"){_, _ -> }
                myAlertDialog.show()
            }
            R.id.nav_change_notebook->{
                val currentLayout = LayoutInflater.from(this)
                val newLayout = currentLayout.inflate(R.layout.edit_dialogue, null)
                val dialogBuilder = AlertDialog.Builder(this)
                dialogBuilder.setView(newLayout)
                val userInput = newLayout.findViewById<EditText>(R.id.input_text)
                dialogBuilder.setCancelable(false)
                dialogBuilder.setPositiveButton("OK") { _, _ ->
                    val newText = userInput.text.toString()
                    var isDuplicateInList = false
                    for (i in addedDrawerMenuItems.indices){
                        if (newText == addedDrawerMenuItems[i]){
                            Toast.makeText(this,"THIS NOTEBOOK ALREADY EXIST",
                                Toast.LENGTH_SHORT).show()
                            isDuplicateInList = true
                            break
                        }
                    }
                    Toast.makeText(this,isDuplicateInList.toString(),Toast.LENGTH_SHORT).show()
                    if (!isDuplicateInList){
                    viewModel.replaceNotebookTitle(selectedNotebook,newText)
                    removeNotebook(selectedNotebook)
                    addNewNotebook(newText)
                    selectedNotebook = newText
                    uploadNotesToRecyclerView(noteRVAdapter,viewModel,selectedNotebook)
                    }
                }
                dialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
                    dialog.cancel()
                }
                dialogBuilder.show()

            }
            R.id.nav_change_font->{
                val myAlertDialog: AlertDialog.Builder = AlertDialog.Builder(this)
                myAlertDialog.setTitle("Select Font")
                myAlertDialog.setItems(fontChoice){_, item ->
                    when{
                        fontChoice[item] == "Roboto" -> {
                            selectedFont = R.style.Roboto
                            myEdit.putInt("selectedFont", selectedFont).apply()
                            changeMainFont(selectedFont)
                            uploadNotesToRecyclerView(noteRVAdapter,viewModel,selectedNotebook)
                            recreate()
                        }
                        fontChoice[item] == "Aldrich" -> {
                            selectedFont = R.style.Aldrich
                            myEdit.putInt("selectedFont", selectedFont).apply()
                            changeMainFont(selectedFont)
                            uploadNotesToRecyclerView(noteRVAdapter,viewModel,selectedNotebook)
                            recreate()
                        }
                        fontChoice[item] == "Garamond" -> {
                            selectedFont = R.style.Garamond
                            myEdit.putInt("selectedFont", selectedFont).apply()
                            changeMainFont(selectedFont)
                            uploadNotesToRecyclerView(noteRVAdapter,viewModel,selectedNotebook)
                            recreate()
                        }
                        fontChoice[item] == "PTSans" -> {
                            selectedFont = R.style.PTSans
                            myEdit.putInt("selectedFont", selectedFont).apply()
                            changeMainFont(selectedFont)
                            uploadNotesToRecyclerView(noteRVAdapter,viewModel,selectedNotebook)
                            recreate()
                        }
                        fontChoice[item] == "Stencil" -> {
                            selectedFont = R.style.Stencil
                            myEdit.putInt("selectedFont", selectedFont).apply()
                            changeMainFont(selectedFont)
                            uploadNotesToRecyclerView(noteRVAdapter,viewModel,selectedNotebook)
                            recreate()
                        }
                    }
                }
                myAlertDialog.show()
            }
            else->{
                latestSelectedNotebookID = item.itemId
                Toast.makeText(this,item.title.toString() + item.itemId.toString(),
                    Toast.LENGTH_SHORT).show()
                navView.menu.findItem(R.id.nav_change_notebook).isVisible = true
                updateRecyclerView(item,noteRVAdapter)
//                val testID = item.itemId.toString()
//                Toast.makeText(this,testID,Toast.LENGTH_SHORT).show()
//                Toast.makeText(this,"Else",Toast.LENGTH_SHORT).show()
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

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(query: String?): Boolean {
        if(query != null){
            searchDatabase(selectedNotebook,query)
        }
        return true
    }

    private fun addNewNotebook(title:String)
    {
        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        val myEdit: SharedPreferences.Editor = sharedPref.edit()
        val navView = findViewById<NavigationView>(R.id.nav_view2)
        addedDrawerMenuItems.add(title)
        myEdit.putStringSet("notebooks",HashSet(addedDrawerMenuItems)).apply()
        val existingMenu = navView.menu
        existingMenu.add(R.id.nav_notebooks, (0..10000000).random(),1,title)
    }

    private fun removeNotebook(title:String)
    {
        val navView = findViewById<NavigationView>(R.id.nav_view2)
        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        val myEdit: SharedPreferences.Editor = sharedPref.edit()
        addedDrawerMenuItems.remove(title)
        navView.menu.removeItem(latestSelectedNotebookID)
        myEdit.putStringSet("notebooks",HashSet(addedDrawerMenuItems)).apply()
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

    private fun updateRecyclerView(item:MenuItem, noteRVAdapter: NoteRVAdapter){
        viewModel.getAllNotesFromNotebook(item.title.toString()).observe(this) { list ->
            list?.let {
                //on below line we are updating our list.
                noteRVAdapter.updateList(it)
                selectedNotebook = item.title.toString()
            }
        }
    }

    private fun uploadNotesToRecyclerView(noteRVAdapter: NoteRVAdapter,
                                          viewModel:NoteViewModel, selectedNotebook:String){
        viewModel.getAllNotesFromNotebook(selectedNotebook).observe(this) { list ->
            list?.let {
                //on below line we are updating our list.
                noteRVAdapter.updateList(it)
            }
        }
    }

    private fun changeMainFont(styleID:Int){
        val navView = findViewById<NavigationView>(R.id.nav_view2)
        navView.setItemTextAppearance(styleID)
    }

}