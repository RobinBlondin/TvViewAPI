package com.example.tvviewapi.service

import io.github.cdimascio.dotenv.Dotenv
import org.springframework.stereotype.Service
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.net.URI

@Service
class R2StorageService {
    private val dotenv: Dotenv = Dotenv.configure()
            .ignoreIfMissing()
            .load()

    private val bucketName = config("R2_BUCKET_NAME")
    private val accountId = config("R2_ACCOUNT_ID")
    private val accessId = config("R2_ACCESS_KEY_ID")
    private val secret = config("R2_SECRET_ACCESS_KEY")

    private val s3Client: S3Client = S3Client.builder()
        .endpointOverride(
            URI.create("https://$accountId.r2.cloudflarestorage.com")
        )
        .region(Region.of("auto"))
        .credentialsProvider(
            StaticCredentialsProvider.create(
                AwsBasicCredentials.create(accessId, secret)

            )
        )
        .forcePathStyle(true)
        .build()

    fun upload(
        fileName: String,
        bytes: ByteArray,
        contentType: String
    ) {
        val request = PutObjectRequest.builder()
            .bucket(bucketName)
            .key("uploads/$fileName")
            .contentType(contentType)
            .build()

        s3Client.putObject(
            request,
            RequestBody.fromBytes(bytes)
        )
    }

    private fun config(name: String): String =
            requireNotNull(dotenv[name] ?: System.getenv(name)) {
                "Missing configuration: $name"
            }
}