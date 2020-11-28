package pl.lyal.infrastructure.http

import cats.effect.Effect
import cats.implicits._
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s.{EntityDecoder, HttpRoutes}
import pl.lyal.domain.service.FullWordService
import pl.lyal.infrastructure.http.model.HttpWord

class WordEndpoints[F[_]: Effect](service: FullWordService[F]) extends Http4sDsl[F] with HttpUtils[F] {
  implicit val httpWordDecoder: EntityDecoder[F, HttpWord] = jsonOf[F, HttpWord]

  private def create: HttpRoutes[F] =
    HttpRoutes.of[F] {
      case request @ POST -> Root =>
        for {
          httpWord <- request.as[HttpWord]
          created  <- service.add(httpWord.toWord)
          result   <- Ok(created.asJson)
        } yield result
    }

  private def update: HttpRoutes[F] =
    HttpRoutes.of[F] {
      case request @ PUT -> Root / LongVar(id) =>
        for {
          httpWord <- request.as[HttpWord]
          updated  <- service.update(httpWord.toWord.copy(id = id.some))
          result   <- Ok(updated.asJson)
        } yield result
    }

  private def get: HttpRoutes[F] =
    HttpRoutes.of[F] {
      case GET -> Root / LongVar(id) =>
        for {
          fullWord <- service.get(id)
          result   <- jsonOkOrNotFound(fullWord)
        } yield result
    }

  private def delete: HttpRoutes[F] =
    HttpRoutes.of[F] {
      case DELETE -> Root / LongVar(id) =>
        for {
          deleted <- service.delete(id)
          result  <- Ok(deleted.asJson)
        } yield result
    }

  def endpoints: HttpRoutes[F] =
    create <+> update <+> get <+> delete
}

object WordEndpoints {
  def apply[F[_]: Effect](service: FullWordService[F]): HttpRoutes[F] =
    new WordEndpoints[F](service).endpoints
}
