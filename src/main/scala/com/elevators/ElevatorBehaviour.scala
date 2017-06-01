package com.elevators

trait ElevatorBehaviour {
  def collectPassengerFrom(
    floorNo: Int,
    passenger: Passenger,
    collectFrom: Set[(Int, Passenger)]
  ): Set[(Int, Passenger)]

  def takePassengerTo(passenger: Passenger,
                      takeTo: Set[Passenger]): Set[Passenger]

  def leavePassengerOff(
    passenger: Passenger,
    takeTo: Set[Passenger],
    delivered: List[Passenger]
  ): (Set[Passenger], List[Passenger])

  def passengerCollected(
    floorNo: Int,
    passenger: Passenger,
    collectFrom: Set[(Int, Passenger)],
    takeTo: Set[Passenger]
  ): (Set[(Int, Passenger)], Set[Passenger])

  def genFloorsToVisit(
    currentFlr: Int,
    collectFrom: Set[(Int, Passenger)],
    takeTo: Set[Passenger],
    delivered: List[Passenger]
  ): (Set[(Int, Passenger)], Set[Passenger], List[Passenger])

  def getNextFloor(currentFlr: Int, nextFloor: Option[Int]): Int
}
