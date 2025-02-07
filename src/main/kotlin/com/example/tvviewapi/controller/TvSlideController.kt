package com.example.tvviewapi.controller

import com.example.tvviewapi.dto.TvSlideDto
import com.example.tvviewapi.service.TvSlideService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*
import kotlin.jvm.optionals.getOrElse

@RestController
@RequestMapping("/api/slides")
class TvSlideController(
      val service: TvSlideService
) {

      @GetMapping("all")
      fun getTvSlides(): ResponseEntity<Set<TvSlideDto>> = ResponseEntity.ok(service.getAllTvSlides())

      @GetMapping("{id}")
      fun getTvSlideById(@PathVariable id: UUID): ResponseEntity<TvSlideDto> =
            service.getTvSlideById(id).map { slide -> ResponseEntity.ok().body(slide) }
                  .getOrElse { ResponseEntity.notFound().build() }

      @PostMapping("create")
      fun createTvSlide(@RequestBody dto: TvSlideDto): ResponseEntity<TvSlideDto> {
            if(dto.url.isEmpty()) {
                  return ResponseEntity.badRequest().build()
            }

            return service.createTvSlide(dto).map { slide -> ResponseEntity.ok().body(slide) }
                  .getOrElse { ResponseEntity.status(HttpStatus.CONFLICT).build() }
      }

      @DeleteMapping("delete/{id}")
      fun deleteTvSlideById(@PathVariable id: UUID) : ResponseEntity<String> {
            if(service.deleteTvSlideById(id)) {
                  return ResponseEntity.ok().body("TvSlide deleted successfully")
            }

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("TvSlide not found")
      }

      @PutMapping("edit")
      fun updateTvSlide(@RequestBody dto: TvSlideDto): ResponseEntity<TvSlideDto> {
            if(dto.id == null) {
                  return ResponseEntity.badRequest().build()
            }

            return service.updateTvSlide(dto).map { slide -> ResponseEntity.ok().body(slide) }
                  .orElseGet { ResponseEntity.notFound().build() }
      }
}