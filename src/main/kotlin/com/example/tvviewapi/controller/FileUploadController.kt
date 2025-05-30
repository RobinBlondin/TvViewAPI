package com.example.tvviewapi.controller

import io.github.cdimascio.dotenv.Dotenv
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDateTime
import kotlin.io.path.exists

@RestController
@RequestMapping("/api/files")
class FileUploadController {

      private val uploadDir = Paths.get("uploads")
      private val dotenv: Dotenv? = Dotenv.configure().ignoreIfMissing().load()

      init {
            if(!uploadDir.exists()) {
                  Files.createDirectories(uploadDir)
            }
      }

      @PostMapping("upload")
      fun uploadFile(@RequestParam("file") file: MultipartFile): ResponseEntity<String> {

            if(file.contentType?.contains("image") != true) {
                  return ResponseEntity.badRequest()
                        .header("X-Request-ID", "File type is not valid")
                        .build()
            }

            val timestamp = LocalDateTime.now().toString().replace(":", "-")
            val fileName = "$timestamp-${file.originalFilename}".replace(" ", "_")
            val filePath = uploadDir.resolve(fileName)

            Files.copy(file.inputStream, filePath)

            val storedUrl =  dotenv?.get("FILE_UPLOAD_DIR")  + "/$fileName"
            return ResponseEntity.ok().body(storedUrl)
      }
}