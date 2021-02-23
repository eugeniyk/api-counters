name := "api-counters-root"

val commonSettings = Seq(
  scalaVersion := "2.13.5",
  crossScalaVersions := Seq(scalaVersion.value, "2.12.12", "2.11.12"),
  organization := "com.github.eugeniyk"
)

lazy val publishSettings = Seq(
  publishMavenStyle := true,
  homepage := Some(url("https://github.com/eugeniyk/api-counters")),
  publishArtifact in Test := false,
  pomIncludeRepository := { _ => false },
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
    else Some("releases" at nexus + "service/local/staging/deploy/maven2")
  },
  scmInfo := Some(
    ScmInfo(
      url("https://github.com/eugeniyk/api-counters"),
      "scm:git:git@github.com:eugeniyk/api-counters.git"
    )
  ),
  developers := List(Developer("eugeniyk",
    "Eugene Kalashnikov",
    "keatrance@gmail.com",
    url("https://github.com/username"))),

  licenses := Seq("GPLv3" -> url("https://opensource.org/licenses/GPL-3.0"))
)

lazy val noPublishSettings = Seq(
  publish := {},
  publishLocal := {},
  publishArtifact := false,
  publishTo := Some(Resolver.file("Unused transient repository", file("target/unusedrepo"))) // http://stackoverflow.com/a/18522706
)

lazy val apiCounters = project.in(file("api-counters"))
  .settings(commonSettings ++ publishSettings)

lazy val performance = project
  .in(file("performance"))
  .settings(commonSettings ++ noPublishSettings)
  .dependsOn(apiCounters)

lazy val root = project.in(file("."))
  .settings(commonSettings ++ noPublishSettings)
  .aggregate(apiCounters, performance)