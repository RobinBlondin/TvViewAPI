package com.example.tvviewapi.repository

import com.example.tvviewapi.entity.TvSlide
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface TVSlideRepository: JpaRepository<TvSlide, UUID>