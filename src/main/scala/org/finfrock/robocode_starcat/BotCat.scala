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

class BotCat extends AdvancedRobot with BotCatable {
  private var isAlive = false;
  private val CHROMOSOME_FILE_NAME = "BotCat.properties"
  private lazy val chromosome = createChromosome()
  private var turnAmount = 0.0;
  private var fireAmount = 0.0;
  private var forwardMovementAmount = 0.0
  private val botCatPainter = new BotCatPainter(this);
  private val robotMovement = new RobotActionManager();
  private lazy val starCatRunner:StartCatControler = new StartCatControler(this);
  private val tankManager = new TankManager(this);
  private val bulletManager = new BulletManager(this);

  // ---------------------------------------------------------------------------
  // BotCatable Methods
  // ---------------------------------------------------------------------------

  def setMovement(movement: RobotActionType) {
    System.out.println(movement);
    robotMovement.setMovement(movement);
  }

  def getTanks(): List[Tank] = tankManager.getTanks()

  def getChromosome() = chromosome

  // ---------------------------------------------------------------------------
  // AdvancedRobot Methods
  // ---------------------------------------------------------------------------

  
  override def run() {
    turnAmount = chromosome.getTurnAmount()
    fireAmount = chromosome.getFireAmount()
    forwardMovementAmount = chromosome.getMovementAmount()
    bulletManager.setMaximumBulletAge(chromosome.getMaximumBulletAge())
    applyGeniticFeatures()
    starCatRunner.start()

    isAlive = true
    var count = 0L
    while (isAlive) {
      if (count == 100000) {
        move()
        count = 0;
      }
      count += 1;
    }
  }

  /**
   * onScannedRobot: What to do when you see another robot
   */
  override def onScannedRobot(e: ScannedRobotEvent) {
    tankManager.onScannedRobot(e, getX(), getY(), getHeading());
  }

  override def onBulletHit(event: BulletHitEvent) {
    bulletManager.add(event)
  }

  override def onBulletMissed(event: BulletMissedEvent) {
    bulletManager.add(event)
  }

  /**
   * This method is called when another robot dies.
   */
  override def onRobotDeath(event: RobotDeathEvent) {
    tankManager.onRobotDeath(event)
  }

  override def onPaint(g: Graphics2D) {
    botCatPainter.onPaint(g);
  }

  def getRecentBulletHitEvents(): List[BulletHitEvent] = {
    bulletManager.getRecentBulletHitEvents()
  }

  def getRecentBulletMissedEvents(): List[BulletMissedEvent] = {
    bulletManager.getRecentBulletMissedEvents()
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
    isAlive = false
    starCatRunner.stop()
  }

  override def onDeath(event: DeathEvent) {
    isAlive = false
    starCatRunner.stop()
  }

  // ---------------------------------------------------------------------------
  // Private Methods
  // ---------------------------------------------------------------------------

  private def createChromosome(): Chromosome = {
    val file = getDataFile(CHROMOSOME_FILE_NAME);

    new PropertyUtilities().readInProperties(file) match{
      case Some(properties) => new BotcatChromosome(properties)
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
    val robotAction = robotMovement.popMovement()
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
    
    setTurnRadarRight(RobotConstance.ONE_REV);
    execute();
  }
}