package com.treetory

import com.treetory.actor.UserRegistry.ActionPerformed
import com.treetory.actor.{Faker, Fakers, User, Users}
import com.treetory.util.{BlobInfo, BlobInfoList, PagedList}
import spray.json.RootJsonFormat

//#json-formats
import spray.json.DefaultJsonProtocol

object JsonFormats  {
  // import the default encoders for primitive types (Int, String, Lists etc)
  import DefaultJsonProtocol._

  implicit val userJsonFormat = jsonFormat3(User)
  implicit val usersJsonFormat = jsonFormat1(Users)

  implicit val actionPerformedJsonFormat = jsonFormat1(ActionPerformed)

  implicit val fakerJsonFormat: RootJsonFormat[Faker] = jsonFormat15(Faker)
  implicit val fakersJsonFormat: RootJsonFormat[Fakers] = jsonFormat1(Fakers)

  implicit val blobInfoJsonFormat: RootJsonFormat[BlobInfo] = jsonFormat5(BlobInfo)
  implicit val blobInfoListJsonFormat: RootJsonFormat[BlobInfoList] = jsonFormat1(BlobInfoList)
  implicit val pagedListJsonFormat: RootJsonFormat[PagedList] = jsonFormat2(PagedList)
}
//#json-formats
