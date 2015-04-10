package org.finfrock.robocode_starcat.genenticalgorithm

import java.util.Properties
import robocode.Rules

class BotcatChromosome(properties1: Properties) extends SizableChromosome(properties1) {
  import BotcatChromosome._
  // --------------------------------------------------------------------------
  // Constructor
  // --------------------------------------------------------------------------

  private def this(botcatChromosome:BotcatChromosome) {
    this(botcatChromosome.properties)
  }

  // --------------------------------------------------------------------------
  // Public Member
  // --------------------------------------------------------------------------

  private var slipnetNodes:List[String] = Nil

  private def getInternalSlipnetNodes():List[String] = {
    var slipnetNodes = List[String]()
    
    val numberOfExtraNodes = getNumberOfExtraNodes();

    for (count <- 0 until numberOfExtraNodes) {
      slipnetNodes ::= (BotcatChromosome.EXTRA_NODE_TAG + count)
    }
    
    return slipnetNodes;
  }
  
  private def getActionSlipnetNodes():List[String] = {
    var slipnetNodes = List[String]()
    
    slipnetNodes ::= TURN_RIGHT
    slipnetNodes ::= TURN_LEFT
    slipnetNodes ::= MOVE_FORWARD
    slipnetNodes ::= MOVE_BACKWARD
    slipnetNodes ::= DO_NOT_MOVE
    slipnetNodes ::= FIRE

    slipnetNodes ::= TURN_TURRET_RIGHT
    slipnetNodes ::= TURN_TURRET_LEFT
    
    return slipnetNodes;
  }
  
  private def getObservationSlipnetNodes():List[String] = {
    var slipnetNodes = List[String]()
    
    slipnetNodes ::= OBSTACLE_LEFT
    slipnetNodes ::= OBSTACLE_RIGHT
    slipnetNodes ::= OBSTACLE_FRONT
    slipnetNodes ::= OBSTACLE_BACKWARD

    slipnetNodes ::= CLEAR_LEFT
    slipnetNodes ::= CLEAR_RIGHT
    slipnetNodes ::= CLEAR_FOREWARD
    slipnetNodes ::= CLEAR_BACKWARD

    slipnetNodes ::= TARGET_FORWARD
    slipnetNodes ::= TARGET_FORWARD_RIGHT
    slipnetNodes ::= TARGET_RIGHT
    slipnetNodes ::= TARGET_BACKWARD_RIGHT
    slipnetNodes ::= TARGET_BACKWARD
    slipnetNodes ::= TARGET_BACKWARD_LEFT
    slipnetNodes ::= TARGET_LEFT
    slipnetNodes ::= TARGET_FORWARD_LEFT
    slipnetNodes ::= ENERGY_HIGH_NODE_NAME
    slipnetNodes ::= ENERGY_LOW_NODE_NAME
    
    slipnetNodes ::= ORIENTATION_NORTH
    slipnetNodes ::= ORIENTATION_SOUTH
    slipnetNodes ::= ORIENTATION_WEST
    slipnetNodes ::= ORIENTATION_EAST

    slipnetNodes ::= TURRET_RIGHT
    slipnetNodes ::= TURRET_LEFT
    slipnetNodes ::= TURRET_BACKWARD
    slipnetNodes ::= TURRET_FORWARD
    slipnetNodes ::= BULLET_HIT
    slipnetNodes ::= BULLET_MISS
    
    return slipnetNodes;
  }
  
  def getSlipnetNodeList():List[String] = {
    if (slipnetNodes == Nil) {
      slipnetNodes :::= getObservationSlipnetNodes()
      slipnetNodes :::= getActionSlipnetNodes()
      slipnetNodes :::= getInternalSlipnetNodes()
    }

    return slipnetNodes;
  }

  /**
   * The Memory Level of the slipnet node.
   * 
   * Note: The higher the memory level the Longer memory is stored in the
   * slipnet node.
   * 
   * @param slipnetNodeName
   *            - the slipnet node name that the memory level is requested.
   */
  def getMemoryLevel(slipnetNodeName:String):Int = {
    val tag = MEMORY_TAG + "_" + slipnetNodeName;

    if (!doesBaseExist(tag)) {
      if(slipnetNodeName.equals(BULLET_HIT) || 
          slipnetNodeName.equals(BULLET_MISS)){
        setValue(tag, 25);
      }
      else{
        setValue(tag, 100);
      }
    }

    return getValue(tag);
  }

  def getInitalActivation(slipnetNodeName:String):Int = {
    val tag = INITAL_ACTIVATION + "_" + slipnetNodeName;

    if (!doesBaseExist(tag)) {
      setValue(tag, 0);
    }

    return getValue(tag);
  }

