package com.example.practica_gestor_personal_de_tareas.red

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// Descripción de la API. listarTareas(1) hace GET /todos?userId=1
interface ApiTareas {
    @GET("todos")
    suspend fun listarTareas(@Query("userId") usuario: Int): List<TareaRemota>
}

// Un único cliente Retrofit para toda la app
object ServicioTareas {
    val api: ApiTareas = Retrofit.Builder()
        .baseUrl("https://jsonplaceholder.typicode.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiTareas::class.java)
}
