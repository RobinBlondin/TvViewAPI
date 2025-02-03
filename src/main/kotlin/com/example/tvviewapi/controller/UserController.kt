package com.example.tvviewapi.controller

import com.example.tvviewapi.dto.UserDto
import com.example.tvviewapi.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = ["*"])
class UserController(val userService: UserService) {
    @PostMapping("/create")
    fun create(@RequestBody userDto: UserDto): ResponseEntity<UserDto> {
        val user = userService.findUserByEmail(userDto.email)

        if(user == null) {
            val dto = userService.create(userDto)
            return ResponseEntity.ok(dto)
        }


        return ResponseEntity.badRequest().body(user)
    }
}