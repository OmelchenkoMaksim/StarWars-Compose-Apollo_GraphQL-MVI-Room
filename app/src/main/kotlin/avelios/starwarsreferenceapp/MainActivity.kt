package avelios.starwarsreferenceapp

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import avelios.starwarsreferenceapp.ui.theme.StarWarsReferenceAppTheme
import coil.compose.rememberAsyncImagePainter
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.exception.ApolloException
import com.apollographql.apollo3.network.okHttpClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class MainActivity : ComponentActivity() {
    private val charactersViewModel: CharactersViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StarWarsReferenceAppTheme {
                val characters by charactersViewModel.characters.collectAsState()
                val starships by charactersViewModel.starships.collectAsState()
                val planets by charactersViewModel.planets.collectAsState()

                MainScreen(characters, starships, planets)
            }
        }
    }
}

@Composable
fun MainScreen(characters: List<StarWarsCharacter>, starships: List<Starship>, planets: List<Planet>) {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { paddingValues: PaddingValues ->
        NavigationGraph(navController, characters, starships, planets, modifier = Modifier.padding(paddingValues))
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        NavigationItem.Characters,
        NavigationItem.Starships,
        NavigationItem.Planets
    )
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
    ) {
        val currentRoute = currentRoute(navController)
        items.forEach { item: NavigationItem ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun NavigationGraph(
    navController: NavHostController,
    characters: List<StarWarsCharacter>,
    starships: List<Starship>,
    planets: List<Planet>,
    modifier: Modifier = Modifier
) {
    NavHost(navController, startDestination = NavigationItem.Characters.route, modifier = modifier) {
        composable(NavigationItem.Characters.route) { CharactersScreen(characters) }
        composable(NavigationItem.Starships.route) { StarshipsScreen(starships) }
        composable(NavigationItem.Planets.route) { PlanetsScreen(planets) }
    }
}

@Composable
fun currentRoute(navController: NavHostController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}

@Composable
fun CharactersScreen(characters: List<StarWarsCharacter>) {
    Column {
        Text(text = "Characters", style = MaterialTheme.typography.bodyLarge)
        LazyColumn {
            items(characters) { character ->
                CharacterItem(character = character)
            }
        }
    }
}

@Composable
fun CharacterItem(character: StarWarsCharacter) {
    Column(modifier = Modifier.padding(8.dp)) {
        val painter = rememberAsyncImagePainter(
            model = "https://starwars-visualguide.com/assets/img/characters/${character.id}.jpg"
        )
        Image(painter = painter, contentDescription = null)
        Text(text = "Name: ${character.name}", style = MaterialTheme.typography.bodyMedium)
        Text(text = "Films Count: ${character.filmsCount}", style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun StarshipsScreen(starships: List<Starship>) {
    Column {
        Text(text = "Starships", style = MaterialTheme.typography.bodyLarge)
        LazyColumn {
            items(starships) { starship ->
                StarshipItem(starship = starship)
            }
        }
    }
}

@Composable
fun PlanetsScreen(planets: List<Planet>) {
    Column {
        Text(text = "Planets", style = MaterialTheme.typography.bodyLarge)
        LazyColumn {
            items(planets) { planet ->
                PlanetItem(planet = planet)
            }
        }
    }
}

@Composable
fun StarshipItem(starship: Starship) {
    Column(modifier = Modifier.padding(8.dp)) {
        val painter = rememberAsyncImagePainter(
            model = "https://starwars-visualguide.com/assets/img/starships/${starship.id}.jpg"
        )
        Image(painter = painter, contentDescription = null)
        Text(text = "Starship: ${starship.name}", style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun PlanetItem(planet: Planet) {
    Column(modifier = Modifier.padding(8.dp)) {
        val painter = rememberAsyncImagePainter(
            model = "https://starwars-visualguide.com/assets/img/planets/${planet.id}.jpg"
        )
        Image(painter = painter, contentDescription = null)
        Text(text = "Planet: ${planet.name}", style = MaterialTheme.typography.bodyMedium)
    }
}

sealed class NavigationItem(var route: String, var icon: ImageVector, var title: String) {
    data object Characters : NavigationItem("characters", Icons.Default.Person, "Characters")
    data object Starships : NavigationItem("starships", Icons.Default.Star, "Starships")
    data object Planets : NavigationItem("planets", Icons.Default.Place, "Planets")
}

@Entity(tableName = "characters")
data class StarWarsCharacter(
    @PrimaryKey val id: String,
    val name: String,
    val filmsCount: Int
)

@Dao
interface CharacterDao {
    @Query("SELECT * FROM characters")
    suspend fun getAllCharacters(): List<StarWarsCharacter>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacters(vararg characters: StarWarsCharacter)
}

@Dao
interface StarshipDao {
    @Query("SELECT * FROM starships")
    suspend fun getAllStarships(): List<Starship>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStarships(vararg starships: Starship)
}

@Dao
interface PlanetDao {
    @Query("SELECT * FROM planets")
    suspend fun getAllPlanets(): List<Planet>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlanets(vararg planets: Planet)
}

@Entity(tableName = "starships")
data class Starship(
    @PrimaryKey val id: String,
    val name: String,
    val model: String
)

@Entity(tableName = "planets")
data class Planet(
    @PrimaryKey val id: String,
    val name: String,
    val climate: String
)

@Database(entities = [StarWarsCharacter::class, Starship::class, Planet::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun characterDao(): CharacterDao
    abstract fun starshipDao(): StarshipDao
    abstract fun planetDao(): PlanetDao
}

class App : Application() {
    private lateinit var networkCallback: ConnectivityManager.NetworkCallback

    private val appModule = module {
        viewModel { CharactersViewModel(get(), get(), get(), get()) }
        single {
            Room.databaseBuilder(get(), AppDatabase::class.java, DATABASE_NAME).build()
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
    }

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(appModule)
        }
        setupNetworkListener()
    }

    private fun setupNetworkListener() {
        val connectivityManager: ConnectivityManager by inject()
        val charactersViewModel: CharactersViewModel by inject()

        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                Toast.makeText(this@App, TOAST_INTERNET_AVAILABLE, Toast.LENGTH_SHORT).show()
                if (charactersViewModel.areListsEmpty()) {
                    charactersViewModel.refreshData()
                }
            }

            override fun onLost(network: Network) {
                Toast.makeText(this@App, TOAST_INTERNET_LOST, Toast.LENGTH_SHORT).show()
            }
        }

        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }

    override fun onTerminate() {
        val connectivityManager: ConnectivityManager by inject()
        connectivityManager.unregisterNetworkCallback(networkCallback)
        super.onTerminate()
    }

    private companion object {
        const val DATABASE_NAME = "starwars-database"
        const val HOST_GRAPHQL = "https://swapi-graphql.netlify.app/.netlify/functions/index"
        const val TOAST_INTERNET_AVAILABLE = "Internet connection established"
        const val TOAST_INTERNET_LOST = "Lost internet connection"
    }
}

class CharactersViewModel(
    private val apolloClient: ApolloClient,
    private val characterDao: CharacterDao,
    private val starshipDao: StarshipDao,
    private val planetDao: PlanetDao
) : ViewModel() {
    private val _characters = MutableStateFlow<List<StarWarsCharacter>>(emptyList())
    val characters: StateFlow<List<StarWarsCharacter>> = _characters

    private val _starships = MutableStateFlow<List<Starship>>(emptyList())
    val starships: StateFlow<List<Starship>> = _starships

    private val _planets = MutableStateFlow<List<Planet>>(emptyList())
    val planets: StateFlow<List<Planet>> = _planets

    init {
        viewModelScope.launch {
            fetchCharacters()
            fetchStarships()
            fetchPlanets()
        }
    }

    fun areListsEmpty(): Boolean {
        return _characters.value.isEmpty() || _starships.value.isEmpty() || _planets.value.isEmpty()
    }

    fun refreshData() {
        viewModelScope.launch {
            fetchCharacters()
            fetchStarships()
            fetchPlanets()
        }
    }

    private suspend fun fetchCharacters() {
        val charactersList = getCharactersFromServer() ?: characterDao.getAllCharacters()
        _characters.value = charactersList
        if (charactersList.isNotEmpty()) {
            characterDao.insertCharacters(*charactersList.toTypedArray())
        }
    }

    private suspend fun getCharactersFromServer(): List<StarWarsCharacter>? {
        val response = try {
            apolloClient.query(GetCharactersQuery()).execute()
        } catch (e: ApolloException) {
            println("ApolloException: $e")
            return null
        }

        return response.data?.allPeople?.people?.map { person ->
            StarWarsCharacter(
                id = person?.id ?: "",
                name = person?.name ?: "",
                filmsCount = person?.filmConnection?.totalCount ?: 0
            )
        }
    }

    private suspend fun fetchStarships() {
        val starshipsList = getStarshipsFromServer() ?: starshipDao.getAllStarships()
        _starships.value = starshipsList
        if (starshipsList.isNotEmpty()) {
            starshipDao.insertStarships(*starshipsList.toTypedArray())
        }
    }

    private suspend fun getStarshipsFromServer(): List<Starship>? {
        val response = try {
            apolloClient.query(GetStarshipsQuery()).execute()
        } catch (e: ApolloException) {
            println("ApolloException: $e")
            return null
        }

        return response.data?.allStarships?.starships?.map { starship ->
            Starship(
                id = starship?.id ?: "",
                name = starship?.name ?: "",
                model = starship?.model ?: ""
            )
        }
    }

    private suspend fun fetchPlanets() {
        val planetsList = getPlanetsFromServer() ?: planetDao.getAllPlanets()
        _planets.value = planetsList
        if (planetsList.isNotEmpty()) {
            planetDao.insertPlanets(*planetsList.toTypedArray())
        }
    }

    private suspend fun getPlanetsFromServer(): List<Planet>? {
        val response = try {
            apolloClient.query(GetPlanetsQuery()).execute()
        } catch (e: ApolloException) {
            println("ApolloException: $e")
            return null
        }

        return response.data?.allPlanets?.planets?.map { planet ->
            Planet(
                id = planet?.id ?: "",
                name = planet?.name ?: "",
                climate = planet?.climate ?: ""
            )
        }
    }
}
