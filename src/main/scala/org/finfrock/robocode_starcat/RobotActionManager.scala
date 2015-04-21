package org.finfrock.robocode_starcat

import org.finfrock.robocode_starcat.RobotActionType._

case class RobotAction(robotActionType:RobotActionType, amount:Int)

/**
 * @author lancewf
 */
class RobotActionManager {
  private var currentMovement = RobotActionType.DONT_MOVE
  private var amount = 0
  
  def popMovement():RobotAction ={
    return new RobotAction(currentMovement, amount)
  }
  
  def setMovement(movement:RobotActionType) {
    if(currentMovement == movement){
      amount+=1
    }
    else{
      currentMovement = movement;
      amount = 0;
    }
  }
}