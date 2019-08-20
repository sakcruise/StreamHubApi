package streamhub

//#user-routes-spec
//#test-top
import akka.actor.{ActorRef, ActorSystem, Scheduler}
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model._
import akka.http.scaladsl.testkit.{RouteTestTimeout, ScalatestRouteTest}
import com.sun.xml.internal.ws.util.Pool.Unmarshaller
import akka.http.scaladsl.server.directives.ParameterDirectives.parameters
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{Matchers, WordSpec}
import streamhub.{UserReportActor, UserReportRoutes}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt

//#set-up
class UserReportRoutesSpec extends WordSpec with Matchers with ScalaFutures with ScalatestRouteTest
  with UserReportRoutes {

  implicit def default(implicit system: ActorSystem): RouteTestTimeout = RouteTestTimeout(50.seconds)

  override def userReportActor: ActorRef = system.actorOf(UserReportActor.props, "userReportActor")

  override val ec: ExecutionContext = system.dispatcher
  override val scheduler: Scheduler = system.scheduler
  lazy val routes = userReportRoutes

  //Positive test
  "UserReportRoutes1" should {
    "return OK (GET /reports)" in {
      val params = "group=broadcaster&metric=sh:program:uniqueUsers&startDate=2019-01-01&endDate=2019-02-01"
      val request = HttpRequest(uri = "/reports?" + params, method = HttpMethods.GET)

      request ~> routes ~> check {
        status should ===(StatusCodes.OK)

        // we expect the response to be json:
        contentType should ===(ContentTypes.`application/json`)

        // and no entries should be in the list:
        entityAs[String] should ===("""{"hits":"1234567"}""")
      }
    }
  }
  //  Test to check invalid group and metric parameters passed.
  "UserReportRoutes2" should {
    "return Forbidden (GET /reports)" in {
      val params = "group=advertiser&metric=sh:program:uniqueUsers&startDate=2019-01-01&endDate=2019-02-01"
      val request = HttpRequest(uri = "/reports?" + params, method = HttpMethods.GET)

      request ~> routes ~> check {
        status should ===(StatusCodes.Forbidden)

        contentType should ===(ContentTypes.`text/plain(UTF-8)`)

        entityAs[String] should ===(StatusCodes.Forbidden.toString() + """ - Invalid query parameters!!!""")
        print(entityAs[String])
      }
    }
  }
  "UserReportRoutes3" should {
    "return Forbidden (GET /reports)" in {
      val params = "group=advertiser&metric=sh:program:uniqueUsers&startDate=2019-01-01&endDate=2019-02-01"
      val request = HttpRequest(uri = "/reports?" + params, method = HttpMethods.GET)

      request ~> routes ~> check {
        status should ===(StatusCodes.Forbidden)

        contentType should ===(ContentTypes.`text/plain(UTF-8)`)

        entityAs[String] should ===(StatusCodes.Forbidden.toString() + """ - Invalid query parameters!!!""")
        print(entityAs[String])
      }
    }
  }
}
