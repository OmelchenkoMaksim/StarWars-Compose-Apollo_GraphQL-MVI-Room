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
import avelios.starwarsreferenceapp.data.local.AppDatabase
import avelios.starwarsreferenceapp.data.repository.StarWarsRepository
import avelios.starwarsreferenceapp.data.repository.StarWarsRepositoryImpl
import avelios.starwarsreferenceapp.mvi.MainAction
import avelios.starwarsreferenceapp.mvi.MainActor
import avelios.starwarsreferenceapp.mvi.MainActorImpl
import avelios.starwarsreferenceapp.util.NetworkManager
import avelios.starwarsreferenceapp.util.NetworkManagerImpl
import avelios.starwarsreferenceapp.util.SettingsManager
import avelios.starwarsreferenceapp.viewmodel.MainViewModel
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.network.okHttpClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
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

        single { appScope }

        single<StarWarsRepository> { StarWarsRepositoryImpl(get(), get(), get(), get()) }
        single<NetworkManager> { NetworkManagerImpl(get(), androidContext()) }

        single<MainActor> {
            MainActorImpl(
                repository = get(),
                networkManager = get(),
                settingsManager = get(),
                coroutineScope = get()
            )
        }

        viewModel { MainViewModel(get(), get()) }
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
                    mainViewModel.handleIntent(MainAction.LoadData)
                }
            }

            override fun onLost(network: Network) = Unit
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
    }
}

internal object GlobalToast {
    private var toast: Toast? = null

    fun show(context: Context, message: String) {
        toast?.cancel()
        toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
        toast?.show()
    }
}

