package com.example.tvviewapi.entity

import jakarta.persistence.*
import lombok.Data
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

@Entity
@Table(name = "_user")
class User (
        var email: String = "",
        var displayName: String = "",
        var enabled: Boolean = true,
        var refreshToken: String = "",
): BaseEntity(), UserDetails {
    override fun getUsername(): String = email

    override fun getPassword(): String? = null
    @Transient
    override fun getAuthorities(): MutableList<GrantedAuthority> = mutableListOf(SimpleGrantedAuthority("ROLE_USER"))

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = enabled
}