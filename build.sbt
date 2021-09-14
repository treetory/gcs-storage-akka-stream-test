
val v2_12 = "2.12.13"
val v2_13 = "2.13.1"

lazy val root = (project in file("."))
  .settings(
    Seq(
      crossScalaVersions := List(v2_13, v2_12),
      organization := "com.treetory",
      name := "gcs-storage-akka-stream-test",
      scalafmtOnCompile := true
    ): _*
  )
  .settings(Defaults.itSettings: _*)
  .settings(
    scalacOptions ++= Seq(
      "-deprecation",
      "-encoding",
      "UTF-8",
      "-explaintypes",
      "-Yrangepos",
      "-feature",
      "-language:higherKinds",
      "-language:existentials",
      "-unchecked",
      "-Xlint:_,-type-parameter-shadow",
      "-Ywarn-dead-code",
      "-Ywarn-numeric-widen",
      "-Ywarn-value-discard",
      "-Ywarn-extra-implicit",
      "-Ywarn-unused:imports",
      "-opt-warnings",
      "-target:jvm-1.8"
    ),
    scalacOptions ++= versionSpecificScalacOptions(scalaVersion.value),
    javacOptions ++= Seq("-source", "1.8", "-target", "1.8"),
    libraryDependencies ++= {
      val akkaHttpVersion = "10.2.6"
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
        "org.scalatest"     %% "scalatest"                % "3.2.0"      % Test,
        "com.google.cloud"  % "google-cloud-storage"      % "1.113.0",
        "org.scalaj"        %% "scalaj-http"              % "2.4.2",
        "org.apache.poi"    % "poi-ooxml"                 % "3.17"
      )
    }
  )
  .configs(IntegrationTest)

lazy val IntegrationTest = config("it") extend Test

inThisBuild(
  List(
    licenses := Seq("MIT" -> url("http://opensource.org/licenses/MIT")),
    developers := List(
      Developer("treetory", "InHwan Chun", "treetory@gmail.com", url("https://github.com/treetory"))
    ),
    // These are the sbt-release-early settings to configure
    pgpPublicRing := file("./ci/local.pubring.asc"),
    pgpSecretRing := file("./ci/local.secring.asc"),
    releaseEarlyWith := SonatypePublisher
  )
)

def versionSpecificScalacOptions(scalaV: String) =
//if (scalaV == v2_12)
  Seq(
    "-Xfuture",
    "-Xsource:2.13",
    "-Yno-adapted-args",
    "-Ywarn-inaccessible",
    "-Ywarn-infer-any",
    "-Ywarn-nullary-override",
    "-Ywarn-nullary-unit",
    "-Ypartial-unification"
  )
//  else
//    Seq(
//      "-Ymacro-annotations"
//    )

assembly / assemblyMergeStrategy := {
  case PathList("reference.conf") => MergeStrategy.concat
  case PathList("module-info.class") => MergeStrategy.discard
  case x if x.endsWith("/module-info.class") => MergeStrategy.discard
  case x =>
    val oldStrategy = (assembly / assemblyMergeStrategy).value
    oldStrategy(x)
}