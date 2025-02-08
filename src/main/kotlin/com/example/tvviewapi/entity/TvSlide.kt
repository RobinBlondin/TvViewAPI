package com.example.tvviewapi.entity

import jakarta.persistence.Entity

@Entity
class TvSlide(
    var url: String = ""
) : BaseEntity()