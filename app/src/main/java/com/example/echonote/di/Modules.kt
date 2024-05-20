package com.example.echonote.di

import com.example.echonote.data.repository.AuthRepositoryImpl
import com.example.echonote.data.repository.NetworkConnectivityObserverImpl
import com.example.echonote.data.repository.NotesRepositoryImpl
import com.example.echonote.domain.repository.AuthRepository
import com.example.echonote.domain.repository.NetworkConnectivityObserver
import com.example.echonote.domain.repository.NotesRepository
import com.example.echonote.presentation.viewModel.AuthViewModel
import com.example.echonote.presentation.viewModel.NetworkViewmodel
import com.example.echonote.presentation.viewModel.NotesViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val firebaseModules = module {
    single { FirebaseAuth.getInstance() }
    single{FirebaseFirestore.getInstance()}
}
val repositoryModules = module {
    single<AuthRepository> { AuthRepositoryImpl(get(),get())}
    single<NotesRepository>{NotesRepositoryImpl(get(),get())}
    single<NetworkConnectivityObserver>{ NetworkConnectivityObserverImpl(androidContext()) }
}

val viewModel = module {
    viewModel<AuthViewModel>{ AuthViewModel(get()) }
    viewModel<NotesViewModel> { NotesViewModel(get()) }
    viewModel<NetworkViewmodel>{NetworkViewmodel(get())}
}