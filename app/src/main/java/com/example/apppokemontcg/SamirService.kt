package com.example.apppokemontcg

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*


interface SamirService {


    @POST("inmuebles/")
    fun postMyData(@Body data: SamirClass): Call<SamirClass>

    @DELETE("inmuebles/")
    fun deleteInmuebleByTitle(@Query("idInmueble") idInmueble: Int): Call<Void>

    @DELETE
    fun deleteInmueble(@Url url:String): Call<Void>

    @POST
    fun putInmueble(@Url url:String, @Body samirClass: SamirClass): Call<SamirClass>


}

