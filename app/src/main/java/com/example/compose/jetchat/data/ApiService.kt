package com.example.compose.jetchat.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

/**
 * Clase de datos que representa un nombre de la API
 */
data class NameResponse(val name: String, val id: String)

/**
 * Interfaz para el servicio de API usando Retrofit
 */
interface NamesApiService {
    @GET("names")
    suspend fun getNames(): List<NameResponse>
}

/**
 * Servicio que maneja la obtención de nombres usando Retrofit
 */
class NamesService {

    private val retrofit: Retrofit by lazy {
        // Configurar logging para debug
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        Retrofit.Builder()
            .baseUrl("https://6854f3b06a6ef0ed66309ff9.mockapi.io/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    private val apiService: NamesApiService by lazy {
        retrofit.create(NamesApiService::class.java)
    }

    /**
     * Obtiene una lista de nombres desde la API
     * @return Flow que emite la lista de nombres
     */
    fun getNames(): Flow<List<NameResponse>> = flow {
        try {
            // Llamada real a la API usando corrutinas
            val names = apiService.getNames()
            emit(names)
        } catch (e: Exception) {
            // En caso de error, emitimos una lista vacía
            emit(emptyList())
        }
    }

    /**
     * Obtiene un nombre aleatorio de la API
     * @return Flow que emite un nombre aleatorio
     */
    fun getRandomName(): Flow<String> = flow {
        try {
            // Obtener todos los nombres de la API
            val names = apiService.getNames()

            if (names.isNotEmpty()) {
                // Seleccionar un nombre aleatorio
                val randomName = names.random()
                emit(randomName.name)
            } else {
                emit("Usuario Anónimo")
            }
        } catch (e: Exception) {
            // En caso de error, usar nombres de respaldo
            val fallbackNames = listOf(
                "Miss Alice Bradtke",
                "Lora Prosacco",
                "Cindy Langosh MD",
                "Timmy Hauck",
                "Alice Stoltenberg",
                "Jacob Denesik V",
                "Elizabeth Tillman",
                "Roy Hane",
                "Melvin Bergstrom",
                "Jennie Hahn",
            )
            emit(fallbackNames.random())
        }
    }
}
