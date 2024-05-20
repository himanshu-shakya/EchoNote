package com.example.echonote.data.repository

import com.example.echonote.core.utils.Result
import com.example.echonote.domain.model.CreateNote
import com.example.echonote.domain.model.Notes
import com.example.echonote.domain.model.toNotes
import com.example.echonote.domain.repository.NotesRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class NotesRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
) : NotesRepository {
    private suspend fun<T> FlowCollector<Result<T>>.emitError(e: Exception) {
        val errorMessage = when (e) {
            is FirebaseFirestoreException -> e.localizedMessage ?: "Unknown FireStore Error"
            else -> e.localizedMessage ?: "Unknown Error"
        }
        emit(Result.Error(errorMessage))
    }
    override suspend fun createNote(createNote: CreateNote): Flow<Result<Boolean>> {
        return flow {
            try {
                val documentReference = firestore.collection("notes").document()
                val noteMap = hashMapOf(
                    "text" to createNote.text,
                    "title" to createNote.title,
                    "image" to createNote.image,
                    "userID" to firebaseAuth.currentUser?.uid,
                    "date" to createNote.date,
                    "isBookmarked" to createNote.isBookmarked
                )
                documentReference.set(noteMap).await()
                emit(Result.Success(true))

            } catch (e: Exception) {
                emitError(e)
            }

        }
    }

    override suspend fun fetchNotes(): Flow<Result<List<Notes>>> {
        return flow {
            try {
                val currentUserId = firebaseAuth.currentUser?.uid
                val notes = firestore.collection("notes").whereEqualTo("userID", currentUserId).get().await().toNotes()
                emit(Result.Success(notes))
            } catch (e: Exception) {
                emitError(e)
            }
        }

    }

    override suspend fun deleteNote(noteId: String): Flow<Result<Boolean>> {
        return flow {
            try {
                firestore.collection("notes").document(noteId).delete().await()
                emit(Result.Success(true))
            } catch (e: Exception){
                emitError(e)
            }
        }
    }

    override suspend fun updateNote(noteId: String,createNote: CreateNote): Flow<Result<Boolean>> {
        return flow {
            try {
                val noteMap = mapOf(
                    "text" to createNote.text,
                    "title" to createNote.title,
                    "image" to createNote.image,
                    "userID" to firebaseAuth.currentUser?.uid,
                    "date" to createNote.date,
                )
                firestore.collection("notes").document(noteId).update(noteMap).await()
                emit(Result.Success(true))
            }catch (e: Exception){
                emitError(e)
            }
        }
    }

    override suspend fun bookmarkNote(noteId: String,bookmark: Boolean): Flow<Result<Boolean>> {
        return flow {
            try {
                firestore.collection("notes").document(noteId).update("isBookmarked" , bookmark).await()
                emit(Result.Success(    true))
            }catch (e: Exception){
                emitError(e)
            }
        }
    }


}

