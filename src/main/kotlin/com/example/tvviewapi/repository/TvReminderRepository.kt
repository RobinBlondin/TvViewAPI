package com.example.tvviewapi.repository

import com.example.tvviewapi.entity.TvReminder
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface TvReminderRepository: JpaRepository<TvReminder, UUID>