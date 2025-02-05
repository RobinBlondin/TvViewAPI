package com.example.tvviewapi.controller

import com.example.tvviewapi.dto.UserDto
import com.example.tvviewapi.response.UserResponse
import com.example.tvviewapi.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserController(val userService: UserService) {

    @PostMapping("/create")
    fun create(userDto: UserDto): ResponseEntity<UserResponse> {

        val user = userService.create(userDto)

        if(user.isEmpty) {
            return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(UserResponse("User already exists", null))
        }

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(UserResponse("User created successfully", user.get()))
    }
}