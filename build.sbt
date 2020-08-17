name := "testcontainers-scala"

version := "0.1"

scalaVersion := "2.13.3"


libraryDependencies ++= Seq(
  "software.amazon.awssdk" % "s3" % "2.13.75",
  "org.scala-lang.modules" %% "scala-java8-compat" % "0.9.1",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "org.scalatest" %% "scalatest" % "3.2.1",
  "com.dimafeng" %% "testcontainers-scala-core" % "0.38.1"
)