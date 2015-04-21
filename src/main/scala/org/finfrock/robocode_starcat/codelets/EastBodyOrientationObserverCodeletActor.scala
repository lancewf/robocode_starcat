package org.finfrock.robocode_starcat.codelets

import org.finfrock.robocode_starcat.RobotUtilities
import java.util.UUID
import akka.actor.ActorRef
import akka.pattern.{ ask, pipe }
import org.finfrock.starcat.codelets.FuzzyBehaviorCodeletActor
import org.finfrock.robocode_starcat.RobocodeWorkspaceActor
import org.finfrock.robocode_starcat.BotCatable
import org.finfrock.starcat.codelets.SlipnetNodeActorActivationRecipient
import org.finfrock.starcat.util.FuzzySet
import akka.actor.Props

object EastBodyOrientationObserverCodeletActor{
  def getProps(
    successActivationRecipients: List[SlipnetNodeActorActivationRecipient]): Props = {
    Props(classOf[EastBodyOrientationObserverCodeletActor], successActivationRecipients)
  }
}
/**
 *
 * M  |
 * e 1|     _-_
 * M  |   _-   -_
 * B  | _-       -_
 * E 0|-___________-_____________
 * R  |0    90    180   270    360  : value
 *    N     E      S     W      N
 */
class EastBodyOrientationObserverCodeletActor(
    val successActivationRecipients: List[SlipnetNodeActorActivationRecipient]) 
    extends FuzzyBehaviorCodeletActor()  with BotCatableCodeletActor {
  
  val failureActivationRecipients:List[SlipnetNodeActorActivationRecipient] = Nil
  val successFuzzySet = new FuzzySet(minimumZeroValueX=0, maximumZeroValueX=180, oneValueX=90)
  val failureFuzzySet = new FuzzySet(minimumZeroValueX=Double.MinValue, maximumZeroValueX=Double.MaxValue, oneValueX=135)

  protected def preformUpdate(botCatable: BotCatable) {
    setCrispValue(botCatable.getHeading)
  }
  
  protected def cloneCodelet():ActorRef ={
    context.system.actorOf(EastBodyOrientationObserverCodeletActor.getProps(successActivationRecipients))
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