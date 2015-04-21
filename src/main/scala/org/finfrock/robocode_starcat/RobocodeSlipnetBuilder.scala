package org.finfrock.robocode_starcat

import org.finfrock.robocode_starcat.genenticalgorithm.Chromosome
import org.finfrock.starcat.configuration.ParameterData
import org.finfrock.starcat.configuration.SlipnetBuilder
import org.finfrock.robocode_starcat.genenticalgorithm.BotcatChromosome
import java.util.UUID
import org.finfrock.robocode_starcat.codelets.FuzzyObstacleBehaviorCodelet
import org.finfrock.starcat.codelets.SlipnetNodeActorActivationRecipient
import akka.actor.ActorRef
import org.finfrock.robocode_starcat.codelets.PerformerBehaviorCodeletActor
import org.finfrock.robocode_starcat.codelets.FuzzyObstacleBehaviorCodelet
import org.finfrock.robocode_starcat.codelets.FuzzyObstacleBehaviorCodeletActor
import org.finfrock.robocode_starcat.codelets.TargetObserverBehaviorCodeletActor
import org.finfrock.robocode_starcat.codelets.EnergyLevelBehaviorCodletActor
import org.finfrock.robocode_starcat.codelets.EastBodyOrientationObserverCodeletActor
import org.finfrock.robocode_starcat.codelets.WestBodyOrientationObserverCodeletActor
import org.finfrock.robocode_starcat.codelets.NorthBodyOrientationObserverCodeletActor
import org.finfrock.robocode_starcat.codelets.SouthBodyOrientationObserverCodeletActor
import org.finfrock.robocode_starcat.codelets.RightTurretOrientationObserverCodeletActor
import org.finfrock.robocode_starcat.codelets.LeftTurretOrientationObserverCodeletActor
import org.finfrock.robocode_starcat.codelets.BackwardTurretOrientationObserverCodeletActor
import org.finfrock.robocode_starcat.codelets.ForwardTurretOrientationObserverCodeletActor
import org.finfrock.robocode_starcat.codelets.BulletHitMissBehaviorCodletActor
import akka.actor.ActorSystem

/**
 * @author lancewf
 */
class RobocodeSlipnetBuilder(system:ActorSystem) extends SlipnetBuilder(system) {
  // --------------------------------------------------------------------------
  // Private Static Data
  // --------------------------------------------------------------------------

  private val OBSERVER = "obstacleObserver"

  // -------------------------------------------------------------------------
  // Public Members
  // -------------------------------------------------------------------------

  def buildSlipnet(chromosome:Chromosome):ActorRef = {
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
      val memoryLevel = chromosome.getMemoryLevel(slipnetNode)

      //only the CodeletPreformers use this. 
      val activationThreashold =
        chromosome.getActivationThreshold(slipnetNode)

      val initalActivation = chromosome.getInitalActivation(slipnetNode)

      createSlipnetNode(slipnetNode, memoryLevel, initalActivation, activationThreashold)
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
    
    createCodelet(
        PerformerBehaviorCodeletActor.getProps(RobotActionType.FORWARD), urgency = 100, numberToEmit = 1,
      slipnetNodeName = BotcatChromosome.MOVE_FORWARD)

    createCodelet(
        PerformerBehaviorCodeletActor.getProps(RobotActionType.FIRE), urgency = 100, numberToEmit = 1,
      slipnetNodeName = BotcatChromosome.FIRE)
      
    createCodelet(
        PerformerBehaviorCodeletActor.getProps(RobotActionType.DONT_MOVE), urgency = 100, numberToEmit = 1,
      slipnetNodeName = BotcatChromosome.DO_NOT_MOVE)
      
    createCodelet(
        PerformerBehaviorCodeletActor.getProps(RobotActionType.BACKWARD), urgency = 100, numberToEmit = 1,
      slipnetNodeName = BotcatChromosome.MOVE_BACKWARD)

    createCodelet(
        PerformerBehaviorCodeletActor.getProps(RobotActionType.TURN_RIGHT), urgency = 100, numberToEmit = 1,
      slipnetNodeName = BotcatChromosome.TURN_RIGHT)

    createCodelet(
        PerformerBehaviorCodeletActor.getProps(RobotActionType.TURN_LEFT), urgency = 100, numberToEmit = 1,
     slipnetNodeName= BotcatChromosome.TURN_LEFT)
      
    createCodelet(
        PerformerBehaviorCodeletActor.getProps(RobotActionType.TURN_TURRET_RIGHT), urgency = 100, numberToEmit = 1,
      slipnetNodeName = BotcatChromosome.TURN_TURRET_RIGHT)
      
    createCodelet(
        PerformerBehaviorCodeletActor.getProps(RobotActionType.TURN_TURRET_LEFT), urgency = 100, numberToEmit = 1,
      slipnetNodeName = BotcatChromosome.TURN_TURRET_LEFT)
  }

