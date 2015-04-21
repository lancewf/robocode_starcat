package org.finfrock.robocode_starcat.codelets

import org.finfrock.robocode_starcat.RobotUtilities
import java.util.UUID
import org.finfrock.starcat.util.FuzzySet
import akka.actor.ActorRef
import akka.pattern.{ ask, pipe }
import org.finfrock.starcat.codelets.FuzzyBehaviorCodeletActor
import org.finfrock.robocode_starcat.RobocodeWorkspaceActor
import org.finfrock.robocode_starcat.BotCatable
import org.finfrock.starcat.codelets.SlipnetNodeActorActivationRecipient
import akka.actor.Props

object BackwardTurretOrientationObserverCodeletActor {
  def getProps(
    successActivationRecipients: List[SlipnetNodeActorActivationRecipient]): Props = {
    Props(classOf[BackwardTurretOrientationObserverCodeletActor], successActivationRecipients)
  }
}

/**
 * 
 * M  |      
 * e 1|------------_-_--------------- 
 * M  |          _-   -_   
 * B  |        _-       -_    
 * E 0|_______-____________-_________
 * R  |0     90    180     270    360  : value
 *     F     R      B      L       F
 *    
 * F - forward
 * R - right
 * B - backward
 * L - left
 */   
class BackwardTurretOrientationObserverCodeletActor(
    val successActivationRecipients: List[SlipnetNodeActorActivationRecipient]) 
     extends FuzzyBehaviorCodeletActor() with BotCatableCodeletActor{
    
  val successFuzzySet = new FuzzySet(minimumZeroValueX=90, maximumZeroValueX=270, oneValueX=180)
  val failureFuzzySet = new FuzzySet(minimumZeroValueX=Double.MinValue, maximumZeroValueX=Double.MaxValue, oneValueX=135)
  val failureActivationRecipients:List[SlipnetNodeActorActivationRecipient] = Nil

  protected def preformUpdate(botCatable: BotCatable) {
    val gunHeading = botCatable.getGunHeading()
    val bodyHeading = botCatable.getHeading()
    val bearing = RobotUtilities.getTurretHeadingFromFront(gunHeading, bodyHeading)

    setCrispValue(bearing)
  }
  
  protected def cloneCodelet():ActorRef ={
    context.system.actorOf(
        BackwardTurretOrientationObserverCodeletActor.getProps(successActivationRecipients))
  }
}