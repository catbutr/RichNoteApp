package com.example.noteapp

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.noteapp.Model.Note
import com.example.noteapp.databinding.ActivityAddEditNoteBinding
import com.example.noteapp.viewModel.NoteViewModel
import com.example.noteapp.viewModel.ScrollViewHandler
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener
import com.jaredrummler.android.colorpicker.ColorShape
import jp.wasabeef.richeditor.RichEditor
import java.text.DateFormat.getDateTimeInstance
import java.util.*
@Suppress("DEPRECATION")
class AddEditNoteActivity : AppCompatActivity(), ColorPickerDialogListener {
    private val textColor = 1
    private val backgroundColor = 2
    private val noteColor = 3
    // on below line we are creating
    // variables for our UI components.
    private lateinit var noteTitleEdt: EditText
    lateinit var noteEdt: RichEditor
    //Work with background color
    var selectedColor = Color.GRAY
    var isNoteFavourite:Boolean = false
    // on below line we are creating variable for
    // view-model and integer for our note id.
    private lateinit var viewModel: NoteViewModel
    private var noteID = -1

    @SuppressLint("IntentReset")
    override fun onCreate(savedInstanceState: Bundle?) {
        val theme = intent.getIntExtra("selectedTheme",0)
        val font = intent.getIntExtra("selectedFont",0)
        isNoteFavourite = intent.getBooleanExtra("isFavourite",false)
        setTheme(theme)
        super.onCreate(savedInstanceState)
        val binding:ActivityAddEditNoteBinding = DataBindingUtil.setContentView(this,
            R.layout.activity_add_edit_note)
        binding.noteEdt = findViewById(R.id.idEdtNoteDesc)
        binding.activity = this
        binding.scrollViewHandler = ScrollViewHandler(findViewById(R.id.idEdtNoteDesc),this)
        //Initialising cache
        // on below line we are initializing our view modal.
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[NoteViewModel::class.java]
        // on below line we are initializing all our variables.
        noteTitleEdt = findViewById(R.id.idEdtNoteName)
        noteTitleEdt.setTextAppearance(font)
        //on below we are initialising rich editor
        noteEdt = findViewById(R.id.idEdtNoteDesc)
        noteEdt.setEditorHeight(200)
        noteEdt.setEditorFontSize(22)
        noteEdt.setEditorFontColor(Color.RED)
        noteEdt.setPadding(10, 10, 10, 10)
        noteEdt.setPlaceholder("Insert text here...")
        noteEdt.settings.javaScriptEnabled = true
        noteEdt.settings.domStorageEnabled = true
        noteEdt.settings.allowFileAccess = true
        noteEdt.LoadFont("Garamond","garamond_regular.ttf")
        noteEdt.LoadFont("Aldrich","aldrich_cyrillic.otf")
        noteEdt.LoadFont("PTSans","ptsans_regular.ttf")
        noteEdt.LoadFont("Montserrat","montserrat_regular.ttf")
        noteEdt.LoadFont("Roboto","roboto_regular.ttf")

        // on below line we are getting data passed via an intent.
        val noteType = intent.getStringExtra("noteType")
        if (noteType.equals("Edit")) {
            // on below line we are setting data to edit text.
            val noteTitle = intent.getStringExtra("noteTitle")
            val noteDescription = intent.getStringExtra("noteDescription")
            selectedColor = intent.getIntExtra("noteColor", Color.GRAY)
            noteID = intent.getIntExtra("noteId", -1)
            noteTitleEdt.setText(/* text = */ noteTitle)
            noteEdt.html = noteDescription
        }
    }

    // method to inflate the options menu when
    // the user opens the menu for the first time
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.add_edit_note_action_bar, menu)
        val favouriteButton = menu.findItem(R.id.favourite)
        if(!isNoteFavourite){
            favouriteButton.setIcon(R.drawable.star)
        }
        else{
            favouriteButton.setIcon(R.drawable.selected_star)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val noteType = intent.getStringExtra("noteType")
        when (item.itemId) {
            R.id.save -> {
                //on below line we are getting title and desc from edit text.
                val noteTitle = noteTitleEdt.text.toString()
                val noteDescription = noteEdt.html
                val selectedNoteBookButton = intent.getStringExtra("selectedNotebook")
                //on below line we are checking the type and then saving or updating the data.
                if (noteType.equals("Edit")) {
                    if (noteTitle.isNotEmpty() && noteDescription.isNotEmpty()) {
                        val sdf = getDateTimeInstance()
                        val currentDateAndTime: String = sdf.format(Date())
                        val updatedNote = Note(noteTitle, noteDescription, currentDateAndTime,
                            selectedColor,selectedNoteBookButton.toString())
                        updatedNote.isFavourite = isNoteFavourite
                        updatedNote.id = noteID
                        viewModel.updateNote(updatedNote)
                    }
                } else {
                    if (noteTitle.isNotEmpty() && noteDescription.isNotEmpty()) {
                        val sdf = getDateTimeInstance()
                        val currentDateAndTime: String = sdf.format(Date())
                        //if the string is not empty we are calling a add note method to add data to our room database.
                        val newNote = Note(noteTitle, noteDescription, currentDateAndTime,selectedColor,
                            selectedNoteBookButton.toString()
                        )
                        newNote.isFavourite = isNoteFavourite
                        viewModel.addNote(newNote)
                    }
                }
                Toast.makeText(this, "$selectedNoteBookButton is current notebook", Toast.LENGTH_LONG).show()
                //opening the new activity on below line
                val mainIntent = Intent(this@AddEditNoteActivity, MainActivity::class.java)
                mainIntent.putExtra("previousNotebook",selectedNoteBookButton)
                mainIntent.putExtra("viewFlag",true)
                startActivity(mainIntent)
                this.finish()
            }
            R.id.colorPicker -> {
                createColorPickerDialog(noteColor)
            }
            R.id.favourite -> {
                if(!isNoteFavourite){
                    isNoteFavourite = true
                    item.setIcon(R.drawable.selected_star)
                }
                else{
                    isNoteFavourite = false
                    item.setIcon(R.drawable.star)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val intent = Intent(this@AddEditNoteActivity, MainActivity::class.java)
        val lastNotebook = intent.getStringExtra("selectedNotebook")
        intent.putExtra("lastNotebook", lastNotebook.toString())
        startActivity(intent)
        this.finish()
    }

    fun createColorPickerDialog(id: Int) {
        ColorPickerDialog.newBuilder()
            .setColor(Color.RED)
            .setDialogType(ColorPickerDialog.TYPE_PRESETS)
            .setAllowCustom(true)
            .setAllowPresets(true)
            .setColorShape(ColorShape.SQUARE)
            .setDialogId(id)
            .show(this)
    }

    override fun onColorSelected(dialogId: Int, color: Int) {
        when (dialogId) {
            textColor -> noteEdt.setTextColor(color)
            backgroundColor -> noteEdt.setTextBackgroundColor(color)
            noteColor ->
            {
                selectedColor = color
            }
        }
    }

    override fun onDialogDismissed(dialogId: Int) {
    }
}