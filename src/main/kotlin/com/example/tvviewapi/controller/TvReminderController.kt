package com.example.tvviewapi.controller

import com.example.tvviewapi.dto.TvReminderDto
import com.example.tvviewapi.service.TvReminderService
import lombok.RequiredArgsConstructor
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reminders")
class TvReminderController(
      val service: TvReminderService
) {
      @GetMapping("all")
      fun getAllReminders(): ResponseEntity<List<TvReminderDto>> = ResponseEntity.ok().body(service.getAllReminders())

      @GetMapping("{id}")
      fun getReminderById(@PathVariable id: UUID):ResponseEntity<TvReminderDto> =
            service.getReminderById(id).map { reminder -> ResponseEntity.ok().body(reminder) }
                  .orElseGet { ResponseEntity.notFound().build() }

      @PostMapping("create")
      fun createReminder(@RequestBody dto: TvReminderDto): ResponseEntity<TvReminderDto> {
            if(dto.id != null || isNotValidDto(dto)) {
                  return ResponseEntity.badRequest().build()
            }

            return ResponseEntity.ok().body(service.createReminder(dto))
      }

      @PutMapping("edit")
      fun updateReminder(@RequestBody dto: TvReminderDto): ResponseEntity<TvReminderDto> {
            if(dto.id == null || isNotValidDto(dto)) {
                  return ResponseEntity.badRequest().build()
            }

            return service.updateReminder(dto).map { reminder -> ResponseEntity.ok().body(reminder) }
                  .orElseGet { ResponseEntity.notFound().build() }
      }

      @DeleteMapping("delete/{id}")
      fun deleteReminderById(@PathVariable id: UUID): ResponseEntity<String> {
            if(service.deleteReminderById(id)) {
                  return ResponseEntity.ok().body("Reminder deleted successfully")
            }
            return ResponseEntity.ok().body("Reminder was not found")
      }

      private fun isNotValidDto(dto: TvReminderDto): Boolean = dto.title.isEmpty() || dto.description.isEmpty()

 }