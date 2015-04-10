package org.finfrock.robocode_starcat

import org.finfrock.robocode_starcat.genenticalgorithm.Chromosome
import org.finfrock.starcat.configuration.ParameterData
import org.finfrock.starcat.configuration.SlipnetBuilder
import org.finfrock.robocode_starcat.codelets.PerformerBehaviorCodelet
import org.finfrock.robocode_starcat.genenticalgorithm.BotcatChromosome
import java.util.UUID
import org.finfrock.robocode_starcat.codelets.FuzzyObstacleBehaviorCodelet
import org.finfrock.starcat.codelets.SlipnetNodeActivationRecipient
import org.finfrock.robocode_starcat.codelets.TargetObserverBehaviorCodelet
import org.finfrock.robocode_starcat.codelets.EnergyLevelBehaviorCodlet
import org.finfrock.robocode_starcat.codelets.EastBodyOrientationObserverCodelet
import org.finfrock.robocode_starcat.codelets.WestBodyOrientationObserverCodelet
import org.finfrock.robocode_starcat.codelets.NorthBodyOrientationObserverCodelet
import org.finfrock.robocode_starcat.codelets.SouthBodyOrientationObserverCodelet
import org.finfrock.robocode_starcat.codelets.RightTurretOrientationObserverCodelet
import org.finfrock.robocode_starcat.codelets.LeftTurretOrientationObserverCodelet
import org.finfrock.robocode_starcat.codelets.BackwardTurretOrientationObserverCodelet
import org.finfrock.robocode_starcat.codelets.ForwardTurretOrientationObserverCodelet
import org.finfrock.robocode_starcat.codelets.BulletHitMissBehaviorCodlet
import org.finfrock.starcat.slipnet.Slipnet

/**
 * @author lancewf
 */
class RobocodeSlipnetBuilder extends SlipnetBuilder {
  // --------------------------------------------------------------------------
  // Private Static Data
  // --------------------------------------------------------------------------

  private val OBSERVER = "obstacleObserver"

  // -------------------------------------------------------------------------
  // Public Members
  // -------------------------------------------------------------------------

  def buildSlipnet(chromosome:Chromosome):Slipnet = {
    initializeSystemConfigurations(chromosome)
    createSlipnetNodes(chromosome)
    createLinks(chromosome)
    createCodelets(chromosome)
    
    getSlipnet()
  }
  // --------------------------------------------------------------------------
  // Protected Overridden Methods
  // --------------------------------------------------------------------------

  /**
   * Create all the slipnet nodes for the system
   */
  protected def createSlipnetNodes(chromosome: Chromosome) {
    createSlipnetNode(OBSERVER, 100, 99, 1);

    for (slipnetNode <- chromosome.getSlipnetNodeList()) {
      val memoryLevel = chromosome.getMemoryLevel(slipnetNode);

      //only the CodeletPreformers use this. 
      val activationThreashold =
        chromosome.getActivationThreshold(slipnetNode);

      val initalActivation = chromosome.getInitalActivation(slipnetNode);

      createSlipnetNode(slipnetNode,
        memoryLevel, initalActivation, activationThreashold);
    }
  }

  /**
   * Create all the links for the system
   */
  protected def createLinks(chromosome: Chromosome) {
    //link length must be from 0 - 100 integer
    for (fromSlipnetNode <- chromosome.getSlipnetNodeList()) {
      for (toSlipnetNode <- chromosome.getSlipnetNodeList()) {
        val value = chromosome.getLinkLength(fromSlipnetNode, toSlipnetNode);

        // A link with length of 100 is the same as no link at all
        // to save process time do not create a link
        if (value < 100) {
          createLink(fromSlipnetNode, toSlipnetNode, value);
        }
      }
    }
  }

  /**
   * Create all the codelets for the system
   */
  protected def createCodelets(chromosome: Chromosome) {
    createCodeletObservers(chromosome)

    createCodeletPreformers(chromosome)
  }

