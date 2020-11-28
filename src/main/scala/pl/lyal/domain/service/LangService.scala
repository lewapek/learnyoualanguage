package pl.lyal.domain.service

import cats.Functor
import cats.syntax.functor._
import pl.lyal.domain.Lang
import pl.lyal.domain.repo.LangRepoAlgebra

class LangService[F[_]: Functor](repo: LangRepoAlgebra[F]) {
  def create(lang: Lang): F[Boolean] = repo.create(lang).map(_.nonEmpty)
  def update(lang: Lang): F[Boolean] = repo.update(lang).map(_ > 0)
  def get(id: Int): F[Option[Lang]]  = repo.get(id)
  def getAll: F[List[Lang]]          = repo.getAll
  def delete(id: Int): F[Boolean]    = repo.delete(id).map(_ > 0)
}

object LangService {
  def apply[F[_]: Functor](repo: LangRepoAlgebra[F]): LangService[F] =
    new LangService[F](repo)
}
