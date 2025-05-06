package com.example.doriclingo.network

import kotlinx.coroutines.flow.Flow

//interface for the network observer, providing flexibility rather than a hard coded class
interface NetworkObserver {
    fun observe(): Flow<Status>

    //enum class used as the status options will not change
    enum class Status{
        //statuses
        Available, Unavailable, Losing, Lost
    }
}