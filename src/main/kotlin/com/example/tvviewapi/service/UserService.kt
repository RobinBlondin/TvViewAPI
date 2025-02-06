package com.example.tvviewapi.service

import com.example.tvviewapi.dto.UserDto
import com.example.tvviewapi.mapper.UserMapper
import com.example.tvviewapi.repository.UserRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService(val userRepository: UserRepository, val userMapper: UserMapper) {

    fun findAll(): Set<UserDto> = userRepository.findAll().map { userMapper.toDto(it) }.toSet()

    fun findUserByEmail(email: String): Optional<UserDto> {
        val user = userRepository.getUserByEmail(email)

        if (user.isPresent) {
            return Optional.of(userMapper.toDto(user.get()))
        }
        return Optional.empty()
    }

    fun createUser(dto: UserDto): Optional<UserDto> {
        if(userRepository.existsByEmail(dto.email)) {
            return Optional.empty()
        }

        val user = userMapper.toEntity(dto)
        val saved = userRepository.save(user)
        return Optional.of(userMapper.toDto(saved))
    }

    fun deleteById(id: UUID) = userRepository.deleteById(id)

    fun updateUser(dto: UserDto): Optional<UserDto> {
        if(!userRepository.existsById(dto.id!!)) {
            return Optional.empty()
        }

        val user = userMapper.toEntity(dto)
        val saved = userRepository.save(user)
        return Optional.of(userMapper.toDto(saved))
    }
}