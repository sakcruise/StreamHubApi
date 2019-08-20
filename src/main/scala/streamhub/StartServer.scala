package streamhub

//scala libs
import akka.actor.Scheduler

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success}

//akka libs
import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer

object StartServer extends App with UserReportRoutes {
  implicit val system: ActorSystem = ActorSystem("userReports")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val ec: ExecutionContext = system.dispatcher
  implicit val scheduler: Scheduler = system.scheduler

  val userReportActor: ActorRef = system.actorOf(UserReportActor.props, "userReportActor")

  //  trait userReportRoutes
  val routes: Route = userReportRoutes

  // http server binding
  val serverBinding: Future[Http.ServerBinding] = Http().bindAndHandle(routes, "localhost", 8080)

  serverBinding.onComplete {
    case Success(bound) =>
      println(s"Server connected at http://${bound.localAddress.getHostString}:${bound.localAddress.getPort}/")
    case Failure(e) =>
      Console.err.println(s"Server could not connect notify to Support team!")
      e.printStackTrace()
      system.terminate()
  }

  Await.result(system.whenTerminated, Duration.Inf)
}