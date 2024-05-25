package com.example.echonote.domain.model

import android.net.Uri
import com.google.firebase.firestore.QuerySnapshot

data class CreateNote(
    val text: String,
    val title: String,
    val image: Uri = Uri.EMPTY,
    val userID: String = "",
    val date: String = "",
    val isBookmarked: Boolean = false
)

data class Notes(
    val id: String,
    val text: String,
    val title: String,
    val image: String,
    val date: String,
    val isBookmarked: Boolean = false
)

fun QuerySnapshot.toNotes(): List<Notes> {
    val notes = mutableListOf<Notes>()
    this.forEach {
        val map = it.data
        val text = map.getValue("text") as String
        val title = map.getValue("title") as String
        val image = map.getValue("image") as String
        val date = map.getValue("date") as String
        val id = it.id
        val isBookmarked = map.getValue("isBookmarked") as Boolean
        notes.add(
            Notes(
                text =text,
                title =title,
                image =image,
                date =date,
                id =id,
                isBookmarked = isBookmarked
            )
        )
    }
    return notes
}


