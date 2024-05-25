package com.example.echonote.data.repository

import android.util.Log
import com.example.echonote.core.utils.Result
import com.example.echonote.domain.model.User
import com.example.echonote.domain.model.toUser
import com.example.echonote.domain.repository.AuthRepository
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) : AuthRepository {
    override suspend fun login(email: String, password: String): Flow<Result<Boolean>> {
        return flow {
            try {
                firebaseAuth.signInWithEmailAndPassword(email, password).await()
                emit(Result.Success(true))
            } catch (e: Exception) {
                emitError(e)
            }
        }
    }

    override suspend fun createAccount(
        userName: String,
        email: String,
        password: String,
    ): Flow<Result<Boolean>> = flow {
        try {
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val user = User(name = userName, email = email)
            storeUser(user, authResult.user!!.uid).collect {
                emit(Result.Success(true))
            }
        } catch (e: Exception) {
            emitError(e)
        }
    }

    override suspend fun storeUser(user: User, userId: String): Flow<Result<Boolean>> = flow {
        try {
            val userDocument = firestore.collection("Users").document(userId)
            val userData = mapOf(
                "userID" to userId,
                "name" to user.name,
                "email" to user.email,
                "avatar" to user.avatar,
            )
            userDocument.set(userData).await()
            emit(Result.Success(true))
        } catch (e: Exception) {
            emitError(e)
        }
    }

    override suspend fun getUser(): Flow<Result<User>> {
        return flow {
            try {
                val userID = firebaseAuth.currentUser?.uid!!
                val user =
                    firestore.collection("Users").whereEqualTo("userID", userID).get().await()
                        .toUser()
                emit(Result.Success(user))
            } catch (e: Exception) {
                emitError(e)
            }
        }
    }

    override suspend fun forgotPassword(email: String): Flow<Result<Boolean>> = flow {
        try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            emit(Result.Success(true))
        } catch (e: Exception) {
            emitError(e)
        }
    }

    override suspend fun logout(): Flow<Result<Boolean>> = flow {
        try {
            firebaseAuth.signOut()
            emit(Result.Success(true))
        } catch (e: Exception) {
            emitError(e)
        }
    }

    override suspend fun deleteAccount(email: String, password: String): Flow<Result<Boolean>> = flow {
        val user = FirebaseAuth.getInstance().currentUser
        val firestore = FirebaseFirestore.getInstance()
        if (user == null) {
            emit(Result.Error("No user is currently signed in"))
            return@flow
        }
        try {
            val credential = EmailAuthProvider.getCredential(email, password)
            user.reauthenticate(credential).await()

            // Delete user document from Firestore
            firestore.collection("Users").document(user.uid).delete().await()

            // Fetch and delete all notes associated with the user
            val notesQuerySnapshot = firestore.collection("Notes")
                .whereEqualTo("userID", user.uid)
                .get()
                .await()
            for (document in notesQuerySnapshot.documents) {
                firestore.collection("Notes").document(document.id).delete().await()
            }

            // Finally, delete the user authentication
            user.delete().await()

            emit(Result.Success(true))
        } catch (e: Exception) {
            Log.i("TAG", "deleteAccount: ${e.message}")
            emit(Result.Error(e.message ?: "Unknown error occurred"))
        }
    }

    private suspend fun <T> FlowCollector<Result<T>>.emitError(e: Exception) {
        val errorMessage = when (e) {
            is FirebaseAuthException -> e.localizedMessage ?: "Unknown Firebase Auth Error"
            is FirebaseFirestoreException -> e.localizedMessage ?: "Unknown Firestore Error"
            else -> e.localizedMessage ?: "Unknown Error"
        }
        emit(Result.Error(errorMessage))
    }

}

