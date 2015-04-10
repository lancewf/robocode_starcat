package org.finfrock.robocode_starcat.codelets

import org.finfrock.starcat.codelets.FuzzyBehaviorCodelet
import org.finfrock.starcat.workspace.Workspace
import org.finfrock.robocode_starcat.RobocodeWorkspace
import org.finfrock.robocode_starcat.RobotUtilities
import org.finfrock.starcat.codelets.SlipnetNodeActivationRecipient
import java.util.UUID

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
class FuzzyObstacleBehaviorCodelet(
    val successActivationRecipients:List[SlipnetNodeActivationRecipient], 
    val failureActivationRecipients:List[SlipnetNodeActivationRecipient],
    headingToLook: Double, bufferDistance: Int) extends FuzzyBehaviorCodelet(
  successOneValueX = bufferDistance / 2,
  successMinimumZeroValueX = 0,
  successMaximumZeroValueX = bufferDistance + bufferDistance / 2,
  failureOneValueX = bufferDistance + bufferDistance / 2,
  failureMinimumZeroValueX = bufferDistance / 2,
  failureMaximumZeroValueX = Double.MaxValue) {
  
  val name:String = UUID.randomUUID().toString()
  val numberToEmit:Int = 1
  val urgency:Double = 1
  
  @Override
  def execute(workspace: Workspace) {
    workspace match {
      case robocodeWorkspace: RobocodeWorkspace => {
        val distance = RobotUtilities.findDistanceToWall(
            robocodeWorkspace.robot, headingToLook)

        setCrispValue(distance);
      }
    }
  }
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