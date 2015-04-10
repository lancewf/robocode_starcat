package org.finfrock.robocode_starcat

object RobotActionType extends Enumeration {
  type RobotActionType = Value
  val FORWARD, BACKWARD, TURN_RIGHT, TURN_LEFT, DONT_MOVE, FIRE, TURN_TURRET_RIGHT, TURN_TURRET_LEFT = Value
}