package com.example.tvviewapi

import io.github.cdimascio.dotenv.Dotenv
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TvViewApiApplication

fun main(args: Array<String>) {
    runApplication<TvViewApiApplication>(*args)
}
