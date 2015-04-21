package org.finfrock.robocode_starcat

import robocode.AdvancedRobot
import org.finfrock.robocode_starcat.RobotActionType._
import robocode.BulletHitEvent
import robocode.BulletMissedEvent
import org.finfrock.robocode_starcat.genenticalgorithm.Chromosome
import robocode.ScannedRobotEvent
import robocode.RobotDeathEvent
import scala.concurrent.Future

class BotCatableImpl(robot: AdvancedRobot,
                     chromosome: Chromosome) extends BotCatable {

  private val tankManager = new TankManager()
  private val bulletManager = new BulletManager(chromosome.getMaximumBulletAge())
  private var currentMovement = RobotActionType.DONT_MOVE
  private var amount = 0

  def setMovement(movement: RobotActionType) {
    if (currentMovement == movement) {
      amount += 1
    } else {
      currentMovement = movement;
      amount = 0;
    }
  }

  def getMovement(): RobotAction = RobotAction(currentMovement, amount)

  def getChromosome = chromosome
  def getGunHeading(): Double = robot.getGunHeading
  def getHeading(): Double = robot.getHeading()
  def getEnergy(): Double = robot.getEnergy
  def getX(): Double = robot.getX
  def getY(): Double = robot.getY
  def getBattleFieldWidth(): Double = robot.getBattleFieldWidth
  def getBattleFieldHeight(): Double = robot.getBattleFieldHeight
  def getTime(): Long = robot.getTime

  def getRecentBulletHitEvents(): Future[List[BulletHitEvent]] =
    Future.successful(bulletManager.getRecentBulletHitEvents(getTime))
  def getRecentBulletMissedEvents(): Future[List[BulletMissedEvent]] =
    Future.successful(bulletManager.getRecentBulletMissedEvents(getTime))
  def getTanks(): Future[List[Tank]] = Future.successful(tankManager.getTanks)

  def onScannedRobot(e: ScannedRobotEvent, robotX: Double,
                     robotY: Double, robotHeading: Double) {
    tankManager.onScannedRobot(e, robotX, robotY, robotHeading, getTime)
  }

  def onRobotDeath(event: RobotDeathEvent) {
    tankManager.onRobotDeath(event, getTime)
  }

  def add(event: BulletHitEvent) {
    bulletManager.add(event, getTime)
  }

  def add(event: BulletMissedEvent) {
    bulletManager.add(event, getTime)
  }
}