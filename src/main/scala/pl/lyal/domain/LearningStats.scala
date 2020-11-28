package pl.lyal.domain

import java.util.Date
import cats.syntax.option._

case class LearningStats(id: Option[Long] = None,
                         wordId: Long,
                         hits: Int,
                         misses: Int,
                         hitsRatio: Option[Double],
                         lastHit: Option[Date],
                         lastMiss: Option[Date],
                         maxHitsInRow: Int,
                         maxMissesInRow: Int,
                         lastHitsInRow: Int,
                         lastMissesInRow: Int)

object LearningStats {
  def emptyWithWordId(wordId: Long): LearningStats =
    LearningStats(
      id = None,
      wordId,
      hits = 0,
      misses = 0,
      hitsRatio = None,
      lastHit = None,
      lastMiss = None,
      maxHitsInRow = 0,
      maxMissesInRow = 0,
      lastHitsInRow = 0,
      lastMissesInRow = 0
    )

  def addHit(s: LearningStats, date: Date): LearningStats = {
    val hits          = s.hits + 1
    val hitsRatio     = (hits.toDouble / (hits + s.misses)).some
    val lastHitsInRow = s.lastHitsInRow + 1
    val maxHitsInRow  = s.maxHitsInRow.max(lastHitsInRow)

    s.copy(
      hits = hits,
      hitsRatio = hitsRatio,
      lastHit = date.some,
      maxHitsInRow = maxHitsInRow,
      lastHitsInRow = lastHitsInRow,
      lastMissesInRow = 0
    )
  }

  def addMiss(s: LearningStats, date: Date): LearningStats = {
    val misses          = s.misses + 1
    val hitsRatio       = (s.hits.toDouble / (misses + s.hits)).some
    val lastMissesInRow = s.lastMissesInRow + 1
    val maxMissesInRow  = s.maxMissesInRow.max(lastMissesInRow)

    s.copy(
      misses = misses,
      hitsRatio = hitsRatio,
      lastMiss = date.some,
      maxMissesInRow = maxMissesInRow,
      lastMissesInRow = lastMissesInRow,
      lastHitsInRow = 0
    )
  }
}