  /**
   * Set the memory level of the slipnet node name passed in with the value
   * passed in.
   * 
   * Note: The higher the memory level the Longer memory is stored in the
   * slipnet node.
   * 
   * @param slipnetNodeName
   *            - the name of the slipnet node to set the memory level of.
   * @param memoryLevel
   *            - the memory level to set on the slipnet node.
   */
  def setMemoryLevel(slipnetNodeName:String, memoryLevel:Int) {
    val tag = MEMORY_TAG + "_" + slipnetNodeName;

    setValue(tag, memoryLevel);
  }

  /**
   * Get the link length of the link between slipnetNodeSourceName to
   * slipnetNodeReceiverName
   * 
   * @param slipnetNodeSourceName
   *            - the slipnet node source of the link length returned
   * 
   * @param slipnetNodeReceiverName
   *            - the slipnet node receiver of the link length returned
   * 
   * @return The length of the link between the source slipnet node and the
   *         receiver slipnet node. The higher the value the Longer the link,
   *         meaning the higher the value the less activation that is
   *         transmitted from the source slipnet node to the receiver slipnet
   *         node.
   */
  def getLinkLength(slipnetNodeSourceName:String,
      slipnetNodeReceiverName:String):Int = {
    val tag = LINK_TAG + "_" + slipnetNodeSourceName + "_" + slipnetNodeReceiverName;

    if (!doesBaseExist(tag)) {
      if(slipnetNodeSourceName.equals(BULLET_HIT) && 
          slipnetNodeReceiverName.equals(FIRE)){
        setValue(tag, 0);
      }
      else{
        setValue(tag, 100);
      }
    }

    return getValue(tag);
  }

  def setLinkLength(slipnetNodeSourceName:String,
      slipnetNodeReceiverName:String, value:Int) {
    val tag = LINK_TAG + "_" + slipnetNodeSourceName + "_" + slipnetNodeReceiverName;

    setValue(tag, value);
  }

  /**
   * Get the color values of the robot parts body, turret, and radar.
   * 
   * @param colorType
   *            - Use the static variables of this class below:
   *            <code>BODY_RED<code>
   * <code>BODY_GREEN<code>
   * <code>BODY_BLUE<code>
   * 
   * <code>TURRET_RED<code>
   * <code>TURRET_GREEN<code>
   * <code>TURRET_BLUE<code>
   * 
   * <code>RADAR_RED<code>
   * <code>RADAR_GREEN<code>
   * <code>RADAR_BLUE<code>
   * 
   * @return
   */
   def getColorValue(colorType:String):Int = {
    if (!doesBaseExist(colorType)) {
      setValue(colorType, 0);
    }

    val value = getValue(colorType)

    adjustValue(MAXIMUM_COLOR_VALUE, MINIMUM_COLOR_VALUE, value).toInt
  }

  /**
   * Set the color values of the robot parts body, turret, and radar.
   * 
   * @param colorType
   *            - Use the static variables of this class below:
   *            <code>BODY_RED<code>
   * <code>BODY_GREEN<code>
   * <code>BODY_BLUE<code>
   * 
   * <code>TURRET_RED<code>
   * <code>TURRET_GREEN<code>
   * <code>TURRET_BLUE<code>
   * 
   * <code>RADAR_RED<code>
   * <code>RADAR_GREEN<code>
   * <code>RADAR_BLUE<code>
   * 
   * @param value
   *            - the color value of the types. The value must be between 0 -
   *            255.
   */
  def setColorValue(colorType:String, value:Int) {
    val adjustedValue = (value.toDouble / 2.55).toInt

    setValue(colorType, adjustedValue);
  }

  /**
   * Get the activation threshold for the chromosome.
   * 
   * Note: When the slipnet node's activation reaches the activation threshold
   * it becomes possible for codelets to be produced.
   * 
   * @param slipnetNodeName
   *            - the name of the slipnet node to set the activation threshold
   *            for.
   */
  def getActivationThreshold(slipnetNodeName:String):Int = {
    val tag = ACTIVATION_THRESHOLD_TAG + "_" + slipnetNodeName;

    if (!doesBaseExist(tag)) {
      setValue(tag, 3);
    }

    return getValue(tag);
  }

  /**
   * Set the activation threshold for the chromosome.
   * 
   * <p>
   * Note: When the slipnet node's activation reaches the activation threshold
   * it becomes possible for codelets to be produced.
   * </p>
   * 
   * @param slipnetNodeName
   * @param value
   */
  def setActivationThreshold(slipnetNodeName:String, value:Int) {
    val tag = ACTIVATION_THRESHOLD_TAG + "_" + slipnetNodeName;

    setValue(tag, value);
  }