  /**
   * Initialize the system configurations
   */
  protected def initializeSystemConfigurations(chromosome: Chromosome) {
    // Adaptive Execute methods
    ParameterData.initializeWorkspaceBehaviorAdaptiveExecute(false)
    ParameterData.initializeWorkspaceControlAdaptiveExecute(false)
    ParameterData.initializeCoderackBehaviorAdaptiveExecute(false);
    ParameterData.initializeCoderackControlAdaptiveExecute(false);
    ParameterData.initializeSlipnetBehaviorAdaptiveExecute(false);
    ParameterData.initializeSlipnetControlAdaptiveExecute(false);

    // Execute Factor methods
    ParameterData.initializeWorkspaceBehaviorExecuteFactor(chromosome
      .getWorkspaceBehaviorExecuteFactor());
    ParameterData.initializeWorkspaceControlExecuteFactor(chromosome
      .getWorkspaceControlExecuteFactor());

    ParameterData.initializeCoderackBehaviorExecuteFactor(chromosome
      .getCoderackBehaviorExecuteFactor());

    ParameterData.initializeCoderackControlExecuteFactor(chromosome
      .getCoderackControlExecuteFactor());

    ParameterData.initializeSlipnetBehaviorExecuteFactor(chromosome
      .getSlipnetBehaviorExecuteFactor());

    ParameterData.initializeSlipnetControlExecuteFactor(chromosome
      .getSlipnetControlExecuteFactor());

    // Reduction factor methods
    ParameterData.initializeWorkspaceBehaviorReductionFactor(chromosome
      .getWorkspaceBehaviorReductionFactor());

    ParameterData.initializeWorkspaceControlReductionFactor(chromosome
      .getWorkspaceControlReductionFactor());

    ParameterData.initializeCoderackBehaviorReductionFactor(chromosome
      .getCoderackBehaviorReductionFactor());

    ParameterData.initializeCoderackControlReductionFactor(chromosome
      .getCoderackControlReductionFactor());

    ParameterData.initializeSlipnetBehaviorReductionFactor(chromosome
      .getSlipnetBehaviorReductionFactor());

    ParameterData.initializeSlipnetControlReductionFactor(chromosome
      .getSlipnetControlReductionFactor());

    // Sleeper methods
    ParameterData.initializeWorkspaceBehaviorSleeper(true);
    ParameterData.initializeWorkspaceControlSleeper(true);
    ParameterData.initializeCoderackBehaviorSleeper(true);
    ParameterData.initializeCoderackControlSleeper(true);
    ParameterData.initializeSlipnetBehaviorSleeper(true);
    ParameterData.initializeSlipnetControlSleeper(true);

    // Sleep time methods
    ParameterData.initializeWorkspaceBehaviorSleepTime(chromosome
      .getWorkspaceBehaviorSleepTime());

    ParameterData.initializeWorkspaceControlSleepTime(chromosome
      .getWorkspaceControlSleepTime());

    ParameterData.initializeCoderackBehaviorSleepTime(chromosome
      .getCoderackBehaviorSleepTime());

    ParameterData.initializeCoderackControlSleepTime(chromosome
      .getCoderackControlSleepTime());

    ParameterData.initializeSlipnetBehaviorSleepTime(chromosome
      .getSlipnetBehaviorSleepTime());

    ParameterData.initializeSlipnetControlSleepTime(chromosome
      .getSlipnetControlSleepTime());
  }

  // --------------------------------------------------------------------------
  // Private Members
  // --------------------------------------------------------------------------

  private def createCodeletPreformers(chromosome: Chromosome) {
    createCodelet(new PerformerBehaviorCodelet(robotAction = RobotActionType.FORWARD),
      BotcatChromosome.MOVE_FORWARD);

    createCodelet(new PerformerBehaviorCodelet(robotAction = RobotActionType.FIRE),
      BotcatChromosome.FIRE);

    createCodelet(new PerformerBehaviorCodelet(robotAction = RobotActionType.DONT_MOVE),
      BotcatChromosome.DO_NOT_MOVE);

    createCodelet(new PerformerBehaviorCodelet(robotAction = RobotActionType.BACKWARD),
      BotcatChromosome.MOVE_BACKWARD);

    createCodelet(
      new PerformerBehaviorCodelet(robotAction = RobotActionType.TURN_RIGHT),
      BotcatChromosome.TURN_RIGHT);

    createCodelet(new PerformerBehaviorCodelet(robotAction = RobotActionType.TURN_LEFT),
      BotcatChromosome.TURN_LEFT);

    createCodelet(new PerformerBehaviorCodelet(robotAction = RobotActionType.TURN_TURRET_RIGHT),
      BotcatChromosome.TURN_TURRET_RIGHT);

    createCodelet(new PerformerBehaviorCodelet(robotAction = RobotActionType.TURN_TURRET_LEFT),
      BotcatChromosome.TURN_TURRET_LEFT);
  }

