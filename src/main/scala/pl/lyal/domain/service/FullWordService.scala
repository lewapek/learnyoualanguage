package pl.lyal.domain.service

import cats.Monad
import cats.data.OptionT
import pl.lyal.domain.repo.{LearningStatsRepoAlgebra, WordRepoAlgebra}
import pl.lyal.domain.{FullWord, LearningStats, Word}
import cats.syntax.functor._
import pl.lyal.Utils
import cats.syntax.traverse._
import cats.instances.list._
import cats.syntax.flatMap._
import pl.lyal.domain.service.FullWordService.QueryType.WordLang

class FullWordService[F[_]: Monad](wordRepo: WordRepoAlgebra[F], statsRepo: LearningStatsRepoAlgebra[F]) {
  import FullWordService._

  def add(word: Word): F[Boolean] = {
    val result = {
      for {
        wordId <- OptionT(wordRepo.create(word))
        emptyStats = LearningStats.emptyWithWordId(wordId)
        statsId <- OptionT(statsRepo.create(emptyStats))
      } yield statsId > 0
    }
    result.getOrElse(false)
  }

  def update(word: Word): F[Boolean] =
    wordRepo.update(word).map(_ > 0)

  def delete(wordId: Long): F[Boolean] = {
    for {
      stats        <- OptionT(statsRepo.getByWordId(wordId))
      statsId      <- OptionT.fromOption[F](stats.id)
      deletedWord  <- OptionT.liftF(wordRepo.delete(wordId))
      deletedStats <- OptionT.liftF(statsRepo.delete(statsId))
    } yield deletedStats > 0 && deletedWord > 0
  }.value.map(_.getOrElse(false))

  def hit(id: Long): F[Option[LearningStats]] = {
    for {
      stats <- OptionT(statsRepo.get(id))
      newStats = LearningStats.addHit(stats, Utils.now)
      updated <- OptionT.liftF(statsRepo.update(newStats))
    } yield {
      if (updated > 0) newStats else stats
    }
  }.value

  def miss(id: Long): F[Option[LearningStats]] = {
    for {
      stats <- OptionT(statsRepo.get(id))
      newStats = LearningStats.addHit(stats, Utils.now)
      updated <- OptionT.liftF(statsRepo.update(newStats))
    } yield {
      if (updated > 0) newStats else stats
    }
  }.value

  def searchBy(criteria: SearchCriteria): F[List[FullWord]] = criteria.queryType match {
    case QueryType.Random             => getNRandom(criteria.amount)
    case q: QueryType.WordLang        => getNByWordLang(q)
    case QueryType.LowestHitsRatio    => getNWithLowestHitsRatio(criteria.amount)
    case QueryType.HighestLastMisses  => getNWithHighestLastMissesInRow(criteria.amount)
    case QueryType.HighestMaxMisses   => getNWithHighestMaxMissesInRow(criteria.amount)
    case QueryType.HighestMissesTotal => getNWithHighestMissesTotal(criteria.amount)
    case QueryType.LastMissTime       => getNByLastMissTime(criteria.amount)
  }

  def get(wordId: Long): F[Option[FullWord]] = {
    for {
      word  <- OptionT(wordRepo.get(wordId))
      stats <- OptionT(statsRepo.getByWordId(wordId))
    } yield FullWord(word, stats)
  }.value

  val getNRandom: Int => F[List[FullWord]] =
    getNByStatsFunction(statsRepo.getNRandom)

  val getNWithLowestHitsRatio: Int => F[List[FullWord]] =
    getNByStatsFunction(statsRepo.getNWithLowestHitsRatio)

  val getNWithHighestLastMissesInRow: Int => F[List[FullWord]] =
    getNByStatsFunction(statsRepo.getNWithHighestLastMissesInRow)

  val getNWithHighestMaxMissesInRow: Int => F[List[FullWord]] =
    getNByStatsFunction(statsRepo.getNWithHighestMaxMissesInRow)

  val getNWithHighestMissesTotal: Int => F[List[FullWord]] =
    getNByStatsFunction(statsRepo.getNWithHighestMissesTotal)

  val getNByLastMissTime: Int => F[List[FullWord]] =
    getNByStatsFunction(statsRepo.getNByLastMissTime)

  private def getNByStatsFunction(f: Int => F[List[LearningStats]])(n: Int): F[List[FullWord]] = {
    val statsWithWords =
      for {
        statsList   <- f(n)
        optWordList <- statsList.traverse(stats => wordRepo.get(stats.wordId))
      } yield (statsList, optWordList)

    statsWithWords.map {
      case (stats, words) =>
        stats.zip(words).collect { case (s, Some(w)) => FullWord(w, s) }
    }
  }

  private def getNByWordLang(wordLang: WordLang): F[List[FullWord]] = {
    import wordLang._
    val wordsWithStats =
      for {
        wordList <- wordRepo.getByWordNameAndLangs(name, sourceLangId, targetLangId)
        optStatsList <- wordList
          .traverse(w => w.id.traverse(statsRepo.getByWordId))
          .map(_.flatten)
      } yield (wordList, optStatsList)
    wordsWithStats.map {
      case (words, stats) =>
        words.zip(stats).collect { case (w, Some(s)) => FullWord(w, s) }
    }
  }
}

object FullWordService {
  case class SearchCriteria(amount: Int, queryType: QueryType)

  sealed trait QueryType
  object QueryType {
    case object Random                                                      extends QueryType
    case class WordLang(name: String, sourceLangId: Int, targetLangId: Int) extends QueryType
    case object LowestHitsRatio                                             extends QueryType
    case object HighestLastMisses                                           extends QueryType
    case object HighestMaxMisses                                            extends QueryType
    case object HighestMissesTotal                                          extends QueryType
    case object LastMissTime                                                extends QueryType
  }

  def apply[F[_]: Monad](wordRepo: WordRepoAlgebra[F], statsRepo: LearningStatsRepoAlgebra[F]): FullWordService[F] =
    new FullWordService[F](wordRepo, statsRepo)
}
