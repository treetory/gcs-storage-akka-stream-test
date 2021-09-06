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

  def getSignedURL(fileName: String): Future[HttpResponse] = {
    val uri: Uri = s"http://localhost:3000/api/v1/gcs?fileName=${fileName}"
    logger.info("{}", uri)
    http.singleRequest(HttpRequest(HttpMethods.GET, uri))
  }

  def receive: Receive = {
    case GetSignedURL(fileName) =>
      sender ! getSignedURL(fileName)
  }

}
