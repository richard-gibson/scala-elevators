package com.elevators

import akka.actor.{ Actor, ActorLogging, ActorRef, Props }

class ElevatorActor(floors: Int, notificationListener: ActorRef)
    extends Actor
    with ActorLogging
    with ElevatorBehaviour {

  override def receive(): Receive = idleReceive(List.empty, 0)

  def movingReceive(collectingPassengerFrom: Set[(Int, Passenger)],
                    takingPassengersTo: Set[Passenger],
                    passengersDelivered: List[Passenger],
                    currentFloor: Int): Receive = {
    case PassengerToCollect(floor, passenger) =>
      context become movingReceive(
        collectPassengerFrom(floor, passenger, collectingPassengerFrom),
        takingPassengersTo,
        passengersDelivered,
        currentFloor
      )
      self ! Move

    case Move =>
      val (collectFrom, takeTo, delivered) =
        genFloorsToVisit(
          currentFloor,
          collectingPassengerFrom,
          takingPassengersTo,
          passengersDelivered
        )

      val nextFloor: Option[Int] = (takeTo.headOption map (_.goingToFloor))
        .orElse(collectFrom.headOption map (_._1))
      val nextFloorToVisit = getNextFloor(currentFloor, nextFloor)

      if (nextFloor.isDefined) {
        context become movingReceive(
          collectFrom,
          takeTo,
          delivered,
          nextFloorToVisit
        )
        self ! Move
      } else {
        context become idleReceive(delivered, currentFloor)
        notificationListener ! Idle
      }

    case ElevatorStateRequest =>
      log.info("Elevator state requested")
      sender
      ElevatorState(
        currentFloor,
        collectingPassengerFrom,
        takingPassengersTo,
        passengersDelivered
      )

  }

  def idleReceive(passengersDelivered: List[Passenger],
                  currentFloor: Int): Receive = {
    case PassengerToCollect(floor, passenger) =>
      context become movingReceive(
        collectPassengerFrom(floor, passenger, Set.empty),
        Set.empty,
        passengersDelivered,
        currentFloor
      )
      self ! Move

    case ElevatorStateRequest =>
      log.info("Elevator state requested")
      sender ! ElevatorState(
        currentFloor,
        Set.empty,
        Set.empty,
        passengersDelivered
      )

  }
}

object ElevatorActor {
  def props(floors: Int, notificationListener: ActorRef): Props =
    Props(classOf[ElevatorActor], floors, notificationListener)
}
