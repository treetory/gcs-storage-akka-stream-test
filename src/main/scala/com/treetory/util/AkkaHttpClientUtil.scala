package com.treetory.util

import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding.Post
import akka.http.scaladsl.model.{ContentTypes, ErrorInfo, HttpEntity, HttpHeader, HttpMethods, HttpRequest, HttpResponse, IllegalUriException, Uri}
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
//    val uri: Uri = s"http://localhost:3000/api/v1/gcs?fileName=${fileName}"
//    logger.info("{}", uri)
    val uri: Uri = s"http://localhost:3000/example/${fileName}"
    HttpRequest(HttpMethods.GET, uri)
  }

  def getJsonFile(fileName: String): Future[HttpResponse] = {
    val uri: Uri = s"http://localhost:3000/example/${fileName}"
    HttpRequest(HttpMethods.GET, uri)
  }

  def sendReportJson(reportJson: String): Future[HttpResponse] = {
    val uri: Uri = reportJson match {
      case x if x.contains("\"panelCode\": \"HEMEaccuTest_DNA_Pipeline\"") => s"http://localhost:3000/report/heme-dna"
      case x if x.contains("\"panelCode\": \"HEMEaccuTest_RNA_Pipeline\"") => s"http://localhost:3000/report/heme-rna"
      case x if x.contains("\"panelCode\": \"LymphomaPanel_DNA_CNUHH_Pipeline\"") => s"http://localhost:3000/report/lymphoma"
      case x if x.contains("\"panelCode\": \"ONCOaccuPanel_AMC_DNA_Pipeline\"") => s"http://localhost:3000/report/onco"
      case _ => throw new Exception("Can't find the uri to send the report data.")
    }
    logger.info("{}", uri)
    HttpRequest(HttpMethods.POST, uri, List(), HttpEntity(ContentTypes.`application/json`, reportJson))
  }

  def containPipelineCode(s: String, pipelineCode: String): Boolean = {
    s.contains(pipelineCode)
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