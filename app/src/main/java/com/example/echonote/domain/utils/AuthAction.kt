package com.example.echonote.domain.utils

sealed class AuthAction {
    data class Login(val email:String, val password:String):AuthAction()
    data class CreateAccount(val userName:String, val email:String,val password:String):AuthAction()
    data class ForgotPassword(val email:String):AuthAction()
    data object  GetUser:AuthAction()
    data object Logout:AuthAction()
    data class DeleteAccount(val email: String, val password: String):AuthAction()

}