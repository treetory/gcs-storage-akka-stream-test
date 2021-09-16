package com.treetory

import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.treetory.util.{AkkaHttpClientUtil, HttpClientUtil}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.slf4j.LoggerFactory

import scala.util.Success

class GoogleStorageSpec extends AnyWordSpec with Matchers with ScalatestRouteTest {

  def logger = LoggerFactory.getLogger(this.getClass)

  val googleStorage = GoogleStorage
  val httpClientUtil = new HttpClientUtil()
  val akkaHttpClientUtil = new AkkaHttpClientUtil()
//  "Signed url for file name and duration" when {
//
//    "file does not exist" should {
//      "return none" in {
//        GoogleStorage.signedUrlFor("non_existing_file") shouldBe None
//      }
//    }
//
//    "file exists" should {
//      "return properly signed url" in {
//        val testFileName = "a_test_content"
////        val testFileName = "ANALYSIS_FILES/1357_HE18-035-0758028-LHT_final.bam.tdf"
//        val testFile = getClass.getResourceAsStream("/" + testFileName)
//        googleStorage
//          .storage
//          .get(googleStorage.config.google.storage.project.bucket)
//          .create(testFileName, testFile)
//
//        val Some(url) = googleStorage.signedUrlFor(testFileName)
//        url.getPath should endWith(testFileName)
//        url.getQuery should include("Expires")
//        url.getQuery should include("Signature")
//        println(url.toURI.toString)
//
//        val flag = googleStorage.delete(testFileName)
//        flag shouldBe(true)
//      }
//    }
//
//  }

  "Signed url for file name and duration" when {
    "file exists" should {
            "return properly signed url" in {
              val testFileName = "ANALYSIS_FILES/1357_HE18-035-0758028-LHT_final.bam.tdf"
              val res = httpClientUtil.getSignedURL(testFileName)
//              val res = akkaHttpClientUtil.getSignedURL(testFileName)
//              res.onComplete {
//              }
//              res.onComplete {
//                case Success(response) => logger.info(response.headers.head.value())
//              }
            }
          }
  }

}
