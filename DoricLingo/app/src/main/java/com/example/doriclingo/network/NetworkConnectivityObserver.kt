package com.example.doriclingo.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class NetworkConnectivityObserver(
    private val context: Context
): NetworkObserver {

    //gets the connectivity manager system function
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    //observes the network status
    override fun observe(): Flow<NetworkObserver.Status> {
        return callbackFlow {
            //creates a callback to respond to changes in state
            val callback = object : ConnectivityManager.NetworkCallback() {
                //called when the network becomes available
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    launch { send(NetworkObserver.Status.Available) }
                }
                //called when the network connection is about to be lost
                override fun onLosing(network: Network, maxMsToLive: Int) {
                    super.onLosing(network, maxMsToLive)
                    launch { send(NetworkObserver.Status.Losing) }

                }
                //called when the network connection is lost
                override fun onLost(network: Network) {
                    super.onLost(network)
                    launch { send(NetworkObserver.Status.Lost) }
                }
                //called when the network is unavailable and connection attempts fail
                override fun onUnavailable() {
                    super.onUnavailable()
                    launch { send(NetworkObserver.Status.Unavailable) }
                }
            }
            //registers the callback with the connectivity manager for the system to listen for connectivity
            connectivityManager.registerDefaultNetworkCallback(callback)
            //unregisters the callback - called when the flow is closed and cleans up the callback
            awaitClose {
                connectivityManager.unregisterNetworkCallback(callback)
            }
        //triggers when there is a change in status
        }.distinctUntilChanged()
    }
}