  /**
   * Get the movement amount.
   * 
   * <p>
   * Note: The movement amount is the amount the botcat moves in its
   * environment when the command to move is made.
   * </p>
   */
  def getMovementAmount():Double = {
    if (!doesBaseExist(MOVEMENT_AMOUNT_TAG)) {
      setValue(MOVEMENT_AMOUNT_TAG, 5);
    }

    val value = getValue(MOVEMENT_AMOUNT_TAG);

    adjustValue(MAXIMUM_MOVEMENT_AMOUNT,
        MINIMUM_MOVEMENT_AMOUNT, value);
  }

  /**
   * Get the turn amount.
   * 
   * <p>
   * Note: The turn amount is the amount the botcat turns in its environment
   * when the command to turn is made.
   * </p>
   */
  def getTurnAmount():Double = {
    if (!doesBaseExist(TURN_AMOUNT_TAG)) {
      setValue(TURN_AMOUNT_TAG, 3);
    }

    val value = getValue(TURN_AMOUNT_TAG);

    adjustValue(MAXIMUM_TURN_AMOUNT,
        MINIMUM_TURN_AMOUNT, value);
  }

  /**
   * The fire power must be in the range from 0.1 to 3
   * 
   * @return
   */
  def getFireAmount():Double = {
    if (!doesBaseExist(FIRE_AMOUNT_TAG)) {
      setValue(FIRE_AMOUNT_TAG, 1);
    }

    val value = getValue(FIRE_AMOUNT_TAG);

    adjustValue(Rules.MAX_BULLET_POWER,
        Rules.MIN_BULLET_POWER, value);
  }

  /**
   * 
   * initial started with 16 nodes
   * 
   * @return
   */
  def getNumberOfExtraNodes():Int = {
    if (!doesBaseExist(EXTRA_NODES_1)) {
      setValue(EXTRA_NODES_1, 0);
      setValue(EXTRA_NODES_2, 0);
      setValue(EXTRA_NODES_3, 0);
      setValue(EXTRA_NODES_4, 0);
    }

    val value1 = getValue(EXTRA_NODES_1);
    val value2 = getValue(EXTRA_NODES_2);
    val value3 = getValue(EXTRA_NODES_3);
    val value4 = getValue(EXTRA_NODES_4);

    val adjustedValue1 = adjustValue(25, 0, value1).toInt
    val adjustedValue2 = adjustValue(25, 0, value2).toInt
    val adjustedValue3 = adjustValue(25, 0, value3).toInt
    val adjustedValue4 = adjustValue(25, 0, value4).toInt

    return adjustedValue1 + adjustedValue2 + adjustedValue3
        + adjustedValue4;
  }

  def getEnergyLevelPeg(peg:Int ):Int = {
    val tag = ENERGY_LEVEL_PEG + "_" + peg;

    if (!doesBaseExist(tag)) {
      setValue(tag, 40);
    }

    return getValue(tag);
  }

  @Override
  def getMaximumBulletAge():Int = {
    if (!doesBaseExist(MAXIMUM_BULLET_AGE)) {
      setValue(MAXIMUM_BULLET_AGE, 60);
    }
    val value = getValue(MAXIMUM_BULLET_AGE);

    adjustValue(130, 10, value).toInt
  }
  
  @Override
  def getBulletAccuracySuccessOne():Int = {
    if (!doesBaseExist(BULLET_ACCURACY_SUCCESS_ONE)) {
      setValue(BULLET_ACCURACY_SUCCESS_ONE, 10);
    }
    val value = getValue(BULLET_ACCURACY_SUCCESS_ONE);

    adjustValue(10, 0, value).toInt
  }
  
  @Override
  def getBulletAccuracyFailureOne():Int = {
    if (!doesBaseExist(BULLET_ACCURACY_FAILURE_ONE)) {
      setValue(BULLET_ACCURACY_FAILURE_ONE, 10);
    }
    val value = getValue(BULLET_ACCURACY_FAILURE_ONE);

    (adjustValue(10, 0, value) * -1).toInt
  }
  
  def getBufferDistance():Int = {
    if (!doesBaseExist(BUFFER_DISTANCE)) {
      setValue(BUFFER_DISTANCE, 10);
    }

    val value = getValue(BUFFER_DISTANCE);

    adjustValue(300, 25, value).toInt
  }

  def getTargetDistance():Int = {
    if (!doesBaseExist(TARGET_DISTANCE)) {
      setValue(TARGET_DISTANCE, 10);
    }

    val value = getValue(TARGET_DISTANCE);

    adjustValue(800, 100, value).toInt
  }

  override def clone():SizableChromosome = {
    return new BotcatChromosome(this);
  }

  @Override
  def getCoderackBehaviorExecuteFactor() = 10

  @Override
  def getCoderackBehaviorReductionFactor() = 0.01

  @Override
  def getCoderackBehaviorSleepTime() = 10L

  @Override
  def getCoderackControlExecuteFactor() = 1

  @Override
  def getCoderackControlReductionFactor() = 0.01;

