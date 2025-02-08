package com.example.tvviewapi.controller

import com.example.tvviewapi.dto.TvReminderDto
import com.example.tvviewapi.dto.TvSlideDto
import com.example.tvviewapi.service.TvSlideService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*
import kotlin.jvm.optionals.getOrNull

@RestController
@RequestMapping("/api/slides")
class TvSlideController(
      val service: TvSlideService
) {

      @GetMapping("all")
      fun getAllSlides(): ResponseEntity<Set<TvSlideDto>> = ResponseEntity.ok().body(service.getAllSlides())

      @GetMapping("{id}")
      fun getSlideById(@PathVariable id: UUID): ResponseEntity<TvSlideDto> =
            service.getSlideById(id).map { slide -> ResponseEntity.ok().body(slide) }
                  .orElseGet { ResponseEntity.notFound().build() }

      @PostMapping("create")
      fun createSlide(@RequestBody dto: TvSlideDto): ResponseEntity<TvSlideDto> {
            if(isNotValidDto(dto)) {
                  return ResponseEntity.badRequest().build()
            }

            return ResponseEntity.ok().body(service.createSlide(dto).getOrNull())
      }

      @DeleteMapping("delete/{id}")
      fun deleteSlideById(@PathVariable id: UUID) : ResponseEntity<String> {
            if(service.deleteSlideById(id)) {
                  return ResponseEntity.ok().body("TvSlide deleted successfully")
            }

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("TvSlide not found")
      }

      @PutMapping("edit")
      fun updateSlide(@RequestBody dto: TvSlideDto): ResponseEntity<TvSlideDto> {
            if(dto.id == null) {
                  return ResponseEntity.badRequest().build()
            }

            return service.updateSlide(dto).map { slide -> ResponseEntity.ok().body(slide) }
                  .orElseGet { ResponseEntity.notFound().build() }
      }

      private fun isNotValidDto(dto: TvSlideDto): Boolean = dto.url.isEmpty() || dto.id != null
}