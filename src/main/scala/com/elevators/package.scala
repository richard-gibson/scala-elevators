/*
 * Copyright year author
 */
package com

package object elevators {

  case class Passenger(goingToFloor: Int)
  case class ElevatorState(floorNo: Int,
                           collectFrom: Set[(Int, Passenger)],
                           takeTo: Set[Passenger],
                           delivered: List[Passenger])

  case object ElevatorStateRequest
  case object Move
  case object Idle
  case object KillElevators
  case object Moving
  case object Restarting

  case class PassengerToCollect(floor: Int, passenger: Passenger)
  case class TooManyInElevatorException() extends Exception
}
