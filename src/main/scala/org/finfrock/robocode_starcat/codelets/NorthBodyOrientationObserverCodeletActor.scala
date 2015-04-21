package org.finfrock.robocode_starcat.codelets

import org.finfrock.robocode_starcat.RobotUtilities
import java.util.UUID
import org.finfrock.starcat.codelets.SlipnetNodeActorActivationRecipient
import org.finfrock.starcat.util.FuzzySet
import org.finfrock.starcat.codelets.FuzzyBehaviorCodeletActor
import akka.actor.ActorRef
import akka.pattern.{ ask, pipe }
import org.finfrock.robocode_starcat.RobocodeWorkspaceActor
import org.finfrock.robocode_starcat.BotCatable
import akka.actor.Props

object NorthBodyOrientationObserverCodeletActor {
  def getProps(
    successActivationRecipients: List[SlipnetNodeActorActivationRecipient]): Props = {
    Props(classOf[NorthBodyOrientationObserverCodeletActor], successActivationRecipients)
  }
}

/**
 * 
 * M  |      
 * e 1|------------_-_--------- 
 * M  |          _-   -_   
 * B  |        _-       -_    
 * E 0|_______-____________-_______
 * R  |-180  -90    0      90     180  : value
 *          West   North  East
 */       
class NorthBodyOrientationObserverCodeletActor(
    val successActivationRecipients: List[SlipnetNodeActorActivationRecipient]) 
     extends FuzzyBehaviorCodeletActor() with BotCatableCodeletActor{
  
  val successFuzzySet = new FuzzySet(minimumZeroValueX=(-90), maximumZeroValueX=90, oneValueX=0)
  val failureFuzzySet = new FuzzySet(minimumZeroValueX=Double.MinValue, maximumZeroValueX=Double.MaxValue, oneValueX=135)
  val failureActivationRecipients:List[SlipnetNodeActorActivationRecipient] = Nil
  
  protected def preformUpdate(botCatable: BotCatable) {
    val bearing = botCatable.getHeading()
    if (bearing > 180) {
      setCrispValue(bearing - 360)
    } else {
      setCrispValue(bearing)
    }
  }
  
  protected def cloneCodelet():ActorRef ={
    context.system.actorOf(NorthBodyOrientationObserverCodeletActor.getProps(
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