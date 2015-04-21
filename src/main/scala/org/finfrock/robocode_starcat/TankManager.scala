package org.finfrock.robocode_starcat

import robocode.ScannedRobotEvent
import robocode.RobotDeathEvent

/**
 * @author lancewf
 */
class TankManager() {
  private var tanks = List[Tank]()
  private val tankBuilder = new TankBuilder()
  
  def getTanks() = tanks
  
  def onScannedRobot(e:ScannedRobotEvent, robotX:Double,
      robotY:Double, robotHeading:Double, robotTime:Long) {
    val newTank = tankBuilder.buildTank(e, robotX, robotY, robotHeading);
    findTank(newTank.name) match{
      case Some(foundTank) => removeTank(foundTank)
      case None => // do nothing
    }
    
    addTank(newTank)
    
    update(robotTime)
  }
  
  def onRobotDeath(event:RobotDeathEvent, robotTime:Long) {
    findTank(event.getName()) match{
      case Some(foundTank) =>{
        removeTank(foundTank)
      } case None => // do nothing
    }
    
    update(robotTime)
  }
  
  // -------------------------------------------------------------------------
  // Private Members
  // -------------------------------------------------------------------------
  
  private def update(robotTime:Long){
    var removedTanks = List[Tank]()
      for (tank <- tanks) {
        val diff = robotTime - tank.time
        if (diff > 500) {
          removedTanks ::= tank
        }
      }
    
    for(tank <- removedTanks){
      removeTank(tank)
    }
  }
  
  private def addTank(addingTank:Tank){
    tanks ::= addingTank
  }
  
  private def removeTank(removingTank:Tank){
    tanks = tanks.filterNot { tank => tank == removingTank }
  }
  
  private def findTank(tankName:String):Option[Tank] ={
    tanks.find(_.name == tankName)
  }
}