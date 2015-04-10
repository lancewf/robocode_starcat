package org.finfrock.robocode_starcat.genenticalgorithm

import java.io.OutputStream
import java.io.File
import java.util.Random


object Chromosome{
  val BODY_RED = "bodyRed";
  val BODY_GREEN = "bodyGreen";
  val BODY_BLUE = "bodyBlue";

  val TURRET_RED = "turretRed";
  val TURRET_GREEN = "turretGreen";
  val TURRET_BLUE = "turretBlue";

  val RADAR_RED = "radarRed";
  val RADAR_GREEN = "radarGreen";
  val RADAR_BLUE = "radarBlue";

  val BULLET_RED = "bulletRed";
  val BULLET_GREEN = "bulletGreen";
  val BULLET_BLUE = "bulletBlue";
}

trait Chromosome {

  def getSlipnetNodeList():List[String]

  def getColorValue(colorType:String):Int

  def getMovementAmount():Double

  def getTurnAmount():Double

  def getFireAmount():Double

  def getLinkLength(slipnetNodeSourceName:String,
      slipnetNodeReceiverName:String):Int

  def getMemoryLevel(slipnetNodeName:String):Int

  def getActivationThreshold(slipnetNodeName:String):Int

  def getInitalActivation(slipnetNodeName:String):Int

  def getNumberOfExtraNodes():Int

  def mutate(random:Random):Chromosome

  def crossover(chromosome:Chromosome, random:Random):Array[Chromosome]

  def save(outputStream:OutputStream)

  def mutateAll(random:Random):Chromosome

  def getBufferDistance():Int

  def getTargetDistance():Int

  def getEnergyLevelPeg(peg:Int):Int

  def getWorkspaceBehaviorExecuteFactor():Int

  def getWorkspaceControlExecuteFactor():Int

  def getCoderackBehaviorExecuteFactor():Int

  def getCoderackControlExecuteFactor():Int

  def getSlipnetBehaviorExecuteFactor():Int

  def getSlipnetControlExecuteFactor():Int

  def getSlipnetControlSleepTime():Long

  def getSlipnetBehaviorSleepTime():Long

  def getCoderackControlSleepTime():Long

  def getCoderackBehaviorSleepTime():Long

  def getWorkspaceControlSleepTime():Long

  def getWorkspaceBehaviorSleepTime():Long

  def getSlipnetControlReductionFactor():Double

  def getSlipnetBehaviorReductionFactor():Double

  def getCoderackControlReductionFactor():Double

  def getCoderackBehaviorReductionFactor():Double

  def getWorkspaceControlReductionFactor():Double

  def getWorkspaceBehaviorReductionFactor():Double

  def getMaximumBulletAge():Int

  def getBulletAccuracySuccessOne():Int

  def getBulletAccuracyFailureOne():Int
}