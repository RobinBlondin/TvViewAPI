package com.example.tvviewapi.controller

import com.example.tvviewapi.service.R2StorageService
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
class FileUploadController(
      private val r2StorageService: R2StorageService
) {

      private val dotenv: Dotenv? = Dotenv.configure().ignoreIfMissing().load()

      @PostMapping("upload")
      fun uploadFile(@RequestParam("file") file: MultipartFile): ResponseEntity<String> {

            if(file.isEmpty || file.contentType?.startsWith("image/") != true) {
                  return ResponseEntity.badRequest().build()
            }

            val timestamp = LocalDateTime.now().toString().replace(":", "-")
            val fileName = "$timestamp-${file.originalFilename}".replace(" ", "_")

            r2StorageService.upload(
                  fileName = fileName,
                  bytes = file.bytes,
                  contentType = file.contentType!!
            )

            val storedUrl = (System.getenv("FILE_UPLOAD_DIR")
                  ?: dotenv?.get("FILE_UPLOAD_DIR"))  + "/$fileName"
            return ResponseEntity.ok().body(storedUrl)
      }
}