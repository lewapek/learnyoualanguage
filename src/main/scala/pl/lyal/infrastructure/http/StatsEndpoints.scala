package pl.lyal.infrastructure.http

import cats.data.Validated.{Invalid, Valid}
import cats.effect.Effect
import cats.implicits._
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s.dsl.impl.ValidatingQueryParamDecoderMatcher
import org.http4s.{HttpRoutes, QueryParamDecoder}
import pl.lyal.domain.service.FullWordService
import pl.lyal.domain.service.FullWordService.{QueryType, SearchCriteria}

class StatsEndpoints[F[_]: Effect](service: FullWordService[F]) extends Http4sDsl[F] with HttpUtils[F] {
  import StatsEndpoints._

  private def hit: HttpRoutes[F] =
    HttpRoutes.of[F] {
      case PUT -> Root / "hit" / LongVar(id) =>
        for {
          newStats <- service.hit(id)
          result   <- Ok(newStats.asJson)
        } yield result
    }

  private def miss: HttpRoutes[F] =
    HttpRoutes.of[F] {
      case PUT -> Root / "miss" / LongVar(id) =>
        for {
          newStats <- service.miss(id)
          result   <- Ok(newStats.asJson)
        } yield result
    }

  private def search: HttpRoutes[F] =
    HttpRoutes.of[F] {
      case GET -> Root / "search" :? QuantityMatcher(n) +& QueryTypeMatcher(query) =>
        val criteria = (n, query).mapN(SearchCriteria.apply)
        criteria match {
          case Valid(c) =>
            for {
              fullWords <- service.searchBy(c)
              result    <- Ok(fullWords.asJson)
            } yield result
          case Invalid(_) => BadRequest("Invalid search criteria")
        }
    }

  def endpoints: HttpRoutes[F] =
    hit <+> miss <+> search
}

object StatsEndpoints {

  implicit val queryTypeDecoder: QueryParamDecoder[QueryType] =
    QueryParamDecoder[String].map {
      case "random"             => QueryType.Random
      case "lowestHitsRatio"    => QueryType.LowestHitsRatio
      case "highestLastMisses"  => QueryType.HighestLastMisses
      case "highestMaxMisses"   => QueryType.HighestMaxMisses
      case "highestMissesTotal" => QueryType.HighestMissesTotal
      case "lastMissTime"       => QueryType.LastMissTime
      case other if other.startsWith("byName:") =>
        val Array(_, name, sourceLang, targetLang) = other.split(':').map(_.trim)
        QueryType.WordLang(name, sourceLang.toInt, targetLang.toInt)
    }

  object QuantityMatcher  extends ValidatingQueryParamDecoderMatcher[Int]("n")
  object QueryTypeMatcher extends ValidatingQueryParamDecoderMatcher[QueryType]("q")

  def apply[F[_]: Effect](service: FullWordService[F]): HttpRoutes[F] =
    new StatsEndpoints[F](service).endpoints
}
