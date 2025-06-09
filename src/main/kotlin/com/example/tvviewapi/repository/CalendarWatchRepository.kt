package com.example.tvviewapi.repository

import com.example.tvviewapi.entity.CalendarWatch
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface CalendarWatchRepository: JpaRepository<CalendarWatch, UUID>