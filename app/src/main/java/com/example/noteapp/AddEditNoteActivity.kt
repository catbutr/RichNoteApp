package com.example.noteapp

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.noteapp.Model.Note
import com.example.noteapp.Repository.TakePictureWithUriReturnContract
import com.example.noteapp.ViewModel.NoteViewModel
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener
import com.jaredrummler.android.colorpicker.ColorShape
import jp.wasabeef.richeditor.RichEditor
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


@Suppress("DEPRECATION")
class AddEditNoteActivity : AppCompatActivity(), ColorPickerDialogListener {
    private lateinit var imageUri: Uri
    private lateinit var videoUri: Uri
    private lateinit var audioUri: Uri
    private lateinit var cameraImageUri: Uri
    private val textColor = 1
    private val backgroundColor = 2
    private val noteColor = 3
    // on below line we are creating
    // variables for our UI components.
    private lateinit var noteTitleEdt: EditText
    lateinit var noteEdt: RichEditor
    private lateinit var saveBtn: Button
    private lateinit var colorBtn:Button
    //Work with background color
    private var selectedColor = Color.GRAY
    private var isColorSelected = false

    // on below line we are creating variable for
    // view-model and integer for our note id.
    private lateinit var viewModel: NoteViewModel
    private var noteID = -1

    @SuppressLint("Recycle")
    private fun getTmpFileUri(): Uri {
        val tmpFile = File.createTempFile("tmp_image_file", ".png", this.filesDir).apply {
            createNewFile()
            deleteOnExit()
        }
        return FileProvider.getUriForFile(applicationContext,
            "${BuildConfig.APPLICATION_ID}.provider", tmpFile)
    }
    private fun takeImage() {
        lifecycleScope.launchWhenStarted {
            getTmpFileUri().let { uri -> takeImageResult.launch(uri)}
        }
    }

