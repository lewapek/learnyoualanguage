package pl.lyal

import cats.effect._
import io.getquill._
import org.http4s.implicits._
import org.http4s.server.{Router, Server}
import pl.lyal.config.{Config, RootConfig}
import pl.lyal.domain.service.{FullWordService, LangService}
import pl.lyal.infrastructure.http.{HttpServer, LangEndpoints, StatsEndpoints, WordEndpoints}
import pl.lyal.infrastructure.repository.{
  DbConnection,
  LangRepoInterpreter,
  LearningStatsRepoInterpreter,
  WordRepoInterpreter
}

import scala.concurrent.ExecutionContext.Implicits

object Main extends IOApp {
  def initServer[F[_]: ContextShift: ConcurrentEffect: Timer]: Resource[F, Server] =
    for {
      config <- Resource.liftF[F, RootConfig](Config.decode[F])
      context     = new PostgresJdbcContext[SnakeCase](SnakeCase, "app.db")
      wordRepo    = WordRepoInterpreter.createWith[F](context)
      statsRepo   = LearningStatsRepoInterpreter[F](context)
      langRepo    = LangRepoInterpreter.createWith[F](context)
      wordService = FullWordService[F](wordRepo, statsRepo)
      langService = LangService[F](langRepo)
      _ <- Resource.liftF(DbConnection.initializeMigration(config.db))
      httpApp = Router(
        "/lang"  -> LangEndpoints[F](langService),
        "/word"  -> WordEndpoints[F](wordService),
        "/stats" -> StatsEndpoints[F](wordService)
      ).orNotFound
      server <- HttpServer.start[F](Implicits.global)(config.http)(httpApp)
    } yield server

  override def run(args: List[String]): IO[ExitCode] =
    initServer.use(_ => IO.never).as(ExitCode.Success)
}
