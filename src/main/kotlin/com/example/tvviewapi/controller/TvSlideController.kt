package com.example.tvviewapi.controller

import com.example.tvviewapi.dto.TvSlideDto
import com.example.tvviewapi.service.TvSlideService
import com.example.tvviewapi.service.WebSocketService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/slides")
class TvSlideController(
      private val tvSlideService: TvSlideService,
      private val webSocketService: WebSocketService
) {

      @GetMapping("all")
      fun getAllSlides(): ResponseEntity<Set<TvSlideDto>> = ResponseEntity.ok().body(tvSlideService.getAllSlides())

      @GetMapping("{id}")
      fun getSlideById(@PathVariable id: UUID): ResponseEntity<TvSlideDto> =
            tvSlideService.getSlideById(id).map { slide -> ResponseEntity.ok().body(slide) }
                  .orElseGet { ResponseEntity.notFound().header("X-Request-ID", "Input data did not match an existing TvSlide").build() }

      @PostMapping("create")
      fun createSlide(@RequestBody dto: TvSlideDto): ResponseEntity<TvSlideDto> {
            if(dto.id != null || isNotValidDto(dto)) {
                  return ResponseEntity.badRequest().header("X-Request-ID", "Input data does not meet requirements").build()
            }

            val saved = tvSlideService.createSlide(dto)

            if(saved.isPresent) {
                  webSocketService.sendRefreshSignal()
                  return ResponseEntity.ok().body(saved.get())
            }
            return ResponseEntity.status(HttpStatus.CONFLICT).header("X-Request-ID", "TvSlide already exists in database").build()
      }

      @DeleteMapping("delete/{id}")
      fun deleteSlideById(@PathVariable id: UUID) : ResponseEntity<String> {
            if(tvSlideService.deleteSlideById(id)) {
                  return ResponseEntity.ok().body("TvSlide deleted successfully")
            }

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Input data did not match an existing TvSlide")
      }

      @PutMapping("edit")
      fun updateSlide(@RequestBody dto: TvSlideDto): ResponseEntity<TvSlideDto> {
            if(dto.id == null || isNotValidDto(dto)) {
                  return ResponseEntity.badRequest().header("X-Request-ID", "Input data does not meet requirements").build()
            }

            return tvSlideService.updateSlide(dto).map { slide -> ResponseEntity.ok().body(slide) }
                  .orElseGet { ResponseEntity.notFound().header("X-Request-ID", "Input data did not match an existing TvSlide").build() }
      }

      private fun isNotValidDto(dto: TvSlideDto): Boolean = dto.url.isEmpty()
}