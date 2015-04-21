package org.finfrock.robocode_starcat

import robocode.BulletHitEvent
import robocode.BulletMissedEvent
import org.finfrock.robocode_starcat.genenticalgorithm.Chromosome
import org.finfrock.robocode_starcat.RobotActionType._
import robocode.RobotDeathEvent
import robocode.ScannedRobotEvent
import scala.concurrent.Future

trait BotCatable {
  def setMovement(action:RobotActionType)
  def getMovement():RobotAction
  def getTanks():Future[List[Tank]]
  def getRecentBulletHitEvents():Future[List[BulletHitEvent]]
  def getRecentBulletMissedEvents():Future[List[BulletMissedEvent]]
  
  def getChromosome():Chromosome
  def getGunHeading():Double
  def getHeading():Double
  def getEnergy():Double
  def getX():Double
  def getY():Double
  def getBattleFieldWidth():Double
  def getBattleFieldHeight():Double
  def getTime():Long
  def onScannedRobot(e: ScannedRobotEvent, robotX: Double,
                     robotY: Double, robotHeading: Double)
  
  def onRobotDeath(event:RobotDeathEvent)
  
  def add(event:BulletHitEvent)
  
  def add(event:BulletMissedEvent)
}