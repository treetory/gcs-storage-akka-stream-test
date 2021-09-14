package com.treetory

import com.treetory.UserRegistry.ActionPerformed
import spray.json.RootJsonFormat

import java.net.URL

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

}
//#json-formats
