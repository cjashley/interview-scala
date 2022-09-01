import cats.effect.{ Blocker, ExitCode, IO, IOApp }
import org.http4s.client.{ Client, JavaNetClientBuilder }

import java.util.concurrent.Executors

object HttpTest extends IOApp {

  val blockingPool           = Executors.newFixedThreadPool(5)
  val blocker                = Blocker.liftExecutorService(blockingPool)
  val httpClient: Client[IO] = JavaNetClientBuilder[IO](blocker).create

  def run(args: List[String]): IO[ExitCode] =
    args.headOption match {
      case Some(name) => {
        val helloJames: IO[String] = httpClient.expect[String]("http://localhost:8081/currencies")

        IO(println(s"Hello, ${name}. " + helloJames)).as(ExitCode.Success)
      }
      case None => {
        val helloJames = httpClient.expect[String]("http://localhost:8081/currencies")

        IO(System.err.println(s"Usage: MyApp currencies ${helloJames}")).as(ExitCode(2))
      }
    }

}
