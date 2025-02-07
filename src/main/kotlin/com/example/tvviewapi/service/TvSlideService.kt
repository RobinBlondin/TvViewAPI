package com.example.tvviewapi.service

import com.example.tvviewapi.dto.TvSlideDto
import com.example.tvviewapi.mapper.TvSlideMapper
import com.example.tvviewapi.repository.TVSlideRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class TvSlideService(
      val repo: TVSlideRepository,
      val tvSlideMapper: TvSlideMapper
) {

      fun getAllTvSlides(): Set<TvSlideDto> = repo.findAll().map { tvSlideMapper.toDto(it) }.toSet()

      fun getTvSlideById(id: UUID): Optional<TvSlideDto> {
            if (!repo.existsById(id)) {
                  return Optional.empty()
            }

            val dto = tvSlideMapper.toDto(repo.findById(id).get())
            return Optional.of(dto)
      }

      fun createTvSlide(dto: TvSlideDto): Optional<TvSlideDto> {
            if (repo.existsByUrl(dto.url) || (dto.id != null && repo.existsById(dto.id!!))) {
                  return Optional.empty()
            }

            val entity = repo.save(tvSlideMapper.toEntity(dto))
            return Optional.of(tvSlideMapper.toDto(entity))
      }

      fun deleteTvSlideById(id: UUID): Boolean {
            if (repo.existsById(id)) {
                  repo.deleteById(id)
                  return true
            }
            return false
      }

      fun updateTvSlide(dto: TvSlideDto): Optional<TvSlideDto> {
            if(!repo.existsById(dto.id!!)) {
                  return Optional.empty()
            }

            val entity = repo.save(tvSlideMapper.toEntity(dto))
            return Optional.of(tvSlideMapper.toDto(entity))
      }
}