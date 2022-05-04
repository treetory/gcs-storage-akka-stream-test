package com.treetory.route

import akka.actor.typed.scaladsl.AskPattern.{Askable, schedulerFromActorSystem}
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.server.Directives.{_symbol2NR, complete, concat, get, onSuccess, parameter, pathEnd, pathPrefix}
import akka.http.scaladsl.server.Route
import akka.stream.scaladsl.FileIO
import akka.util.Timeout
import com.treetory.actor.FakerRegistry.{ConvertToFakers, CreateExcel, GetExcel, GetFakers, GetPagedFakers, GetTotalFakers, getTotalFakersExcelFile}
import com.treetory.JsonFormats
import com.treetory.actor.{Faker, FakerRegistry, Fakers}

import java.io.File
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

class FakerRoutes(fakerRegistry: ActorRef[FakerRegistry.Command])(implicit val system: ActorSystem[_]) {

  //#user-routes-class
  import JsonFormats._
  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
  //#import-json-formats

  // If ask takes more time than this to complete the request is failed
//  private implicit val timeout = Timeout.create(system.settings.config.getDuration("my-app.routes.ask-timeout"))
  private implicit val timeout = Timeout(120.seconds)
  private implicit val executionContext = system.executionContext

  def getFakers(count: Int): Future[Future[Seq[Faker]]] = fakerRegistry.ask(GetFakers(count, _))
  def convertToFakers(text: String): Future[Seq[Faker]] = fakerRegistry.ask(ConvertToFakers(text, _))
  def export(fakers: Seq[Faker]): Future[Unit] = fakerRegistry.ask(CreateExcel(fakers, _))
  def getExcel(fileName: String): Future[File] = fakerRegistry.ask(GetExcel(fileName, _))
  def getPagedFakers(count: Int): Future[File] = fakerRegistry.ask(GetPagedFakers(count, _))
  def getTotalFakers(count: Int): Future[File] = fakerRegistry.ask(GetTotalFakers(count, _))

  val fakerRoutes: Route =
    pathPrefix("faker") {
      concat(
        pathEnd {
          concat(
            get {
              parameter('count.as[Int]) { count =>
                onSuccess(getFakers(count)) { res =>
                  onSuccess(res) { fakers =>
                    complete(StatusCodes.OK, Fakers(fakers))
                  }
                }
              }
            }
          )
        },
        pathPrefix("download") {
          concat(
            parameter('fileName.as[String]) { fileName =>
              get {
                onSuccess(getExcel(fileName)) { file =>
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
            }
          )
        },
        pathPrefix("paged"){
          pathEnd {
            concat(
              get {
                parameter('count.as[Int]) { count =>
                  onSuccess(getPagedFakers(count)) { file =>
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
              }
            )
          }
        },
        pathPrefix("total"){
          pathEnd {
            concat(
              get {
                parameter('count.as[Int]) { count =>
                  onSuccess(getTotalFakers(count)) { file =>
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
              }
            )
          }
        }
      )
    }
  //#all-routes
}
