package com.example.noteapp.utils

import android.content.Context
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.LifecycleOwner
import com.example.noteapp.MainActivity
import com.example.noteapp.R
import com.example.noteapp.viewModel.DataManager
import com.google.android.material.navigation.NavigationView

class NotebookUtil(private val thisNavView:NavigationView,
                   private val thisContext: Context,
                   private val thisActivity: MainActivity,
                   private val thisLCOwner: LifecycleOwner) {

    private val vmUtil = NoteViewModelUtil(thisActivity.viewModel,thisLCOwner, thisActivity)


    private fun addNewNotebook(title:String)
    {
        val prefs = DataManager(thisContext)
        thisActivity.addedDrawerMenuItems.add(title)
        prefs.setValue_stringSet("notebooks",HashSet(
            thisActivity.addedDrawerMenuItems))
        val existingMenu = thisNavView.menu
        existingMenu.add(R.id.nav_notebooks, (0..10000000).random(),1,title)
    }

    private fun removeNotebook(title:String)
    {
        val prefs = DataManager(thisContext)
        thisActivity.addedDrawerMenuItems.remove(title)
        thisNavView.menu.removeItem(thisActivity.latestSelectedNotebookID)
        prefs.setValue_stringSet("notebooks",HashSet(
            thisActivity.addedDrawerMenuItems))
    }

    fun addNotebook(){
        val currentLayout = LayoutInflater.from(thisContext)
        val newLayout = currentLayout.inflate(R.layout.edit_dialogue, null)
        val dialogBuilder = AlertDialog.Builder(thisContext)
        dialogBuilder.setView(newLayout)
        val userInput = newLayout.findViewById<EditText>(R.id.input_text)
        dialogBuilder.setCancelable(false)
        dialogBuilder.setPositiveButton("OK") { _, _ ->
            addNewNotebook(userInput.text.toString()) }
        dialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()}
        dialogBuilder.show()
    }

    fun removeNotebook(){
        val myAlertDialog: AlertDialog.Builder = AlertDialog.Builder(thisContext)
        myAlertDialog.setTitle("Do you really want to delete the notebook" +
                "and all the notes?")
        myAlertDialog.setPositiveButton("Accept") {_, _ ->
            removeNotebook(thisActivity.selectedNotebook)
            thisActivity.viewModel.deleteAllNotesFromNotebook(thisActivity.selectedNotebook)
            thisActivity.selectedNotebook = "My Notes"
            vmUtil.uploadNotesToRecyclerView(thisActivity.noteRVAdapter,
                thisActivity.viewModel,thisActivity.selectedNotebook,
            thisActivity.isAsc)
        }
        myAlertDialog.setNegativeButton("Cancel"){_, _ -> }
        myAlertDialog.show()
    }

    fun renameNotebook(){
        val currentLayout = LayoutInflater.from(thisContext)
        val newLayout = currentLayout.inflate(R.layout.edit_dialogue, null)
        val dialogBuilder = AlertDialog.Builder(thisContext)
        dialogBuilder.setView(newLayout)
        val userInput = newLayout.findViewById<EditText>(R.id.input_text)
        dialogBuilder.setCancelable(false)
        dialogBuilder.setPositiveButton("OK") { _, _ ->
            val newText = userInput.text.toString()
            var isDuplicateInList = false
            for (i in thisActivity.addedDrawerMenuItems.indices){
                if (newText == thisActivity.addedDrawerMenuItems[i]){
                    Toast.makeText(thisContext,"THIS NOTEBOOK ALREADY EXIST",
                        Toast.LENGTH_SHORT).show()
                    isDuplicateInList = true
                    break
                }
            }
            Toast.makeText(thisContext,isDuplicateInList.toString(), Toast.LENGTH_SHORT).show()
            if (!isDuplicateInList){
                thisActivity.viewModel.replaceNotebookTitle(thisActivity.selectedNotebook,newText)
                removeNotebook(thisActivity.selectedNotebook)
                addNewNotebook(newText)
                thisActivity.selectedNotebook = newText
                vmUtil.uploadNotesToRecyclerView(thisActivity.noteRVAdapter,
                    thisActivity.viewModel,thisActivity.selectedNotebook,
                    thisActivity.isAsc)
            }
        }
        dialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }
        dialogBuilder.show()
    }
}