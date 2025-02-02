package com.example.tvviewapi.mapper

import com.example.tvviewapi.dto.TvReminderDto
import com.example.tvviewapi.entity.TvReminder
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
interface TvReminderMapper {
    fun toDto(tvReminder: TvReminder): TvReminderDto
    fun toEntity(tvReminderDto: TvReminderDto):  TvReminder
}