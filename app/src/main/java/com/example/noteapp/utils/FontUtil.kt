package com.example.noteapp.utils

import android.content.Context
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.LifecycleOwner
import com.example.noteapp.MainActivity
import com.example.noteapp.R
import com.example.noteapp.viewModel.DataManager

class FontUtil(private val thisContext: Context,
               private val thisActivity: MainActivity,
               private val thisLCOwner:LifecycleOwner) {

    private val vmUtil = NoteViewModelUtil(thisActivity.viewModel,thisLCOwner, thisActivity)

    private fun changingFont(selectedFont:Int){
        val prefs = DataManager(thisContext)
        prefs.setValue_int("selectedFont", selectedFont)
        vmUtil.uploadNotesToRecyclerView(thisActivity.noteRVAdapter,
            thisActivity.viewModel,
            thisActivity.selectedNotebook,thisActivity.isAsc)
        thisActivity.recreate()
    }

    fun selectingFont(){
        val myFontAlertDialog: AlertDialog.Builder = AlertDialog.Builder(thisContext)
        myFontAlertDialog.setTitle("Select Font")
        val fontChoice = arrayOf<CharSequence>("Roboto","Aldrich","Garamond","PTSans","Montserrat","Cancel")
        myFontAlertDialog.setItems(fontChoice){ _, item ->
            when{
                fontChoice[item] == "Roboto" -> {
                    thisActivity.selectedFont = R.style.Roboto
                    changingFont(R.style.Roboto)
                }
                fontChoice[item] == "Aldrich" -> {
                    thisActivity.selectedFont = R.style.Aldrich
                    changingFont(R.style.Aldrich)
                }
                fontChoice[item] == "Garamond" -> {
                    thisActivity.selectedFont = R.style.Garamond
                    changingFont(R.style.Garamond)
                }
                fontChoice[item] == "PTSans" -> {
                    thisActivity.selectedFont = R.style.PTSans
                    changingFont(R.style.PTSans)
                }
                fontChoice[item] == "Montserrat" -> {
                    thisActivity.selectedFont = R.style.Montserrat
                    changingFont(R.style.Montserrat)
                }
            }
        }
        myFontAlertDialog.show()
    }
}