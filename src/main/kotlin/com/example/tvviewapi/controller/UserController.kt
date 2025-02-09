package com.example.tvviewapi.controller

import com.example.tvviewapi.dto.UserDto
import com.example.tvviewapi.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/users")
class UserController(
    val service: UserService
) {

    @GetMapping("all")
    fun getUsers(): ResponseEntity<Set<UserDto>> = ResponseEntity.ok().body(service.findAll())

    @GetMapping("{email}")
    fun getUserByEmail(@PathVariable email: String): ResponseEntity<UserDto> =
        service.findUserByEmail(email)
            .map { user -> ResponseEntity.ok().body(user) }
            .orElseGet { ResponseEntity.notFound().header("X-Request-ID", "Input data did not match an existing User").build() }

    @DeleteMapping("delete/{id}")
    fun deleteById(@PathVariable id: UUID): ResponseEntity<String> {
        if(service.deleteById(id)) {
            return ResponseEntity.ok().body("User deleted successfully")
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Input data did not match an existing User")
    }

    @PutMapping("edit")
    fun updateUser(@RequestBody dto: UserDto): ResponseEntity<UserDto> {
        if(dto.id == null || isNotValidDto(dto)) {
            return ResponseEntity.badRequest().header("X-Request-ID", "Input data does not meet requirements").build()
        }

        return service.updateUser(dto)
            .map { user -> ResponseEntity.ok().body(user) }
            .orElseGet { ResponseEntity.notFound().header("X-Request-ID", "Input data did not match an existing User ").build()}
    }

    @PostMapping("/create")
    fun createUser(@RequestBody dto: UserDto): ResponseEntity<UserDto> {
        if(dto.id != null || isNotValidDto(dto)) {
            return ResponseEntity.badRequest().header("X-Request-ID", "Input data does not meet requirements").build()
        }

        return service.createUser(dto)
            .map { user -> ResponseEntity.status(HttpStatus.CREATED).body(user) }
            .orElseGet { ResponseEntity.status(HttpStatus.CONFLICT).build() }
    }

    private fun isNotValidDto(dto: UserDto) = dto.email.isEmpty() || dto.displayName.isEmpty()
}