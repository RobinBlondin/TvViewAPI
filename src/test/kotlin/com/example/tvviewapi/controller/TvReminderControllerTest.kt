package com.example.tvviewapi.controller

import com.example.tvviewapi.dto.TvReminderDto
import com.example.tvviewapi.enums.SocketMessage
import com.example.tvviewapi.service.TvReminderService
import com.example.tvviewapi.service.WebSocketService
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import java.util.Optional
import java.util.UUID

@WebMvcTest(TvReminderController::class)
class TvReminderControllerTest {
      @Autowired
      lateinit var mockMvc: MockMvc

      @MockkBean
      lateinit var tvReminderService: TvReminderService

      @MockkBean
      lateinit var webSocketService: WebSocketService

      private val objectMapper = jacksonObjectMapper()
      private val reminderDescription = "reminder"

      private val dummyReminder = TvReminderDto(
            id = UUID.randomUUID(),
            description = reminderDescription,
            done = false
      )

      @Test
      fun `should get all reminders`() {
            every { tvReminderService.getAllReminders()} returns listOf(dummyReminder)

            mockMvc.get("/api/reminders/all") {
                  with(jwt())
            }.andExpect {
                  status { isOk() }
                  content { contentType(MediaType.APPLICATION_JSON) }
                  jsonPath("$[0].description") { value(reminderDescription) }
                  jsonPath("$.size()") { value(1) }
            }
      }

      @Test
      fun `should return reminder by id`() {
            every { tvReminderService.getReminderById(dummyReminder.id!!) } returns Optional.of(dummyReminder)

            mockMvc.get("/api/reminders/${dummyReminder.id}") {
                  with(jwt())
            }.andExpect {
                  status { isOk() }
                  content { contentType(MediaType.APPLICATION_JSON) }
                  jsonPath("$.description") { value(reminderDescription) }
            }
      }

      @Test
      fun `should return 404 if reminder not found`() {
            every { tvReminderService.getReminderById(any()) } returns Optional.empty()

            mockMvc.get("/api/reminders/${dummyReminder.id}") {
                  with(jwt())
            }.andExpect {
                  status { isNotFound() }
            }
      }

      @Test
      fun `should create a new slide`() {
            val reminderToCreate = dummyReminder.copy(id = null, "new reminder", done = false)

            every { tvReminderService.createReminder(reminderToCreate) } returns reminderToCreate
            every { webSocketService.sendSignalToAllClients(SocketMessage.REMINDER) } returns Unit

            mockMvc.post("/api/reminders/create") {
                  with(jwt())
                  contentType = MediaType.APPLICATION_JSON
                  content = objectMapper.writeValueAsString(reminderToCreate)
            }.andExpect {
                  status { isOk() }
                  content { contentType(MediaType.APPLICATION_JSON) }
                  jsonPath("$.description") { value(reminderToCreate.description) }
            }
      }

      @Test
      fun `should return bad request for invalid create`() {
            val invalidDto = dummyReminder.copy(id = UUID.randomUUID(), description = "")

            mockMvc.post("/api/reminders/create") {
                  with(jwt())
                  contentType = MediaType.APPLICATION_JSON
                  content = objectMapper.writeValueAsString(invalidDto)
            }.andExpect {
                  status { isBadRequest() }
            }
      }

      @Test
      fun `should delete slide if exists`() {
            every { tvReminderService.deleteReminderById(dummyReminder.id!!) } returns true
            every { webSocketService.sendSignalToAllClients(SocketMessage.REMINDER) } returns Unit

            mockMvc.delete("/api/reminders/delete/${dummyReminder.id}") {
                  with(jwt())
            }.andExpect {
                  status { isOk() }
                  jsonPath("$") { value("Reminder deleted successfully") }
            }
      }

      @Test
      fun `should return 404 if delete fails`() {
            every { tvReminderService.deleteReminderById(any()) } returns false

            mockMvc.delete("/api/reminders/delete/${dummyReminder.id}") {
                  with(jwt())
            }.andExpect {
                  status { isOk() }
                  jsonPath("$") { value("Input data did not match an existing TvReminder") }
            }
      }

      @Test
      fun `should delete checked reminders`() {
            every { tvReminderService.deleteCheckedReminders() } returns Unit
            every { webSocketService.sendSignalToAllClients(SocketMessage.REMINDER) } returns Unit

            mockMvc.delete("/api/reminders/delete/done") {
                  with(jwt())
            }.andExpect {
                  status { isOk() }
                  jsonPath("$") { value("Checked reminders deleted successfully") }
            }
      }

      @Test
      fun `should update reminder`() {
            val reminderToUpdate = dummyReminder.copy(description = "updated reminder")

            every { tvReminderService.updateReminder(reminderToUpdate) } returns Optional.of(reminderToUpdate)
            every { webSocketService.sendSignalToAllClients(SocketMessage.REMINDER) } returns Unit

            mockMvc.put("/api/reminders/edit") {
                  with(jwt())
                  contentType = MediaType.APPLICATION_JSON
                  content = objectMapper.writeValueAsString(reminderToUpdate)
            }.andExpect {
                  status { isOk() }
                  content { contentType(MediaType.APPLICATION_JSON) }
                  jsonPath("$.description") { value(reminderToUpdate.description) }
            }
      }

      @Test
      fun `should return bad request for invalid update`() {
            val invalidDto = dummyReminder.copy(id = null, description = "")

            mockMvc.put("/api/reminders/edit") {
                  with(jwt())
                  contentType = MediaType.APPLICATION_JSON
                  content = objectMapper.writeValueAsString(invalidDto)
            }.andExpect {
                  status { isBadRequest() }
            }
      }

      @Test
      fun `should return 404 if update fails`() {
            val invalidDto = dummyReminder.copy(id = UUID.randomUUID(), description = "updated reminder")

            every { tvReminderService.updateReminder(invalidDto) } returns Optional.empty()
            every { webSocketService.sendSignalToAllClients(SocketMessage.REMINDER) } returns Unit

            mockMvc.put("/api/reminders/edit") {
                  with(jwt())
                  contentType = MediaType.APPLICATION_JSON
                  content = objectMapper.writeValueAsString(invalidDto)
            }.andExpect {
                  status { isNotFound() }
            }
      }
}