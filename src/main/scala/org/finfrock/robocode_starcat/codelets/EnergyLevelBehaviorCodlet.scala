package org.finfrock.robocode_starcat.codelets

import org.finfrock.starcat.codelets.FuzzyBehaviorCodelet
import org.finfrock.starcat.workspace.Workspace
import org.finfrock.robocode_starcat.RobocodeWorkspace
import org.finfrock.robocode_starcat.RobotUtilities
import org.finfrock.starcat.codelets.SlipnetNodeActivationRecipient
import java.util.UUID

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
class EnergyLevelBehaviorCodlet(
  value0: Int, value1: Int, value2: Int, value3: Int,
  val successActivationRecipients: List[SlipnetNodeActivationRecipient],
  val failureActivationRecipients: List[SlipnetNodeActivationRecipient]) extends FuzzyBehaviorCodelet(
  successOneValueX = value0,
  successMinimumZeroValueX = Double.MinValue,
  successMaximumZeroValueX = value0 + value1,
  failureOneValueX = 100 - value2,
  failureMinimumZeroValueX = (100 - value2) - value3,
  failureMaximumZeroValueX = Double.MaxValue) {

  val name: String = UUID.randomUUID().toString()
  val numberToEmit: Int = 1
  val urgency: Double = 1

  /**
   *
   * @param value0 initial = 10 ; range 0 - 100
   * @param value1 initial = 60 ; range 0 - 100
   * @param value2 initial = 10 ; range 0 - 100
   * @param value3 initial = 60 ; range 0 - 100
   *
   * setSuccessMinimumZeroValueX(Double.MIN_VALUE);
   * setSuccessOneValueX(10);
   * setSuccessMaximumZeroValueX(70);
   *
   * setFailureMinimumZeroValueX(30);
   * setFailureOneValueX(90);
   * setFailureMaximumZeroValueX(Double.MAX_VALUE);
   */

  @Override
  def execute(workspace: Workspace) {
    workspace match {
      case robocodeWorkspace: RobocodeWorkspace => {
        setCrispValue(robocodeWorkspace.robot.getEnergy())
      }
    }
  }
}