package com.example.tvviewapi.service

import com.example.tvviewapi.dto.CalendarWatchDto
import com.example.tvviewapi.mapper.CalendarWatchMapper
import com.example.tvviewapi.repository.CalendarWatchRepository
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

@Service
class CalendarWatchService(
      private val calendarWatchRepository: CalendarWatchRepository,
      private val mapper: CalendarWatchMapper,
      private val userService: UserService,
      private val googleCalendarService: GoogleCalendarService
) {
      fun getAllCalendarWatches(): List<CalendarWatchDto> {
            return calendarWatchRepository.findAll().map { calendarWatch ->
                  mapper.toDto(calendarWatch)
            }
      }

      fun stopAllCalendarWatches(): String? {
            val watches = getAllCalendarWatches()
            calendarWatchRepository.deleteAll()
            val accessToken = googleCalendarService.refreshAccessToken()
            watches.forEach { watch ->
                  val payload = mapOf(
                        "id" to watch.channelId,
                        "resourceId" to watch.resourceId
                  )

                  WebClient
                        .create()
                        .post()
                        .uri("https://www.googleapis.com/calendar/v3/channels/stop")
                        .header("Authorization", "Bearer $accessToken")
                        .header("Content-Type", "application/json")
                        .bodyValue(payload)
            }
            return accessToken
      }

      fun saveCalendarWatch(calendarWatchDto: CalendarWatchDto): CalendarWatchDto {
            val entity = mapper.toEntity(calendarWatchDto)
            val savedEntity = calendarWatchRepository.save(entity)
            return mapper.toDto(savedEntity)
      }
}