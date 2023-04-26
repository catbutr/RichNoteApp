package com.example.noteapp.Model

import android.graphics.Color
import android.os.Parcelable
import androidx.core.content.res.ResourcesCompat
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.noteapp.NoteRVAdapter.NoteRVCompat.getResources
import com.example.noteapp.R
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize as Parcelize

@Parcelize
@Entity(tableName = "Notes")
data class Note(
    @ColumnInfo(name = "title")
    val noteTitle: String,
    @ColumnInfo(name = "text")
    val text: String,
    @ColumnInfo(name = "date_time")
    val dateTime: String,
    @ColumnInfo(name = "color")
    var backgroundColor: Int = Color.GRAY
): Parcelable{
    @IgnoredOnParcel
    @PrimaryKey(autoGenerate = true) var id = 0
}