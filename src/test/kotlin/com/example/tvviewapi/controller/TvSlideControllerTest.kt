package com.example.tvviewapi.controller

import com.example.tvviewapi.dto.TvSlideDto
import com.example.tvviewapi.enums.SocketMessage
import com.example.tvviewapi.service.TvSlideService
import com.example.tvviewapi.service.WebSocketService
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.just
import io.mockk.runs
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt
import org.springframework.test.web.servlet.*
import java.util.*


@WebMvcTest(TvSlideController::class)
class TvSlideControllerTest {
      @Autowired
      lateinit var mockMvc: MockMvc

      @MockkBean
      lateinit var  tvSlideService: TvSlideService

      @MockkBean
      lateinit var webSocketService: WebSocketService

      private val objectMapper = jacksonObjectMapper()
      private val slideUrl = "https://example.com/image.jpg"

      private val dummySlide = TvSlideDto(
            id = UUID.randomUUID(),
            url = slideUrl,
      )

      @Test
      fun `should get all slides`() {
            every { tvSlideService.getAllSlides() } returns setOf(dummySlide)

            mockMvc.get ("/api/slides/all" ) { with(jwt()) }
                  .andExpect {
                        status { isOk()  }
                        content { contentType(MediaType.APPLICATION_JSON) }
                        jsonPath("$[0].url") { value(slideUrl) }
                        jsonPath("$.size()") { value(1) }
                  }
      }

      @Test
      fun `should return a slide by id`() {
            every { tvSlideService.getSlideById(dummySlide.id!!) } returns Optional.of(dummySlide)

            mockMvc.get ("/api/slides/${dummySlide.id}" ) { with(jwt()) }
                  .andExpect {
                        status { isOk()  }
                        content { contentType(MediaType.APPLICATION_JSON) }
                        jsonPath("$.url") { value(slideUrl) }
                  }
      }

      @Test
      fun `should return 404 if slide not found`() {
            every { tvSlideService.getSlideById(any()) } returns Optional.empty()

            mockMvc.get ("/api/slides/${dummySlide.id}" ) { with(jwt()) }
                  .andExpect {
                        status { isNotFound()  }
                  }
      }

      @Test
      fun `should create a new slide`() {
            val slideToCreate = dummySlide.copy(id = null)

            every { tvSlideService.createSlide(slideToCreate) } returns Optional.of(dummySlide)
            every { webSocketService.sendSignalToAllClients(SocketMessage.SLIDE) } just runs

            mockMvc.post("/api/slides/create") {
                  with(jwt())
                  contentType = MediaType.APPLICATION_JSON
                  content = objectMapper.writeValueAsString(slideToCreate)
            }.andExpect {
                  status { isOk() }
                  jsonPath("$.url" ) { value(slideUrl)  }
            }
      }


      @Test
      fun `should return bad request for invalid create`() {
            val invalidDto = dummySlide.copy(id = UUID.randomUUID(), url = "")

            mockMvc.post("/api/slides/create") {
                  with(jwt())
                  contentType = MediaType.APPLICATION_JSON
                  content = objectMapper.writeValueAsString(invalidDto)
            }.andExpect {
                  status { isBadRequest() }
            }
      }

      @Test
      fun `should delete slide if exists`() {
            every { tvSlideService.deleteSlideById(dummySlide.id!!) } returns true
            every { webSocketService.sendSignalToAllClients(SocketMessage.SLIDE) } just runs

            mockMvc.delete("/api/slides/delete/${dummySlide.id}") {
                  with(jwt())
            }.andExpect {
                  status { isOk() }
                  jsonPath("$") { value("TvSlide deleted successfully") }
            }
      }

      @Test
      fun `should return 404 if delete fails`() {
            every { tvSlideService.deleteSlideById(any()) } returns false

            mockMvc.delete("/api/slides/delete/${UUID.randomUUID()}") {
                  with(jwt())
            }
                  .andExpect {
                        status { isNotFound() }
                        jsonPath("$") { value("Input data did not match an existing TvSlide") }
                  }
      }

      @Test
      fun `should update slide`() {
            val slideToUpdate = dummySlide.copy(id = dummySlide.id, url = "new url")
            every { tvSlideService.updateSlide(slideToUpdate) } returns Optional.of(slideToUpdate)
            every { webSocketService.sendSignalToAllClients(SocketMessage.SLIDE) } just runs

            mockMvc.put("/api/slides/edit") {
                  with(jwt())
                  contentType = MediaType.APPLICATION_JSON
                  content = objectMapper.writeValueAsString(slideToUpdate)
            }.andExpect {
                  status { isOk() }
                  jsonPath("$.url") { value(slideToUpdate.url) }
            }
      }


      @Test
      fun `should return bad request for invalid update`() {
            val invalidDto = dummySlide.copy(id = null)

            mockMvc.put("/api/slides/edit") {
                  with(jwt())
                  contentType = MediaType.APPLICATION_JSON
                  content = objectMapper.writeValueAsString(invalidDto)
            }.andExpect {
                  status { isBadRequest() }
            }
      }

      @Test
      fun `should return not found when update fails`() {
            every { tvSlideService.updateSlide(dummySlide) } returns Optional.empty()
            every { webSocketService.sendSignalToAllClients(SocketMessage.SLIDE) } just runs

            mockMvc.put("/api/slides/edit") {
                  with(jwt())
                  contentType = MediaType.APPLICATION_JSON
                  content = objectMapper.writeValueAsString(dummySlide)
            }.andExpect {
                  status { isNotFound() }
            }
      }
}
