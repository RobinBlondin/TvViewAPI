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
    fun getUsers(): ResponseEntity<Set<UserDto>> {
        val users = service.findAll()

        return ResponseEntity
            .ok()
            .body(users)
    }

    @GetMapping("{email}")
    fun getUserByEmail(@PathVariable email: String): ResponseEntity<UserDto> =
        service.findUserByEmail(email)
            .map { user -> ResponseEntity.status(HttpStatus.OK).body(user) }
            .orElseGet { ResponseEntity.status(HttpStatus.NOT_FOUND).build() }


    @DeleteMapping("delete/{id}")
    fun deleteById(@PathVariable id: UUID): ResponseEntity<String> {
        if(service.deleteById(id)) {
            return ResponseEntity.ok().body("User deleted successfully")
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User could not be found")
    }

    @PutMapping("edit")
    fun updateUser(@RequestBody dto: UserDto): ResponseEntity<UserDto> {
        if(dto.id == null) {
            return ResponseEntity.badRequest().build()
        }

        return service.updateUser(dto)
            .map { user -> ResponseEntity.status(HttpStatus.OK).body(user) }
            .orElseGet { ResponseEntity.status(HttpStatus.NOT_FOUND).build() }

    }


    @PostMapping("/create")
    fun createUser(@RequestBody dto: UserDto): ResponseEntity<UserDto> {
        if(dto.email.isEmpty()) {
            return ResponseEntity.badRequest().build()
        }

        return service.createUser(dto)
            .map { user -> ResponseEntity.status(HttpStatus.CREATED).body(user) }
            .orElseGet { ResponseEntity.status(HttpStatus.CONFLICT).build() }
    }






}