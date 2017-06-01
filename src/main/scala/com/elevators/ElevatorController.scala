package com.elevators

import akka.actor.{ Actor, ActorLogging, ActorRef, Props }
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.duration._
import scala.concurrent.{ duration, ExecutionContext, Future }

class ElevatorController(noOfElevators: Int,
                         floors: Int,
                         notificationListener: ActorRef,
                         executionContext: ExecutionContext)
    extends Actor
    with ActorLogging {

  implicit val timeout: Timeout = Timeout(5 seconds)
  implicit val ec: ExecutionContext = executionContext

  val elevators: Seq[ActorRef] =
    (1 to noOfElevators) map (_ =>
                                context.actorOf(
                                  ElevatorActor.props(floors, self)
                                ))

  override def receive: Receive = {
    case passengerToCollect: PassengerToCollect =>
    case ElevatorStateRequest =>
    case Idle =>
  }

}

object ElevatorController {
  def props(elevators: Int,
            floors: Int,
            notificationListener: ActorRef,
            executionContext: ExecutionContext): Props = ???

}
