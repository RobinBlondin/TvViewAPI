package com.example.tvviewapi.entity

import jakarta.persistence.Entity
import lombok.Data

@Entity
class TvSlide(
    var url: String = ""
) : BaseEntity()