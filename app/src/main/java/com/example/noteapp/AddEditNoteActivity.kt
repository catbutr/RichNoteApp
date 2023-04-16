package com.example.noteapp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.chinalwb.are.AREditor
import com.example.noteapp.Model.Note
import com.example.noteapp.ViewModel.NoteViewModel
import jp.wasabeef.richeditor.RichEditor
import jp.wasabeef.richeditor.RichEditor.OnTextChangeListener
import java.text.SimpleDateFormat
import java.util.*


class AddEditNoteActivity : AppCompatActivity() {
    // on below line we are creating
    // variables for our UI components.
    lateinit var noteTitleEdt: EditText
    lateinit var noteEdt: RichEditor
    lateinit var saveBtn: Button

    // on below line we are creating variable for
    // viewmodal and integer for our note id.
    lateinit var viewModel: NoteViewModel
    var noteID = -1;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.testing_layout)

        // on below line we are initializing our view modal.
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[NoteViewModel::class.java]

        // on below line we are initializing all our variables.
        noteTitleEdt = findViewById(R.id.idEdtNoteName)
        noteEdt = findViewById(R.id.idEdtNoteDesc)
        saveBtn = findViewById(R.id.idBtn)

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
        noteEdt.settings.allowFileAccess = true;
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
            private var isChanged = false
            override fun onClick(v: View?) {
                noteEdt.setTextColor(if (isChanged) Color.BLACK else Color.RED)
                isChanged = !isChanged
            }
        })

        findViewById<View>(R.id.action_bg_color).setOnClickListener(object : View.OnClickListener {
            private var isChanged = false
            override fun onClick(v: View?) {
                noteEdt.setTextBackgroundColor(if (isChanged) Color.TRANSPARENT else Color.YELLOW)
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
            noteEdt.insertImage(
                "https://raw.githubusercontent.com/wasabeef/art/master/chip.jpg",
                "dachshund", 320
            )
        }

        findViewById<View>(R.id.action_insert_youtube).setOnClickListener {
            noteEdt.insertYoutubeVideo(
            "https://www.youtube.com/embed/pS5peqApgUA") }

        findViewById<View>(R.id.action_insert_audio).setOnClickListener{
            noteEdt.insertAudio(
                "https://file-examples-com.github.io/uploads/2017/11/file_example_MP3_5MG.mp3") }

        findViewById<View>(R.id.action_insert_video).setOnClickListener {
            noteEdt.insertVideo(
                "https://test-videos.co.uk/vids/bigbuckbunny/mp4/h264/1080/Big_Buck_Bunny_1080_10s_10MB.mp4",
                360
            )
        }

        findViewById<View>(R.id.action_insert_link).setOnClickListener {
            noteEdt.insertLink(
                "https://github.com/wasabeef",
                "wasabeef"
            )
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
                    val updatedNote = Note(noteTitle, noteDescription, currentDateAndTime)
                    updatedNote.id = noteID
                    viewModel.updateNote(updatedNote)
                    Toast.makeText(this, "Note Updated..", Toast.LENGTH_LONG).show()
                }
            } else {
                if (noteTitle.isNotEmpty() && noteDescription.isNotEmpty()) {
                    val sdf = SimpleDateFormat("dd MMM, yyyy - HH:mm")
                    val currentDateAndTime: String = sdf.format(Date())
                    //if the string is not empty we are calling a add note method to add data to our room database.
                    viewModel.addNote(Note(noteTitle, noteDescription, currentDateAndTime))
                    Toast.makeText(this, "$noteTitle Added", Toast.LENGTH_LONG).show()
                }
            }
            //opening the new activity on below line
            startActivity(Intent(applicationContext, MainActivity::class.java))
            this.finish()
        }
    }
}