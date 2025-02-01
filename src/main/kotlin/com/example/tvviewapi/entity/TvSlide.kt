package com.example.tvviewapi.entity

import jakarta.persistence.Entity

@Entity
class TvSlide(
    private val url: String
) : BaseEntity()