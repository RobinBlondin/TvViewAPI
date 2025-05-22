package com.example.tvviewapi.controller

import com.example.tvviewapi.dto.UserDto
import com.example.tvviewapi.service.UserService
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt
import org.springframework.test.web.servlet.*
import java.util.*

@WebMvcTest(UserController::class)
class UserControllerTest {
      @Autowired
      lateinit var mockMvc: MockMvc

      @MockkBean
      lateinit var userService: UserService

      private val objectMapper = jacksonObjectMapper()
      private val userEmail = "user@email.com"
      private val userDisplayName = "user"

      private val dummyUser = UserDto(
            id = UUID.randomUUID(),
            email = userEmail,
            displayName = userDisplayName
      )

      @Test
      fun `should get all users`() {
            every { userService.findAll() } returns setOf(dummyUser)

            mockMvc.get("/api/users/all") {
                  with(jwt())
            }.andExpect {
                  status { isOk() }
                  content { contentType(MediaType.APPLICATION_JSON) }
                  jsonPath("$[0].email") { value(userEmail) }
                  jsonPath("$.size()") { value(1) }
            }
      }

      @Test
      fun `should get user by email`() {
            every { userService.findUserByEmail(userEmail) } returns Optional.of(dummyUser)

            mockMvc.get("/api/users/$userEmail") {
                  with(jwt())
            }.andExpect {
                  status { isOk() }
                  content { contentType(MediaType.APPLICATION_JSON) }
                  jsonPath("$.email") { value(userEmail) }
            }
      }

      @Test
      fun `should return 404 if user not found`() {
            every { userService.findUserByEmail(any()) } returns Optional.empty()

            mockMvc.get("/api/users/$userEmail") {
                  with(jwt())
            }.andExpect {
                  status { isNotFound() }
            }
      }

      @Test
      fun `should create a new user`() {
            val userToCreate = dummyUser.copy(id = null)

            every { userService.createUser(userToCreate) } returns Optional.of(dummyUser)

            mockMvc.post("/api/users/create") {
                  with(jwt())
                  contentType = MediaType.APPLICATION_JSON
                  content = objectMapper.writeValueAsString(userToCreate)
            }.andExpect {
                  status { isCreated() }
                  content { contentType(MediaType.APPLICATION_JSON) }
                  jsonPath("$.email") { value(userEmail) }
            }
      }

      @Test
      fun `should return 409 if user already exists`() {
            val userToCreate = dummyUser.copy(id = null)

            every { userService.createUser(userToCreate) } returns Optional.empty()

            mockMvc.post("/api/users/create") {
                  with(jwt())
                  contentType = MediaType.APPLICATION_JSON
                  content = objectMapper.writeValueAsString(userToCreate)
            }.andExpect {
                  status { isConflict() }
            }
      }

      @Test
      fun `should return bad request for create with no email`() {
            val invalidDto = dummyUser.copy(id = UUID.randomUUID(), email = "")

            mockMvc.post("/api/users/create") {
                  with(jwt())
                  contentType = MediaType.APPLICATION_JSON
                  content = objectMapper.writeValueAsString(invalidDto)
            }.andExpect {
                  status { isBadRequest() }
            }
      }

      @Test
      fun `should return bad request for create with no display name`() {
            val invalidDto = dummyUser.copy(id = UUID.randomUUID(), displayName = "")

            mockMvc.post("/api/users/create") {
                  with(jwt())
                  contentType = MediaType.APPLICATION_JSON
                  content = objectMapper.writeValueAsString(invalidDto)
            }.andExpect {
                  status { isBadRequest() }
            }
      }

      @Test
      fun `should update user`() {
            val userToUpdate = dummyUser.copy(displayName = "newDisplayName")

            every { userService.updateUser(userToUpdate) } returns Optional.of(userToUpdate)

            mockMvc.put("/api/users/edit") {
                  with(jwt())
                  contentType = MediaType.APPLICATION_JSON
                  content = objectMapper.writeValueAsString(userToUpdate)
            }.andExpect {
                  status { isOk() }
                  content { contentType(MediaType.APPLICATION_JSON) }
                  jsonPath("$.displayName") { value("newDisplayName") }
            }
      }

      @Test
      fun `should return 404 if user not found for update`() {
            val userToUpdate = dummyUser.copy(displayName = "newDisplayName")

            every { userService.updateUser(userToUpdate) } returns Optional.empty()

            mockMvc.put("/api/users/edit") {
                  with(jwt())
                  contentType = MediaType.APPLICATION_JSON
                  content = objectMapper.writeValueAsString(userToUpdate)
            }.andExpect {
                  status { isNotFound() }
            }
      }

      @Test
      fun `should return bad request for update with no email`() {
            val invalidDto = dummyUser.copy(id = UUID.randomUUID(), email = "")

            mockMvc.put("/api/users/edit") {
                  with(jwt())
                  contentType = MediaType.APPLICATION_JSON
                  content = objectMapper.writeValueAsString(invalidDto)
            }.andExpect {
                  status { isBadRequest() }
            }
      }

      @Test
      fun `should return bad request for update with no display name`() {
            val invalidDto = dummyUser.copy(id = UUID.randomUUID(), displayName = "")

            mockMvc.put("/api/users/edit") {
                  with(jwt())
                  contentType = MediaType.APPLICATION_JSON
                  content = objectMapper.writeValueAsString(invalidDto)
            }.andExpect {
                  status { isBadRequest() }
            }
      }

      @Test
      fun `should delete user by id`() {
            val userId = UUID.randomUUID()

            every { userService.deleteById(userId) } returns true

            mockMvc.delete("/api/users/delete/$userId") {
                  with(jwt())
            }.andExpect {
                  status { isOk() }
                  jsonPath("$") { value("User deleted successfully") }
            }
      }

      @Test
      fun `should return 404 if user not found for delete`() {
            val userId = UUID.randomUUID()

            every { userService.deleteById(userId) } returns false

            mockMvc.delete("/api/users/delete/$userId") {
                  with(jwt())
            }.andExpect {
                  status { isNotFound() }
                  jsonPath("$") { value("Input data did not match an existing User") }
            }
      }
}