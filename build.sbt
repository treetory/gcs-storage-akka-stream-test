name := "gcs-storage-akka-stream-test"

version := "0.1"

scalaVersion := "2.12.13"

val akkaHttpVersion = "10.1.5"
val slickVersion = "3.3.3"

libraryDependencies ++= {
  val akkaVersion = "2.5.31"
  Seq(
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "org.scalatest" %% "scalatest" % "3.2.0" % Test,
    "com.google.cloud" % "google-cloud-storage" % "1.113.0"
  )
}
//idePackagePrefix := Some("com.treetory")
