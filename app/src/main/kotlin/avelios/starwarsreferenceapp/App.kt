package avelios.starwarsreferenceapp

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.widget.Toast
import androidx.room.Room
import avelios.starwarsreferenceapp.ui.theme.ThemeVariant
import avelios.starwarsreferenceapp.ui.theme.TypographyVariant
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.network.okHttpClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import timber.log.Timber

internal class App : Application() {
    private lateinit var networkCallback: ConnectivityManager.NetworkCallback
    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val appModule = module {
        single {
            Room.databaseBuilder(get(), AppDatabase::class.java, ROOM_NAME).build()
        }
        single { get<AppDatabase>().characterDao() }
        single { get<AppDatabase>().starshipDao() }
        single { get<AppDatabase>().planetDao() }
        single {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()

            ApolloClient.Builder()
                .serverUrl(HOST_GRAPHQL)
                .okHttpClient(okHttpClient)
                .build()
        }
        single {
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        }
        single { provideSharedPreferences(androidContext()) }
        single { SettingsManager(get()) }

        single { StarWarsRepository(get(), get(), get(), get()) }

        single { MainActor(get()) }

        single { NetworkManager(get()) }
        viewModel { MainViewModel(get(), get(), get()) }
    }

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(appModule)
        }
        Timber.plant(Timber.DebugTree())
        setupNetworkListener()
    }

    private fun provideSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE)
    }

    private fun setupNetworkListener() {
        val connectivityManager: ConnectivityManager by inject()
        val mainViewModel: MainViewModel by inject()

        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                appScope.launch {
                    Toast.makeText(this@App, INTERNET_AVAILABLE, Toast.LENGTH_SHORT).show()
                    mainViewModel.loadData()
                }
            }

            override fun onLost(network: Network) {
                appScope.launch {
                    Toast.makeText(this@App, INTERNET_LOST, Toast.LENGTH_SHORT).show()
                }
            }
        }

        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }

    private companion object {
        const val SP_FILE_NAME = "settings"
        const val ROOM_NAME = "starwars-database"
        const val HOST_GRAPHQL = "https://swapi-graphql.netlify.app/.netlify/functions/index"
        const val INTERNET_AVAILABLE = "Internet connection established"
        const val INTERNET_LOST = "Lost internet connection"
    }
}

internal class SettingsManager(private val sharedPreferences: SharedPreferences) {

    fun saveThemeVariant(themeVariant: ThemeVariant) {
        sharedPreferences.edit().putString(THEME_VARIANT, themeVariant.name).apply()
    }

    fun loadThemeVariant(): ThemeVariant {
        val themeVariantName = sharedPreferences.getString(THEME_VARIANT, ThemeVariant.MorningMystic.name)
        return ThemeVariant.valueOf(themeVariantName ?: ThemeVariant.MorningMystic.name)
    }

    fun saveTypographyVariant(typographyVariant: TypographyVariant) {
        sharedPreferences.edit().putString(TYPOGRAPHY_VARIANT, typographyVariant.name).apply()
    }

    fun loadTypographyVariant(): TypographyVariant {
        val typographyVariantName = sharedPreferences.getString(TYPOGRAPHY_VARIANT, TypographyVariant.Classic.name)
        return TypographyVariant.valueOf(typographyVariantName ?: TypographyVariant.Classic.name)
    }

    fun setDarkMode(isDarkMode: Boolean) {
        sharedPreferences.edit().putBoolean(IS_DARK_MODE, isDarkMode).apply()
    }

    fun isDarkMode(): Boolean {
        return sharedPreferences.getBoolean(IS_DARK_MODE, false)
    }

    internal companion object PreferencesConstants {
        const val THEME_VARIANT = "theme_variant"
        const val TYPOGRAPHY_VARIANT = "typography_variant"
        const val IS_DARK_MODE = "isDarkMode"
    }
}

class NetworkManager(private val connectivityManager: ConnectivityManager) {
    private val _isNetworkAvailable = MutableStateFlow(false)
    val isNetworkAvailable: StateFlow<Boolean> = _isNetworkAvailable.asStateFlow()

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            _isNetworkAvailable.value = true
        }

        override fun onLost(network: Network) {
            _isNetworkAvailable.value = false
        }

        override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
            _isNetworkAvailable.value = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        }
    }

    init {
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)

        _isNetworkAvailable.value = isNetworkCurrentlyAvailable()
    }

    private fun isNetworkCurrentlyAvailable(): Boolean {
        val activeNetwork = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }
}
