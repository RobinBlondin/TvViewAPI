package com.example.tvviewapi

import io.github.cdimascio.dotenv.Dotenv
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TvViewApiApplication

fun main(args: Array<String>) {
    val dotenv = Dotenv.load()
    dotenv.entries().forEach { entry ->
        System.setProperty(entry.key, entry.value)
    }

    runApplication<TvViewApiApplication>(*args)
}
