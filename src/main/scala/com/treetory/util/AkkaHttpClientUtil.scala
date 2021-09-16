package com.treetory.util

import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpMethods, HttpRequest, HttpResponse, Uri}
import com.treetory.config.AkkaHttpClientConfig
import org.slf4j.LoggerFactory

import scala.concurrent.Future

class AkkaHttpClientUtil(implicit sendHttpRequest: HttpRequest => Future[HttpResponse] = Http(
  /*ActorSystem(Behaviors.empty, "SingleRequest")*/
  /*HttpClientConfig.client*/
  AkkaHttpClientConfig.client)
  .singleRequest(_
    /*,HttpClientConfig.context*/
    ,AkkaHttpClientConfig.context
  )) {

  def logger = LoggerFactory.getLogger(this.getClass)

  def getSignedURL(fileName: String): Future[HttpResponse] = {
    val uri: Uri = s"http://localhost:3000/api/v1/gcs?fileName=${fileName}"
    logger.info("{}", uri)
    HttpRequest(HttpMethods.GET, uri)
  }

}

//object AkkaHttpClientUtil {
//  def main(args: Array[String]): Unit = {
//    implicit val system = ActorSystem(Behaviors.empty,"SingleRequest")
//    implicit val executionContext = system.executionContext
//
//    val responseFuture: Future[HttpResponse] = Http().singleRequest(
//      HttpRequest(
//        uri = s"http://localhost:3000/api/v1/gcs?fileName=ANALYSIS_FILES/1357_HE18-035-0758028-LHT_final.bam.tdf"
//      )
//    )
//
//    responseFuture
//      .onComplete {
//        case Success(res) => println(res)
//        case Failure(_)   => sys.error("something wrong")
//      }
//  }
//}