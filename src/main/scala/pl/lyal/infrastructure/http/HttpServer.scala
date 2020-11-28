package pl.lyal.infrastructure.http

import cats.effect.{ConcurrentEffect, Resource, Timer}
import org.http4s.HttpApp
import org.http4s.server.Server
import org.http4s.server.blaze.BlazeServerBuilder
import pl.lyal.config.HttpConfig

import scala.concurrent.ExecutionContext

object HttpServer {
  def start[F[_]: ConcurrentEffect: Timer](ec: ExecutionContext)(httpConfig: HttpConfig)(
      httpApp: HttpApp[F]): Resource[F, Server] =
    BlazeServerBuilder[F](ec)
      .bindHttp(httpConfig.port, httpConfig.host)
      .withHttpApp(httpApp)
      .resource

}
