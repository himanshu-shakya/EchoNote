package com.example.echonote.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.echonote.presentation.CreateAccountScreen
import com.example.echonote.presentation.CreateNotesScreen
import com.example.echonote.presentation.ForgotPasswordScreen
import com.example.echonote.presentation.LoginScreen
import com.example.echonote.presentation.MainScreen
import com.example.echonote.presentation.SplashScreen
import com.example.echonote.presentation.UpdateNoteScreen
import com.example.echonote.presentation.viewModel.AuthViewModel
import com.example.echonote.presentation.viewModel.NotesViewModel
import com.google.firebase.auth.FirebaseAuth
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun Navigation(
) {
    val navController = rememberNavController()
    val authViewModel = koinViewModel<AuthViewModel>()
    val firebaseAuth = koinInject<FirebaseAuth>()
    val notesViewModel= koinViewModel<NotesViewModel>()
    NavHost(navController = navController, startDestination = NavConstants.SPLASH_SCREEN.name){

        composable(route =NavConstants.SPLASH_SCREEN.name){
            SplashScreen(navController,firebaseAuth)
        }
        composable(route = NavConstants.LOGIN_SCREEN.name){
            LoginScreen(authViewModel =authViewModel ,navController)
        }
        composable(route  = NavConstants.CREATE_ACCOUNT_SCREEN.name){
            CreateAccountScreen(authViewModel = authViewModel,navController)
        }
        composable(route = NavConstants.MAIN_SCREEN.name){
            MainScreen(navController,notesViewModel)
        }
        composable(route = NavConstants.FORGOT_PASSWORD_SCREEN.name){
            ForgotPasswordScreen(authViewModel = authViewModel, navController = navController)
        }
        composable(route = NavConstants.CREATE_NOTE_SCREEN.name){
            CreateNotesScreen(navController,notesViewModel)
        }
        composable(route =NavConstants.UPDATE_NOTE_SCREEN.name){
            UpdateNoteScreen(notesViewModel = notesViewModel, navController = navController)
        }
    }

}

enum class NavConstants  {
    CREATE_NOTE_SCREEN,
    CREATE_ACCOUNT_SCREEN,
    LOGIN_SCREEN,
    MAIN_SCREEN,
    SPLASH_SCREEN,
    FORGOT_PASSWORD_SCREEN,
    UPDATE_NOTE_SCREEN
}