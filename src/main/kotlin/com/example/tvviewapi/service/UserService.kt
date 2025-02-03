package com.example.tvviewapi.service

import com.example.tvviewapi.dto.UserDto
import com.example.tvviewapi.entity.User
import com.example.tvviewapi.mapper.UserMapper
import com.example.tvviewapi.repository.UserRepository
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull

@Service
class UserService(val userRepository: UserRepository, val userMapper: UserMapper) {
    fun findAll(): List<UserDto> = userRepository.findAll().map { userMapper.toDto(it) }

    fun findUserByEmail(email: String): UserDto? {
        val user = userRepository.getUserByEmail(email)

        if (user.isPresent) {
            return userMapper.toDto(user.get())
        }
        return null
    }

    fun create(userDto: UserDto): UserDto {
        val user = userMapper.toEntity(userDto)
        return userMapper.toDto(userRepository.save(user))
    }
}