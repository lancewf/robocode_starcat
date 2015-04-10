package org.finfrock.robocode_starcat.codelets

import org.finfrock.starcat.codelets.FuzzyBehaviorCodelet
import org.finfrock.starcat.workspace.Workspace
import org.finfrock.robocode_starcat.RobocodeWorkspace
import org.finfrock.robocode_starcat.RobotUtilities
import java.util.Random
import org.finfrock.starcat.codelets.SlipnetNodeActivationRecipient
import java.util.UUID

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
class TargetObserverBehaviorCodelet(
    val successActivationRecipients:List[SlipnetNodeActivationRecipient], 
    headingToLook: Double, targetDistance: Int) extends FuzzyBehaviorCodelet(
  successOneValueX = targetDistance / 2,
  successMinimumZeroValueX = Double.MinValue,
  successMaximumZeroValueX = targetDistance + targetDistance / 2,
  failureOneValueX = targetDistance + targetDistance / 2,
  failureMinimumZeroValueX = targetDistance / 2,
  failureMaximumZeroValueX = Double.MaxValue) {

  val name: String = UUID.randomUUID().toString()
  val numberToEmit: Int = 1
  val urgency: Double = 1
  val failureActivationRecipients: List[SlipnetNodeActivationRecipient] = Nil
  private val random = new Random()

  @Override
  def execute(workspace: Workspace) {
    workspace match {
      case robocodeWorkspace: RobocodeWorkspace => {
        val distance = RobotUtilities.findDistanceToOtherTanks(headingToLook,
          robocodeWorkspace.robot, random);

        setCrispValue(distance);
      }
    }
  }
}

object TargetObserverBehaviorCodelet {
  val FORWARD = 0;
  val FORWARD_RIGHT = 45;
  val RIGHT = 90;
  val BACKWARD_RIGHT = 135;
  val BACKWARD = 180;
  val BACKWARD_LEFT = -135;
  val LEFT = -90;
  val FORWARD_LEFT = -45;
}