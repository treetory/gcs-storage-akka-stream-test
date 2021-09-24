package com.treetory.util

import com.google.api.gax.paging.Page
import com.google.cloud.http.HttpTransportOptions
import com.google.cloud.storage.Storage.BlobListOption
import com.google.cloud.storage.{Blob, BlobId, Storage, StorageOptions}
import com.treetory.config.Config

import java.net.URL
import java.util.concurrent.TimeUnit
import scala.collection.mutable.ListBuffer

final case class BlobInfo(
                         blobId: String,
                         bucket: String,
                         name: String,
                         createTime: Long,
                         deleteTime: Long
                         )

final case class BlobInfoList(blobInfoList: List[BlobInfo])

final case class PagedList(token: String, blobInfoList: List[BlobInfo])

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

  def pagedList(token: String): PagedList = {
    val blobInfoList: ListBuffer[BlobInfo] = new ListBuffer[BlobInfo]
    val pagedList: Page[Blob] = storage.list(
      config.google.storage.project.bucket,
      BlobListOption.pageSize(100),
      BlobListOption.pageToken(token)
    )
    pagedList.getValues.forEach(blob => {
      blobInfoList += BlobInfo(blob.getBlobId.toString, blob.getBucket, blob.getName, blob.getCreateTime, blob.getDeleteTime)
    })
    PagedList(pagedList.getNextPageToken, blobInfoList.toList)
  }
}

object GoogleStorage extends GoogleStorage
