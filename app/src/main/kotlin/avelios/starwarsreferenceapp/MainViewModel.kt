package avelios.starwarsreferenceapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal class MainViewModel(
    private val actor: MainActor
) : ViewModel() {

    private val _state = MutableStateFlow<MainState>(MainState.Loading)
    val state: StateFlow<MainState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<MainEffect>()

    val favoriteCharacters: StateFlow<Map<String, Boolean>> = actor.favoriteCharacters
    val isNetworkAvailable: StateFlow<Boolean> = actor.isNetworkAvailable
    val charactersPager: StateFlow<Flow<PagingData<StarWarsCharacter>>> = actor.charactersPager
    val starshipsPager: StateFlow<Flow<PagingData<Starship>>> = actor.starshipsPager
    val planetsPager: StateFlow<Flow<PagingData<Planet>>> = actor.planetsPager
    val selectedCharacter: StateFlow<StarWarsCharacter?> = actor.selectedCharacter
    val selectedStarship: StateFlow<Starship?> = actor.selectedStarship
    val selectedPlanet: StateFlow<Planet?> = actor.selectedPlanet
    val isLoading: StateFlow<Boolean> = actor.isLoading

    init {
        viewModelScope.launch {
            actor.state.collect { newState ->
                _state.value = newState
            }
        }

        viewModelScope.launch {
            actor.effect.collect { newEffect ->
                _effect.emit(newEffect)
            }
        }

        handleIntent(MainAction.LoadData)
    }

    fun handleIntent(intent: MainAction) {
        viewModelScope.launch {
            actor.handleIntent(intent)
        }
    }
}
