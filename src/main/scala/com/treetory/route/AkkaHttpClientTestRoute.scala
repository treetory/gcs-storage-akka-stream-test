package com.treetory.route

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives.{_symbol2NR, as, complete, entity, get, onSuccess, parameter, pathEnd, pathPrefix, post, redirect, rejectEmptyResponse}
import akka.http.scaladsl.server.{Directives, Route}
import com.treetory.util.AkkaHttpClientUtil
import org.slf4j.LoggerFactory

import scala.concurrent.Future

object AkkaHttpClientTestRoute {

  def logger = LoggerFactory.getLogger(this.getClass)

  val akkaHttpClientUtil = new AkkaHttpClientUtil()

  def getSignedURL(fileName: String): Future[HttpResponse] = akkaHttpClientUtil.getSignedURL(fileName)

  def sendReportJson(reportJson: String): Future[HttpResponse] = akkaHttpClientUtil.sendReportJson(reportJson)

  val akkaHttpClientRoutes: Route =
    Directives.concat(
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
      },
      pathPrefix("example") {
        pathEnd {
          Directives.concat(
            get {
              parameter('fileName.as[String]) { (fileName) =>
                rejectEmptyResponse {
                  onSuccess(getSignedURL(fileName)) { response =>
                    logger.info("{}", response.entity);
                    if (response.status.intValue() == 200) {
                      complete(HttpEntity(ContentTypes.`application/json`, response.entity.dataBytes))
                    } else {
                      complete(StatusCodes.NotFound)
                    }
                  }
                }
              }
            },
            post {
              entity(as[String]) { (reportJson) =>
                rejectEmptyResponse {
                  onSuccess(sendReportJson(reportJson)) { response =>
                    logger.info("{}", response.entity);
                    complete(HttpEntity(ContentTypes.`application/json`, response.entity.dataBytes))
                  }
                }
              }
            }
          )
        }
      }
    )
}
