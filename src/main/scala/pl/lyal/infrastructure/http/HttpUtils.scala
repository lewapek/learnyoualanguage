package pl.lyal.infrastructure.http

import java.text.SimpleDateFormat
import java.util.Date

import cats.Applicative
import io.circe.generic.auto._
import io.circe.syntax._
import io.circe.{Encoder, Json}
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s.{EntityEncoder, Response}
import pl.lyal.domain.{FullWord, LearningStats, Word}

trait HttpUtils[F[_]] {
  self: Http4sDsl[F] =>

  implicit val wordEncoder: EntityEncoder[F, Word]                   = jsonEncoderOf[F, Word]
  implicit val learningStatsEncoder: EntityEncoder[F, LearningStats] = jsonEncoderOf[F, LearningStats]
  implicit val fullWordEncoder: EntityEncoder[F, FullWord]           = jsonEncoderOf[F, FullWord]
  implicit val dateEncoder: Encoder[Date] = new Encoder[Date] {
    val format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    override def apply(date: Date): Json = Json.fromString(format.format(date))
  }

  def jsonOkOrNotFound[A: Encoder](opt: Option[A])(implicit A: Applicative[F]): F[Response[F]] =
    opt.map(a => Ok(a.asJson)).getOrElse(NotFound("Resource not found"))
}
