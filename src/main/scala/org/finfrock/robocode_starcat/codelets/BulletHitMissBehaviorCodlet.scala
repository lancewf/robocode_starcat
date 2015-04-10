package org.finfrock.robocode_starcat.codelets

import org.finfrock.starcat.workspace.Workspace
import org.finfrock.robocode_starcat.RobocodeWorkspace
import org.finfrock.robocode_starcat.RobotUtilities
import org.finfrock.starcat.codelets.FuzzyBehaviorCodelet
import org.finfrock.starcat.codelets.SlipnetNodeActivationRecipient
import java.util.UUID

/**
 *            M
 *            e 1|
 *            m  |--------_           _--------
 *            b  |         -_       _-
 *            e  | failure   -_   _-  success
 *            r 0|_____________-_-_____________
 * Bullet Power  |-5    -2      0      2     5
 *
 */
class BulletHitMissBehaviorCodlet(successOne:Int, failureOne:Int,
      val successActivationRecipients: List[SlipnetNodeActivationRecipient],
  val failureActivationRecipients: List[SlipnetNodeActivationRecipient]) extends FuzzyBehaviorCodelet(
    successOneValueX = successOne, 
    successMinimumZeroValueX = 0, 
    successMaximumZeroValueX = Double.MaxValue, 
    failureOneValueX = failureOne, 
    failureMinimumZeroValueX = Double.MaxValue * -1, 
    failureMaximumZeroValueX = 0) {
  
  val name: String = UUID.randomUUID().toString()
  val numberToEmit: Int = 1
  val urgency: Double = 1
  /**
   * successOne = 2; Max 10; Min 0
   * failureOne = -2; Max 0; Min -10
   * setSuccessMinimumZeroValueX(0);
   * setSuccessOneValueX(2);
   * setSuccessMaximumZeroValueX(Double.MAX_VALUE);
   *
   * setFailureMinimumZeroValueX(Double.MIN_VALUE);
   * setFailureOneValueX(-2);
   * setFailureMaximumZeroValueX(0);
   */

  @Override
  def execute(workspace: Workspace) {
    workspace match {
      case robocodeWorkspace: RobocodeWorkspace => {
        var power = 0.0;
        for (bulletHitEvent <- robocodeWorkspace.robot.getRecentBulletHitEvents()) {
          val bullet = bulletHitEvent.getBullet();

          power += bullet.getPower();
        }

        for (bulletMissedEvent <- robocodeWorkspace.robot.getRecentBulletMissedEvents()) {
          val bullet = bulletMissedEvent.getBullet();

          power -= bullet.getPower();
        }

        setCrispValue(power);
      }
    }
  }
}