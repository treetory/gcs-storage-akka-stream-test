package com.treetory

import akka.http.scaladsl.model.{StatusCodes, Uri}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import org.slf4j.LoggerFactory

object GoogleStorageRoute {

  def logger = LoggerFactory.getLogger(this.getClass)

  val googleStorage = GoogleStorage

  val googleStorageRoutes: Route =
    pathPrefix("gcs") {
      pathEnd {
        get {
          parameter('fileName.as[String]) { (fileName) =>
            val signedURL = googleStorage.signedUrlFor(fileName).get
            complete(signedURL.toURI.toString)
            redirect(Uri.apply(signedURL.toURI.toString), StatusCodes.TemporaryRedirect)
          }
        }
      }
    }
}
