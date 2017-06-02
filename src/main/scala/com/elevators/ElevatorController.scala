package com.elevators

import akka.actor.SupervisorStrategy._
import akka.pattern.ask
import akka.actor._
import akka.util.Timeout

import scala.concurrent.{ duration, ExecutionContext, Future }
import duration._

class ElevatorController(noOfElevators: Int,
                         floors: Int,
                         maxPassengers: Int,
                         notificationListener: ActorRef,
                         executionContext: ExecutionContext)
    extends Actor
    with ActorLogging {
  implicit val timeout: Timeout = Timeout(5 seconds)
  implicit val ec: ExecutionContext = executionContext

  override val supervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 3, withinTimeRange = 1 minute) {
      case e @ (_: TooManyInElevatorException | _: ActorKilledException) =>
        log.error("Too many people in elevator or elevator died ", e)
        notificationListener ! Restarting
        Restart
      case _: Exception => Escalate
    }

  val elevators: Seq[ActorRef] =
    (1 to noOfElevators) map (_ =>
                                context.actorOf(
                                  ElevatorActor
                                    .props(floors, maxPassengers, self)
                                ))

  override def receive: Receive = {
    case passengerToCollect: PassengerToCollect =>
      //find the least busy elevator by requesting
      val leastBusyElevator: Future[ActorRef] =
        Future
          .sequence(
            elevators.map(
              elevator => getElevatorWorkLoad(elevator).map((_, elevator))
            )
          )
          .map(_.sortBy(_._1))
          .map(_.head._2)
      leastBusyElevator.foreach(_ ! passengerToCollect)

    case ElevatorStateRequest =>
      //sender can mutate by the time a future has completed
      val currentSender = sender
      val elevatorStates: Future[Seq[ElevatorState]] = Future.sequence(
        elevators.map(
          elevator => (elevator ? ElevatorStateRequest).mapTo[ElevatorState]
        )
      )
      elevatorStates.foreach(stateList => currentSender ! stateList)

    case Idle =>
      notificationListener ! Idle

    case KillElevators =>
      log.info("killing all elevators!!!")
      elevators foreach (elevator => elevator ! Kill)
  }

  def getElevatorWorkLoad(elevator: ActorRef): Future[Int] =
    (elevator ? ElevatorStateRequest)
      .mapTo[ElevatorState]
      .map(state => state.collectFrom.size + state.takeTo.size)

}

object ElevatorController {
  def props(elevators: Int,
            floors: Int,
            maxInElevator: Int,
            notificationListener: ActorRef,
            executionContext: ExecutionContext): Props =
    Props(
      classOf[ElevatorController],
      elevators,
      floors,
      maxInElevator,
      notificationListener,
      executionContext
    )
}
