package org.finfrock.robocode_starcat.codelets

import org.finfrock.robocode_starcat.RobotUtilities
import java.util.Random
import java.util.UUID
import org.finfrock.starcat.util.FuzzySet
import org.finfrock.starcat.codelets.SlipnetNodeActorActivationRecipient
import org.finfrock.starcat.codelets.FuzzyBehaviorCodeletActor
import akka.actor.ActorRef
import org.finfrock.robocode_starcat.RobocodeWorkspaceActor
import akka.pattern.{ ask, pipe }
import scala.concurrent.duration._
import akka.util.Timeout
import org.finfrock.robocode_starcat.BotCatable
import akka.actor.Props

/**
 * Codelet that senses how close to an Target in one given direction
 * M 1|
 * e  |----__   __-----
 * M  |      -_-
 * B  |     _- -_  x <-------------- their is a target in this direction
 * E 0|___-_______-____
 * R  |min   one    max  : value
 *
 * @author User Lance Finfrock
 */
class TargetObserverBehaviorCodeletActor(
    val successActivationRecipients:List[SlipnetNodeActorActivationRecipient], 
    headingToLook: Double, targetDistance: Int) extends FuzzyBehaviorCodeletActor with BotCatableCodeletActor {
  
  val successFuzzySet: FuzzySet = new FuzzySet(Double.MinValue, targetDistance + targetDistance / 2, targetDistance / 2)
  val failureFuzzySet: FuzzySet = new FuzzySet(targetDistance / 2, Double.MaxValue, targetDistance + targetDistance / 2)
  val failureActivationRecipients: List[SlipnetNodeActorActivationRecipient] = Nil
  private val random = new Random()
  implicit val timeout = Timeout(5 seconds)
  implicit val ex = context.dispatcher
  
  protected def preformUpdate(botCatable: BotCatable) {
    val setCrispValueFuture = for{tanks <- botCatable.getTanks()} yield{
      val heading = botCatable.getHeading()
      val x = botCatable.getX()
      val y = botCatable.getY()
      val distance = RobotUtilities.findDistanceToOtherTanks(headingToLook,
        heading, x, y, random, tanks)
      FuzzyBehaviorCodeletActor.SetCrispValue(distance)
    }
    
    setCrispValueFuture.pipeTo(self)
  }
  
  protected def cloneCodelet():ActorRef ={
    context.system.actorOf(TargetObserverBehaviorCodeletActor.getProps(
      successActivationRecipients, headingToLook, targetDistance))
  }
}

object TargetObserverBehaviorCodeletActor {
  val FORWARD = 0;
  val FORWARD_RIGHT = 45;
  val RIGHT = 90;
  val BACKWARD_RIGHT = 135;
  val BACKWARD = 180;
  val BACKWARD_LEFT = -135;
  val LEFT = -90;
  val FORWARD_LEFT = -45;
  
  def getProps(
      successActivationRecipients:List[SlipnetNodeActorActivationRecipient],
    headingToLook: Double, targetDistance: Int):Props ={
    Props(classOf[TargetObserverBehaviorCodeletActor], successActivationRecipients, headingToLook, targetDistance)
  }
}