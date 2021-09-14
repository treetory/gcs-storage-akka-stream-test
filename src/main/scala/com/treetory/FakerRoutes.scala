package com.treetory

import akka.actor.typed.scaladsl.AskPattern.{Askable, schedulerFromActorSystem}
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.{complete, concat, get, onSuccess, pathEnd, pathPrefix}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.util.Timeout
import com.treetory.FakerRegistry.{ConvertToFakers, GetFakers}

import scala.concurrent.Future

class FakerRoutes(fakerRegistry: ActorRef[FakerRegistry.Command])(implicit val system: ActorSystem[_]) {

  //#user-routes-class
  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
  import JsonFormats._
  //#import-json-formats

  // If ask takes more time than this to complete the request is failed
  private implicit val timeout = Timeout.create(system.settings.config.getDuration("my-app.routes.ask-timeout"))

  def getFakers(): Future[Future[String]] = fakerRegistry.ask(GetFakers)
  def convertToFakers(text: String): Future[Seq[Faker]] = fakerRegistry.ask(ConvertToFakers(text, _))

  val fakerRoutes: Route =
    pathPrefix("faker") {
        //#users-get-delete
        pathEnd {
          concat(
            get {
              onSuccess(getFakers()) { res =>
                onSuccess(res) { response =>
//                  val data = Unmarshal(response).to[Fakers]
//                  complete(data)
                  onSuccess(convertToFakers(response)) { fakers =>
                    complete(StatusCodes.OK, Fakers(fakers))
                  }
                }
              }
            })
        }
      //#users-get-delete
    }
  //#all-routes

}
