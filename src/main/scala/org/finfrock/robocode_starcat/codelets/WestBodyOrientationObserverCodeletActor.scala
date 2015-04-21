package org.finfrock.robocode_starcat.codelets

import org.finfrock.robocode_starcat.RobotUtilities
import java.util.UUID
import org.finfrock.starcat.codelets.SlipnetNodeActorActivationRecipient
import org.finfrock.starcat.codelets.FuzzyBehaviorCodeletActor
import akka.actor.ActorRef
import org.finfrock.robocode_starcat.RobocodeWorkspaceActor
import akka.pattern.{ ask, pipe }
import org.finfrock.robocode_starcat.BotCatable
import org.finfrock.starcat.util.FuzzySet
import akka.actor.Props

object WestBodyOrientationObserverCodeletActor {
  def getProps(
    successActivationRecipients: List[SlipnetNodeActorActivationRecipient]): Props = {
    Props(classOf[WestBodyOrientationObserverCodeletActor], successActivationRecipients)
  }
}

/**
 *
 * M  |
 * e 1|                 _-_
 * M  |               _-   -_
 * B  |             _-       -_
 * E 0|____________-___________-_
 * R  |0    90    180   270    360  : value
 *    N     E      S     W      N
 */
class WestBodyOrientationObserverCodeletActor(
  val successActivationRecipients: List[SlipnetNodeActorActivationRecipient]) 
  extends FuzzyBehaviorCodeletActor() with BotCatableCodeletActor {
  
  val successFuzzySet = new FuzzySet(180, 360, 270)
  val failureFuzzySet = new FuzzySet(Double.MinValue, Double.MaxValue, 135)
  val failureActivationRecipients: List[SlipnetNodeActorActivationRecipient] = Nil

  protected def preformUpdate(botCatable: BotCatable) {
    setCrispValue(botCatable.getHeading())
  }
  
  protected def cloneCodelet():ActorRef ={
    context.system.actorOf(WestBodyOrientationObserverCodeletActor.getProps(
      successActivationRecipients))
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