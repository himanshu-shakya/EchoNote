package com.example.echonote

import android.app.Application
import com.example.echonote.di.firebaseModules
import com.example.echonote.di.repositoryModules
import com.example.echonote.di.viewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.app
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class EchoNoteApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        startKoin {
            androidContext(this@EchoNoteApp)
            modules(
                listOf(
                    viewModel,
                    firebaseModules,
                    repositoryModules
                )
            )
        }
    }
}