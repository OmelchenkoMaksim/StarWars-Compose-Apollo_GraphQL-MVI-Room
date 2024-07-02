package avelios.starwarsreferenceapp.util

import kotlinx.coroutines.flow.StateFlow

interface NetworkManager {
    val isNetworkAvailable: StateFlow<Boolean>
}
