package com.example.tvviewapi.service

import com.example.tvviewapi.dto.CalendarEventDto
import com.example.tvviewapi.dto.CalendarWatchDto
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.api.client.util.DateTime
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.model.Events
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.GoogleCredentials
import io.github.cdimascio.dotenv.Dotenv
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestTemplate
import org.springframework.web.reactive.function.client.WebClient
import java.io.ByteArrayInputStream
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters
import java.util.*

@Service
class GoogleCalendarService(
      private val userService: UserService,
      private val calendarWatchService: CalendarWatchService
) {

      private val jsonFactory: JsonFactory = GsonFactory.getDefaultInstance()
      private val transport = GoogleNetHttpTransport.newTrustedTransport()
      private val dotenv: Dotenv? = Dotenv.configure().ignoreIfMissing().load()
      private val serviceEmail: String? = dotenv?.get("SERVICE_ACCOUNT_EMAIL")

      fun getCalendarEvents(): List<CalendarEventDto> {

            val service = Calendar.Builder(transport, jsonFactory, HttpCredentialsAdapter(getCredentials()))
                  .setApplicationName("TvViewApi")
                  .build()

            val calendarId = dotenv?.get("CALENDAR_ID")
                  ?: throw RuntimeException("CALENDAR_ID environment variable is missing")

            val eventTimeSpan = getWeekStartAndEnd()

            val events: Events = service.events().list(calendarId)
                  .setTimeMin(eventTimeSpan.first)
                  .setTimeMax(eventTimeSpan.second)
                  .setMaxResults(30)
                  .setOrderBy("startTime")
                  .setSingleEvents(true)
                  .execute()

            return events.items.map { event ->
                  val startTime = event.start.dateTime?.toString()
                        ?: event.start.date?.toString()
                        ?: "Unknown"

                  val endTime = event.end.dateTime?.toString()
                        ?: event.end.date?.toString()
                        ?: "Unknown"

                  CalendarEventDto(
                        title = event.summary ?: "",
                        description = event.description ?: "",
                        location = event.location ?: "",
                        startTime = startTime,
                        endTime = endTime
                  )
            }
      }

      @Scheduled(cron = "0 0 3 * * ?", zone = "Europe/Stockholm")
      fun refreshCalendarWatch() {
            val newAccessToken = calendarWatchService.stopAllCalendarWatches()

            if( newAccessToken == null ) {
                  println("Failed to refresh access token for calendar watch")
                  return
            }
            startWatchingCalendar(newAccessToken)
      }

      fun startWatchingCalendar(accessToken: String) {
            val watchUrl = dotenv?.get("CALENDAR_WATCH_URL")
                  ?: throw RuntimeException("CALENDAR_WATCH_URL environment variable is missing")

            val callbackUrl = dotenv.get("CALENDAR_WATCH_CALLBACK_URL")
                  ?: throw RuntimeException("CALENDAR_WATCH_CALLBACK_URL environment variable is missing")

            val payload = mapOf(
                  "id" to UUID.randomUUID().toString(),
                  "type" to "web_hook",
                  "address" to callbackUrl,
                  "params" to mapOf("ttl" to "86400")
            )

            println("Starting watch for calendar")

            WebClient.create()
                  .post()
                  .uri(watchUrl)
                  .header("Authorization", "Bearer $accessToken")
                  .contentType(MediaType.APPLICATION_JSON)
                  .bodyValue(payload)
                  .retrieve()
                  .bodyToMono(String::class.java)
                  .subscribe { response: String->
                        println("Calendar  $response")
                        val mapper = ObjectMapper()
                        val json = mapper.readTree(response)

                        val dto = CalendarWatchDto(
                              channelId = json.get("id").asText(),
                              resourceId = json.get("resourceId").asText(),
                        )

                        calendarWatchService.saveCalendarWatch(dto)

                  }
      }

      fun refreshAccessToken(): String? {
            val user = userService.findUserByEmail(serviceEmail!!)
                  .orElseThrow { RuntimeException("User not found: $serviceEmail") }

            val refreshToken = user.refreshToken

            val requestParams = LinkedMultiValueMap<String, String>().apply {
                  add("client_id", dotenv?.get("FRONTEND_GOOGLE_CLIENT_ID"))
                  add("client_secret", dotenv?.get("FRONTEND_GOOGLE_CLIENT_SECRET"))
                  add("refresh_token", refreshToken)
                  add("grant_type", "refresh_token")
            }

            val headers = HttpHeaders()
            headers.contentType = MediaType.APPLICATION_FORM_URLENCODED

            val restTemplate = RestTemplate()
            val requestEntity = HttpEntity(requestParams, headers)

            val response = restTemplate.exchange(
                  "https://oauth2.googleapis.com/token",
                  HttpMethod.POST,
                  requestEntity,
                  Map::class.java
            )

            if (!response.statusCode.is2xxSuccessful) return null

            return response.body?.get("access_token") as? String
      }


      private fun getCredentials(): GoogleCredentials {
            val credentialsJson = dotenv?.get("CREDENTIALS_JSON")
                  ?: throw RuntimeException("CREDENTIALS_JSON environment variable is missing")


            val credentialsStream = ByteArrayInputStream(credentialsJson.toByteArray(Charsets.UTF_8))
            return GoogleCredentials.fromStream(credentialsStream)
                  .createScoped(listOf("https://www.googleapis.com/auth/calendar.readonly"))
      }


      private fun getWeekStartAndEnd(): Pair<DateTime, DateTime> {
            val zoneId = ZoneId.systemDefault()
            val today = LocalDate.now()

            val startOfWeek =
                  if ( today.dayOfWeek == DayOfWeek.SUNDAY )
                        today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)).atStartOfDay(zoneId)
                  else
                        today.with(TemporalAdjusters.previous(DayOfWeek.SUNDAY)).atStartOfDay(zoneId)

            val endOfWeek = startOfWeek.plusDays(6).withHour(23).withMinute(59).withSecond(59).withNano(999_999_999)

            val startDateTime = DateTime(startOfWeek.toInstant().toEpochMilli())
            val endDateTime = DateTime(endOfWeek.toInstant().toEpochMilli())

            return Pair(startDateTime, endDateTime)
      }

}
