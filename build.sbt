name := "api-counters-root"

val commonSettings = Seq(
  scalaVersion := "2.12.10",
  crossScalaVersions := Seq(scalaVersion.value, "2.11.12"),
  organization := "com.github.eugeniyk"
)

lazy val publishSettings = Seq(
  publishMavenStyle := true
)

lazy val noPublishSettings = Seq(
  publish := {},
  publishLocal := {},
  publishArtifact := false
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