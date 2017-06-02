package com.elevators

import akka.actor.{ ActorRef, ActorSystem }
import akka.pattern.ask
import akka.testkit.{ ImplicitSender, TestKit, TestProbe }
import akka.util.Timeout
import org.scalatest._
import Matchers._

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.Try

class ElevatorControllerSpec
    extends TestKit(ActorSystem("Test-Elevator-System"))
    with ImplicitSender
    with AsyncWordSpecLike
    with BeforeAndAfterAll {

  implicit val timeout = Timeout(5 seconds)

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  "An Elevator Controller" when {
    val listener = TestProbe("notification-listener")
    implicit val ec = system.dispatcher
    val elevatorController: ActorRef =
      system.actorOf(
        ElevatorController.props(
          elevators = 1,
          floors = 7,
//          maxInElevator = 3,
          notificationListener = listener.ref,
          executionContext = ec
        )
      )

    "called" should {
      "collect the calling passenger and deliver to requested floor" in {
        elevatorController ! PassengerToCollect(1, Passenger(goingToFloor = 9))
        waitTillIdle(1 second, listener)

        val elevatorStates: Future[Seq[ElevatorState]] =
          (elevatorController ? ElevatorStateRequest).mapTo[Seq[ElevatorState]]

        elevatorStates.map { states =>
          {
            states.flatMap(_.delivered) should contain(
              Passenger(goingToFloor = 9)
            )
            states.flatMap(_.collectFrom) should be(empty)
          }

        }
      }
    }

    "when receives a kill switch message" should {
      "restart kill all elevators and start lifts as empty" in {
        pending
        elevatorController ! PassengerToCollect(0, Passenger(goingToFloor = 9))
        elevatorController ! PassengerToCollect(0, Passenger(goingToFloor = 9))
        elevatorController ! KillElevators

        val receivedRestart = waitForRestart(10 second, listener)
        elevatorController ! PassengerToCollect(0, Passenger(goingToFloor = 9))
        elevatorController ! PassengerToCollect(0, Passenger(goingToFloor = 9))
        val receivedIdle = waitTillIdle(1 second, listener)
        Future.unit.map(
          _ =>
            receivedRestart.isSuccess && receivedIdle.isSuccess shouldBe (true)
        )
      }
    }
  }

  def waitForRestart(timeout: Duration, listener: TestProbe): Try[Any] = {
    val restartingReceived: PartialFunction[Any, Boolean] = {
      case Restarting => true
      case _ => false
    }
    Try(listener.fishForMessage(timeout)(restartingReceived))
  }

  def waitTillIdle(timeout: Duration, listener: TestProbe): Try[Any] = {
    val idleReceived: PartialFunction[Any, Boolean] = {
      case Idle => true
      case _ => false
    }
    Try(listener.fishForMessage(timeout)(idleReceived))
  }

}
