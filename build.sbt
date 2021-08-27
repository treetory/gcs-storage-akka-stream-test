
name := "gcs-storage-akka-stream-test"

version := "0.1"

scalaVersion := "2.12.13"

//val akkaHttpVersion = "10.1.5"
val akkaHttpVersion = "10.2.6"
val slickVersion = "3.3.3"

libraryDependencies ++= {
  //val akkaVersion = "2.5.31"
  val akkaVersion = "2.6.16"
  Seq(
    "com.typesafe.akka" %% "akka-http"                % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-spray-json"     % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-actor-typed"         % akkaVersion,
    "com.typesafe.akka" %% "akka-stream"              % akkaVersion,
    "com.typesafe.akka" %% "akka-actor"               % akkaVersion,
    "ch.qos.logback"    % "logback-classic"           % "1.2.3",
    "com.typesafe.akka" %% "akka-http-testkit"        % akkaHttpVersion % Test,
    "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion     % Test,
    "org.scalatest"     %% "scalatest" % "3.2.0"      % Test,
    "com.google.cloud"  % "google-cloud-storage"      % "1.113.0"
  )
}
//idePackagePrefix := Some("com.treetory")
assembly / assemblyMergeStrategy := {
  case PathList("module-info.class") => MergeStrategy.discard
  case x if x.endsWith("/module-info.class") => MergeStrategy.discard
  case x =>
    val oldStrategy = (assembly / assemblyMergeStrategy).value
    oldStrategy(x)
}