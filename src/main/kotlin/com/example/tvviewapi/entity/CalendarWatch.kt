package com.example.tvviewapi.entity

import jakarta.persistence.Entity

@Entity
class CalendarWatch: BaseEntity() {
    var channelId: String = ""
    var resourceId: String = ""
}