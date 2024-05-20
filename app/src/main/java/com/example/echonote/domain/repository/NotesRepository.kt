package com.example.echonote.domain.repository

import com.example.echonote.core.utils.Result
import com.example.echonote.domain.model.CreateNote
import com.example.echonote.domain.model.Notes
import kotlinx.coroutines.flow.Flow

interface NotesRepository{
    suspend fun createNote(createNote:CreateNote): Flow<Result<Boolean>>
    suspend fun fetchNotes(): Flow<Result<List<Notes>>>
    suspend fun deleteNote(noteId:String):Flow<Result<Boolean>>

    suspend fun updateNote(noteId:String,createNote:CreateNote):Flow<Result<Boolean>>
    suspend fun bookmarkNote(noteId:String,bookmark:Boolean):Flow<Result<Boolean>>
}