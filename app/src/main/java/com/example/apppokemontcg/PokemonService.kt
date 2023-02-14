package com.example.apppokemontcg

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface PokemonService {
    @GET("cards")
    fun getCards(
        @Query("c099e29e-2bde-4974-a532-cb7e2cf90072") apiKey: String,
        @Query("page") page: Int
    ): Call<PokemonResponse>

    @GET("cards")
    fun getCarta(
        @Query("c099e29e-2bde-4974-a532-cb7e2cf90072") apiKey: String,
        @Query("name") nombre: String
    ): Call<PokemonResponse>

}