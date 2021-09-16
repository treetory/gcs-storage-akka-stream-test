package com.treetory.route

import akka.actor.typed.scaladsl.AskPattern.{Askable, schedulerFromActorSystem}
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.{_symbol2NR, complete, concat, get, onSuccess, parameter, pathEnd, pathPrefix}
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import com.treetory.actor.FakerRegistry.{ConvertToFakers, CreateExcel, GetFakers}
import com.treetory.JsonFormats
import com.treetory.actor.{Faker, FakerRegistry, Fakers}

import scala.concurrent.Future

class FakerRoutes(fakerRegistry: ActorRef[FakerRegistry.Command])(implicit val system: ActorSystem[_]) {

  //#user-routes-class
  import JsonFormats._
  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
  //#import-json-formats

  // If ask takes more time than this to complete the request is failed
  private implicit val timeout = Timeout.create(system.settings.config.getDuration("my-app.routes.ask-timeout"))

  def getFakers(count: Int): Future[Future[String]] = fakerRegistry.ask(GetFakers(count, _))
  def convertToFakers(text: String): Future[Seq[Faker]] = fakerRegistry.ask(ConvertToFakers(text, _))
  def export(fakers: Seq[Faker]): Future[Unit] = fakerRegistry.ask(CreateExcel(fakers, _))

  val fakerRoutes: Route =
    pathPrefix("faker") {
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
      }
    }
  //#all-routes
}
