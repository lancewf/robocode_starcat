package org.finfrock.robocode_starcat

import robocode.AdvancedRobot
import robocode.ScannedRobotEvent
import robocode.BulletHitEvent
import robocode.BulletMissedEvent
import robocode.RobotDeathEvent
import java.awt.Graphics2D
import robocode.HitWallEvent
import robocode.HitRobotEvent
import robocode.WinEvent
import robocode.DeathEvent
import java.awt.Color
import org.finfrock.robocode_starcat.RobotActionType._
import org.finfrock.robocode_starcat.genenticalgorithm.Chromosome
import org.finfrock.robocode_starcat.genenticalgorithm.BotcatChromosome
import akka.pattern.{ ask, pipe }
import akka.util.Timeout
import scala.concurrent.duration._
import scala.concurrent.Await

class BotCat2 extends BotCat
class BotCat extends AdvancedRobot {
  private val botCatPainter = new BotCatPainter(this)
  private val CHROMOSOME_FILE_NAME = "BotCat.chromosome.properties"
  private lazy val turnAmount = chromosome.getTurnAmount()
  private lazy val fireAmount = chromosome.getFireAmount()
  private lazy val forwardMovementAmount = chromosome.getMovementAmount()
  private lazy val chromosome = createChromosome()
  private lazy val starCatRunner = new StartCatControler(this, chromosome)

  // ---------------------------------------------------------------------------
  // AdvancedRobot Methods
  // ---------------------------------------------------------------------------

  
  override def run() {
    applyGeniticFeatures()
    starCatRunner.start()
    while (true) {
        move()
    }
  }

  /**
   * onScannedRobot: What to do when you see another robot
   */
  override def onScannedRobot(e: ScannedRobotEvent) {
    starCatRunner.botCatable.onScannedRobot(e, getX(), getY(), getHeading());
  }

  override def onBulletHit(event: BulletHitEvent) {
    starCatRunner.botCatable.add(event)
  }

  override def onBulletMissed(event: BulletMissedEvent) {
    starCatRunner.botCatable.add(event)
  }

  /**
   * This method is called when another robot dies.
   */
  override def onRobotDeath(event: RobotDeathEvent) {
    starCatRunner.botCatable.onRobotDeath(event)
  }

  override def onPaint(g: Graphics2D) {
    botCatPainter.onPaint(g);
  }

  /**
   * This method is called when your robot collides with a wall.
   *
   * @see robocode.Robot#onHitWall(robocode.HitWallEvent)
   */
  override def onHitWall(event: HitWallEvent) {}

  override def onHitRobot(event: HitRobotEvent) {}

  /**
   * This method is called if your robot wins a battle.
   *
   * @see robocode.Robot#onWin(robocode.WinEvent)
   */
  override def onWin(event: WinEvent) {
    starCatRunner.stop()
  }

  override def onDeath(event: DeathEvent) {
    starCatRunner.stop()
  }

  // ---------------------------------------------------------------------------
  // Private Methods
  // ---------------------------------------------------------------------------

  private def createChromosome(): Chromosome = {
    val file = getDataFile(CHROMOSOME_FILE_NAME)

    new PropertyUtilities().readInProperties(file) match{
      case Some(properties) => new BotcatChromosome(properties)
      case None => throw new Exception("property file not read in: " + CHROMOSOME_FILE_NAME)
    }
  }

  private def applyGeniticFeatures() {
    val bodyColor = new Color(
      chromosome.getColorValue(Chromosome.BODY_RED),
      chromosome.getColorValue(Chromosome.BODY_GREEN),
      chromosome.getColorValue(Chromosome.BODY_BLUE));

    val turretColor = new Color(
      chromosome.getColorValue(Chromosome.TURRET_RED),
      chromosome.getColorValue(Chromosome.TURRET_GREEN),
      chromosome.getColorValue(Chromosome.TURRET_BLUE));

    val radarColor = new Color(
      chromosome.getColorValue(Chromosome.RADAR_RED),
      chromosome.getColorValue(Chromosome.RADAR_GREEN),
      chromosome.getColorValue(Chromosome.RADAR_BLUE));

    val bulletColor = new Color(
      chromosome.getColorValue(Chromosome.BULLET_RED),
      chromosome.getColorValue(Chromosome.BULLET_GREEN),
      chromosome.getColorValue(Chromosome.BULLET_BLUE));

    setColors(bodyColor, turretColor, radarColor);

    setBulletColor(bulletColor);
  }

  /**
   * Perform a move on the robotCode battle field
   */
  private def move() {
    val robotAction = starCatRunner.botCatable.getMovement()
    
    println("movement: " + robotAction)
    //blocking not good, but need to work with only one thread here.
    robotAction.robotActionType match {
      case RobotActionType.FORWARD =>{
        setAhead(forwardMovementAmount * robotAction.amount);
      }
      case RobotActionType.BACKWARD =>{
        setBack(forwardMovementAmount * robotAction.amount);
      }
      case RobotActionType.TURN_LEFT =>{
        setTurnLeft(turnAmount * robotAction.amount);
      }
      case RobotActionType.TURN_RIGHT =>{
        setTurnRight(turnAmount * robotAction.amount);
      }
      case RobotActionType.TURN_TURRET_LEFT =>{
        setTurnGunLeft(turnAmount * robotAction.amount)
      }
      case RobotActionType.TURN_TURRET_RIGHT =>{
        setTurnGunRight(turnAmount * robotAction.amount)
      }
      case RobotActionType.FIRE =>{
        setFire(fireAmount * robotAction.amount)
      }
      case RobotActionType.DONT_MOVE =>{
        // do nothing
      }
    }
    
    setTurnRadarRight(RobotConstance.ONE_REV)
    execute()
  }
}