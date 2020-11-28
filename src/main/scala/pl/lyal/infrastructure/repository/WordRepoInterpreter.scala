package pl.lyal.infrastructure.repository

import cats.Applicative
import cats.syntax.applicative._
import io.getquill.{PostgresJdbcContext, _}
import pl.lyal.Utils
import pl.lyal.domain.Word
import pl.lyal.domain.repo.WordRepoAlgebra

class WordSql(val context: PostgresJdbcContext[SnakeCase]) {
  import context._

  def create(word: Word) =
    run {
      quote {
        query[Word].insert(lift(word)).returningGenerated(_.id)
      }
    }

  def update(word: Word) = {
    import word._
    val now = Utils.now
    run {
      quote {
        query[Word].update(
          _.name             -> lift(name),
          _.sourceLanguageId -> lift(sourceLanguageId),
          _.targetLanguageId -> lift(targetLanguageId),
          _.targetNames      -> lift(targetNames),
          _.synonyms         -> lift(synonyms),
          _.usages           -> lift(usages),
          _.updated          -> lift(now)
        )
      }
    }
  }

  def get(id: Long) =
    run {
      quote {
        query[Word].filter(_.id.contains(lift(id)))
      }
    }

  def getByWordNameAndLangs(name: String, sourceLangId: Int, targetLangId: Int) =
    run {
      quote {
        query[Word].filter { w =>
          w.name == lift(name) && w.sourceLanguageId == lift(sourceLangId) && w.targetLanguageId == lift(targetLangId)
        }
      }
    }

  def delete(id: Long) =
    run {
      quote {
        query[Word].filter(_.id.contains(lift(id))).delete
      }
    }

}

class WordRepoInterpreter[F[_]: Applicative](val context: PostgresJdbcContext[SnakeCase]) extends WordRepoAlgebra[F] {
  val sql = new WordSql(context)

  override def create(word: Word): F[Option[Long]] = sql.create(word).pure

  override def update(word: Word): F[Long] = sql.update(word).pure

  override def get(id: Long): F[Option[Word]] = sql.get(id).headOption.pure

  override def getByWordNameAndLangs(name: String, sourceLangId: Int, targetLangId: Int): F[List[Word]] =
    sql.getByWordNameAndLangs(name, sourceLangId, targetLangId).pure

  override def delete(id: Long): F[Long] = sql.delete(id).pure
}

object WordRepoInterpreter {
  def createWith[F[_]: Applicative](context: PostgresJdbcContext[SnakeCase]): WordRepoInterpreter[F] =
    new WordRepoInterpreter[F](context)
}
