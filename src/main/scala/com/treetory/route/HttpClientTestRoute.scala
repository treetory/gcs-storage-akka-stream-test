package com.treetory.route

import akka.http.scaladsl.server.Directives.{_symbol2NR, complete, get, parameter, pathEnd, pathPrefix}
import akka.http.scaladsl.server.Route
import com.treetory.util.HttpClientUtil
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
