package com.treetory

import akka.actor.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.{HttpMethods, HttpRequest, HttpResponse, Uri}
import akka.http.scaladsl.unmarshalling.Unmarshal
import org.slf4j.LoggerFactory
import spray.json.DefaultJsonProtocol.{jsonFormat1, jsonFormat15, _}
import spray.json._

import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

final case class Faker(
    id: Long,
    title: String,
    client: String,
    area: String,
    country: String,
    contact: String,
    assignee: String,
    progress: Float,
    startTimestamp: Long,
    endTimestamp: Long,
    budget: Float,
    transaction: String,
    account: String,
    version: String,
    available: Boolean
)
final case class Fakers(fakers: Seq[Faker])

object FakerRegistry {

  implicit val system = ActorSystem("HttpClient")
  implicit val dispatcher = system.dispatcher

  sealed trait Command
  final case class GetFakers(replyTo: ActorRef[Future[String]]) extends Command
  final case class ConvertToFakers(text: String, replyTo: ActorRef[Seq[Faker]]) extends Command

  def logger = LoggerFactory.getLogger(this.getClass)

  def apply(): Behavior[Command] = registry()

  val http = Http()
  val scheme = "http"
  val host = "localhost"
  val port = 7000
  val path = Uri.Path("/api/faker")

  def getMockup(): Future[String] = {
    val uri = Uri()
      .withScheme(scheme)
      .withHost(host)
      .withPort(port)
      .withPath(path.+("/mockup"))

    logger.info("{}", uri)
    val responseFuture : Future[HttpResponse] = http.singleRequest(HttpRequest(HttpMethods.GET, uri))
    responseFuture
      .flatMap(_.entity.toStrict(60.seconds)
        .map(res => {
          val str: String = res.data.utf8String
          str
        })
//        .map(str => convertToFaker(str.data.utf8String))
//        .map(_ => Unmarshal(_).to[Fakers])
      )
  }

  def convertToFakers(text: String): Seq[Faker] = {
    logger.debug("{}", text)
    val jsonObj = text.parseJson.convertTo[JsObject].fields("data").convertTo[JsArray].elements
    jsonObj.map(a => {
      val ff = a.asJsObject
      Faker(
        id = ff.fields("id").convertTo[Long],
        title = ff.fields("title").convertTo[String],
        client = ff.fields("client").convertTo[String],
        area = ff.fields("area").convertTo[String],
        country = ff.fields("country").convertTo[String],
        contact = ff.fields("contact").convertTo[String],
        assignee = ff.fields("assignee").convertTo[String],
        progress = ff.fields("progress").convertTo[Float],
        startTimestamp = ff.fields("startTimestamp").convertTo[Long],
        endTimestamp = ff.fields("endTimestamp").convertTo[Long],
        budget = ff.fields("budget").convertTo[Float],
        transaction = ff.fields("transaction").convertTo[String],
        account = ff.fields("account").convertTo[String],
        version = ff.fields("version").convertTo[String],
        available = ff.fields("available").convertTo[Boolean]
      )
    })
  }

  private def registry(): Behavior[Command] =
    Behaviors.receiveMessage {
      case GetFakers(replyTo) =>
        replyTo ! getMockup()
        Behaviors.same
      case ConvertToFakers(text, replyTo) =>
        replyTo ! convertToFakers(text)
        Behaviors.same
    }

}
