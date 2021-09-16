package com.treetory.route

import akka.actor.typed.scaladsl.AskPattern.{Askable, schedulerFromActorSystem}
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.server.Directives.{_symbol2NR, complete, concat, get, onSuccess, parameter, pathEnd, pathPrefix}
import akka.http.scaladsl.server.Route
import akka.stream.scaladsl.FileIO
import akka.util.Timeout
import com.treetory.actor.FakerRegistry.{ConvertToFakers, CreateExcel, GetExcel, GetFakers}
import com.treetory.JsonFormats
import com.treetory.actor.{Faker, FakerRegistry, Fakers}

import java.io.File
import scala.concurrent.Future

class FakerRoutes(fakerRegistry: ActorRef[FakerRegistry.Command])(implicit val system: ActorSystem[_]) {

  //#user-routes-class
  import JsonFormats._
  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
  //#import-json-formats

  // If ask takes more time than this to complete the request is failed
  private implicit val timeout = Timeout.create(system.settings.config.getDuration("my-app.routes.ask-timeout"))
  private implicit val executionContext = system.executionContext

  def getFakers(count: Int): Future[Future[String]] = fakerRegistry.ask(GetFakers(count, _))
  def convertToFakers(text: String): Future[Seq[Faker]] = fakerRegistry.ask(ConvertToFakers(text, _))
  def export(fakers: Seq[Faker]): Future[Unit] = fakerRegistry.ask(CreateExcel(fakers, _))
  def getExcel(): Future[File] = fakerRegistry.ask(GetExcel(_))

  val fakerRoutes: Route =
    pathPrefix("faker") {
      concat(
        pathEnd {
          concat(
            get {
              parameter('count.as[Int]) { count =>
                onSuccess(getFakers(count)) { res =>
                  onSuccess(res) { response =>
                    //                  val data = Unmarshal(response).to[Fakers]
                    //                  complete(data)
                    onSuccess(convertToFakers(response)) { fakers =>
                      //                    complete(StatusCodes.OK, Fakers(fakers))
                      onSuccess(export(fakers)) {
                        complete(StatusCodes.OK, Fakers(fakers))
                      }
                    }
                  }
                }
              }
            }
          )
        },
        pathPrefix("excel") {
          concat(
            get {
              onSuccess(getExcel()) { file =>
                val fileSrc = FileIO.fromPath(file.toPath).watchTermination() { (mat, futDone) =>
                  futDone.onComplete { _ =>
                    file.delete()
                  }
                  mat
                }
                complete {
                    HttpEntity.Default(ContentTypes.`application/octet-stream`, file.length, fileSrc)
                }
              }
            }
          )
        }
      )

    }
  //#all-routes
}
