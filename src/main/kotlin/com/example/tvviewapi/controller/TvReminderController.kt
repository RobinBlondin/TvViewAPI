package com.example.tvviewapi.controller

import com.example.tvviewapi.dto.TvReminderDto
import com.example.tvviewapi.service.TvReminderService
import com.example.tvviewapi.service.WebSocketService
import lombok.RequiredArgsConstructor
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reminders")
class TvReminderController(
      private val reminderService: TvReminderService,
      private val webSocketService: WebSocketService
) {
      @GetMapping("all")
      fun getAllReminders(): ResponseEntity<List<TvReminderDto>> = ResponseEntity.ok().body(reminderService.getAllReminders())

      @GetMapping("{id}")
      fun getReminderById(@PathVariable id: UUID):ResponseEntity<TvReminderDto> =
            reminderService.getReminderById(id).map { reminder -> ResponseEntity.ok().body(reminder) }
                  .orElseGet { ResponseEntity.notFound().header("X-Request-ID", "Input data did not match an existing TvReminder").build() }

      @PostMapping("create")
      fun createReminder(@RequestBody dto: TvReminderDto): ResponseEntity<TvReminderDto> {
            if(dto.id != null || isNotValidDto(dto)) {
                  return ResponseEntity.badRequest().header("X-Request-ID", "Input data does not meet requirements").build()
            }

            val saved = reminderService.createReminder(dto)
            webSocketService.sendRefreshSignal()

            return ResponseEntity.ok().body(saved)
      }

      @PutMapping("edit")
      fun updateReminder(@RequestBody dto: TvReminderDto): ResponseEntity<TvReminderDto> {
            if(dto.id == null || isNotValidDto(dto)) {
                  return ResponseEntity.badRequest().header("X-Request-ID", "Input data does not meet requirements").build()
            }

            return reminderService.updateReminder(dto).map { reminder -> ResponseEntity.ok().body(reminder) }
                  .orElseGet { ResponseEntity.notFound().header("X-Request-ID", "Input data did not match an existing TvReminder").build() }
      }

      @DeleteMapping("delete/{id}")
      fun deleteReminderById(@PathVariable id: UUID): ResponseEntity<String> {
            if(reminderService.deleteReminderById(id)) {
                  return ResponseEntity.ok().body("Reminder deleted successfully")
            }
            return ResponseEntity.ok().body("Input data did not match an existing TvReminder")
      }

      private fun isNotValidDto(dto: TvReminderDto): Boolean = dto.title.isEmpty() || dto.description.isEmpty()

 }