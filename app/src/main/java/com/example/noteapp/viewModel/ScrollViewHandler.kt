package com.example.noteapp.viewModel

import android.content.ContentValues
import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.text.InputType
import android.util.Log
import android.widget.EditText
import android.widget.LinearLayout
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import com.example.noteapp.AddEditNoteActivity
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener
import com.jaredrummler.android.colorpicker.ColorShape
import jp.wasabeef.richeditor.RichEditor

 class ScrollViewHandler(private val richEdt:RichEditor, private val thisActivity: AddEditNoteActivity):
    ColorPickerDialogListener {

    private lateinit var imageUri: Uri
    private lateinit var videoUri: Uri
    private lateinit var audioUri: Uri
    private val getImage = 1
    private val getVideo = 2
    private val getAudio = 3
    private val textColor = 1
    private val backgroundColor = 2
    private val noteColor = 3
    private var clickCounter = 0

    private val imageLauncher= getSomethingFromGallery(richEdt, thisActivity,getImage)
    private val videoLauncher= getSomethingFromGallery(richEdt, thisActivity,getVideo)
    private val audioLauncher= getSomethingFromGallery(richEdt, thisActivity,getAudio)

     fun createColorPickerDialog(id: Int, activity: AddEditNoteActivity) {
        ColorPickerDialog.newBuilder()
            .setColor(Color.RED)
            .setDialogType(ColorPickerDialog.TYPE_PRESETS)
            .setAllowCustom(true)
            .setAllowPresets(true)
            .setColorShape(ColorShape.SQUARE)
            .setDialogId(id)
            .show(activity)
    }

    override fun onColorSelected(dialogId: Int, color: Int) {
        when (dialogId) {
            textColor -> richEdt.setTextColor(color)
            backgroundColor -> richEdt.setTextBackgroundColor(color)
            noteColor ->
            {
                thisActivity.selectedColor = color
            }
        }
    }

    override fun onDialogDismissed(dialogId: Int) {
    }

    fun getSomethingFromGallery(noteEdt:RichEditor, activity: AddEditNoteActivity, contentType:Int): ActivityResultLauncher<String> {
        val getGalleryImageActivityResultLauncher =
            activity.registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                Log.d(ContentValues.TAG, "getImageActivityResultLauncher: uri = $uri")
                if (uri != null) {
                    when {
                        contentType == getImage -> {
                            imageUri = uri
                            val outputFile =
                                activity.filesDir.resolve((0..100000).random().toString())
                            activity.contentResolver.openInputStream(imageUri)
                                ?.copyTo(outputFile.outputStream())
                            imageUri = outputFile.toUri()
                            noteEdt.insertImage(imageUri.toString(), "Test", 320)
                        }
                        contentType == getVideo ->{
                            videoUri = uri
                            val outputFile =
                                activity.filesDir.resolve((0..100000).random().toString())
                            activity.contentResolver.openInputStream(videoUri)
                                ?.copyTo(outputFile.outputStream())
                            videoUri = outputFile.toUri()
                            noteEdt.insertVideo(videoUri.toString(), 320, 320)
                        }
                        contentType == getAudio->{
                            audioUri = uri
                            val outputFile =
                                activity.filesDir.resolve((0..100000).random().toString())
                            activity.contentResolver.openInputStream(audioUri)
                                ?.copyTo(outputFile.outputStream())
                            audioUri = outputFile.toUri()
                            noteEdt.insertAudio(audioUri.toString())
                        }
                    }
                }
            }
        return getGalleryImageActivityResultLauncher
    }

    fun undo(noteEdt:RichEditor) { noteEdt.undo() }

    fun redo(noteEdt:RichEditor) { noteEdt.redo() }

    fun setBold(noteEdt:RichEditor) { noteEdt.setBold() }

    fun setItalic(noteEdt:RichEditor) { noteEdt.setItalic() }

    fun setSubscript(noteEdt:RichEditor) { noteEdt.setSubscript() }

    fun setSuperscript(noteEdt:RichEditor){ noteEdt.setSuperscript() }

    fun setStrikeThrough(noteEdt:RichEditor){ noteEdt.setStrikeThrough() }

    fun setUnderline(noteEdt:RichEditor){ noteEdt.setUnderline() }

    fun setHeading(noteEdt: RichEditor,level:Int){
        noteEdt.setHeading(level)
    }

    fun setTextColor(activity: AddEditNoteActivity) {
        createColorPickerDialog(textColor,activity)
    }

    fun setBackgroundTextColor(noteEdt:RichEditor, activity: AddEditNoteActivity) {
        if (clickCounter%2 != 0){
                noteEdt.setTextBackgroundColor(Color.TRANSPARENT)
                clickCounter++
            }
        else
            {
                createColorPickerDialog(backgroundColor, activity)
                clickCounter++
            }
    }

    fun setIndent(noteEdt:RichEditor) { noteEdt.setIndent() }

    fun setOutdent(noteEdt:RichEditor) { noteEdt.setOutdent() }

     fun setAlign(noteEdt:RichEditor, alignment:Int){
         when(alignment){
             1->{
                 noteEdt.setAlignLeft()
             }
             2->{
                 noteEdt.setAlignCenter()
             }
             3->{
                 noteEdt.setAlignRight()
             }
         }
     }

    fun setBlockQuote(noteEdt:RichEditor) { noteEdt.setBlockquote() }

    fun setBullets(noteEdt:RichEditor){ noteEdt.setBullets() }

    fun setNumbers(noteEdt:RichEditor){ noteEdt.setNumbers() }

    fun insertImage(context: Context) {
        val choice = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
        val myAlertDialog: AlertDialog.Builder = AlertDialog.Builder(context)
        myAlertDialog.setTitle("Select Image")
        myAlertDialog.setItems(choice) { _, item ->
            when {
                choice[item] == "Choose from Gallery" -> {
                    imageLauncher.launch("image/*")
                }
                // Select "Cancel" to cancel the task
                choice[item] == "Cancel" -> {
                }
            }
        }
        myAlertDialog.show()
    }

    fun insertYoutubeLink(noteEdt:RichEditor, context: Context){
        val myAlertDialog: AlertDialog.Builder = AlertDialog.Builder(context)
        val inputField = EditText(context)
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

    fun insertAudio(context: Context){
        val choice = arrayOf<CharSequence>("Take Video", "Choose from Gallery", "Cancel")
        val myAlertDialog: AlertDialog.Builder = AlertDialog.Builder(context)
        myAlertDialog.setTitle("Select Video")
        myAlertDialog.setItems(choice) { _, item ->
            when {
                choice[item] == "Choose from Gallery" -> {
                    audioLauncher.launch("audio/*")
                }
                // Select "Cancel" to cancel the task
                choice[item] == "Cancel" -> {
                }
            }
        }
        myAlertDialog.show()
    }

    fun insertVideo(context: Context) {
        val choice = arrayOf<CharSequence>("Take Video", "Choose from Gallery", "Cancel")
        val myAlertDialog: AlertDialog.Builder = AlertDialog.Builder(context)
        myAlertDialog.setTitle("Select Video")
        myAlertDialog.setItems(choice) { _, item ->
            when {
                choice[item] == "Choose from Gallery" -> {
                    videoLauncher.launch("video/*")
                }
                // Select "Cancel" to cancel the task
                choice[item] == "Cancel" -> {
                }
            }
        }
        myAlertDialog.show()
    }

    fun insertLink(noteEdt:RichEditor, context: Context) {
        val myAlertDialog: AlertDialog.Builder = AlertDialog.Builder(context)
        val inputLinkField = EditText(context)
        val inputTextFiled = EditText(context)
        val layout = LinearLayout(context)
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
            noteEdt.insertLink(hyperlinkText, titleText)
        }
        myAlertDialog.setNegativeButton("Cancel"
        ) { dialog, _ -> dialog.cancel() }

        myAlertDialog.show()
    }
    fun setTodo(noteEdt:RichEditor) { noteEdt.insertTodo()}
    fun changeFont(noteEdt:RichEditor, context: Context) {
        val choice = arrayOf<CharSequence>("Roboto","Aldrich","Garamond","PTSans","Montserrat","Cancel")
        val myAlertDialog: AlertDialog.Builder = AlertDialog.Builder(context)
        myAlertDialog.setTitle("Select font")
        myAlertDialog.setItems(choice) { _, item ->
            when {
                choice[item] == "Roboto" -> {
                    noteEdt.setFontFamily("Roboto")
                }
                choice[item] == "Aldrich" -> {
                    noteEdt.setFontFamily("Aldrich")
                }
                choice[item] == "Garamond" -> {
                    noteEdt.setFontFamily("Garamond")
                }
                choice[item] == "PTSans" -> {
                    noteEdt.setFontFamily("PTSans")
                }
                choice[item] == "Montserrat" -> {
                    noteEdt.setFontFamily("Montserrat")
                }
                // Select "Cancel" to cancel the task
                choice[item] == "Cancel" -> {
                }
            }
        }
        myAlertDialog.show()
    }
}