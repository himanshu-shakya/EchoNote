package com.example.echonote.domain.utils

sealed class NotesAction{
    data class CreateNote(val createNote: com.example.echonote.domain.model.CreateNote):NotesAction()
    data object FetchNotes:NotesAction()
    data class DeleteNote(val noteId:String):NotesAction()
    data class UpdateNote(val noteId:String, val note:com.example.echonote.domain.model.CreateNote):NotesAction()
    data class BookmarkNote(val noteId:String, val bookmark:Boolean):NotesAction()
}