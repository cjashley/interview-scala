
import cats.effect.{Blocker, ExitCode, IO, IOApp}
import org.http4s.client.{Client, JavaNetClientBuilder}


import java.util.concurrent.Executors
val blockingPool = Executors.newFixedThreadPool(5)
val blocker = Blocker.liftExecutorService(blockingPool)
val httpClient: Client[IO] = JavaNetClientBuilder[IO](blocker).create
