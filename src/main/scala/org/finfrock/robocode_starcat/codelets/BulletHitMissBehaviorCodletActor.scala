package org.finfrock.robocode_starcat.codelets

import org.finfrock.robocode_starcat.RobotUtilities
import java.util.UUID
import org.finfrock.starcat.util.FuzzySet
import akka.actor.ActorRef
import akka.pattern.{ ask, pipe }
import scala.concurrent.duration._
import akka.util.Timeout
import org.finfrock.starcat.codelets.FuzzyBehaviorCodeletActor
import org.finfrock.robocode_starcat.RobocodeWorkspaceActor
import org.finfrock.robocode_starcat.BotCatable
import org.finfrock.starcat.codelets.SlipnetNodeActorActivationRecipient
import akka.actor.Props
import robocode.BulletMissedEvent
import robocode.BulletHitEvent

object BulletHitMissBehaviorCodletActor{
  def getProps(successOne:Int, failureOne:Int,
    successActivationRecipients: List[SlipnetNodeActorActivationRecipient], 
    failureActivationRecipients: List[SlipnetNodeActorActivationRecipient]): Props = {
    Props(classOf[BulletHitMissBehaviorCodletActor], successOne, failureOne, 
        successActivationRecipients, failureActivationRecipients)
  }
}
/**
 *            M
 *            e 1|
 *            m  |--------_           _--------
 *            b  |         -_       _-
 *            e  | failure   -_   _-  success
 *            r 0|_____________-_-_____________
 * Bullet Power  |-5    -2      0      2     5
 *
 */
class BulletHitMissBehaviorCodletActor(successOne:Int, failureOne:Int,
      val successActivationRecipients: List[SlipnetNodeActorActivationRecipient],
  val failureActivationRecipients: List[SlipnetNodeActorActivationRecipient]) 
  extends FuzzyBehaviorCodeletActor() with BotCatableCodeletActor {
  
  implicit val timeout = Timeout(5 seconds)
  implicit val ex = context.dispatcher
  
  val successFuzzySet = new FuzzySet(minimumZeroValueX=0, maximumZeroValueX=Double.MaxValue, oneValueX=successOne)
  val failureFuzzySet = new FuzzySet(minimumZeroValueX=(Double.MaxValue * -1), maximumZeroValueX=0, oneValueX=failureOne)

  protected def preformUpdate(botCatable: BotCatable) {
    val setCrispValueFuture = for {
      bulletHitEvents <- botCatable.getRecentBulletHitEvents()
      bulletMissedEvents <- botCatable.getRecentBulletMissedEvents()
    } yield {
      val hitEventSum = bulletHitEvents.map(_.getBullet().getPower()).sum
      val missedEventSum = bulletMissedEvents.map(_.getBullet().getPower()).sum
      FuzzyBehaviorCodeletActor.SetCrispValue(hitEventSum + missedEventSum)
    }
    
    setCrispValueFuture.pipeTo(self)
  }
  
  protected def cloneCodelet():ActorRef ={
    context.system.actorOf(BulletHitMissBehaviorCodletActor.getProps(successOne, failureOne, 
            successActivationRecipients, failureActivationRecipients))
  }
  
//  protected def preformPreExecuteCoderack(coderack: ActorRef){}
//
//  protected def preformPreExecuteSlipnet(slipnet: ActorRef){}
//
//  protected def preformPreExecuteWorkspace(workspace: ActorRef){}
//
//  protected def preformExecuteCoderack(coderack: ActorRef){}
//
//  protected def preformExecuteWorkspace(workspace: ActorRef){}
//
//  protected def preformPostExecuteCoderack(coderack: ActorRef){}
//
//  protected def preformPostExecuteSlipnet(slipnet: ActorRef){}
}