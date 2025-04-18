package com.example.tvviewapi

import io.github.cdimascio.dotenv.Dotenv
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class TvViewApiApplication

fun main(args: Array<String>) {
      val dotenv = Dotenv.configure().ignoreIfMissing().load()

        dotenv.entries().forEach {
            System.setProperty(it.key, it.value)
        }
    runApplication<TvViewApiApplication>(*args)
}
