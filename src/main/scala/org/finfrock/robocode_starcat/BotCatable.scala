package org.finfrock.robocode_starcat

import robocode.BulletHitEvent
import robocode.BulletMissedEvent
import org.finfrock.robocode_starcat.genenticalgorithm.Chromosome
import org.finfrock.robocode_starcat.RobotActionType._

trait BotCatable {
  def setMovement(action:RobotActionType);
  def getTanks():List[Tank]
  
  def getChromosome():Chromosome
  def getGunHeading():Double
  def getHeading():Double
  def getEnergy():Double
  def getX():Double
  def getY():Double
  def getBattleFieldWidth():Double
  def getBattleFieldHeight():Double
  def getTime():Long
  def getRecentBulletHitEvents():List[BulletHitEvent]
  def getRecentBulletMissedEvents():List[BulletMissedEvent]
}