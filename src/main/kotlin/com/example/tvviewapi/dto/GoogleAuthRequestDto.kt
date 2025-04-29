package com.example.tvviewapi.dto

data class GoogleAuthRequestDto(
      val code: String,
      val clientId: String,
      val clientSecret: String,
      val redirectUri: String,
      val isTvView: Boolean
)
