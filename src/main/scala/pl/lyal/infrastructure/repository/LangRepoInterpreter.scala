package pl.lyal.infrastructure.repository

import cats.Applicative
import cats.effect.{Effect => CatsEffect}
import cats.syntax.applicative._
import io.getquill.{PostgresJdbcContext, _}
import pl.lyal.domain.Lang
import pl.lyal.domain.repo.LangRepoAlgebra

class LangSql(val context: PostgresJdbcContext[SnakeCase]) {
  import context._

  def create(lang: Lang) =
    run {
      quote {
        query[Lang].insert(lift(lang)).returningGenerated(_.id)
      }
    }

  def update(lang: Lang) =
    run {
      quote {
        query[Lang].update(lift(lang))
      }
    }

  def get(id: Int) =
    run {
      quote {
        query[Lang].filter(_.id.contains(lift(id)))
      }
    }

  def getAll =
    run {
      quote(query[Lang])
    }

  def delete(id: Int) =
    run {
      quote {
        query[Lang].filter(_.id.contains(lift(id))).delete
      }
    }

}

class LangRepoInterpreter[F[_]: Applicative](val context: PostgresJdbcContext[SnakeCase]) extends LangRepoAlgebra[F] {
  val sql = new LangSql(context)

  override def create(lang: Lang): F[Option[Int]] = sql.create(lang).pure

  override def update(lang: Lang): F[Long] = sql.update(lang).pure

  override def get(id: Int): F[Option[Lang]] = sql.get(id).headOption.pure

  override def getAll: F[List[Lang]] = sql.getAll.pure

  override def delete(id: Int): F[Long] = sql.delete(id).pure
}

object LangRepoInterpreter {
  def createWith[F[_]: Applicative](context: PostgresJdbcContext[SnakeCase]): LangRepoInterpreter[F] =
    new LangRepoInterpreter[F](context)
}
