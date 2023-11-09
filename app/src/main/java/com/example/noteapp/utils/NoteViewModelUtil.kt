package com.example.noteapp.utils

import android.view.MenuItem
import androidx.lifecycle.LifecycleOwner
import com.example.noteapp.MainActivity
import com.example.noteapp.NoteRVAdapter
import com.example.noteapp.viewModel.NoteViewModel

class NoteViewModelUtil(private val viewModel: NoteViewModel,
                        private val thisContext:LifecycleOwner,
                        private val thisActivity:MainActivity) {

    fun uploadNotesToRecyclerView(noteRVAdapter: NoteRVAdapter,
                                          viewModel: NoteViewModel,
                                  selectedNotebook:String,
                                  isAsc:Boolean){
        if (!isAsc) {
            viewModel.getAllNotesFromNotebookDesc(selectedNotebook).observe(thisContext) { list ->
                list?.let {
                    //on below line we are updating our list.
                    noteRVAdapter.updateList(it)
                }
            }
        }
        else{
            viewModel.getAllNotesFromNotebookAsc(selectedNotebook).observe(thisContext) { list ->
                list?.let {
                    //on below line we are updating our list.
                    noteRVAdapter.updateList(it)
                }
            }
        }
    }

    fun updateRecyclerView(item: MenuItem, noteRVAdapter: NoteRVAdapter,isAsc:Boolean){
        if (!isAsc) {
            viewModel.getAllNotesFromNotebookDesc(item.title.toString())
                .observe(thisContext) { list ->
                    list?.let {
                        //on below line we are updating our list.
                        noteRVAdapter.updateList(it)
                        thisActivity.selectedNotebook = item.title.toString()
                    }
                }
        }
        else{
            viewModel.getAllNotesFromNotebookAsc(item.title.toString())
                .observe(thisContext) { list ->
                    list?.let {
                        //on below line we are updating our list.
                        noteRVAdapter.updateList(it)
                        thisActivity.selectedNotebook = item.title.toString()
                    }
                }
        }
    }

    fun updateRecyclerViewForActionBar(selectedNotebook: String,isAsc:Boolean){
        thisActivity.notesRV.adapter = thisActivity.noteRVAdapter
        if (!isAsc) {
            viewModel.getAllNotesFromNotebookDesc(selectedNotebook)
                .observe(thisContext) { list ->
                    list?.let {
                        //on below line we are updating our list.
                        thisActivity.noteRVAdapter.updateList(it)
                    }
                }
        }
        else{
            viewModel.getAllNotesFromNotebookAsc(selectedNotebook)
                .observe(thisContext) { list ->
                    list?.let {
                        //on below line we are updating our list.
                        thisActivity.noteRVAdapter.updateList(it)
                    }
                }
        }
    }

    fun updateFavouritesRecyclerView(item: MenuItem, noteRVAdapter: NoteRVAdapter){
        viewModel.getAllFavourite().observe(thisContext) { list ->
            list?.let {
                //on below line we are updating our list.
                noteRVAdapter.updateList(it)
                thisActivity.selectedNotebook = item.title.toString()
            }
        }
    }

    fun searchDatabase(notebook:String, query: String) {
        // %" "% because our custom sql query will require that
        val searchQuery = "%$query%"
        thisActivity.notesRV.adapter = thisActivity.noteRVAdapter
        viewModel.searchDatabaseByTitle(notebook,searchQuery).observe(thisContext) { list ->
            list.let {
                thisActivity.noteRVAdapter.updateList(it)
            }
        }
    }
}