  private def createCodeletObservers(chromosome: Chromosome) {
    val bufferDistance = chromosome.getBufferDistance();

    // configuration/codelet
    createCodelet(new FuzzyObstacleBehaviorCodelet(
      successActivationRecipients = List(SlipnetNodeActivationRecipient(getSlipnetNode(BotcatChromosome.OBSTACLE_LEFT), 100)),
      failureActivationRecipients = List(SlipnetNodeActivationRecipient(getSlipnetNode(BotcatChromosome.CLEAR_LEFT), 100)),
      FuzzyObstacleBehaviorCodelet.LEFT, bufferDistance), sourceNode = OBSERVER)

    createCodelet(new FuzzyObstacleBehaviorCodelet(
      successActivationRecipients = List(SlipnetNodeActivationRecipient(getSlipnetNode(BotcatChromosome.OBSTACLE_RIGHT), 100)),
      failureActivationRecipients = List(SlipnetNodeActivationRecipient(getSlipnetNode(BotcatChromosome.CLEAR_RIGHT), 100)),
      FuzzyObstacleBehaviorCodelet.RIGHT, bufferDistance),
      OBSERVER)

    createCodelet(new FuzzyObstacleBehaviorCodelet(
      successActivationRecipients = List(SlipnetNodeActivationRecipient(getSlipnetNode(BotcatChromosome.OBSTACLE_FRONT), 100)),
      failureActivationRecipients = List(SlipnetNodeActivationRecipient(getSlipnetNode(BotcatChromosome.CLEAR_FOREWARD), 100)),
      FuzzyObstacleBehaviorCodelet.FORWARD, bufferDistance),
      OBSERVER)
    createCodelet(new FuzzyObstacleBehaviorCodelet(
      successActivationRecipients = List(SlipnetNodeActivationRecipient(getSlipnetNode(BotcatChromosome.OBSTACLE_BACKWARD), 100)),
      failureActivationRecipients = List(SlipnetNodeActivationRecipient(getSlipnetNode(BotcatChromosome.CLEAR_BACKWARD), 100)),
      FuzzyObstacleBehaviorCodelet.BACKWARD, bufferDistance),
      OBSERVER);

    val targetDistance = chromosome.getTargetDistance();

    // Target Observers
    createCodelet(new TargetObserverBehaviorCodelet(
      successActivationRecipients = List(SlipnetNodeActivationRecipient(getSlipnetNode(BotcatChromosome.TARGET_FORWARD), 100)),
      TargetObserverBehaviorCodelet.FORWARD, targetDistance),
      OBSERVER)
    createCodelet(new TargetObserverBehaviorCodelet(
      successActivationRecipients = List(SlipnetNodeActivationRecipient(getSlipnetNode(BotcatChromosome.TARGET_FORWARD_RIGHT), 100)),
      TargetObserverBehaviorCodelet.FORWARD_RIGHT, targetDistance),
      OBSERVER)
    createCodelet(new TargetObserverBehaviorCodelet(
      successActivationRecipients = List(SlipnetNodeActivationRecipient(getSlipnetNode(BotcatChromosome.TARGET_RIGHT), 100)),
      TargetObserverBehaviorCodelet.RIGHT, targetDistance),
      OBSERVER)
    createCodelet(new TargetObserverBehaviorCodelet(
      successActivationRecipients = List(SlipnetNodeActivationRecipient(getSlipnetNode(BotcatChromosome.TARGET_BACKWARD_RIGHT), 100)),
      TargetObserverBehaviorCodelet.BACKWARD_RIGHT, targetDistance),
      OBSERVER)
    createCodelet(new TargetObserverBehaviorCodelet(
      successActivationRecipients = List(SlipnetNodeActivationRecipient(getSlipnetNode(BotcatChromosome.TARGET_BACKWARD), 100)),
      TargetObserverBehaviorCodelet.BACKWARD, targetDistance),
      OBSERVER)
    createCodelet(new TargetObserverBehaviorCodelet(
      successActivationRecipients = List(SlipnetNodeActivationRecipient(getSlipnetNode(BotcatChromosome.TARGET_BACKWARD_LEFT), 100)),
      TargetObserverBehaviorCodelet.BACKWARD_LEFT, targetDistance),
      OBSERVER)
    createCodelet(new TargetObserverBehaviorCodelet(
      successActivationRecipients = List(SlipnetNodeActivationRecipient(getSlipnetNode(BotcatChromosome.TARGET_LEFT), 100)),
      TargetObserverBehaviorCodelet.LEFT, targetDistance),
      OBSERVER)
    createCodelet(new TargetObserverBehaviorCodelet(
      successActivationRecipients = List(SlipnetNodeActivationRecipient(getSlipnetNode(BotcatChromosome.TARGET_FORWARD_LEFT), 100)),
      TargetObserverBehaviorCodelet.FORWARD_LEFT, targetDistance),
      OBSERVER)

    val value0 = chromosome.getEnergyLevelPeg(0);
    val value1 = chromosome.getEnergyLevelPeg(1);
    val value2 = chromosome.getEnergyLevelPeg(2);
    val value3 = chromosome.getEnergyLevelPeg(3);

    createCodelet(new EnergyLevelBehaviorCodlet(
      value0, value1, value2, value3,
      successActivationRecipients = List(SlipnetNodeActivationRecipient(getSlipnetNode(BotcatChromosome.ENERGY_LOW_NODE_NAME), 100)),
      failureActivationRecipients = List(SlipnetNodeActivationRecipient(getSlipnetNode(BotcatChromosome.ENERGY_HIGH_NODE_NAME), 100))), 
      OBSERVER)

    //Orientation
    createCodelet(new EastBodyOrientationObserverCodelet(
      successActivationRecipients = List(SlipnetNodeActivationRecipient(getSlipnetNode(BotcatChromosome.ORIENTATION_EAST), 100))),
      OBSERVER)

    createCodelet(new WestBodyOrientationObserverCodelet(
      successActivationRecipients = List(SlipnetNodeActivationRecipient(getSlipnetNode(BotcatChromosome.ORIENTATION_WEST), 100))),
      OBSERVER)

    createCodelet(new NorthBodyOrientationObserverCodelet(
      successActivationRecipients = List(SlipnetNodeActivationRecipient(getSlipnetNode(BotcatChromosome.ORIENTATION_NORTH), 100))),
      OBSERVER)

    createCodelet(new SouthBodyOrientationObserverCodelet(
      successActivationRecipients = List(SlipnetNodeActivationRecipient(getSlipnetNode(BotcatChromosome.ORIENTATION_SOUTH), 100))),
      OBSERVER)

    createCodelet(new RightTurretOrientationObserverCodelet(
      successActivationRecipients = List(SlipnetNodeActivationRecipient(getSlipnetNode(BotcatChromosome.TURRET_RIGHT), 100))),
      OBSERVER)

    createCodelet(new LeftTurretOrientationObserverCodelet(
      successActivationRecipients = List(SlipnetNodeActivationRecipient(getSlipnetNode(BotcatChromosome.TURRET_LEFT), 100))),
      OBSERVER)

    createCodelet(new BackwardTurretOrientationObserverCodelet(
      successActivationRecipients = List(SlipnetNodeActivationRecipient(getSlipnetNode(BotcatChromosome.TURRET_BACKWARD), 100))),
      OBSERVER)

    createCodelet(new ForwardTurretOrientationObserverCodelet(
      successActivationRecipients = List(SlipnetNodeActivationRecipient(getSlipnetNode(BotcatChromosome.TURRET_FORWARD), 100))),
      OBSERVER)

    val bulletAccuracySuccessOne = chromosome.getBulletAccuracySuccessOne();
    val bulletAccuracyFailureOne = chromosome.getBulletAccuracyFailureOne();

    createCodelet(new BulletHitMissBehaviorCodlet(bulletAccuracySuccessOne,
      bulletAccuracyFailureOne,
      successActivationRecipients = List(SlipnetNodeActivationRecipient(getSlipnetNode(BotcatChromosome.BULLET_HIT), 100)),
      failureActivationRecipients = List(SlipnetNodeActivationRecipient(getSlipnetNode(BotcatChromosome.BULLET_MISS), 100))),
      OBSERVER)
  }
}