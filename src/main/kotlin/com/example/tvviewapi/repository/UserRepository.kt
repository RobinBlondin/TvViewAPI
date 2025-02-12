package com.example.tvviewapi.repository

import com.example.tvviewapi.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository: JpaRepository<User, UUID> {
    fun getUserByEmail(email: String): Optional<User>
    fun existsByEmail(email: String): Boolean
}
