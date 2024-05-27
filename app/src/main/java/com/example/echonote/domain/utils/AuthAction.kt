package com.example.echonote.domain.utils

import com.example.echonote.domain.model.User

sealed class AuthAction {
    data class Login(val email:String, val password:String):AuthAction()
    data class CreateAccount(val userName:String, val email:String,val password:String):AuthAction()
    data class ForgotPassword(val email:String):AuthAction()
    data class DeleteAccount(val email: String, val password: String):AuthAction()
    data class UpdateUser(val user: User):AuthAction()
    data object  GetUser:AuthAction()
    data object Logout:AuthAction()

}