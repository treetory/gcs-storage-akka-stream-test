package com.treetory

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.{Directives, Route}
import com.treetory.actor.{FakerRegistry, GcsRestClientRegistry, UserRegistry}
import com.treetory.route.{AkkaHttpClientTestRoute, FakerRoutes, GcsRestClientActorRoutes, GcsRestClientRoutes, GoogleStorageRoute, HttpClientTestRoute, UserRoutes}

import scala.util.{Failure, Success}

//#main-class
object QuickstartApp {

  //#start-http-server
  private def startHttpServer(routes: Route)(implicit system: ActorSystem[_]): Unit = {
    // Akka HTTP still needs a classic ActorSystem to start
    import system.executionContext

    val futureBinding = Http().newServerAt("localhost", 8080).bind(routes)
    futureBinding.onComplete {
      case Success(binding) =>
        val address = binding.localAddress
        system.log.info("Server online at http://{}:{}/", address.getHostString, address.getPort)
      case Failure(ex) =>
        system.log.error("Failed to bind HTTP endpoint, terminating system", ex)
        system.terminate()
    }
  }
  //#start-http-server
  def main(args: Array[String]): Unit = {
    //#server-bootstrapping
    val rootBehavior = Behaviors.setup[Nothing] { context =>
      val userRegistryActor = context.spawn(UserRegistry(), "UserRegistryActor")
      context.watch(userRegistryActor)

      val fakerRegistryActor = context.spawn(FakerRegistry(), "FakerRegistryActor")
      context.watch(fakerRegistryActor)

      val gcsRestClientActor2 = context.spawn(GcsRestClientRegistry(), "GcsRestClientActor2")
      context.watch(gcsRestClientActor2)

      val routes =
        Directives.concat(
          (new UserRoutes(userRegistryActor)(context.system)).userRoutes,
          (new FakerRoutes(fakerRegistryActor)(context.system)).fakerRoutes,
          (new GcsRestClientRoutes(gcsRestClientActor2)(context.system)).gcsRestClientActorRoutes2,
          GoogleStorageRoute.googleStorageRoutes,
          AkkaHttpClientTestRoute.akkaHttpClientRoutes,
          GcsRestClientActorRoutes.gcsRestClientActorRoutes,
          HttpClientTestRoute.httpClientTestRoute
        )

      startHttpServer(routes)(context.system)

      Behaviors.empty
    }
    val system = ActorSystem[Nothing](rootBehavior, "GoogleCloudStorageAkkaHttpServer")
    //#server-bootstrapping
  }
}
//#main-class
