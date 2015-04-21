package org.finfrock.robocode_starcat

import org.finfrock.starcat.core.StandardMetabolism
import akka.actor.ActorSystem
import akka.actor.Props
import org.finfrock.robocode_starcat.genenticalgorithm.Chromosome
import robocode.AdvancedRobot
import akka.actor.TypedActor
import akka.actor.TypedProps
import org.finfrock.starcat.core.Metabolism
import org.finfrock.starcat.core.Component
import akka.actor.ActorRef
import org.finfrock.starcat.core.SlipnetBehaviorRegularPulse
import org.finfrock.starcat.core.SlipnetControlRegularPulse
import org.finfrock.starcat.core.CoderackBehaviorRegularPulse
import org.finfrock.starcat.core.CoderackControlRegularPulse
import org.finfrock.starcat.core.WorkspaceBehaviorRegularPulse
import org.finfrock.starcat.core.WorkspaceControlRegularPulse
import org.finfrock.starcat.coderack.CoderackActor

class StartCatControler(robot: AdvancedRobot,
                        chromosome: Chromosome) {

  private val system = ActorSystem("Starcat")
  val botCatable = TypedActor(system).typedActorOf(TypedProps(classOf[BotCatable],
    new BotCatableImpl(robot, chromosome)), "BotCatable")
    
  private val slipnetBuilder = new RobocodeSlipnetBuilder(system)
  private val coderack = system.actorOf(CoderackActor.getProps(), "Coderack")
  private val slipnet = slipnetBuilder.buildSlipnet(chromosome)
  private val workspace = system.actorOf(RobocodeWorkspaceActor.getProps(botCatable), "RobocodeWorkspace")

  buildCoderackStandardMetabolism(coderack)
  buildSlipnetStandardMetabolism(slipnet)
  buildWorkspaceStandardMetabolism(workspace)

  // note: you add to a component the listener that will be listening for
  // codelets from that component (i.e. read it as "add me as a source of
  // codelets to the
  // listener named in the argument)
  slipnet ! Component.AddCodeletEventListener(coderack)
  coderack ! Component.AddCodeletEventListener(workspace)
  workspace ! Component.AddCodeletEventListener(slipnet)

  // -----------------------------------------------------------------------------
  // #region Public Members
  // -----------------------------------------------------------------------------

  /**
   * Starts all the starcat components' threads
   */
  def start() {
    coderack ! Metabolism.Start
    workspace ! Metabolism.Start
    slipnet ! Metabolism.Start
  }

  /**
   * Stops all the starcat componets' threads
   */
  def stop() {
    coderack ! Metabolism.Stop
    workspace ! Metabolism.Stop
    slipnet ! Metabolism.Stop
    system.shutdown()
  }

  private def buildSlipnetStandardMetabolism(slipnet: ActorRef) {
    val slipnetBehaviorRegularPulse =
      system.actorOf(SlipnetBehaviorRegularPulse.getProps(slipnet), "SlipnetBehaviorRegularPulse")

    val slipnetControlRegularPulse =
      system.actorOf(SlipnetControlRegularPulse.getProps(slipnet), "SlipnetControlRegularPulse")

    val slipnetMetabolism = system.actorOf(StandardMetabolism.getProps(slipnet, slipnetBehaviorRegularPulse, slipnetControlRegularPulse),
      "SlipnetStandardMetabolism")
      
    slipnet ! Component.SetMetabolism(slipnetMetabolism)
  }

  private def buildWorkspaceStandardMetabolism(workspace: ActorRef) {
    val workspaceBehaviorRegularPulse =
      system.actorOf(WorkspaceBehaviorRegularPulse.getProps(workspace), "WorkspaceBehaviorRegularPulse")

    val workspaceControlRegularPulse =
      system.actorOf(WorkspaceControlRegularPulse.getProps(workspace), "WorkspaceControlRegularPulse")

    val workspaceMetabolism = system.actorOf(StandardMetabolism.getProps(workspace, workspaceBehaviorRegularPulse, workspaceControlRegularPulse),
      "WorkspaceStandardMetabolism")
      
    workspace ! Component.SetMetabolism(workspaceMetabolism)
  }

  private def buildCoderackStandardMetabolism(coderack: ActorRef) {
    val coderackBehaviorRegularPulse =
      system.actorOf(CoderackBehaviorRegularPulse.getProps(coderack), "CoderackBehaviorRegularPulse")

    val coderackControlRegularPulse =
      system.actorOf(CoderackControlRegularPulse.getProps(coderack), "CoderackControlRegularPulse")

    val coderackMetabolism= system.actorOf(StandardMetabolism.getProps(coderack, coderackBehaviorRegularPulse, coderackControlRegularPulse),
      "CoderackStandardMetabolism")
      
    coderack ! Component.SetMetabolism(coderackMetabolism)
  }
}