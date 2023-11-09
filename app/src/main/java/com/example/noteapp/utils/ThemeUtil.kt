package com.example.noteapp.utils

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.example.noteapp.MainActivity
import com.example.noteapp.R
import com.example.noteapp.viewModel.DataManager

class ThemeUtil(private val thisContext: Context,
                private val thisActivity: MainActivity) {

    private fun selectingTheme(themeID: Int){
        val prefs = DataManager(thisContext)
        prefs.setValue_int("chosenTheme", themeID)
        thisContext.setTheme(themeID)
        thisActivity.recreate()
    }

    fun changingThemeDialog(){
        val myThemeAlertDialog: AlertDialog.Builder = AlertDialog.Builder(thisContext)
        myThemeAlertDialog.setTitle("Select Theme")
        val themeChoice = arrayOf<CharSequence>("Standard", "Red","Blue",
            "Green","Yellow","Orange",
            "Purple","Cancel")
        myThemeAlertDialog.setItems(themeChoice) { _, item ->
            when {
                themeChoice[item] == "Standard" -> {
                    selectingTheme(R.style.Theme_NoteApp)
                }
                themeChoice[item] == "Red" -> {
                    selectingTheme(R.style.Theme_NoteApp_Red)
                }
                themeChoice[item] == "Blue" -> {
                    selectingTheme(R.style.Theme_NoteApp_Blue)
                }
                themeChoice[item] == "Green" -> {
                    selectingTheme(R.style.Theme_NoteApp_Green)
                }
                themeChoice[item] == "Yellow" -> {
                    selectingTheme(R.style.Theme_NoteApp_Yellow)
                }
                themeChoice[item] == "Orange" -> {
                    selectingTheme(R.style.Theme_NoteApp_Orange)
                }
                themeChoice[item] == "Purple" -> {
                    selectingTheme(R.style.Theme_NoteApp_Purple)
                }
            }
        }
        myThemeAlertDialog.show()
    }
}