package clover.tsp.front

import cats.effect.{ Blocker, ExitCode }
import clover.tsp.front.config.{ initDb, mkTransactor, Config }
import clover.tsp.front.domain.DBItem
import doobie.util.transactor.Transactor
import clover.tsp.front.http.DBService
import clover.tsp.front.repository.implementations
import clover.tsp.front.repository.implementations.{ DoobieRepository, PostgresRepository }
import clover.tsp.front.repository.interfaces.Repository
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.CORS
import pureconfig.generic.auto._
import zio._
import zio.blocking.Blocking
import zio.clock.Clock
import zio.console._
import zio.interop.catz._

object Main extends App {

  type AppEnvironment = Clock with Console with Blocking with Repository
  type AppTask[A]     = RIO[AppEnvironment, A]

  override def run(args: List[String]): ZIO[Environment, Nothing, Int] =
    (for {
      cfg <- ZIO.fromEither(pureconfig.loadConfig[Config])
      _   <- initDb(cfg.dbConfig)

      blockingEC  <- ZIO.environment[Blocking].flatMap(_.blocking.blockingExecutor).map(_.asEC)
      block       = Blocker.liftExecutionContext(blockingEC)
      transactorR = mkTransactor(cfg.dbConfig, Platform.executor.asEC, block)

      xa = Transactor.fromDriverManager[Task](
        "org.postgresql.Driver",
        "jdbc:postgresql://localhost:5434/test_db",
        "test_user",
        "test_password"
      )
      pgRepository = PostgresRepository(xa)
      httpApp = Router[AppTask](
        "/tsp_processing" -> DBService(s"${cfg.appConfig.baseUrl}/db_info", pgRepository).service
      ).orNotFound
      server = ZIO.runtime[AppEnvironment].flatMap { implicit rts =>
        BlazeServerBuilder[AppTask]
          .bindHttp(cfg.appConfig.port, cfg.appConfig.host)
          .withHttpApp(CORS(httpApp))
          .serve
          .compile[AppTask, AppTask, ExitCode]
          .drain
      }

      store   <- Ref.make(DBItem("some data"))
      counter <- Ref.make(0L)

      program <- transactorR.use { transactor =>
                  server.provideSome[Environment] { base =>
                    new Clock with Console with Blocking with DoobieRepository with Repository {
                      override protected def xa: doobie.Transactor[Task] = transactor

                      override val console: Console.Service[Any]   = base.console
                      override val clock: Clock.Service[Any]       = base.clock
                      override val blocking: Blocking.Service[Any] = base.blocking

                      override val dbInfoRepository: Repository.SimpleService[Any] =
                        implementations.SimpleRepository(store, counter)
                      override val todoRepository: Repository.Service[Any] = null
                    }
                  }
                }
    } yield program).foldM(err => putStrLn(s"Execution failed with: $err") *> ZIO.succeed(1), _ => ZIO.succeed(0))

}
