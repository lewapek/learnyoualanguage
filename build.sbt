name := "LearnYouALanguage"

version := "0.0.1"

scalaVersion := "2.13.3"

val CatsVersion        = "2.2.0"
val CirceVersion       = "0.13.0"
val CirceConfigVersion = "0.8.0"
val Http4sVersion      = "1.0.0-M6"
val DoobieVersion      = "0.9.2"
val H2Version          = "1.4.200"
val LogbackVersion     = "1.2.3"
val FlywayVersion      = "7.2.0"

libraryDependencies ++= Seq(
  "org.typelevel"  %% "cats-core"           % CatsVersion,
  "org.typelevel"  %% "cats-effect"         % CatsVersion,
  "io.circe"       %% "circe-generic"       % CirceVersion,
  "io.circe"       %% "circe-config"        % CirceConfigVersion,
  "org.http4s"     %% "http4s-blaze-server" % Http4sVersion,
  "org.http4s"     %% "http4s-dsl"          % Http4sVersion,
  "org.http4s"     %% "http4s-circe"        % Http4sVersion,
  "org.tpolecat"   %% "doobie-core"         % DoobieVersion,
  "org.tpolecat"   %% "doobie-postgres"     % DoobieVersion,
  "org.tpolecat"   %% "doobie-hikari"       % DoobieVersion,
  "com.h2database" % "h2"                   % H2Version,
  "org.flywaydb"   % "flyway-core"          % FlywayVersion,
  "ch.qos.logback" % "logback-classic"      % LogbackVersion,
  "org.postgresql" % "postgresql"           % "42.2.8",
  "io.getquill"    %% "quill-jdbc"          % "3.5.3"
)

addCompilerPlugin(("org.typelevel" %% "kind-projector" % "0.11.0").cross(CrossVersion.full))
addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1")

scalacOptions ++= Seq(
  "-Xfatal-warnings"
)
