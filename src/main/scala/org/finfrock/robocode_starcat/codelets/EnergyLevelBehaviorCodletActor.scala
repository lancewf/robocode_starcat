package org.finfrock.robocode_starcat.codelets

import org.finfrock.starcat.util.FuzzySet
import akka.actor.ActorRef
import akka.pattern.{ ask, pipe }
import org.finfrock.starcat.codelets.FuzzyBehaviorCodeletActor
import org.finfrock.robocode_starcat.RobocodeWorkspaceActor
import org.finfrock.robocode_starcat.BotCatable
import org.finfrock.robocode_starcat.RobotUtilities
import java.util.UUID
import org.finfrock.starcat.codelets.SlipnetNodeActorActivationRecipient
import akka.actor.Props

object EnergyLevelBehaviorCodletActor{
    def getProps(value0: Int, value1: Int, value2: Int, value3: Int,
      successActivationRecipients:List[SlipnetNodeActorActivationRecipient],
      failureActivationRecipients: List[SlipnetNodeActorActivationRecipient]):Props ={
    Props(classOf[EnergyLevelBehaviorCodletActor], value0, value1, value2, value3, successActivationRecipients, failureActivationRecipients)
  }
}
/**
 *            M
 *            e 1|
 *            m  |-------_         _-------
 *            b  |        ---___---
 *            e  |        __-   -__
 *            r 0|______--_________--_______
 * Energy Level  |0    30    50     70   100
 *
 */
class EnergyLevelBehaviorCodletActor(
  value0: Int, value1: Int, value2: Int, value3: Int,
  val successActivationRecipients: List[SlipnetNodeActorActivationRecipient],
  val failureActivationRecipients: List[SlipnetNodeActorActivationRecipient]) 
  extends FuzzyBehaviorCodeletActor() with BotCatableCodeletActor {

  val successFuzzySet = new FuzzySet(minimumZeroValueX=Double.MinValue, maximumZeroValueX=(value0 + value1), value0)
  val failureFuzzySet = new FuzzySet(minimumZeroValueX=((100 - value2) - value3), maximumZeroValueX=Double.MaxValue, oneValueX=value2)

  /**
   *
   * @param value0 initial = 10 ; range 0 - 100
   * @param value1 initial = 60 ; range 0 - 100
   * @param value2 initial = 10 ; range 0 - 100
   * @param value3 initial = 60 ; range 0 - 100
   *
   */
  
  protected def preformUpdate(botCatable: BotCatable) {
      setCrispValue(botCatable.getEnergy)
  }
  
  protected def cloneCodelet():ActorRef ={
    context.system.actorOf(EnergyLevelBehaviorCodletActor.getProps(value0, value1, value2, value3,
      successActivationRecipients, failureActivationRecipients))
  }
}