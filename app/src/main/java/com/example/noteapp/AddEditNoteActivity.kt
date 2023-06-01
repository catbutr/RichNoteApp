package com.example.noteapp

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import com.example.noteapp.Model.Note
import com.example.noteapp.ViewModel.NoteViewModel
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener
import com.jaredrummler.android.colorpicker.ColorShape
import jp.wasabeef.richeditor.RichEditor
import java.text.DateFormat.getDateTimeInstance
import java.util.*
//TODO:Установить новую версию Rich Editor
@Suppress("DEPRECATION")
class AddEditNoteActivity : AppCompatActivity(), ColorPickerDialogListener {
    private lateinit var imageUri: Uri
    private lateinit var videoUri: Uri
    private lateinit var audioUri: Uri
    private val textColor = 1
    private val backgroundColor = 2
    private val noteColor = 3
    // on below line we are creating
    // variables for our UI components.
    private lateinit var noteTitleEdt: EditText
    lateinit var noteEdt: RichEditor

    //Work with background color
    private var selectedColor = Color.GRAY
    // on below line we are creating variable for
    // view-model and integer for our note id.
    private lateinit var viewModel: NoteViewModel
    private var noteID = -1

    private fun createColorPickerDialog(id: Int) {
        ColorPickerDialog.newBuilder()
            .setColor(Color.RED)
            .setDialogType(ColorPickerDialog.TYPE_PRESETS)
            .setAllowCustom(true)
            .setAllowPresets(true)
            .setColorShape(ColorShape.SQUARE)
            .setDialogId(id)
            .show(this)
    }

