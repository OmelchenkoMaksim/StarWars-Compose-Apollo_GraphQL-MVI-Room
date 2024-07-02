package avelios.starwarsreferenceapp.domain.model

import avelios.starwarsreferenceapp.data.local.entity.Starship

data class StarshipsResponse(
    val starships: List<Starship>,
    val pageInfo: PageInfo
)
