package com.example.tvviewapi.service

import com.example.tvviewapi.dto.UserDto
import com.example.tvviewapi.mapper.UserMapper
import com.example.tvviewapi.repository.UserRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService(
    val repo: UserRepository,
    val userMapper: UserMapper
) {

    fun findAll(): Set<UserDto> = repo.findAll().map { userMapper.toDto(it) }.toSet()

    fun isRegisteredUser(email: String): Boolean = repo.existsByEmail(email)

    fun findUserByEmail(email: String  = "robin.blondin@gmail.com"): Optional<UserDto> = repo.getUserByEmail(email).map { userMapper.toDto(it) }

    fun createUser(dto: UserDto): Optional<UserDto> {
        if(repo.existsByEmail(dto.email) || (dto.id != null && repo.existsById(dto.id!!))) {
            return Optional.empty()
        }

        val entity = repo.save(userMapper.toEntity(dto))
        return Optional.of(userMapper.toDto(entity))
    }

    fun deleteById(id: UUID): Boolean {
        if(repo.existsById(id)) {
            repo.deleteById(id)
            return true
        }
        return false
    }

    fun updateUser(dto: UserDto): Optional<UserDto> {
        if(!repo.existsById(dto.id!!)) {
            return Optional.empty()
        }

        val entity = repo.save(userMapper.toEntity(dto))
        return Optional.of(userMapper.toDto(entity))
    }

    fun getUserEmails(): Set<String> = repo.findAll().map { it.email }.toSet()

}