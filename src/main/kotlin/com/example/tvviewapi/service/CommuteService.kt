package com.example.tvviewapi.service

import com.example.tvviewapi.dto.DepartureDto
import com.example.tvviewapi.dto.DepartureBoard
import io.github.cdimascio.dotenv.Dotenv
import okhttp3.OkHttpClient
import okhttp3.Request
import org.springframework.stereotype.Service
import kotlinx.serialization.json.*

@Service
class CommuteService(
      val jsonParser: Json = Json { ignoreUnknownKeys = true },
      val commuteStopId: String? = System.getenv("COMMUTE_STOP_ID"),
      val commuteApiKey: String? = System.getenv("COMMUTE_API_KEY")
) {

      private fun fetchJson(url: String): String {
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            client.newCall(request).execute().use { response ->
                  return response.body?.string() ?: ""
            }
      }

      fun getDepartures(): List<DepartureDto> {
            val json = fetchJson("https://api.resrobot.se/v2.1/departureBoard?id=${commuteStopId}&format=json&accessId=${commuteApiKey}")
            return  jsonParser.decodeFromString<DepartureBoard>(json).departures
      }

}