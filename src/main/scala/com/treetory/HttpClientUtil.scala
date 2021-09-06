package com.treetory

import org.slf4j.LoggerFactory
import scalaj.http.{Http, HttpResponse}

class HttpClientUtil {

  def logger = LoggerFactory.getLogger(this.getClass)

  def getSignedURL(fileName: String): String = {
    val response: HttpResponse[String] =
      Http("http://localhost:3000/api/v1/gcs")
      .param("fileName", fileName)
        .asString

    logger.info("{}", response.headers)

    response.header("Location").get
  }

}