package com.example.tvviewapi.controller

import com.example.tvviewapi.dto.UserDto
import com.example.tvviewapi.entity.User
import com.example.tvviewapi.repository.UserRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import java.util.*

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest(
      @Autowired
      val userRepository: UserRepository,
      @Autowired
      val mockMvc: MockMvc,
      @Autowired
      val objectMapper: ObjectMapper
) {

      val baseUrl = "http://localhost:8080/api/users/"
      final val email = "test@test.com"
      final val displayName = "Test User"
      val updatedName = "Updated User"
      val existingId = UUID.randomUUID()

      val user = User(
            email = email,
            displayName = displayName
      )

      val dto = UserDto(
            email = email,
            displayName = displayName
      )

      val noNameDto = UserDto(
            email = email,
            displayName = ""
      )

      val noEmailDto = UserDto(
            email = "",
            displayName = displayName
      )

      val toBeUpdatedExistingId = UserDto(
            id = existingId,
            email = email,
            displayName = updatedName
      )

      val toBeUpdateRandomId = UserDto(
            id = UUID.randomUUID(),
            email = email,
            displayName = updatedName
      )

      @Test
      fun getUsers() {
      }

      @Test
      fun getUserByEmail() {
      }

      @Test
      fun deleteById() {
      }

      @Test
      fun updateUser() {
      }

      @Test
      fun createUser() {
      }

      @Test
      fun getService() {
      }
}