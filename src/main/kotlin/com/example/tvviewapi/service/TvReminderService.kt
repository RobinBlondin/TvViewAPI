package com.example.tvviewapi.service

import com.example.tvviewapi.dto.TvReminderDto
import com.example.tvviewapi.mapper.TvReminderMapper
import com.example.tvviewapi.repository.TvReminderRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.*

@Service
class TvReminderService(
      val repo: TvReminderRepository,
      val tvReminderMapper: TvReminderMapper,
      val webSocketService: WebSocketService
) {
      fun getAllReminders(): List<TvReminderDto> = repo.findAll().map { tvReminderMapper.toDto(it) }

      fun getReminderById(id: UUID): Optional<TvReminderDto>  = repo.findById(id).map { tvReminderMapper.toDto(it) }

      fun createReminder(dto: TvReminderDto): TvReminderDto {
            val entity = repo.save(tvReminderMapper.toEntity(dto))
            return tvReminderMapper.toDto(entity)
      }

      fun updateReminder(dto: TvReminderDto): Optional<TvReminderDto> {
            if(!repo.existsById(dto.id!!)) {
                  return Optional.empty()
            }
            return Optional.of(createReminder(dto))
      }

      fun deleteReminderById(id: UUID): Boolean {
            if(repo.existsById(id)) {
                  repo.deleteById(id)
                  return true
            }
            return false
      }

      @Scheduled(cron = "0 0 0 * * ?")
      fun deleteCheckedReminders() {
            val finishedReminders = repo.findAll().filter { it.done }
            finishedReminders.forEach { repo.delete(it) }
            webSocketService.sendSignalToAllClients()
      }
}