package streamhub
import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import scala.concurrent._
import ExecutionContext.Implicits.global

final case class uniqueUsersCC(hits: String)
final case class ParamsCC(group: String, metric: String, startdate: String, enddate: String)

object UserReportActor {
  final case class GetUniqueUsers(params: ParamsCC)

  val system = ActorSystem("UserReports")
  def props: Props = Props[UserReportActor]
  var sleep = 0 // increase this 120000 to retry 3 times call the notify_to_support() function
}

class UserReportActor extends Actor with ActorLogging {
  import UserReportActor._

  var t = 0
  def receive: Receive = {
    case GetUniqueUsers(paramsUri: ParamsCC) =>
      val p = paramsUri
      val mySender = sender()
      if (t < 3) {
        heavyQuery(p, mySender)
        t += 1
        println("retrying " + t)
      }
      else {
        t = 0
        notify_to_support(mySender)
      }
  }

  private def heavyQuery(p:ParamsCC, mySender: ActorRef): Unit = {
    val f = Future {
      // Assume this is unavoidable blocking operation
       Thread.sleep ( sleep )
      //      use the params to query
//      println(p.group)
      mySender ! uniqueUsersCC("1234567")
    }
  }

  private def notify_to_support(mySender: ActorRef): Unit = {
    val f = Future {
      mySender ! uniqueUsersCC("Connection error: Notified to support team")
    }

  }

}


