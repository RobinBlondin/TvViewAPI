package com.example.tvviewapi.mapper

import com.example.tvviewapi.dto.UserDto
import com.example.tvviewapi.entity.User
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
interface UserMapper {
    fun toDto(user: User): UserDto
    fun toEntity(userDto: UserDto): User
}