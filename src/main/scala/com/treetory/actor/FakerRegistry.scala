package com.treetory.actor

import akka.actor.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpMethods, HttpRequest, HttpResponse, Uri}
import com.treetory.util.ExcelExporterUtil.{`export`, getExcel}
import org.slf4j.LoggerFactory
import spray.json.DefaultJsonProtocol._
import spray.json._

import java.io.File
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
  final case class GetFakers(count:Int, replyTo: ActorRef[Future[String]]) extends Command
  final case class ConvertToFakers(text: String, replyTo: ActorRef[Seq[Faker]]) extends Command
  final case class CreateExcel(fakers: Seq[Faker], replyTo: ActorRef[Unit]) extends Command
  final case class GetExcel(replyTo: ActorRef[File]) extends Command

  def logger = LoggerFactory.getLogger(this.getClass)

  def apply(): Behavior[Command] = registry()

  val http = Http()
  val scheme = "http"
  val host = "localhost"
  val port = 7000
  val path = Uri.Path("/api/faker")

  def getMockup(count: Int): Future[String] = {
    val uri = Uri()
      .withScheme(scheme)
      .withHost(host)
      .withPort(port)
      .withPath(path.+("/mockup"))
      .withQuery(Uri.Query(s"count=${count}"))

    logger.info("{}", uri)
    val responseFuture : Future[HttpResponse] = http.singleRequest(HttpRequest(HttpMethods.GET, uri))
    responseFuture
      .flatMap(_
        .entity
        .withoutSizeLimit()
        .toStrict(60.seconds, 100000000)
        .map(res => {
          val str: String = res.data.utf8String
          str
        })
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
      case GetFakers(count: Int, replyTo) =>
        replyTo ! getMockup(count)
        Behaviors.same
      case ConvertToFakers(text: String, replyTo) =>
        replyTo ! convertToFakers(text)
        Behaviors.same
      case CreateExcel(faker: Seq[Faker], replyTo) =>
        replyTo ! export(faker)
        Behaviors.same
      case GetExcel(replyTo) =>
        replyTo ! getExcel()
        Behaviors.same
    }

}
