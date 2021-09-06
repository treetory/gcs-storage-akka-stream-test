package com.treetory

import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import org.slf4j.LoggerFactory

import scala.concurrent.Future

object AkkaHttpClientTestRoute {

  def logger = LoggerFactory.getLogger(this.getClass)

  val akkaHttpClientUtil = new AkkaHttpClientUtil()

  def getSignedURL(fileName: String): Future[HttpResponse] = akkaHttpClientUtil.getSignedURL(fileName)

  val akkaHttpClientRoutes: Route =
    pathPrefix("gcs") {
      pathEnd {
        get {
          parameter('fileName.as[String]) { (fileName) =>
            rejectEmptyResponse {
              onSuccess(getSignedURL(fileName)) { response =>
                complete(response.getHeader("Location").get().value())
              }
            }
          }
        }
      }
    }

}
