package com.example.echonote.domain.repository

import com.example.echonote.core.utils.Result
import com.example.echonote.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(email:String, password:String): Flow<Result<Boolean>>
    suspend fun createAccount(userName:String,email: String,password: String):Flow<Result<Boolean>>
    suspend fun forgotPassword(email: String):Flow<Result<Boolean>>
    suspend fun storeUser(user: User,userId:String):Flow<Result<Boolean>>
    suspend fun getUser():Flow<Result<User>>
    suspend fun logout(): Flow<Result<Boolean>>
    suspend fun deleteAccount(email :String , password: String): Flow<Result<Boolean>>
    suspend fun updateUser(user: User): Flow<Result<Boolean>>
}