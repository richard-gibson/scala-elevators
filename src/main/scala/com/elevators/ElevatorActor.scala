package com.elevators

import akka.actor.Actor.Receive
import akka.actor.{ Actor, ActorLogging, ActorRef, Props }

class ElevatorActor(floors: Int, notificationListener: ActorRef)
    extends Actor
    with ActorLogging
    with ElevatorBehaviour {

  private var collectingPassengerFrom: Set[(Int, Passenger)] = Set.empty
  private var takingPassengersTo: Set[Passenger] = Set.empty
  private var passengersDelivered: List[Passenger] = List.empty
  private var currentFloor: Int = 0
  private var isMoving: Boolean = false

  override def receive: Receive = {
    case ElevatorStateRequest =>


    case PassengerToCollect(floor, passenger) =>


    case Move =>



  }

}

object ElevatorActor {
  def apply(floors: Int, notificationListener: ActorRef): Props = ???

}
