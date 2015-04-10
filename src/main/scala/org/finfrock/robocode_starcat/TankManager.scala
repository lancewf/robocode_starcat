package org.finfrock.robocode_starcat

import robocode.ScannedRobotEvent
import robocode.RobotDeathEvent

/**
 * @author lancewf
 */
class TankManager(robot:BotCatable) {
  private var tanks = List[Tank]()
  private val tankBuilder = new TankBuilder()
  
  def getTanks() = tanks
  
  def onScannedRobot(e:ScannedRobotEvent, robotX:Double,
      robotY:Double, robotHeading:Double) {
    val newTank = tankBuilder.buildTank(e, robotX, robotY, robotHeading);
    findTank(newTank.name) match{
      case Some(foundTank) => removeTank(foundTank)
      case None => // do nothing
    }
    
    addTank(newTank)
    
    update()
  }
  
  def onRobotDeath(event:RobotDeathEvent) {
    findTank(event.getName()) match{
      case Some(foundTank) =>{
        removeTank(foundTank)
      } case None => // do nothing
    }
    
    update();
  }
  
  // -------------------------------------------------------------------------
  // Private Members
  // -------------------------------------------------------------------------
  
  private def update(){
    var removedTanks = List[Tank]()
      for (tank <- tanks) {
        val diff = robot.getTime() - tank.time
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