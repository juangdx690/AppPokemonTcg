package com.example.apppokemontcg


data class PokemonResponse(val cards: List<PokemonCard>)

data class PokemonCard(
    val name: String,
    val imageUrl: String,
    var hp:String,
    var rarity:String
)


