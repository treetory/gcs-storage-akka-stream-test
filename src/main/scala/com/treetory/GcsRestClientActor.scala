package com.treetory

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.{Actor, ActorLogging}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpMethods, HttpRequest, HttpResponse, Uri}
import org.slf4j.LoggerFactory

import scala.concurrent.Future

final case class GetSignedURL(fileName: String)

class GcsRestClientActor extends Actor with ActorLogging {

  def logger = LoggerFactory.getLogger(this.getClass)

  val http = Http(ActorSystem(Behaviors.empty, "SingleRequest"))

  val scheme = "http"
  val host = "localhost"
  val port = 3000
  val path = Uri.Path("/api/v1/gcs")

  def getSignedURL(fileName: String): Future[HttpResponse] = {

    val uri = Uri()
      .withScheme(scheme)
      .withHost(host)
      .withPort(port)
      .withPath(path)
      .withQuery(Uri.Query(s"fileName=${fileName}"))

    logger.info("{}", uri)
    http.singleRequest(HttpRequest(HttpMethods.GET, uri))
  }

  def receive: Receive = {
    case GetSignedURL(fileName) =>
      sender ! getSignedURL(fileName)
  }

}
