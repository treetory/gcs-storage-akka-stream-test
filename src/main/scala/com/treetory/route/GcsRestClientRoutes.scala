package com.treetory.route

import akka.actor.typed.scaladsl.AskPattern.{Askable, schedulerFromActorSystem}
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.server.Directives.{_symbol2NR, complete, get, onSuccess, parameter, pathEnd, pathPrefix}
import akka.http.scaladsl.server.{Directives, Route}
import akka.stream.scaladsl.FileIO
import akka.util.Timeout
import com.treetory.JsonFormats
import com.treetory.actor.GcsRestClientRegistry
import com.treetory.actor.GcsRestClientRegistry.{Excel, Page}
import com.treetory.util.PagedList

import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

class GcsRestClientRoutes(gcsRestClientRegistry: ActorRef[GcsRestClientRegistry.Command])(implicit val system: ActorSystem[_]) {

  import JsonFormats._
  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
  //#import-json-formats

  // If ask takes more time than this to complete the request is failed
  private implicit val timeout = Timeout(5.minutes)
  private implicit val executionContext = system.executionContext

  def getPagedList(token: String): Future[PagedList] = gcsRestClientRegistry.ask(Page(token, _))

  val gcsRestClientActorRoutes2: Route =
    pathPrefix("gcs") {
      Directives.concat(
        pathPrefix("registry") {
          pathEnd {
            get {
              parameter('token.as[String]) { (token) =>
                onSuccess(gcsRestClientRegistry.ask(Page(token, _))) { pagedList =>
                  complete(StatusCodes.OK, pagedList)
                }
              }
            }
          }
        },
        pathPrefix("excel") {
          pathEnd {
            get {
              parameter('token.as[String]) { (token) =>
                onSuccess(gcsRestClientRegistry.ask(Excel(token, _))) { file =>
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
          }
        }
      )
    }

}
