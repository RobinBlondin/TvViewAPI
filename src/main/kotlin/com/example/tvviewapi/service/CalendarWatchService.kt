package com.example.tvviewapi.service

import com.example.tvviewapi.dto.CalendarWatchDto
import com.example.tvviewapi.mapper.CalendarWatchMapper
import com.example.tvviewapi.repository.CalendarWatchRepository
import org.springframework.stereotype.Service

@Service
class CalendarWatchService(
      private val calendarWatchRepository: CalendarWatchRepository,
      private val mapper: CalendarWatchMapper,
) {
      fun getAllCalendarWatches(): List<CalendarWatchDto> {
            return calendarWatchRepository.findAll().map { calendarWatch ->
                  mapper.toDto(calendarWatch)
            }
      }

      fun saveCalendarWatch(calendarWatchDto: CalendarWatchDto): CalendarWatchDto {
            val entity = mapper.toEntity(calendarWatchDto)
            val savedEntity = calendarWatchRepository.save(entity)
            return mapper.toDto(savedEntity)
      }

      fun deleteAllCalendarWatches() {
            calendarWatchRepository.deleteAll()
      }
}