package com.example.doriclingo.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.TimeoutCancellationException

//timeout duration in milliseconds
private const val TIMEOUT_DURATION = 5000L

//function to test if the network is available
suspend fun isNetworkAvailable(context: Context): Boolean {
    return try {
        //runs the code block adhering to the timeout length
        withTimeout(TIMEOUT_DURATION) {
            //gets the system connectivity manager to access the network state
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            //checks network capabilities of the current device
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //gets the current network and returns false if unavailable
                val network = connectivityManager.activeNetwork ?: return@withTimeout false
                //gets the capabilities of the current network
                val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return@withTimeout false
                //checks for internet capability
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            } else {
                //uses an older API for devices below android M
                val networkInfo = connectivityManager.activeNetworkInfo
                //returns true if the network is connected, returns false if not
                networkInfo != null && networkInfo.isConnected
            }
        }
    } catch (e: TimeoutCancellationException) {
        //returns false if the timeout is reached
        false
    }
}

