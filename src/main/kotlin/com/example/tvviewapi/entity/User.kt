package com.example.tvviewapi.entity

import jakarta.persistence.*
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

@Entity
class User (
    private val email: String = "",
    private val displayName: String = "",
    private var enable: Boolean = true
): BaseEntity(), UserDetails {
    override fun getUsername(): String = email

    override fun getPassword(): String? = null
    @Transient
    override fun getAuthorities(): MutableList<GrantedAuthority> = mutableListOf(SimpleGrantedAuthority("ROLE_USER"))

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = enable
}