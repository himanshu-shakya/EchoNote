package com.example.echonote.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.echonote.domain.repository.NetworkConnectivityObserver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NetworkViewmodel(private val networkConnectivityObserver: NetworkConnectivityObserver): ViewModel() {
    private val _networkState = MutableStateFlow(NetworkConnectivityObserver.Status.UnAvailable
    )
    val networkState = _networkState.asStateFlow()
    init {
        observeNetworkConnectivity()
    }
    private fun  observeNetworkConnectivity() {
        viewModelScope.launch {
            networkConnectivityObserver.observer().collect {status->
                _networkState.update { status }
            }
        }
    }

}