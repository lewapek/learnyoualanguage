name := "LearnYouALanguage"

version := "0.0.1"

scalaVersion := "2.13.3"

val CatsVersion        = "2.2.0"
val CirceVersion       = "0.13.0"
val CirceConfigVersion = "0.8.0"
val Http4sVersion      = "1.0.0-M6"

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core"           % CatsVersion,
  "org.typelevel" %% "cats-effect"         % CatsVersion,
  "io.circe"      %% "circe-generic"       % CirceVersion,
  "io.circe"      %% "circe-config"        % CirceConfigVersion,
  "org.http4s"    %% "http4s-blaze-server" % Http4sVersion
)

addCompilerPlugin(("org.typelevel" %% "kind-projector" % "0.11.0").cross(CrossVersion.full))
addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1")
