package com.example.echonote.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.echonote.presentation.BookmarkNotesScreen
import com.example.echonote.presentation.CreateAccountScreen
import com.example.echonote.presentation.CreateNotesScreen
import com.example.echonote.presentation.DeleteAccountScreen
import com.example.echonote.presentation.EditProfileScreen
import com.example.echonote.presentation.ForgotPasswordScreen
import com.example.echonote.presentation.LoginScreen
import com.example.echonote.presentation.MainScreen
import com.example.echonote.presentation.SettingsScreen
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
    val notesViewModel = koinViewModel<NotesViewModel>()
    NavHost(navController = navController, startDestination = NavConstants.SPLASH_SCREEN.name) {
        composable(route = NavConstants.SPLASH_SCREEN.name) {
            SplashScreen(navController, firebaseAuth)
        }
        navigation(
            route = NavConstants.AUTH_ROUTE.name,
            startDestination = NavConstants.LOGIN_SCREEN.name
        ) {

            composable(route = NavConstants.LOGIN_SCREEN.name) {
                LoginScreen(authViewModel = authViewModel, navController)
            }
            composable(route = NavConstants.CREATE_ACCOUNT_SCREEN.name) {
                CreateAccountScreen(authViewModel = authViewModel, navController)
            }
            composable(route = NavConstants.FORGOT_PASSWORD_SCREEN.name) {
                ForgotPasswordScreen(authViewModel = authViewModel, navController = navController)
            }
        }
        navigation(
            route = NavConstants.MAIN_ROUTE.name,
            startDestination = NavConstants.MAIN_SCREEN.name
        ) {
            composable(route = NavConstants.MAIN_SCREEN.name) {
                MainScreen(
                    navController = navController,
                    notesViewModel = notesViewModel,
                    authViewModel = authViewModel,
                )
            }
            composable(route = NavConstants.CREATE_NOTE_SCREEN.name) {
                CreateNotesScreen(navController, notesViewModel)
            }
            composable(route = NavConstants.UPDATE_NOTE_SCREEN.name) {
                UpdateNoteScreen(notesViewModel = notesViewModel, navController = navController)
            }
            composable(route = NavConstants.SETTINGS_SCREEN.name) {
                SettingsScreen(navController = navController, authViewModel = authViewModel)
            }
            composable(route = NavConstants.BOOKMARKS_SCREEN.name) {
                BookmarkNotesScreen(notesViewModel = notesViewModel, navController = navController)
            }
            composable(route = NavConstants.DELETE_ACCOUNT_SCREEN.name) {
                DeleteAccountScreen(authViewModel = authViewModel, navController = navController)
            }
            composable(route = NavConstants.EDIT_PROFILE_SCREEN.name) {
                EditProfileScreen(authViewModel = authViewModel, navController = navController)
            }
        }


    }

}

enum class NavConstants {

    AUTH_ROUTE,
    CREATE_NOTE_SCREEN,
    CREATE_ACCOUNT_SCREEN,
    LOGIN_SCREEN,
    MAIN_SCREEN,
    SPLASH_SCREEN,
    FORGOT_PASSWORD_SCREEN,
    UPDATE_NOTE_SCREEN,
    SETTINGS_SCREEN,
    BOOKMARKS_SCREEN,
    DELETE_ACCOUNT_SCREEN,
    EDIT_PROFILE_SCREEN,
    MAIN_ROUTE
}