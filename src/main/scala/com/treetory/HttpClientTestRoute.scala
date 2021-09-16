package com.treetory

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import org.slf4j.LoggerFactory

object HttpClientTestRoute {

  def logger = LoggerFactory.getLogger(this.getClass)

  val httpClientUtil = new HttpClientUtil()

  val httpClientTestRoute: Route =
    pathPrefix("gcs") {
      pathPrefix("scalaj") {
        pathEnd {
          get {
            parameter('fileName.as[String]) { (fileName) =>
              val signedURL = httpClientUtil.getSignedURL(fileName)
              logger.info("{}", signedURL)
              complete(signedURL)
            }
          }
        }
      }
    }

}
