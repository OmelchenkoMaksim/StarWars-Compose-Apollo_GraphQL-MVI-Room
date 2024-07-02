package avelios.starwarsreferenceapp.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.widget.Toast
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NetworkManagerImpl(
    private val connectivityManager: ConnectivityManager,
    private val context: Context
) : NetworkManager {
    private val _isNetworkAvailable = MutableStateFlow(false)
    override val isNetworkAvailable: StateFlow<Boolean> = _isNetworkAvailable.asStateFlow()

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            _isNetworkAvailable.value = true
            showToast(INTERNET_AVAILABLE)
        }

        override fun onLost(network: Network) {
            _isNetworkAvailable.value = false
            showToast(INTERNET_LOST)
        }

        override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
            _isNetworkAvailable.value = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            if (_isNetworkAvailable.value) showToast(INTERNET_AVAILABLE)
            else showToast(INTERNET_LOST)
        }
    }

    init {
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)

        _isNetworkAvailable.value = isNetworkCurrentlyAvailable()
        if (_isNetworkAvailable.value) showToast(INTERNET_AVAILABLE)
        else showToast(INTERNET_LOST)
    }

    private fun isNetworkCurrentlyAvailable(): Boolean {
        val activeNetwork = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private companion object {
        const val INTERNET_AVAILABLE = "Internet Connection Established!"
        const val INTERNET_LOST = "Lost Internet Connection!"
    }
}
