package pl.lyal.infrastructure.repository

import cats.effect.Sync
import cats.syntax.functor._
import io.getquill.{PostgresJdbcContext, SnakeCase}
import org.flywaydb.core.Flyway
import pl.lyal.config.DbConfig

object DbConnection {
  def context: PostgresJdbcContext[SnakeCase.type] =
    new PostgresJdbcContext(SnakeCase, "app.db")

  def initializeMigration[F[_]](dbConfig: DbConfig)(implicit S: Sync[F]): F[Unit] =
    S.delay {
        Flyway
          .configure()
          .dataSource(dbConfig.jdbcUrl, dbConfig.user, dbConfig.password)
          .load()
          .migrate()
      }
      .as(())
}
