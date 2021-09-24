package com.treetory.route

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.{_symbol2NR, get, onSuccess, parameter, pathEnd, pathPrefix, redirect}
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.util.Timeout
import com.treetory.actor.{GcsRestClientActor, GetSignedURL}
import com.treetory.route.AkkaHttpClientTestRoute.getSignedURL
import org.slf4j.LoggerFactory

import scala.concurrent.duration.DurationInt

object GcsRestClientActorRoutes {

  implicit val system: ActorSystem = ActorSystem("GoogleCloudStorageAkkaHttpServer")
  implicit val timeout: Timeout    = Timeout(60.seconds)

  def logger = LoggerFactory.getLogger(this.getClass)

  val gcsRestClientActor: ActorRef = system.actorOf(Props[GcsRestClientActor])

  val gcsRestClientActorRoutes: Route =
    pathPrefix("gcs") {
      pathPrefix("actor") {
        pathEnd {
          get {
            parameter('fileName.as[String]) { (fileName) =>
              val signedURL = gcsRestClientActor ? GetSignedURL(fileName)
              logger.info("{}", signedURL)
              onSuccess(getSignedURL(fileName)) { response =>
                redirect(response.getHeader("Location").get().value(), StatusCodes.TemporaryRedirect)
              }
            }
          }
        }
      }
    }
}
