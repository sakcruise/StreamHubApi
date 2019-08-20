package streamhub

import scala.concurrent.duration._
import akka.pattern.{ask, retry}
import akka.actor.{ActorRef, ActorSystem, Scheduler}
import akka.event.Logging
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.util.Timeout
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.PathDirectives.path
import akka.http.scaladsl.server.directives.ParameterDirectives.parameters
import akka.http.scaladsl.server.directives.MethodDirectives.get
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import spray.json.DefaultJsonProtocol
import streamhub.UserReportActor.GetUniqueUsers

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

trait UserReportRoutes extends SprayJsonSupport  {
  import DefaultJsonProtocol._

  implicit val uniqueUsersJsonFormat =jsonFormat1(uniqueUsersCC)

  def system: ActorSystem
  implicit val ec: ExecutionContext
  implicit val scheduler: Scheduler

  lazy val log = Logging(system, classOf[UserReportRoutes])

  def userReportActor: ActorRef

  implicit val timeout = Timeout(5.seconds)

  lazy val userReportRoutes: Route =
    path("reports") {
      get {
        parameters('group, 'metric, 'startDate, 'endDate) { (group, metric, startDate, endDate) =>
          val p = ParamsCC(group, metric, startDate, endDate)
          p match {
            case ParamsCC("advertiser", "sh:program:uniqueUsers", _, _) =>
              complete(HttpResponse(StatusCodes.Forbidden, entity = StatusCodes.Forbidden.toString() + " - Invalid query parameters!!!"))
            case _ =>
              val uniqueUsers: Future[uniqueUsersCC] =
                retry(() => userReportActor ? GetUniqueUsers(p), 3, 1 seconds).mapTo[uniqueUsersCC]
              complete(uniqueUsers)
          }
        }
      }
    }
}

