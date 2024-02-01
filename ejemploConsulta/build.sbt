ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.1"

lazy val root = (project in file("."))
  .settings(
    name := "ejemploConsulta",
    libraryDependencies ++= Seq(
      "org.tototoshi" %% "slick" % "3.3.3",
      "org.tototoshi" %% "slick-hikaricp" % "3.3.3",
      "org.xerial" % "sqlite-jdbc" % "3.36.0"
    )
  )



