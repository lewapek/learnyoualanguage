package pl.lyal.domain.repo

import pl.lyal.domain.Lang

trait LangRepoAlgebra[F[_]] {
  def create(lang: Lang): F[Option[Int]]
  def update(lang: Lang): F[Long]
  def get(id: Int): F[Option[Lang]]
  def getAll: F[List[Lang]]
  def delete(id: Int): F[Long]
}
