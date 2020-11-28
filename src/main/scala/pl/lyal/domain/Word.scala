package pl.lyal.domain

import java.util.Date

case class Word(id: Option[Long] = None,
                name: String,
                sourceLanguageId: Int,
                targetLanguageId: Int,
                targetNames: Vector[String],
                synonyms: Vector[String],
                usages: Vector[String],
                created: Date,
                updated: Date)
