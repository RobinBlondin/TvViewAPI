package com.example.tvviewapi.service

import com.example.tvviewapi.dto.CalendarEventDto
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.api.client.util.DateTime
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.model.Events
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.GoogleCredentials
import io.github.cdimascio.dotenv.Dotenv
import org.springframework.stereotype.Service
import java.io.InputStream
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters

@Service
class GoogleCalendarService {

      private val jsonFactory: JsonFactory = GsonFactory.getDefaultInstance()
      private val transport = GoogleNetHttpTransport.newTrustedTransport()

      fun getCalendarEvents(): List<CalendarEventDto> {

            val service = Calendar.Builder(transport, jsonFactory, HttpCredentialsAdapter(getCredentials()))
                  .setApplicationName("TvViewApi")
                  .build()

            val calendarId = System.getenv("CALENDAR_ID")
            val eventTimeSpan = getWeekStartAndEnd()

            val events: Events = service.events().list(calendarId)
                  .setTimeMin(eventTimeSpan.first)
                  .setTimeMax(eventTimeSpan.second)
                  .setMaxResults(10)
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

      private fun getCredentials(): GoogleCredentials {
            val credentials = System.getenv("credentials.json")

            val credentialsStream: InputStream = this::class.java.getResourceAsStream(credentials)
                  ?: throw RuntimeException("Could not find credentials")

            return GoogleCredentials.fromStream(credentialsStream)
                  .createScoped(listOf("https://www.googleapis.com/auth/calendar.readonly"))
      }

      private fun getWeekStartAndEnd(): Pair<DateTime, DateTime> {
            val zoneId = ZoneId.systemDefault()
            val today = LocalDate.now()
            val startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).atStartOfDay(zoneId)

            val endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
                  .atTime(LocalTime.MAX).atZone(zoneId)

            val startDateTime = DateTime(startOfWeek.toInstant().toEpochMilli())
            val endDateTime = DateTime(endOfWeek.toInstant().toEpochMilli())

            return Pair(startDateTime, endDateTime)
      }
}
