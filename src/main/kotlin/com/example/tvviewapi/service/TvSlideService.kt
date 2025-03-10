package com.example.tvviewapi.service

import com.example.tvviewapi.dto.TvSlideDto
import com.example.tvviewapi.mapper.TvSlideMapper
import com.example.tvviewapi.repository.TVSlideRepository
import org.springframework.stereotype.Service
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

@Service
class TvSlideService(
      val repo: TVSlideRepository,
      val tvSlideMapper: TvSlideMapper
) {


      fun getAllSlides(): Set<TvSlideDto> = repo.findAll().map { tvSlideMapper.toDto(it) }.toSet()

      fun getSlideById(id: UUID): Optional<TvSlideDto> = repo.findById(id).map { tvSlideMapper.toDto(it) }

      fun createSlide(dto: TvSlideDto): Optional<TvSlideDto> {
            if (repo.existsByUrl(dto.url)) {
                  return Optional.empty()
            }

            val entity = repo.save(tvSlideMapper.toEntity(dto))
            return Optional.of(tvSlideMapper.toDto(entity))
      }

      fun deleteSlideById(id: UUID): Boolean {
            if (repo.existsById(id)) {
                  deleteFile(id)
                  repo.deleteById(id)
                  return true
            }
            return false
      }

      fun updateSlide(dto: TvSlideDto): Optional<TvSlideDto> {
            if(!repo.existsById(dto.id!!)) {
                  return Optional.empty()
            }

            val entity = repo.save(tvSlideMapper.toEntity(dto))
            return Optional.of(tvSlideMapper.toDto(entity))
      }

      private fun deleteFile(id: UUID): Boolean {
            if (!repo.existsById(id)) {
                  return false
            }
            val uploadDir = Paths.get("uploads")
            val url = repo.findById(id).get().url
            val filePath = Paths.get("${uploadDir}/${getFileNameFromUrl(url)}")

            try {
                  Files.delete(filePath)
            } catch (e: Exception) {
                  println("Error deleting file: ${e.message}")
                  return false
            }

            Files.delete(filePath)
            return true
      }

      private fun getFileNameFromUrl(url: String): String {
            return url.substring(url.lastIndexOf("/") + 1)
      }
}