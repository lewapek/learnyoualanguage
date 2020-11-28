package pl.lyal.infrastructure.http

import cats.effect.Effect
import cats.implicits._
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s.{EntityDecoder, HttpRoutes}
import pl.lyal.domain.Lang
import pl.lyal.domain.service.LangService

class LangEndpoints[F[_]: Effect](service: LangService[F]) extends Http4sDsl[F] with HttpUtils[F] {
  implicit val langDecoder: EntityDecoder[F, Lang] = jsonOf[F, Lang]

  private def create: HttpRoutes[F] =
    HttpRoutes.of[F] {
      case request @ POST -> Root =>
        for {
          lang    <- request.as[Lang]
          created <- service.create(lang)
          result  <- Ok(created.asJson)
        } yield result
    }

  private def update: HttpRoutes[F] =
    HttpRoutes.of[F] {
      case request @ PUT -> Root / IntVar(id) =>
        for {
          lang    <- request.as[Lang]
          updated <- service.update(lang.copy(id = id.some))
          result  <- Ok(updated.asJson)
        } yield result
    }

  private def get: HttpRoutes[F] =
    HttpRoutes.of[F] {
      case GET -> Root / IntVar(id) =>
        for {
          lang   <- service.get(id)
          result <- jsonOkOrNotFound(lang)
        } yield result
    }

  private def getAll: HttpRoutes[F] =
    HttpRoutes.of[F] {
      case GET -> Root =>
        for {
          langs  <- service.getAll
          result <- Ok(langs.asJson)
        } yield result
    }

  private def delete: HttpRoutes[F] =
    HttpRoutes.of[F] {
      case DELETE -> Root / IntVar(id) =>
        for {
          deleted <- service.delete(id)
          result  <- Ok(deleted.asJson)
        } yield result
    }

  def endpoints: HttpRoutes[F] =
    create <+> update <+> get <+> getAll <+> delete
}

object LangEndpoints {
  def apply[F[_]: Effect](service: LangService[F]): HttpRoutes[F] =
    new LangEndpoints[F](service).endpoints
}
