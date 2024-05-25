package com.example.echonote.data.repository

import android.net.Uri
import com.example.echonote.core.utils.Result
import com.example.echonote.domain.model.CreateNote
import com.example.echonote.domain.model.Notes
import com.example.echonote.domain.model.toNotes
import com.example.echonote.domain.repository.NotesRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class NotesRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    private val storage:FirebaseStorage
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

                val storageReference = storage.reference
                val userId = firebaseAuth.currentUser?.uid ?: throw Exception("User not authenticated")
                // Check if image path is not empty
                if (createNote.image != Uri.EMPTY) {
                    // Create a reference to the location where you want to upload the image
                    val imageReference = storageReference.child("images/$userId/${createNote.image}")
                    // Upload the image to Firebase Storage
                    val uploadTask: UploadTask = imageReference.putFile(createNote.image)
                    uploadTask.await()

                    // Get the download URL of the uploaded image
                    val downloadUrl = imageReference.downloadUrl.await().toString()
                    // Create a note map with the download URL
                    val noteMap = hashMapOf(
                        "text" to createNote.text,
                        "title" to createNote.title,
                        "image" to downloadUrl,
                        "userID" to firebaseAuth.currentUser?.uid,
                        "date" to createNote.date,
                        "isBookmarked" to createNote.isBookmarked
                    )

                    // Save the note to Firestore
                    firestore.collection("notes").document().set(noteMap).await()
                    emit(Result.Success(true))
                } else {
                    // Create a note map without the image URL
                    val noteMap = hashMapOf(
                        "text" to createNote.text,
                        "title" to createNote.title,
                        "image" to "",
                        "userID" to firebaseAuth.currentUser?.uid,
                        "date" to createNote.date,
                        "isBookmarked" to createNote.isBookmarked
                    )

                    // Save the note to Firestore
                    firestore.collection("notes").document().set(noteMap).await()
                    emit(Result.Success(true))
                }

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

    override suspend fun updateNote(noteId: String, createNote: CreateNote): Flow<Result<Boolean>> {
        return flow {
            try {
                val userId = firebaseAuth.currentUser?.uid ?: throw Exception("User not authenticated")

                // Ensure that the user has permission to update the note
                val noteDocument = firestore.collection("notes").document(noteId).get().await()
                val noteUserId = noteDocument.getString("userID")
                if (noteUserId != userId) {
                    throw Exception("User does not have permission to update this note")
                }

                // Prepare the update data
                val updateMap = mutableMapOf<String, Any?>(
                    "text" to createNote.text,
                    "title" to createNote.title,
                    "date" to createNote.date
                )

                // If a new image is provided, upload it to Firebase Storage
                if (createNote.image != Uri.EMPTY) {
                    val imageUrl = uploadImageToStorage(userId, createNote.image)
                    updateMap["image"] = imageUrl
                }

                // Update the note in Firestore
                firestore.collection("notes").document(noteId).update(updateMap).await()

                emit(Result.Success(true))
            } catch (e: Exception) {
                // Emit error result if an exception occurs
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
    private suspend fun uploadImageToStorage(userId: String, imageUri: Uri): String {
        val storageReference = storage.reference
        val imageReference = storageReference.child("images/$userId/${imageUri.lastPathSegment}")
        val uploadTask = imageReference.putFile(imageUri)
        uploadTask.await()
        return imageReference.downloadUrl.await().toString()
    }

}

