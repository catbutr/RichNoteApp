package com.example.noteapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
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
import com.example.noteapp.utils.FontUtil
import com.example.noteapp.utils.NoteViewModelUtil
import com.example.noteapp.utils.NotebookUtil
import com.example.noteapp.utils.ThemeUtil
import com.example.noteapp.viewModel.DataManager
import com.example.noteapp.viewModel.NoteViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity(), NoteClickInterface, SearchView.OnQueryTextListener,
    NoteClickDeleteInterface, NavigationView.OnNavigationItemSelectedListener {

    //ViewModel
    lateinit var viewModel: NoteViewModel
    //UI elements
    lateinit var notesRV: RecyclerView
    private lateinit var addFAB: FloatingActionButton
    private lateinit var drawerLayout:DrawerLayout
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    //Working variables
    var selectedTheme:Int = R.style.Theme_NoteApp
    var selectedNotebook:String = "My Notes"
    var addedDrawerMenuItems = mutableListOf<String>()
    var selectedFont = R.style.Roboto
    lateinit var noteRVAdapter:NoteRVAdapter
    var latestSelectedNotebookID = R.id.nav_my_notes
    var isAsc:Boolean = false
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("selectedNotebook", selectedNotebook)
        outState.putStringArrayList("newItems",java.util.ArrayList(addedDrawerMenuItems))
        outState.putInt("selectedFont",selectedFont)
        super.onSaveInstanceState(outState)
    }
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        selectedNotebook = savedInstanceState.getString("selectedNotebook").toString()
        selectedFont = savedInstanceState.getInt("selectedFont")
        addedDrawerMenuItems = savedInstanceState.getStringArrayList("newItems")!!.toMutableList()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        //Data store
        val prefs = DataManager(this)
        selectedTheme = prefs.getValue_int("chosenTheme")
        selectedFont = prefs.getValue_int("selectedFont")
        isAsc = prefs.getValue_boolean("isAsc")!!
        noteRVAdapter = NoteRVAdapter(this, this,
            selectedFont)
        setTheme(selectedTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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
        val navView: NavigationView = findViewById<NavigationView>(R.id.nav_view2)
        navView.setItemTextAppearance(selectedFont)
        navView.setNavigationItemSelectedListener(this)
        navView.menu.findItem(R.id.nav_change_notebook).isVisible = false
        val savedNotebooks = prefs.getValue_stringSet("notebooks")!!.toMutableList()
        if (savedNotebooks.isNotEmpty()) {
            for (i in savedNotebooks.indices) {
                navView.menu.add(R.id.nav_notebooks, (0..10000000).random(), 1, savedNotebooks[i])
            }
        }
        addedDrawerMenuItems.addAll(savedNotebooks)
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
        val vmUtil = NoteViewModelUtil(viewModel,this, this)
        if (intent.getBooleanExtra("viewFlag",false)) {
            selectedNotebook = intent.getStringExtra("previousNotebook").toString()
        }
        //on below line we are calling all notes method from our view modal class to observer the changes on list.
        vmUtil.uploadNotesToRecyclerView(noteRVAdapter,viewModel,selectedNotebook,isAsc)
        addFAB.setOnClickListener {
            //adding a click listener for fab button and opening a new intent to add a new note.
            val intent = Intent(this@MainActivity, AddEditNoteActivity::class.java)
            intent.putExtra("selectedNotebook",selectedNotebook)
            startActivity(intent)
            this.finish()
        }
    }

    override fun onNoteClick(note: Note) {
        val prefs = DataManager(this)
        prefs.setValue_stringSet("notebooks",HashSet(addedDrawerMenuItems))
        //opening a new intent and passing a data to it.
        val intent = Intent(this@MainActivity, AddEditNoteActivity::class.java)
        intent.putExtra("noteType", "Edit")
        intent.putExtra("noteTitle", note.noteTitle)
        intent.putExtra("noteDescription", note.noteText)
        intent.putExtra("noteId", note.id)
        intent.putExtra("noteColor",note.backgroundColor)
        intent.putExtra("selectedNotebook",note.noteBook)
        intent.putExtra("selectedTheme", prefs.getValue_int("chosenTheme"))
        intent.putExtra("selectedFont",selectedFont)
        intent.putExtra("isFavourite",note.isFavourite)
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
        val vmUtil = NoteViewModelUtil(viewModel,this,this)
        notesRV.adapter = noteRVAdapter
        val navView = findViewById<NavigationView>(R.id.nav_view2)
        val notebookUtil = NotebookUtil(navView,this,this,this)
        when (item.itemId)
        {
            R.id.nav_new_notebook-> {
                notebookUtil.addNotebook()
            }
            R.id.nav_my_notes->{
                navView.menu.findItem(R.id.nav_change_notebook).isVisible = false
                vmUtil.updateRecyclerView(item,noteRVAdapter,isAsc)
                Toast.makeText(this,"Nav_My_Notes",Toast.LENGTH_SHORT).show()
            }
            R.id.nav_delete_notebook->{
                notebookUtil.removeNotebook()
            }
            R.id.nav_change_notebook->{
                notebookUtil.renameNotebook()
            }
            R.id.nav_settings->{
                val themeSettings = ThemeUtil(this,this)
                val fontSetting = FontUtil(this,this,this)
                val myAlertDialog: AlertDialog.Builder = AlertDialog.Builder(this)
                myAlertDialog.setTitle("Select settings")
                val settingsChoice = arrayOf<CharSequence>("Change theme","Change font","Cancel")
                myAlertDialog.setItems(settingsChoice){_, item ->
                    when{
                        settingsChoice[item] == "Change font"->{
                            fontSetting.selectingFont()
                        }
                        settingsChoice[item] == "Change theme"->{
                            themeSettings.changingThemeDialog()
                        }
                    }
                }
                myAlertDialog.show()
            }
            R.id.nav_favourites->{
                latestSelectedNotebookID = item.itemId
                Toast.makeText(this,item.title.toString() + item.itemId.toString(),
                    Toast.LENGTH_SHORT).show()
                navView.menu.findItem(R.id.nav_change_notebook).isVisible = true
                vmUtil.updateFavouritesRecyclerView(item,noteRVAdapter)
            }
            else->{
                latestSelectedNotebookID = item.itemId
                Toast.makeText(this,item.title.toString() + item.itemId.toString(),
                    Toast.LENGTH_SHORT).show()
                navView.menu.findItem(R.id.nav_change_notebook).isVisible = true
                vmUtil.updateRecyclerView(item,noteRVAdapter,isAsc)
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
        val sorting = menu.findItem(R.id.date_sort)
        if(!isAsc){
            sorting.setIcon(R.drawable.sort_desc)
        }
        else{
            sorting.setIcon(R.drawable.sort_asc)
        }
        searchView?.setOnQueryTextListener(this)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean{
        val vmUtil = NoteViewModelUtil(viewModel,this,this)
        when (item.itemId){
            R.id.date_sort->{
                if(!isAsc){
                    isAsc = true
                    item.setIcon(R.drawable.sort_asc)
                    vmUtil.updateRecyclerViewForActionBar(selectedNotebook,isAsc)
                }
                else{
                    isAsc = false
                    item.setIcon(R.drawable.sort_desc)
                    vmUtil.updateRecyclerViewForActionBar(selectedNotebook,isAsc)
                }
            }
        }
        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(query: String?): Boolean {
        val vmUtil = NoteViewModelUtil(viewModel,this, this)
        if(query != null){
            vmUtil.searchDatabase(selectedNotebook,query)
        }
        return true
    }
}