package pl.lyal.infrastructure.http.model

import pl.lyal.Utils
import pl.lyal.domain.Word

case class HttpWord(name: String,
                    sourceLanguageId: Int,
                    targetLanguageId: Int,
                    targetNames: Vector[String],
                    synonyms: Vector[String],
                    usages: Vector[String]) {
  def toWord: Word = {
    val now = Utils.now
    Word(None, name, sourceLanguageId, targetLanguageId, targetNames, synonyms, usages, now, now)
  }
}
