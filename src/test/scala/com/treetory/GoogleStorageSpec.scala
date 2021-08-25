package com.treetory

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class GoogleStorageSpec extends AnyWordSpec with Matchers with ScalaFutures{

  val googleStorage = GoogleStorage

  "Signed url for file name and duration" when {

    "file does not exist" should {
      "return none" in {
        GoogleStorage.signedUrlFor("non_existing_file") shouldBe None
      }
    }

    "file exists" should {
      "return properly signed url" in {
        val testFileName = "a_test_content"
        val testFile = getClass.getResourceAsStream("/" + testFileName)
        googleStorage
          .storage
          .get(googleStorage.config.google.storage.project.bucket)
          .create(testFileName, testFile)

        val Some(url) = googleStorage.signedUrlFor(testFileName)
        url.getPath should endWith(testFileName)
        url.getQuery should include("Expires")
        url.getQuery should include("Signature")
        println(url.toURI.toString)
      }
    }

  }

}
