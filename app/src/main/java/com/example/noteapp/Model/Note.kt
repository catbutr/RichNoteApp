package com.example.noteapp.Model

import android.graphics.Color
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "Notes")
data class Note(
    @ColumnInfo(name = "title")
    val noteTitle: String,
    @ColumnInfo(name = "text")
    val noteText: String,
    @ColumnInfo(name = "date_time")
    val dateTime: String,
    @ColumnInfo(name = "color")
    var backgroundColor: Int = Color.GRAY,
    @ColumnInfo(name = "notebook")
    var noteBook: String = "My Notes"
): Parcelable{
    @IgnoredOnParcel
    @PrimaryKey(autoGenerate = true) var id = 0
    @IgnoredOnParcel
    var isFavourite = false
}