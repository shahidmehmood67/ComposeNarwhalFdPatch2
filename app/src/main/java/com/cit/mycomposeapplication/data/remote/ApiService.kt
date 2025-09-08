package com.cit.mycomposeapplication.data.remote

import android.util.Log
import com.cit.mycomposeapplication.models.AyahResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.client.engine.okhttp.OkHttp
import kotlinx.serialization.json.Json

object ApiService {
    private val ktorClient = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true // This will ignore unknown JSON fields
            })
        }
    }

    suspend fun fetchAyah(translationId: String): AyahResponse? {
        return try {
            val response: HttpResponse = ktorClient.get("https://api.quran.com/api/v4/verses/random") {
                parameter("words", false)
                parameter("translations", translationId)
                parameter("fields", "text_indopak")
            }
            val finalUrl = response.request.url.toString()
            Log.d("ApiService", "Final API URL: $finalUrl")

            val responseBody = response.body<AyahResponse>()
            Log.d("ApiService", "API Response: $responseBody")
            responseBody
        } catch (e: Exception) {
            Log.e("ApiService", "fetchAyah Error: ${e.localizedMessage}", e)
            null
        }
    }
}



