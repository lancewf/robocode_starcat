package org.finfrock.robocode_starcat

import org.junit._
import Assert._
import akka.testkit.TestActorRef
import akka.actor.ActorSystem
import akka.testkit.{ TestActors, DefaultTimeout, ImplicitSender, TestKit }
import akka.actor.Actor
import org.finfrock.starcat.codelets.Codelet
import org.finfrock.starcat.codelets.CodeletActor
import akka.actor.Props
import org.finfrock.robocode_starcat.codelets.TargetObserverBehaviorCodeletActor
import org.finfrock.starcat.codelets.SlipnetNodeActorActivationRecipient
import scala.concurrent.Future
import robocode.BulletMissedEvent
import robocode.BulletHitEvent
import org.finfrock.robocode_starcat.RobotActionType._
import robocode.RobotDeathEvent
import robocode.ScannedRobotEvent
import org.finfrock.robocode_starcat.genenticalgorithm.Chromosome
import org.finfrock.robocode_starcat.codelets.SouthBodyOrientationObserverCodeletActor
import org.finfrock.starcat.slipnet.SlipnetNodeActor
import akka.actor.ActorLogging
import org.finfrock.starcat.codelets.FuzzyBehaviorCodeletActor

@Test
class CodeletTest extends TestKit(ActorSystem("TestKitUsageSpec")) with DefaultTimeout with ImplicitSender {

  private val botCatable = new BotCatable {
      def setMovement(action: RobotActionType){}
      def getMovement(): RobotAction = RobotAction(RobotActionType.FORWARD, 3)
      def getTanks(): Future[List[Tank]] = Future.successful(Nil)
      def getRecentBulletHitEvents(): Future[List[BulletHitEvent]]  = Future.successful(Nil)
      def getRecentBulletMissedEvents(): Future[List[BulletMissedEvent]]  = Future.successful(Nil)

      def getChromosome(): Chromosome = null
      def getGunHeading(): Double = 0.0
      def getHeading(): Double = 180.0
      def getEnergy(): Double = 0.0
      def getX(): Double = 0.0
      def getY(): Double = 0.0
      def getBattleFieldWidth(): Double = 0.0
      def getBattleFieldHeight(): Double = 0.0
      def getTime(): Long = 0L
      def onScannedRobot(e: ScannedRobotEvent, robotX: Double,
                         robotY: Double, robotHeading: Double){}

      def onRobotDeath(event: RobotDeathEvent){}

      def add(event: BulletHitEvent){}

      def add(event: BulletMissedEvent){}
    }
  
  @Test
  def testOK() = assertTrue(true)

  @Test
  def testOne() {
    val successActivationRecipients = List(SlipnetNodeActorActivationRecipient(self, 50))
    val codeletActor = system.actorOf(TargetObserverBehaviorCodeletActor.getProps(successActivationRecipients,
      TargetObserverBehaviorCodeletActor.FORWARD, 50), "Target_Codelet")

    codeletActor ! CodeletActor.ExecuteWorkspace(self)

    expectMsg(RobocodeWorkspaceActor.GetBotCatable)
  }

  @Test
  def testTwo() {
    val successActivationRecipients = List(SlipnetNodeActorActivationRecipient(self, 50))
    val codeletActor = system.actorOf(
        SouthBodyOrientationObserverCodeletActor.getProps(successActivationRecipients), "South_Codelet")

    codeletActor ! botCatable
    codeletActor ! FuzzyBehaviorCodeletActor.GetCrispValue

    expectMsg(180)
  }

  @Test
  def testThree() {
    val successActivationRecipients = List(SlipnetNodeActorActivationRecipient(self, 50))
    val codeletActor = system.actorOf(
        SouthBodyOrientationObserverCodeletActor.getProps(successActivationRecipients), "South_Codelet")

    codeletActor ! botCatable
    codeletActor ! CodeletActor.ExecuteSlipnet(self)

    expectMsg(SlipnetNodeActor.AddActivationToBuffer(50))
  }
}


