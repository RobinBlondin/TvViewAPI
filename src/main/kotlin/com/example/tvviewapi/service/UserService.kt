package com.example.tvviewapi.service

import com.example.tvviewapi.dto.UserDto
import com.example.tvviewapi.entity.User
import com.example.tvviewapi.mapper.UserMapper
import com.example.tvviewapi.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.*
import kotlin.jvm.optionals.getOrNull

@Service
class UserService(val userRepository: UserRepository, val userMapper: UserMapper) {
    fun findAll(): List<UserDto> = userRepository.findAll().map { userMapper.toDto(it) }

    fun findUserByEmail(email: String): Optional<UserDto> {
        val user = userRepository.getUserByEmail(email)

        if (user.isPresent) {
            return Optional.of(userMapper.toDto(user.get()))
        }
        return Optional.empty()
    }

    fun create(userDto: UserDto): Optional<UserDto> {
        if(findUserByEmail(userDto.email).isPresent) {
            return Optional.empty()
        }

        val user = userMapper.toEntity(userDto)
        val saved = userRepository.save(user)

        return Optional.of(userMapper.toDto(saved))
    }

}