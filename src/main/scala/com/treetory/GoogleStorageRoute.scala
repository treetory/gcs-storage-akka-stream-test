package com.treetory

import akka.http.scaladsl.model.{StatusCodes, Uri}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

object GoogleStorageRoute {
  //#user-routes-class
  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

  val googleStorage = GoogleStorage

  val googleStorageRoutes: Route =
    pathPrefix("gcs") {
      pathEnd {
        get {
          parameter('fileName.as[String]) { (fileName) =>
            val signedURL = googleStorage.signedUrlFor(fileName).get
//            complete(signedURL.toURI.toString)
            redirect(Uri.apply(signedURL.toURI.toString), StatusCodes.TemporaryRedirect)
          }
        }
      }
    }
}
