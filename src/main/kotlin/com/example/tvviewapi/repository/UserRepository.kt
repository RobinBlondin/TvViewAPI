package com.example.tvviewapi.repository

import com.example.tvviewapi.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserRepository: JpaRepository<User, UUID> {
    fun getUserByEmail(email: String): Optional<User>
}