    private val getGalleryImageActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            Log.d(TAG, "getImageActivityResultLauncher: uri = $uri")
            if (uri != null) {
                imageUri = uri
                val outputFile = this.filesDir.resolve((0..100000).random().toString())
                contentResolver.openInputStream(imageUri)?.copyTo(outputFile.outputStream())
                imageUri = outputFile.toUri()
                noteEdt.insertImage(imageUri.toString(), "ERROR",
                    "320","320",
                    true)
            }
        }

    private val getGalleryVideoActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            Log.d(TAG, "getVideoActivityResultLauncher: uri = $uri")
            if (uri != null) {
                videoUri = uri
                val outputFile = this.filesDir.resolve((0..100000).random().toString())
                contentResolver.openInputStream(videoUri)?.copyTo(outputFile.outputStream())
                videoUri = outputFile.toUri()
                noteEdt.insertVideo(videoUri.toString(), "ERROR", "320","320")
            }
        }

    private val getGalleryAudioActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            Log.d(TAG, "getVideoActivityResultLauncher: uri = $uri")
            if (uri != null) {
                audioUri = uri
                val outputFile = this.filesDir.resolve((0..100000).random().toString())
                contentResolver.openInputStream(audioUri)?.copyTo(outputFile.outputStream())
                audioUri = outputFile.toUri()
                noteEdt.insertAudio(audioUri.toString())
            }
        }

    @SuppressLint("IntentReset")
    override fun onCreate(savedInstanceState: Bundle?) {
        val theme = intent.getIntExtra("selectedTheme",0)
        val font = intent.getIntExtra("selectedFont",0)
        setTheme(theme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_note)
        //Initialising cache
        // on below line we are initializing our view modal.
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[NoteViewModel::class.java]
        // on below line we are initializing all our variables.
        noteTitleEdt = findViewById(R.id.idEdtNoteName)
        noteTitleEdt.setTextAppearance(font)
        noteEdt = findViewById(R.id.idEdtNoteDesc)
        //on below we are initialising rich editor
        noteEdt.setEditorHeight(200)
        noteEdt.setEditorFontSize(22)
        noteEdt.setEditorFontColor(Color.RED)
        noteEdt.setPadding(10, 10, 10, 10)
        noteEdt.setPlaceholder("Insert text here...")
        noteEdt.settings.allowFileAccess = true

        findViewById<View>(R.id.action_undo).setOnClickListener { noteEdt.undo() }

        findViewById<View>(R.id.action_redo).setOnClickListener { noteEdt.redo() }

        findViewById<View>(R.id.action_bold).setOnClickListener { noteEdt.toggleBold() }

        findViewById<View>(R.id.action_italic).setOnClickListener { noteEdt.toggleItalic() }

        findViewById<View>(R.id.action_subscript).setOnClickListener { noteEdt.setSubscript() }

        findViewById<View>(R.id.action_superscript).setOnClickListener{ noteEdt.setSuperscript() }

        findViewById<View>(R.id.action_strikethrough).setOnClickListener{ noteEdt.toggleStrikeThrough() }

        findViewById<View>(R.id.action_underline).setOnClickListener{ noteEdt.toggleUnderline() }

        findViewById<View>(R.id.action_heading1).setOnClickListener { noteEdt.setHeading(1) }

        findViewById<View>(R.id.action_heading2).setOnClickListener{ noteEdt.setHeading(2) }

        findViewById<View>(R.id.action_heading3).setOnClickListener{ noteEdt.setHeading(3) }

        findViewById<View>(R.id.action_heading4).setOnClickListener{ noteEdt.setHeading(4) }

        findViewById<View>(R.id.action_heading5).setOnClickListener{ noteEdt.setHeading(5) }

        findViewById<View>(R.id.action_heading6).setOnClickListener{ noteEdt.setHeading(6) }

        findViewById<View>(R.id.action_txt_color).setOnClickListener {
            createColorPickerDialog(
                textColor
            )
        }

        findViewById<View>(R.id.action_bg_color).setOnClickListener(object : View.OnClickListener {
            private var isChanged = false
            override fun onClick(v: View?) {
                if (isChanged){
                    noteEdt.setTextBackgroundColor(Color.TRANSPARENT)
                }
                else
                {
                    createColorPickerDialog(backgroundColor)
                }
                isChanged = !isChanged
            }
        })

        findViewById<View>(R.id.action_indent).setOnClickListener { noteEdt.setIndent() }

        findViewById<View>(R.id.action_outdent).setOnClickListener { noteEdt.setOutdent() }

        findViewById<View>(R.id.action_align_left).setOnClickListener{ noteEdt.setAlignLeft() }

        findViewById<View>(R.id.action_align_center).setOnClickListener{ noteEdt.setAlignCenter() }

        findViewById<View>(R.id.action_align_right).setOnClickListener{ noteEdt.setAlignRight() }

        findViewById<View>(R.id.action_blockquote).setOnClickListener { noteEdt.setBlockquote() }

        findViewById<View>(R.id.action_insert_bullets).setOnClickListener{ noteEdt.setBullets() }

        findViewById<View>(R.id.action_insert_numbers).setOnClickListener{ noteEdt.setNumbers() }

        findViewById<View>(R.id.action_insert_image).setOnClickListener {
            val choice = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
            val myAlertDialog: AlertDialog.Builder = AlertDialog.Builder(this)
            myAlertDialog.setTitle("Select Image")
            myAlertDialog.setItems(choice) { _, item ->
                when {
                    choice[item] == "Choose from Gallery" -> {
                        getGalleryImageActivityResultLauncher.launch("image/*")
                    }
                    // Select "Cancel" to cancel the task
                    choice[item] == "Cancel" -> {
                    }
                }
            }
            myAlertDialog.show()
        }

        findViewById<View>(R.id.action_insert_youtube).setOnClickListener{
            val myAlertDialog: AlertDialog.Builder = AlertDialog.Builder(this)
            val inputField = EditText(this)
            var inputLink:String
            myAlertDialog.setTitle("Insert Link")
            inputField.hint = "Enter Text"
            inputField.inputType = InputType.TYPE_CLASS_TEXT
            myAlertDialog.setView(inputField)
            // Set up the buttons
            myAlertDialog.setPositiveButton("OK"
            ) { _, _ ->
                // Here you get get input text from the Edittext
                inputLink = inputField.text.toString()
                inputLink = inputLink.replace("youtu.be/", "www.youtube.com/embed/")
                noteEdt.insertYoutubeVideo(inputLink)
            }
            myAlertDialog.setNegativeButton("Cancel"
            ) { dialog, _ -> dialog.cancel() }

            myAlertDialog.show()
        }

        findViewById<View>(R.id.action_insert_audio).setOnClickListener{
            val choice = arrayOf<CharSequence>("Take Video", "Choose from Gallery", "Cancel")
            val myAlertDialog: AlertDialog.Builder = AlertDialog.Builder(this)
            myAlertDialog.setTitle("Select Video")
            myAlertDialog.setItems(choice) { _, item ->
                when {
                    choice[item] == "Choose from Gallery" -> {
                        getGalleryAudioActivityResultLauncher.launch("audio/*")
                    }
                    // Select "Cancel" to cancel the task
                    choice[item] == "Cancel" -> {
                    }
                }
            }
            myAlertDialog.show()
        }

        findViewById<View>(R.id.action_insert_video).setOnClickListener {
            val choice = arrayOf<CharSequence>("Take Video", "Choose from Gallery", "Cancel")
            val myAlertDialog: AlertDialog.Builder = AlertDialog.Builder(this)
            myAlertDialog.setTitle("Select Video")
            myAlertDialog.setItems(choice) { _, item ->
                when {
                    choice[item] == "Choose from Gallery" -> {
                        getGalleryVideoActivityResultLauncher.launch("video/*")
                    }
                    // Select "Cancel" to cancel the task
                    choice[item] == "Cancel" -> {
                    }
                }
            }
            myAlertDialog.show()
        }

        findViewById<View>(R.id.action_insert_link).setOnClickListener {
            val myAlertDialog: AlertDialog.Builder = AlertDialog.Builder(this)
            val inputLinkField = EditText(this)
            val inputTextFiled = EditText(this)
            val layout = LinearLayout(this)
            var hyperlinkText:String
            var titleText:String
            myAlertDialog.setTitle("Insert Link")
            inputLinkField.hint = "Enter Link"
            inputLinkField.inputType = InputType.TYPE_CLASS_TEXT
            inputTextFiled.hint = "Enter Text"
            inputTextFiled.inputType = InputType.TYPE_CLASS_TEXT
            layout.orientation = LinearLayout.VERTICAL
            layout.addView(inputTextFiled)
            layout.addView(inputLinkField)

            myAlertDialog.setView(layout)
            // Set up the buttons
            myAlertDialog.setPositiveButton("OK"
            ) { _, _ ->
                // Here you get get input text from the Edittext
                hyperlinkText = inputLinkField.text.toString()
                titleText = inputTextFiled.text.toString()
                noteEdt.insertLink(hyperlinkText, titleText,titleText)
            }
            myAlertDialog.setNegativeButton("Cancel"
            ) { dialog, _ -> dialog.cancel() }

            myAlertDialog.show()
        }
        findViewById<View>(R.id.action_insert_checkbox).setOnClickListener { noteEdt.insertCheckbox()}


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
                        updatedNote.id = noteID
                        viewModel.updateNote(updatedNote)
//                        Toast.makeText(this, "Note Updated..", Toast.LENGTH_LONG).show()
                    }
                } else {
                    if (noteTitle.isNotEmpty() && noteDescription.isNotEmpty()) {
                        val sdf = getDateTimeInstance()
                        val currentDateAndTime: String = sdf.format(Date())
                        //if the string is not empty we are calling a add note method to add data to our room database.
                        viewModel.addNote(Note(noteTitle, noteDescription, currentDateAndTime,selectedColor,
                            selectedNoteBookButton.toString()
                        ))
//                        Toast.makeText(this, "$noteTitle Added", Toast.LENGTH_LONG).show()
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
            R.id.colorPicker -> createColorPickerDialog(noteColor)
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
        Toast.makeText(this, "Dialog dismissed", Toast.LENGTH_SHORT).show()
    }
}