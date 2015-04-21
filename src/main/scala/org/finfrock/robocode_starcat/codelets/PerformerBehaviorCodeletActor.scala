package org.finfrock.robocode_starcat.codelets

import org.finfrock.robocode_starcat.RobotUtilities
import org.finfrock.robocode_starcat.RobotActionType._
import java.util.UUID
import akka.actor.ActorRef
import akka.pattern.{ ask, pipe }
import org.finfrock.robocode_starcat.BotCatable
import org.finfrock.robocode_starcat.RobocodeWorkspaceActor
import scala.concurrent.duration._
import akka.util.Timeout
import org.finfrock.starcat.codelets.BehaviorCodeletActor
import akka.actor.Props
import org.finfrock.starcat.codelets.SlipnetNodeActorActivationRecipient

object PerformerBehaviorCodeletActor{
  def getProps(robotAction: RobotActionType):Props ={
    Props(classOf[PerformerBehaviorCodeletActor], robotAction)
  }
}
class PerformerBehaviorCodeletActor(robotAction: RobotActionType) extends BotCatableCodeletActor {

  val successActivationRecipients:List[SlipnetNodeActorActivationRecipient] = Nil
  val failureActivationRecipients:List[SlipnetNodeActorActivationRecipient] = Nil

  protected def preformUpdate(botCatable: BotCatable) {
    botCatable.setMovement(robotAction)
  }
  
  protected def cloneCodelet():ActorRef ={
    context.system.actorOf(PerformerBehaviorCodeletActor.getProps(
      robotAction))
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