  private def createCodeletObservers(chromosome: Chromosome) {
    val bufferDistance = chromosome.getBufferDistance();

    // configuration/codelet
    createCodelet(FuzzyObstacleBehaviorCodeletActor.getProps(
      successActivationRecipients = List(SlipnetNodeActorActivationRecipient(getSlipnetNode(BotcatChromosome.OBSTACLE_LEFT), 100)),
      failureActivationRecipients = List(SlipnetNodeActorActivationRecipient(getSlipnetNode(BotcatChromosome.CLEAR_LEFT), 100)),
      headingToLook = FuzzyObstacleBehaviorCodelet.LEFT, 
      bufferDistance = bufferDistance),
      urgency = 1, numberToEmit = 1, slipnetNodeName = OBSERVER)
      
    createCodelet(FuzzyObstacleBehaviorCodeletActor.getProps(
      successActivationRecipients = List(SlipnetNodeActorActivationRecipient(getSlipnetNode(BotcatChromosome.OBSTACLE_RIGHT), 100)),
      failureActivationRecipients = List(SlipnetNodeActorActivationRecipient(getSlipnetNode(BotcatChromosome.CLEAR_RIGHT), 100)),
      headingToLook = FuzzyObstacleBehaviorCodelet.RIGHT, 
      bufferDistance = bufferDistance),
      urgency = 1, numberToEmit = 1, slipnetNodeName = OBSERVER)
      
    createCodelet(FuzzyObstacleBehaviorCodeletActor.getProps(
      successActivationRecipients = List(SlipnetNodeActorActivationRecipient(getSlipnetNode(BotcatChromosome.OBSTACLE_FRONT), 100)),
      failureActivationRecipients = List(SlipnetNodeActorActivationRecipient(getSlipnetNode(BotcatChromosome.CLEAR_FOREWARD), 100)),
      headingToLook = FuzzyObstacleBehaviorCodelet.FORWARD, 
      bufferDistance = bufferDistance),
      urgency = 1, numberToEmit = 1, slipnetNodeName = OBSERVER)
      
    createCodelet(FuzzyObstacleBehaviorCodeletActor.getProps(
      successActivationRecipients = List(SlipnetNodeActorActivationRecipient(getSlipnetNode(BotcatChromosome.OBSTACLE_BACKWARD), 100)),
      failureActivationRecipients = List(SlipnetNodeActorActivationRecipient(getSlipnetNode(BotcatChromosome.CLEAR_BACKWARD), 100)),
      headingToLook = FuzzyObstacleBehaviorCodelet.BACKWARD, 
      bufferDistance = bufferDistance),
      urgency = 1, numberToEmit = 1, slipnetNodeName= OBSERVER)

    val targetDistance = chromosome.getTargetDistance()
    
    // Target Observers
    createCodelet(TargetObserverBehaviorCodeletActor.getProps(
      successActivationRecipients = List(SlipnetNodeActorActivationRecipient(getSlipnetNode(BotcatChromosome.TARGET_FORWARD), 100)),
      headingToLook = TargetObserverBehaviorCodeletActor.FORWARD, 
      targetDistance = targetDistance),
      urgency = 1, numberToEmit = 1, slipnetNodeName = OBSERVER)
      
    createCodelet(TargetObserverBehaviorCodeletActor.getProps(
      successActivationRecipients = List(SlipnetNodeActorActivationRecipient(getSlipnetNode(BotcatChromosome.TARGET_FORWARD_RIGHT), 100)),
      headingToLook = TargetObserverBehaviorCodeletActor.FORWARD_RIGHT, 
      targetDistance = targetDistance),
      urgency = 1, numberToEmit = 1, slipnetNodeName = OBSERVER)
      
    createCodelet(TargetObserverBehaviorCodeletActor.getProps(
      successActivationRecipients = List(SlipnetNodeActorActivationRecipient(getSlipnetNode(BotcatChromosome.TARGET_RIGHT), 100)),
      headingToLook = TargetObserverBehaviorCodeletActor.RIGHT, 
      targetDistance = targetDistance),
      urgency = 1, numberToEmit = 1, slipnetNodeName = OBSERVER)
      
    createCodelet(TargetObserverBehaviorCodeletActor.getProps(
      successActivationRecipients = List(SlipnetNodeActorActivationRecipient(getSlipnetNode(BotcatChromosome.TARGET_BACKWARD_RIGHT), 100)),
      headingToLook = TargetObserverBehaviorCodeletActor.BACKWARD_RIGHT, 
      targetDistance = targetDistance),
      urgency = 1, numberToEmit = 1, slipnetNodeName = OBSERVER)
      
    createCodelet(TargetObserverBehaviorCodeletActor.getProps(
      successActivationRecipients = List(SlipnetNodeActorActivationRecipient(getSlipnetNode(BotcatChromosome.TARGET_BACKWARD), 100)),
      headingToLook = TargetObserverBehaviorCodeletActor.BACKWARD, 
      targetDistance = targetDistance),
      urgency = 1, numberToEmit = 1, slipnetNodeName = OBSERVER)

    createCodelet(TargetObserverBehaviorCodeletActor.getProps(
      successActivationRecipients = List(SlipnetNodeActorActivationRecipient(getSlipnetNode(BotcatChromosome.TARGET_BACKWARD_LEFT), 100)),
      headingToLook = TargetObserverBehaviorCodeletActor.BACKWARD_LEFT, 
      targetDistance = targetDistance),
      urgency = 1, numberToEmit = 1, slipnetNodeName = OBSERVER)

    createCodelet(TargetObserverBehaviorCodeletActor.getProps(
      successActivationRecipients = List(SlipnetNodeActorActivationRecipient(getSlipnetNode(BotcatChromosome.TARGET_LEFT), 100)),
      headingToLook = TargetObserverBehaviorCodeletActor.LEFT, 
      targetDistance = targetDistance),
      urgency = 1, numberToEmit = 1, slipnetNodeName = OBSERVER)
      

    createCodelet(TargetObserverBehaviorCodeletActor.getProps(
      successActivationRecipients = List(SlipnetNodeActorActivationRecipient(getSlipnetNode(BotcatChromosome.TARGET_FORWARD_LEFT), 100)),
      headingToLook = TargetObserverBehaviorCodeletActor.FORWARD_LEFT, 
      targetDistance = targetDistance),
      urgency = 1, numberToEmit = 1, slipnetNodeName = OBSERVER)

    val value0 = chromosome.getEnergyLevelPeg(0)
    val value1 = chromosome.getEnergyLevelPeg(1)
    val value2 = chromosome.getEnergyLevelPeg(2)
    val value3 = chromosome.getEnergyLevelPeg(3)
    
    createCodelet(EnergyLevelBehaviorCodletActor.getProps(
      value0, value1, value2, value3,
      successActivationRecipients = List(SlipnetNodeActorActivationRecipient(getSlipnetNode(BotcatChromosome.ENERGY_LOW_NODE_NAME), 100)),
      failureActivationRecipients = List(SlipnetNodeActorActivationRecipient(getSlipnetNode(BotcatChromosome.ENERGY_HIGH_NODE_NAME), 100))),
      urgency = 1, numberToEmit = 1, slipnetNodeName = OBSERVER)

    //Orientation
      
    createCodelet(EastBodyOrientationObserverCodeletActor.getProps(
      successActivationRecipients = List(SlipnetNodeActorActivationRecipient(getSlipnetNode(BotcatChromosome.ORIENTATION_EAST), 100))),
      urgency = 1, numberToEmit = 1, slipnetNodeName = OBSERVER)
      
    createCodelet(WestBodyOrientationObserverCodeletActor.getProps(
      successActivationRecipients = List(SlipnetNodeActorActivationRecipient(getSlipnetNode(BotcatChromosome.ORIENTATION_WEST), 100))),
      urgency = 1, numberToEmit = 1, slipnetNodeName = OBSERVER)
      
    createCodelet(NorthBodyOrientationObserverCodeletActor.getProps(
      successActivationRecipients = List(SlipnetNodeActorActivationRecipient(getSlipnetNode(BotcatChromosome.ORIENTATION_NORTH), 100))),
      urgency = 1, numberToEmit = 1, slipnetNodeName = OBSERVER)
      
    createCodelet(SouthBodyOrientationObserverCodeletActor.getProps(
      successActivationRecipients = List(SlipnetNodeActorActivationRecipient(getSlipnetNode(BotcatChromosome.ORIENTATION_SOUTH), 100))),
      urgency = 1, numberToEmit = 1, slipnetNodeName = OBSERVER)

    createCodelet(RightTurretOrientationObserverCodeletActor.getProps(
      successActivationRecipients = List(SlipnetNodeActorActivationRecipient(getSlipnetNode(BotcatChromosome.TURRET_RIGHT), 100))),
      urgency = 1, numberToEmit = 1, slipnetNodeName = OBSERVER)

    createCodelet(LeftTurretOrientationObserverCodeletActor.getProps(
      successActivationRecipients = List(SlipnetNodeActorActivationRecipient(getSlipnetNode(BotcatChromosome.TURRET_LEFT), 100))),
      urgency = 1, numberToEmit = 1, slipnetNodeName = OBSERVER)

    createCodelet(BackwardTurretOrientationObserverCodeletActor.getProps(
      successActivationRecipients = List(SlipnetNodeActorActivationRecipient(getSlipnetNode(BotcatChromosome.TURRET_BACKWARD), 100))),
      urgency = 1, numberToEmit = 1, slipnetNodeName = OBSERVER)
      
    createCodelet(ForwardTurretOrientationObserverCodeletActor.getProps(
      successActivationRecipients = List(SlipnetNodeActorActivationRecipient(getSlipnetNode(BotcatChromosome.TURRET_FORWARD), 100))),
      urgency = 1, numberToEmit = 1, slipnetNodeName = OBSERVER)

    val bulletAccuracySuccessOne = chromosome.getBulletAccuracySuccessOne();
    val bulletAccuracyFailureOne = chromosome.getBulletAccuracyFailureOne();

    createCodelet(BulletHitMissBehaviorCodletActor.getProps(
      bulletAccuracySuccessOne,
      bulletAccuracyFailureOne,
      successActivationRecipients = List(SlipnetNodeActorActivationRecipient(getSlipnetNode(BotcatChromosome.BULLET_HIT), 100)),
      failureActivationRecipients = List(SlipnetNodeActorActivationRecipient(getSlipnetNode(BotcatChromosome.BULLET_MISS), 100))),
      urgency = 1, numberToEmit = 1, slipnetNodeName = OBSERVER)
  }
}