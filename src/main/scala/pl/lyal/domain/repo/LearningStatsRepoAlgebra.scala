package pl.lyal.domain.repo

import pl.lyal.domain.LearningStats

trait LearningStatsRepoAlgebra[F[_]] {
  def create(s: LearningStats): F[Option[Long]]
  def update(s: LearningStats): F[Long]
  def get(id: Long): F[Option[LearningStats]]
  def getByWordId(wordId: Long): F[Option[LearningStats]]
  def getNRandom(n: Int): F[List[LearningStats]]
  def getNWithLowestHitsRatio(n: Int): F[List[LearningStats]]
  def getNWithHighestLastMissesInRow(n: Int): F[List[LearningStats]]
  def getNWithHighestMaxMissesInRow(n: Int): F[List[LearningStats]]
  def getNWithHighestMissesTotal(n: Int): F[List[LearningStats]]
  def getNByLastMissTime(n: Int): F[List[LearningStats]]
  def delete(id: Long): F[Long]
}
