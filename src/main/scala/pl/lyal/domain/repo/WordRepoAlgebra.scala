package pl.lyal.domain.repo

import pl.lyal.domain.Word

trait WordRepoAlgebra[F[_]] {
  def create(word: Word): F[Option[Long]]
  def update(word: Word): F[Long]
  def get(id: Long): F[Option[Word]]
  def getByWordNameAndLangs(name: String, sourceLangId: Int, targetLangId: Int): F[List[Word]]
  def delete(id: Long): F[Long]
}
