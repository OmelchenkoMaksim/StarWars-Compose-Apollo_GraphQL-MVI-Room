package avelios.starwarsreferenceapp

import avelios.starwarsreferenceapp.data.local.dao.CharacterDao
import avelios.starwarsreferenceapp.data.local.dao.PlanetDao
import avelios.starwarsreferenceapp.data.local.dao.StarshipDao
import avelios.starwarsreferenceapp.data.local.entity.Planet
import avelios.starwarsreferenceapp.data.local.entity.StarWarsCharacter
import avelios.starwarsreferenceapp.data.local.entity.Starship
import avelios.starwarsreferenceapp.data.repository.StarWarsRepositoryImpl
import com.apollographql.apollo3.ApolloClient
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class StarWarsRepositoryImplTest {

    private lateinit var apolloClient: ApolloClient
    private lateinit var characterDao: CharacterDao
    private lateinit var starshipDao: StarshipDao
    private lateinit var planetDao: PlanetDao

    private lateinit var repository: StarWarsRepositoryImpl

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        apolloClient = mockk()
        characterDao = mockk()
        starshipDao = mockk()
        planetDao = mockk()

        repository = StarWarsRepositoryImpl(apolloClient, characterDao, starshipDao, planetDao)
    }

    @Test
    fun `test get all characters returns expected result`() = runTest(testDispatcher) {
        coEvery { characterDao.getAllCharacters() } returns expectedCharacters

        val result = repository.getAllCharacters()

        assertEquals(expectedCharacters, result)
    }

    @Test
    fun `test get all starships returns expected result`() = runTest(testDispatcher) {
        coEvery { starshipDao.getAllStarships() } returns expectedStarships

        val result = repository.getAllStarships()

        assertEquals(expectedStarships, result)
    }

    @Test
    fun `test get all planets returns expected result`() = runTest(testDispatcher) {
        coEvery { planetDao.getAllPlanets() } returns expectedPlanets

        val result = repository.getAllPlanets()

        assertEquals(expectedPlanets, result)
    }

    @Test
    fun `test update favorite status`() = runTest(testDispatcher) {
        val characterId = "1234"
        val isFavorite = true

        coEvery { characterDao.updateFavoriteStatus(characterId, isFavorite) } returns Unit

        repository.updateFavoriteStatus(characterId, isFavorite)

        coVerify { characterDao.updateFavoriteStatus(characterId, isFavorite) }
    }

    @Test
    fun `test update characters`() = runTest(testDispatcher) {
        coEvery { characterDao.insertCharacters(*characters.toTypedArray()) } returns Unit

        repository.updateCharacters(characters)

        coVerify { characterDao.insertCharacters(*characters.toTypedArray()) }
    }

    @Test
    fun `test update starships`() = runTest(testDispatcher) {
        coEvery { starshipDao.insertStarships(starships) } returns Unit

        repository.updateStarships(starships)

        coVerify { starshipDao.insertStarships(starships) }
    }

    @Test
    fun `test update planets`() = runTest(testDispatcher) {
        coEvery { planetDao.insertPlanets(planets) } returns Unit

        repository.updatePlanets(planets)

        coVerify { planetDao.insertPlanets(planets) }
    }

    companion object TestData {
        val expectedCharacters = listOf(
            StarWarsCharacter(
                id = "1",
                name = "Luke Skywalker",
                birthYear = "19BBY",
                eyeColor = "Blue",
                gender = "Male",
                hairColor = "Blond",
                height = 172,
                mass = 77.0,
                homeworld = "Tatooine",
                filmsCount = 4,
                skinColor = "Fair",
                isFavorite = false
            ),
            StarWarsCharacter(
                id = "2",
                name = "Darth Vader",
                birthYear = "41.9BBY",
                eyeColor = "Yellow",
                gender = "Male",
                hairColor = "None",
                height = 202,
                mass = 136.0,
                homeworld = "Tatooine",
                filmsCount = 4,
                skinColor = "White",
                isFavorite = false
            )
        )

        val expectedStarships = listOf(
            Starship(
                id = "1",
                name = "Millennium Falcon",
                model = "YT-1300",
                starshipClass = "Light Freighter",
                manufacturers = listOf("Corellian Engineering Corporation"),
                length = 34.75f,
                crew = "4",
                passengers = "6",
                maxAtmospheringSpeed = 1050,
                hyperdriveRating = 0.5f
            ),
            Starship(
                id = "2",
                name = "X-Wing",
                model = "T-65",
                starshipClass = "Starfighter",
                manufacturers = listOf("Incom Corporation"),
                length = 12.5f,
                crew = "1",
                passengers = "0",
                maxAtmospheringSpeed = 1050,
                hyperdriveRating = 1.0f
            )
        )

        val expectedPlanets = listOf(
            Planet(
                id = "1",
                name = "Tatooine",
                climates = listOf("Arid"),
                diameter = 10465,
                rotationPeriod = 23,
                orbitalPeriod = 304,
                gravity = "1 standard",
                population = 200000.0,
                terrains = listOf("Desert"),
                surfaceWater = 1.0
            ),
            Planet(
                id = "2",
                name = "Naboo",
                climates = listOf("Temperate"),
                diameter = 12120,
                rotationPeriod = 26,
                orbitalPeriod = 312,
                gravity = "1 standard",
                population = 4500000000.0,
                terrains = listOf("Grasslands", "Swamps"),
                surfaceWater = 12.0
            )
        )

        val characters = listOf(
            StarWarsCharacter(
                id = "1",
                name = "Luke Skywalker",
                birthYear = "19BBY",
                eyeColor = "Blue",
                gender = "Male",
                hairColor = "Blond",
                height = 172,
                mass = 77.0,
                homeworld = "Tatooine",
                filmsCount = 4,
                skinColor = "Fair",
                isFavorite = false
            ),
            StarWarsCharacter(
                id = "2",
                name = "Darth Vader",
                birthYear = "41.9BBY",
                eyeColor = "Yellow",
                gender = "Male",
                hairColor = "None",
                height = 202,
                mass = 136.0,
                homeworld = "Tatooine",
                filmsCount = 4,
                skinColor = "White",
                isFavorite = false
            )
        )

        val starships = listOf(
            Starship(
                id = "1",
                name = "Millennium Falcon",
                model = "YT-1300",
                starshipClass = "Light Freighter",
                manufacturers = listOf("Corellian Engineering Corporation"),
                length = 34.75f,
                crew = "4",
                passengers = "6",
                maxAtmospheringSpeed = 1050,
                hyperdriveRating = 0.5f
            ),
            Starship(
                id = "2",
                name = "X-Wing",
                model = "T-65",
                starshipClass = "Starfighter",
                manufacturers = listOf("Incom Corporation"),
                length = 12.5f,
                crew = "1",
                passengers = "0",
                maxAtmospheringSpeed = 1050,
                hyperdriveRating = 1.0f
            )
        )

        val planets = listOf(
            Planet(
                id = "1",
                name = "Tatooine",
                climates = listOf("Arid"),
                diameter = 10465,
                rotationPeriod = 23,
                orbitalPeriod = 304,
                gravity = "1 standard",
                population = 200000.0,
                terrains = listOf("Desert"),
                surfaceWater = 1.0
            ),
            Planet(
                id = "2",
                name = "Naboo",
                climates = listOf("Temperate"),
                diameter = 12120,
                rotationPeriod = 26,
                orbitalPeriod = 312,
                gravity = "1 standard",
                population = 4500000000.0,
                terrains = listOf("Grasslands", "Swamps"),
                surfaceWater = 12.0
            )
        )
    }
}