  @Override
  def getCoderackControlSleepTime() = 10L

  @Override
  def getSlipnetBehaviorExecuteFactor() = 10

  @Override
  def getSlipnetBehaviorReductionFactor() = 0.01

  @Override
  def getSlipnetBehaviorSleepTime() =  10L

  @Override
  def getSlipnetControlExecuteFactor() = 1

  @Override
  def getSlipnetControlReductionFactor() = 0.01

  @Override
  def getSlipnetControlSleepTime() = 10L

  @Override
  def getWorkspaceBehaviorExecuteFactor() = 20

  @Override
  def getWorkspaceBehaviorReductionFactor() = 0.01

  @Override
  def getWorkspaceBehaviorSleepTime() = 10L

  @Override
  def getWorkspaceControlExecuteFactor() = 1

  @Override
  def getWorkspaceControlReductionFactor() = 0.01

  @Override
  def getWorkspaceControlSleepTime() = 10L
}

object BotcatChromosome{
  val MINIMUM_TURN_AMOUNT = 1;

  val MAXIMUM_TURN_AMOUNT = 45;

  val MINIMUM_MOVEMENT_AMOUNT = 1;

  val MAXIMUM_MOVEMENT_AMOUNT = 50;

  val MINIMUM_COLOR_VALUE = 0;

  val MAXIMUM_COLOR_VALUE = 255;

  val BULLET_HIT = "bulletHit";
  val BULLET_MISS = "bulletMiss";
  
  val TURRET_FORWARD = "turretForward";
  val TURRET_BACKWARD = "turretBackward";
  val TURRET_RIGHT = "turretRight";
  val TURRET_LEFT = "turretLeft";
  
  val ENERGY_LOW_NODE_NAME = "energyLow";
  val ENERGY_HIGH_NODE_NAME = "energyHigh";
  val OBSTACLE_LEFT = "obstacleLeft";
  val OBSTACLE_RIGHT = "obstacleRight";
  val OBSTACLE_FRONT = "obstacleFront";
  val CLEAR_RIGHT = "clearRight";
  val CLEAR_LEFT = "clearLeft";
  val CLEAR_FOREWARD = "clearFront";
  val OBSTACLE_BACKWARD = "obstacleBackward";
  val CLEAR_BACKWARD = "clearBackward";

  val ORIENTATION_NORTH = "orientationNorth";
  val ORIENTATION_SOUTH = "orientationSouth";
  val ORIENTATION_WEST = "orientationWest";
  val ORIENTATION_EAST = "orientatinoEast";
  
  val TARGET_FORWARD = "targetForward";
  val TARGET_FORWARD_RIGHT = "targetForwardRight";
  val TARGET_RIGHT = "targetRight";
  val TARGET_BACKWARD_RIGHT = "targetBackwardRight";
  val TARGET_BACKWARD = "targetBackward";
  val TARGET_BACKWARD_LEFT = "targetBackwardLeft";
  val TARGET_LEFT = "targetLeft";
  val TARGET_FORWARD_LEFT = "targetForwardLeft";

  val MOVE_FORWARD = "moveForward";
  val FIRE = "fire";
  val DO_NOT_MOVE = "doNotMove";
  val MOVE_BACKWARD = "moveBackward";
  val TURN_RIGHT = "turnRight";
  val TURN_LEFT = "turnLeft";

  val TURN_TURRET_RIGHT = "turnTurretRight";
  val TURN_TURRET_LEFT = "turnTurretLeft";

  val EXTRA_NODE_TAG = "extraNode";
  val MOVEMENT_AMOUNT_TAG = "movementAmount";
  val TURN_AMOUNT_TAG = "turnAmount";
  val FIRE_AMOUNT_TAG = "fireAmount";

  val EXTRA_NODES_1 = "extraNodes1";
  val EXTRA_NODES_2 = "extraNodes2";
  val EXTRA_NODES_3 = "extraNodes3";
  val EXTRA_NODES_4 = "extraNodes4";

  val ENERGY_LEVEL_PEG = "energyLevelPeg";

  val TARGET_DISTANCE = "targetDistance";
  val BUFFER_DISTANCE = "bufferDistance";
  
  //new
  val BULLET_ACCURACY_SUCCESS_ONE = "bulletAccuracySuccessOne";
  val BULLET_ACCURACY_FAILURE_ONE = "bulletAccuracyFailureOne";
  val MAXIMUM_BULLET_AGE = "maximumBulletAge";

  // --------------------------------------------------------------------------
  // Static Private Data
  // --------------------------------------------------------------------------

  private val MEMORY_TAG = "memory";
  private val INITAL_ACTIVATION = "initalActivation";
  private val LINK_TAG = "link";
  private val ACTIVATION_THRESHOLD_TAG = "activationThreashold";
}