    private val takeImageResult = registerForActivityResult(TakePictureWithUriReturnContract()) { (isSuccess, imageUri) ->
        if (isSuccess) {
            noteEdt.insertImage(imageUri.toString(), "Test", 320)
        }
    }

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
                noteEdt.insertImage(imageUri.toString(), "Test", 320)
            }
        }

    private val getCameraImageActivityResultLauncher =
        registerForActivityResult(TakePictureWithUriReturnContract()) { (isSuccess, uri) ->
            Log.d(TAG, "getImageActivityResultLauncher: uri = $uri")
            if (isSuccess && uri != null) {
                imageUri = uri
                val outputFile = this.filesDir.resolve((0..100000).random().toString())
                contentResolver.openInputStream(imageUri)?.copyTo(outputFile.outputStream())
                cameraImageUri = outputFile.toUri()
                noteEdt.insertImage(cameraImageUri.toString(), "Test", 320)
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
                noteEdt.insertVideo(videoUri.toString(), 320, 320)
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
        super.onCreate(savedInstanceState)
        setContentView(R.layout.testing_layout)
        //Initialising cache
        // on below line we are initializing our view modal.
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[NoteViewModel::class.java]

        // on below line we are initializing all our variables.
        noteTitleEdt = findViewById(R.id.idEdtNoteName)
        noteEdt = findViewById(R.id.idEdtNoteDesc)
        saveBtn = findViewById(R.id.idBtn)
        colorBtn = findViewById(R.id.idBtnColor)
        //on below we are initialising rich editor
        noteEdt.setEditorHeight(200)
        noteEdt.setEditorFontSize(22)
        noteEdt.setEditorFontColor(Color.RED)
        //mEditor.setEditorBackgroundColor(Color.BLUE);
        //mEditor.setBackgroundColor(Color.BLUE);
        //mEditor.setBackgroundResource(R.drawable.bg);
        //mEditor.setEditorBackgroundColor(Color.BLUE);
        //mEditor.setBackgroundColor(Color.BLUE);
        //mEditor.setBackgroundResource(R.drawable.bg);
        noteEdt.setPadding(10, 10, 10, 10)
        //mEditor.setBackground("https://raw.githubusercontent.com/wasabeef/art/master/chip.jpg");
        //mEditor.setBackground("https://raw.githubusercontent.com/wasabeef/art/master/chip.jpg");
        noteEdt.setPlaceholder("Insert text here...")
        noteEdt.settings.allowFileAccess = true
        //mEditor.setInputEnabled(false);

        //mEditor.setInputEnabled(false);

        findViewById<View>(R.id.action_undo).setOnClickListener { noteEdt.undo() }

        findViewById<View>(R.id.action_redo).setOnClickListener { noteEdt.redo() }

        findViewById<View>(R.id.action_bold).setOnClickListener { noteEdt.setBold() }

        findViewById<View>(R.id.action_italic).setOnClickListener { noteEdt.setItalic() }

        findViewById<View>(R.id.action_subscript).setOnClickListener { noteEdt.setSubscript() }

        findViewById<View>(R.id.action_superscript).setOnClickListener{ noteEdt.setSuperscript() }

        findViewById<View>(R.id.action_strikethrough).setOnClickListener{ noteEdt.setStrikeThrough() }

        findViewById<View>(R.id.action_underline).setOnClickListener{ noteEdt.setUnderline() }

        findViewById<View>(R.id.action_heading1).setOnClickListener { noteEdt.setHeading(1) }

        findViewById<View>(R.id.action_heading2).setOnClickListener{ noteEdt.setHeading(2) }

        findViewById<View>(R.id.action_heading3).setOnClickListener{ noteEdt.setHeading(3) }

        findViewById<View>(R.id.action_heading4).setOnClickListener{ noteEdt.setHeading(4) }

        findViewById<View>(R.id.action_heading5).setOnClickListener{ noteEdt.setHeading(5) }

        findViewById<View>(R.id.action_heading6).setOnClickListener{ noteEdt.setHeading(6) }

        findViewById<View>(R.id.action_txt_color).setOnClickListener(object : View.OnClickListener
        {
            override fun onClick(v: View?) {
                createColorPickerDialog(textColor);
            }
        })

        findViewById<View>(R.id.action_bg_color).setOnClickListener(object : View.OnClickListener {
            private var isChanged = false
            override fun onClick(v: View?) {
                if (isChanged){
                    noteEdt.setTextBackgroundColor(Color.TRANSPARENT)
                }
                else
                {
                    createColorPickerDialog(backgroundColor);
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
            myAlertDialog.setItems(choice, DialogInterface.OnClickListener { dialog, item ->
                when {
                    choice[item] == "Choose from Gallery" -> {
                        getGalleryImageActivityResultLauncher.launch("image/*")
                    }
                    // Select "Take Photo" to take a photo
                    choice[item] == "Take Photo" -> {
                        getCameraImageActivityResultLauncher.launch(cameraImageUri)
                    }
                    // Select "Cancel" to cancel the task
                    choice[item] == "Cancel" -> {
                    }
                }
            })
            myAlertDialog.show()
        }

        findViewById<View>(R.id.action_insert_youtube).setOnClickListener{
            val myAlertDialog: AlertDialog.Builder = AlertDialog.Builder(this)
            val inputField = EditText(this)
            var m_Text:String
            myAlertDialog.setTitle("Insert Link")
            inputField.setHint("Enter Text")
            inputField.inputType = InputType.TYPE_CLASS_TEXT
            myAlertDialog.setView(inputField)
            // Set up the buttons
            myAlertDialog.setPositiveButton("OK"
            ) { dialog, which ->
                // Here you get get input text from the Edittext
                m_Text = inputField.text.toString();
                m_Text = m_Text.replace("youtu.be/", "www.youtube.com/embed/")
                noteEdt.insertYoutubeVideo(m_Text)
            }
            myAlertDialog.setNegativeButton("Cancel"
            ) { dialog, which -> dialog.cancel() }

            myAlertDialog.show()
        }

        findViewById<View>(R.id.action_insert_audio).setOnClickListener{
            val choice = arrayOf<CharSequence>("Take Video", "Choose from Gallery", "Cancel")
            val myAlertDialog: AlertDialog.Builder = AlertDialog.Builder(this)
            myAlertDialog.setTitle("Select Video")
            myAlertDialog.setItems(choice, DialogInterface.OnClickListener { dialog, item ->
                when {
                    choice[item] == "Choose from Gallery" -> {
                        getGalleryAudioActivityResultLauncher.launch("audio/*")
                    }
                    // Select "Take Photo" to take a photo
                    choice[item] == "Take Video" -> {
                        val cameraPicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        startActivityForResult(cameraPicture, 0)
                    }
                    // Select "Cancel" to cancel the task
                    choice[item] == "Cancel" -> {
                    }
                }
            })
            myAlertDialog.show()
        }

        findViewById<View>(R.id.action_insert_video).setOnClickListener {
            val choice = arrayOf<CharSequence>("Take Video", "Choose from Gallery", "Cancel")
            val myAlertDialog: AlertDialog.Builder = AlertDialog.Builder(this)
            myAlertDialog.setTitle("Select Video")
            myAlertDialog.setItems(choice) { dialog, item ->
                when {
                    choice[item] == "Choose from Gallery" -> {
                        getGalleryVideoActivityResultLauncher.launch("video/*")
                    }
                    // Select "Take Photo" to take a photo
                    choice[item] == "Take Video" -> {
                        val cameraPicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        startActivityForResult(cameraPicture, 0)
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
            myAlertDialog.setPositiveButton("OK",
                DialogInterface.OnClickListener { dialog, which ->
                    // Here you get get input text from the Edittext
                    hyperlinkText = inputLinkField.text.toString();
                    titleText = inputTextFiled.text.toString()
                    noteEdt.insertLink(hyperlinkText,titleText)
                })
            myAlertDialog.setNegativeButton("Cancel",
                DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })

            myAlertDialog.show()
        }
        findViewById<View>(R.id.action_insert_checkbox).setOnClickListener { noteEdt.insertTodo() }


        // on below line we are getting data passed via an intent.
        val noteType = intent.getStringExtra("noteType")
        if (noteType.equals("Edit")) {
            // on below line we are setting data to edit text.
            val noteTitle = intent.getStringExtra("noteTitle")
            val noteDescription = intent.getStringExtra("noteDescription")
            noteID = intent.getIntExtra("noteId", -1)
            saveBtn.text = "Update Note"
            noteTitleEdt.setText(/* text = */ noteTitle)
            noteEdt.html = noteDescription
        } else {
            saveBtn.text = "Save Note"
        }

        //on below line we are adding click listner to our save button.
        saveBtn.setOnClickListener {
            //on below line we are getting title and desc from edit text.
            val noteTitle = noteTitleEdt.text.toString()
            val noteDescription = noteEdt.html
            //on below line we are checking the type and then saving or updating the data.
            if (noteType.equals("Edit")) {
                if (noteTitle.isNotEmpty() && noteDescription.isNotEmpty()) {
                    val sdf = SimpleDateFormat("dd MMM, yyyy - HH:mm")
                    val currentDateAndTime: String = sdf.format(Date())
                    val updatedNote = Note(noteTitle, noteDescription, currentDateAndTime,
                        selectedColor)
                    updatedNote.id = noteID
                    viewModel.updateNote(updatedNote)
                    Toast.makeText(this, "Note Updated..", Toast.LENGTH_LONG).show()
                }
            } else {
                if (noteTitle.isNotEmpty() && noteDescription.isNotEmpty()) {
                    val sdf = SimpleDateFormat("dd MMM, yyyy - HH:mm")
                    val currentDateAndTime: String = sdf.format(Date())
                    //if the string is not empty we are calling a add note method to add data to our room database.
                    viewModel.addNote(Note(noteTitle, noteDescription, currentDateAndTime,selectedColor))
                    Toast.makeText(this, "$noteTitle Added", Toast.LENGTH_LONG).show()
                }
            }
            //opening the new activity on below line
            startActivity(Intent(applicationContext, MainActivity::class.java))
            this.finish()
        }

        colorBtn.setOnClickListener{
            createColorPickerDialog(noteColor);
        }



    }

    override fun onBackPressed() {
        val intent = Intent(this@AddEditNoteActivity, MainActivity::class.java)
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
        Toast.makeText(this, "Dialog dismissed", Toast.LENGTH_SHORT).show();
    }
}