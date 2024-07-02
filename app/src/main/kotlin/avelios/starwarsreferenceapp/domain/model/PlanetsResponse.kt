package avelios.starwarsreferenceapp.domain.model

import avelios.starwarsreferenceapp.data.local.entity.Planet

data class PlanetsResponse(
    val planets: List<Planet>,
    val pageInfo: PageInfo
)
