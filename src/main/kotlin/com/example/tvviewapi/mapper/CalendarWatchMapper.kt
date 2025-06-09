package com.example.tvviewapi.mapper

import com.example.tvviewapi.dto.CalendarWatchDto
import com.example.tvviewapi.entity.CalendarWatch
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
interface CalendarWatchMapper {
      fun toDto(calendarWatch: CalendarWatch): CalendarWatchDto
      fun toEntity(dto: CalendarWatchDto): CalendarWatch
}