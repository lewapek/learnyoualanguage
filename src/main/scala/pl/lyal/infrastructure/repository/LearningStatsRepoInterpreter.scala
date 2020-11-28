package pl.lyal.infrastructure.repository

import cats.Applicative
import io.getquill.{Ord, PostgresJdbcContext, SnakeCase}
import pl.lyal.domain.{Lang, LearningStats, Word}
import pl.lyal.domain.repo.{LangRepoAlgebra, LearningStatsRepoAlgebra}
import cats.syntax.applicative._

class LearningStatsSql(val context: PostgresJdbcContext[SnakeCase]) {
  import context._

  def create(s: LearningStats) =
    run {
      quote {
        query[LearningStats].insert(lift(s)).returningGenerated(_.id)
      }
    }

  def update(s: LearningStats) =
    run {
      quote {
        query[LearningStats].update(lift(s))
      }
    }

  def get(id: Long) =
    run {
      quote {
        query[LearningStats].filter(_.id.contains(lift(id)))
      }
    }

  def getByWordId(wordId: Long) =
    run {
      quote {
        query[LearningStats].filter(_.wordId == lift(wordId))
      }
    }

  def delete(id: Long) =
    run {
      quote {
        query[LearningStats].filter(_.id.contains(lift(id))).delete
      }
    }

  def getNRandom(n: Int) =
    run {
      quote {
        query[LearningStats].sortBy(_ => infix"random()".as[Double])(Ord.ascNullsLast).take(lift(n))
      }
    }

  def getNWithLowestHitsRatio(n: Int) =
    run {
      quote {
        query[LearningStats].sortBy(_.hitsRatio)(Ord.ascNullsLast).take(lift(n))
      }
    }

  def getNWithHighestLastMissesInRow(n: Int) =
    run {
      quote {
        query[LearningStats].sortBy(_.lastMissesInRow)(Ord.descNullsLast).take(lift(n))
      }
    }

  def getNWithHighestMaxMissesInRow(n: Int) =
    run {
      quote {
        query[LearningStats].sortBy(_.maxMissesInRow)(Ord.descNullsLast).take(lift(n))
      }
    }

  def getNWithHighestMissesTotal(n: Int) =
    run {
      quote {
        query[LearningStats].sortBy(_.misses)(Ord.descNullsLast).take(lift(n))
      }
    }

  def getNByLastMissTime(n: Int) =
    run {
      quote {
        query[LearningStats].sortBy(_.lastMiss)(Ord.descNullsLast).take(lift(n))
      }
    }
}

class LearningStatsRepoInterpreter[F[_]: Applicative](val context: PostgresJdbcContext[SnakeCase])
    extends LearningStatsRepoAlgebra[F] {
  val sql = new LearningStatsSql(context)

  override def create(s: LearningStats): F[Option[Long]] = sql.create(s).pure

  override def update(s: LearningStats): F[Long] = sql.update(s).pure

  override def get(id: Long): F[Option[LearningStats]] = sql.get(id).headOption.pure

  override def getByWordId(wordId: Long): F[Option[LearningStats]] = sql.getByWordId(wordId).headOption.pure

  override def getNRandom(n: Int): F[List[LearningStats]] = sql.getNRandom(n).pure

  override def getNWithLowestHitsRatio(n: Int): F[List[LearningStats]] = sql.getNWithLowestHitsRatio(n).pure

  override def getNWithHighestLastMissesInRow(n: Int): F[List[LearningStats]] =
    sql.getNWithHighestLastMissesInRow(n).pure

  override def getNWithHighestMaxMissesInRow(n: Int): F[List[LearningStats]] = sql.getNWithHighestMaxMissesInRow(n).pure

  override def getNWithHighestMissesTotal(n: Int): F[List[LearningStats]] = sql.getNWithHighestMissesTotal(n).pure

  override def getNByLastMissTime(n: Int): F[List[LearningStats]] = sql.getNByLastMissTime(n).pure

  override def delete(id: Long): F[Long] = sql.delete(id).pure
}

object LearningStatsRepoInterpreter {
  def apply[F[_]: Applicative](context: PostgresJdbcContext[SnakeCase]): LearningStatsRepoInterpreter[F] =
    new LearningStatsRepoInterpreter[F](context)
}
