package com.example.noteapp.Model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize as Parcelize

@Parcelize
@Entity(tableName = "Notes")
data class Note(
    @ColumnInfo(name = "title")
    val noteTitle: String,
    @ColumnInfo(name = "text")
    val text: String,
    @ColumnInfo(name = "date_time")
    val dateTime: String
): Parcelable{
    @PrimaryKey(autoGenerate = true) var id = 0
}