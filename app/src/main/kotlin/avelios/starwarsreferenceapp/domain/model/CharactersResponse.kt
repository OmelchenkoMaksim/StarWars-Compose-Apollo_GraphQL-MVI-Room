package avelios.starwarsreferenceapp.domain.model

import avelios.starwarsreferenceapp.data.local.entity.StarWarsCharacter

data class CharactersResponse(
    val characters: List<StarWarsCharacter>,
    val pageInfo: PageInfo
)
