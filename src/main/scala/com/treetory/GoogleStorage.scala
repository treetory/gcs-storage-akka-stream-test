package com.treetory

import com.google.cloud.http.HttpTransportOptions
import com.google.cloud.storage.{Blob, BlobId, Storage, StorageOptions}

import java.net.URL
import java.util.concurrent.TimeUnit

trait GoogleStorage {
  val config: Config = Config.default

  val storage: Storage = StorageOptions
    .newBuilder()
    .setTransportOptions(createTransportOptions(config))
    .setCredentials(config.credentials)
    .setProjectId(config.google.storage.project.name)
    .build()
    .getService

  private def createTransportOptions(config: Config): HttpTransportOptions = {
    HttpTransportOptions.newBuilder().setHttpTransportFactory(config.httpTransportFactory).build()
  }

  def signedUrlFor(fileName: String): Option[URL] = {
    Option(getBlob(fileName)).map(_.signUrl(5L, TimeUnit.MINUTES))
  }

  def getBlob(fileName: String): Blob = {
    storage.get(config.google.storage.project.bucket)
      .get(fileName)
  }

  def list(fileName: String): Any = {
    storage.list(config.google.storage.project.bucket)
  }

  def delete(fileName: String): Boolean = {
    val blobId = BlobId.of(config.google.storage.project.bucket, fileName)
    storage.delete(blobId)
  }
}

object GoogleStorage extends GoogleStorage
