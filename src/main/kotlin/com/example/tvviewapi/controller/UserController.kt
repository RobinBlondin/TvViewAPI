package com.example.tvviewapi.controller

import com.example.tvviewapi.dto.UserDto
import com.example.tvviewapi.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/users")
class UserController(val userService: UserService) {

    @GetMapping("all")
    fun getUsers(): ResponseEntity<Set<UserDto>> {
        val users = userService.findAll()

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(users)
    }

    @GetMapping("{email}")
    fun getUserByEmail(@PathVariable email: String): ResponseEntity<UserDto> {
        val user = userService.findUserByEmail(email)

        if(user.isEmpty) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null)
        }

        return ResponseEntity.status(HttpStatus.OK).body(user.get())
    }

    @DeleteMapping("delete/{id}")
    fun deleteById(@PathVariable id: UUID) = userService.deleteById(id)

    @PutMapping("update")
    fun updateUser(@RequestBody dto: UserDto): ResponseEntity<UserDto> {
        if(dto.id == null) {
            return ResponseEntity.badRequest().build()
        }

        return userService.updateUser(dto)
            .map { user ->
                ResponseEntity.status(HttpStatus.OK).body(user)
            }
            .orElseGet { ResponseEntity.status(HttpStatus.NOT_FOUND).build() }

    }


    @PostMapping("/create")
    fun createUser(@RequestBody dto: UserDto): ResponseEntity<UserDto> {
        if(dto.email.isEmpty()) {
            return ResponseEntity.badRequest().build()
        }

        return userService.createUser(dto)
            .map { user -> ResponseEntity.status(HttpStatus.CREATED).body(user) }
            .orElseGet { ResponseEntity.status(HttpStatus.CONFLICT).build() }
    }






}