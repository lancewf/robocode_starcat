package org.finfrock.robocode_starcat.codelets

import org.finfrock.robocode_starcat.RobotUtilities
import java.util.UUID
import org.finfrock.starcat.codelets.SlipnetNodeActorActivationRecipient
import akka.actor.ActorRef
import akka.pattern.{ ask, pipe }
import org.finfrock.robocode_starcat.RobocodeWorkspaceActor
import org.finfrock.robocode_starcat.BotCatable
import org.finfrock.starcat.util.FuzzySet
import org.finfrock.starcat.codelets.FuzzyBehaviorCodeletActor
import akka.actor.Props

object FuzzyObstacleBehaviorCodeletActor{
  def getProps(successActivationRecipients:List[SlipnetNodeActorActivationRecipient], 
    failureActivationRecipients:List[SlipnetNodeActorActivationRecipient],
    headingToLook: Double, bufferDistance: Int):Props ={
    Props(classOf[FuzzyObstacleBehaviorCodeletActor], successActivationRecipients, failureActivationRecipients, headingToLook, bufferDistance)
  }
}

/**
 * A Codelet that observes obstacles (walls and robots) in a certain direction
 *
 * M 1|
 * e  |----__   __-----
 * M  |      -_-
 * B  |     _- -_
 * E 0|___-_______-____
 * R  |min    one    max  : value
 *
 * @author User Lance Finfrock
 */
class FuzzyObstacleBehaviorCodeletActor(
    val successActivationRecipients:List[SlipnetNodeActorActivationRecipient], 
    val failureActivationRecipients:List[SlipnetNodeActorActivationRecipient],
    headingToLook: Double, bufferDistance: Int) extends FuzzyBehaviorCodeletActor() with BotCatableCodeletActor {
  
  val successFuzzySet = new FuzzySet(minimumZeroValueX=0, maximumZeroValueX=(bufferDistance + bufferDistance / 2), oneValueX=(bufferDistance / 2))
  val failureFuzzySet = new FuzzySet(minimumZeroValueX=(bufferDistance / 2), maximumZeroValueX=Double.MaxValue, oneValueX=(bufferDistance + bufferDistance / 2))

  protected def preformUpdate(botCatable: BotCatable) {
    val heading = botCatable.getHeading()
    val x = botCatable.getX()
    val y = botCatable.getY()
    val battleFieldWidth = botCatable.getBattleFieldWidth()
    val battleFieldHeight = botCatable.getBattleFieldHeight()
    val distance = RobotUtilities.findDistanceToWall(heading, x, y, battleFieldWidth, battleFieldHeight, headingToLook)
    setCrispValue(distance)
  }
  
  protected def cloneCodelet():ActorRef ={
    context.system.actorOf(FuzzyObstacleBehaviorCodeletActor.getProps(
      successActivationRecipients, failureActivationRecipients, headingToLook, bufferDistance))
  }
//  
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

object FuzzyObstacleBehaviorCodelet {
  val FORWARD = 0;
  val FORWARD_RIGHT = 45;
  val RIGHT = 90;
  val BACKWARD_RIGHT = 135;
  val BACKWARD = 180;
  val BACKWARD_LEFT = -135;
  val LEFT = -90;
  val FORWARD_LEFT = -45;
}