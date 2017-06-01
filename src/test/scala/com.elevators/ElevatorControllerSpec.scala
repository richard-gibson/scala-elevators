package com.elevators

import akka.actor.{ActorRef, ActorSystem}
import akka.pattern.ask
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import akka.util.Timeout
import org.scalatest.Matchers._
import org.scalatest._

import scala.concurrent.Future
import scala.concurrent.duration._

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
    "called" should {
      val listener = TestProbe("notification-listener")
      implicit val ec = system.dispatcher
      val elevatorController: ActorRef =
        system.actorOf(ElevatorController.props(3, 7, listener.ref, ec))

      "collect the calling passenger and deliver to requested floor" in {
        elevatorController ! PassengerToCollect(1, Passenger(goingToFloor = 9))
        waitTillIdle(1 second, listener)

        pending
      }
    }
  }

  def waitTillIdle(timeout: Duration, listener: TestProbe): Any = {
    val idleReceived: PartialFunction[Any, Boolean] = {
      case Idle => true
      case _ => false
    }
    listener.fishForMessage(1 second)(idleReceived)
  }



}
