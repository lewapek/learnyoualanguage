package pl.lyal.config

import cats.ApplicativeError
import io.circe.Decoder
import io.circe.generic.semiauto._
import io.circe.config.parser

final case class RootConfig(http: HttpConfig, db: DbConfig)

final case class HttpConfig(host: String, port: Int)

final case class DbConfig(dataSourceClassName: String,
                          dataSource: DbDataSource,
                          connectionTimeout: Int,
                          minimumIdle: Int,
                          maximumPoolSize: Int) {
  def user: String     = dataSource.user
  def password: String = dataSource.password
  def jdbcUrl: String =
    s"jdbc:postgresql://${dataSource.serverName}:${dataSource.portNumber}/${dataSource.databaseName}"
}
final case class DbDataSource(serverName: String, portNumber: Int, user: String, password: String, databaseName: String)

object Config {
  implicit val rootConfigDecoder: Decoder[RootConfig]     = deriveDecoder
  implicit val httpConfigDecoder: Decoder[HttpConfig]     = deriveDecoder
  implicit val dbConfigDecoder: Decoder[DbConfig]         = deriveDecoder
  implicit val dbDataSourceDecoder: Decoder[DbDataSource] = deriveDecoder

  def decode[F[_]](implicit ar: ApplicativeError[F, Throwable]): F[RootConfig] =
    parser.decodePathF[F, RootConfig]("app")
}
