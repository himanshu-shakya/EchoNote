package com.example.echonote.domain.repository

import kotlinx.coroutines.flow.Flow

interface NetworkConnectivityObserver {
    fun observer(): Flow<Status>
    enum class Status {
        Available, UnAvailable , Losing , Lost
    }

}