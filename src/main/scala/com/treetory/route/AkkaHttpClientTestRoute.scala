package com.treetory.route

import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives.{_symbol2NR, complete, get, onSuccess, parameter, pathEnd, pathPrefix, redirect, rejectEmptyResponse}
import akka.http.scaladsl.server.Route
import com.treetory.util.AkkaHttpClientUtil
import org.slf4j.LoggerFactory

import scala.concurrent.Future

object AkkaHttpClientTestRoute {

  def logger = LoggerFactory.getLogger(this.getClass)

  val akkaHttpClientUtil = new AkkaHttpClientUtil()

  def getSignedURL(fileName: String): Future[HttpResponse] = akkaHttpClientUtil.getSignedURL(fileName)

  val akkaHttpClientRoutes: Route =
    pathPrefix("gcs") {
      pathPrefix("akka-http") {
        pathEnd {
          get {
            parameter('fileName.as[String]) { (fileName) =>
              rejectEmptyResponse {
                onSuccess(getSignedURL(fileName)) { response =>
                  logger.info("{}", response.status.intValue());
                  if (response.status.intValue() == 307) {
                    redirect(response.getHeader("Location").get().value(), StatusCodes.TemporaryRedirect)
                  } else if (response.status.intValue() == 200) {
                    complete(StatusCodes.OK)
                  } else {
                    complete(StatusCodes.NotFound)
                  }
                }
              }
            }
          }
        }
      }
    }
}
