package actors

import akka.actor.{ Actor, ActorSystem, PoisonPill, Props }
import scala.concurrent.duration._

sealed trait Emotion

case object Angry extends Emotion

case object Happy extends Emotion

case object StartSwapping

class HotSwapActor extends Actor {
  def angry(happyCnt: Int, angryCnt: Int): Receive = {
    case Angry => sender() ! s"I am already angry!! $angryCnt"
    case Happy => context become happy(happyCnt + 1, angryCnt)
  }

  def happy(happyCnt: Int, angryCnt: Int): Receive = {
    case Happy => sender() ! s"I am already happy :-) $happyCnt"
    case Angry => context become angry(happyCnt, angryCnt + 1)
  }

  def receive = {
    case Angry => context become angry(0, 1)
    case Happy => context become happy(1, 0)
  }
}

class HotSwapper extends Actor {
  var countDown = 100
  val hotSwap = context actorOf Props[HotSwapActor]

  def receive = {
    case StartSwapping =>
      (1 to 25) foreach (_ => hotSwap ! Happy)
      (1 to 25) foreach (_ => hotSwap ! Angry)
      (1 to 25) foreach (_ => hotSwap ! Happy)
      (1 to 25) foreach (_ => hotSwap ! Angry)
    case s: String =>
      println(s"${self.path} received $s, count down $countDown")

      if (countDown > 0) {
        countDown -= 1
      } else {
        sender() ! PoisonPill
        self ! PoisonPill
      }
  }

  def genMsg(i: Int): Emotion =
    if (i % 2 == 1) Happy
    else Angry
}

object HotSwapApp extends App {

  val system = ActorSystem("Hot-swap")

  val hotswapper = system.actorOf(Props[HotSwapper], "HotSwapper")

  import system.dispatcher

  system.scheduler.scheduleOnce(500 millis) {
    hotswapper ! StartSwapping
  }
}
