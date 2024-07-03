package avelios.starwarsreferenceapp.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import avelios.starwarsreferenceapp.GlobalToast
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


/**
 * Manages network connectivity status and provides a flow to observe network availability changes.
 */
interface NetworkManager {
    val isNetworkAvailable: StateFlow<Boolean>
}

class NetworkManagerImpl(
    private val connectivityManager: ConnectivityManager,
    private val context: Context
) : NetworkManager {
    private val _isNetworkAvailable = MutableStateFlow(false)
    override val isNetworkAvailable: StateFlow<Boolean> = _isNetworkAvailable.asStateFlow()

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            _isNetworkAvailable.value = true
            GlobalToast.show(context, INTERNET_AVAILABLE)
        }

        override fun onLost(network: Network) {
            _isNetworkAvailable.value = false
            GlobalToast.show(context, INTERNET_LOST)
        }

        override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
            _isNetworkAvailable.value = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            if (_isNetworkAvailable.value) GlobalToast.show(context, INTERNET_AVAILABLE)
            else GlobalToast.show(context, INTERNET_LOST)
        }
    }

    init {
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)

        _isNetworkAvailable.value = isNetworkCurrentlyAvailable()
        if (_isNetworkAvailable.value) GlobalToast.show(context, INTERNET_AVAILABLE)
        else GlobalToast.show(context, INTERNET_LOST)
    }

    private fun isNetworkCurrentlyAvailable(): Boolean {
        val activeNetwork = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }

    private companion object {
        const val INTERNET_AVAILABLE = "Internet Connection Established!"
        const val INTERNET_LOST = "Lost Internet Connection!"
    }
}

