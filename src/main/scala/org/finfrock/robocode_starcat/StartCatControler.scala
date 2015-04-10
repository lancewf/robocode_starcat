package org.finfrock.robocode_starcat

import org.finfrock.starcat.coderack.Coderack
import org.finfrock.starcat.slipnet.Slipnet
import org.finfrock.starcat.core.StandardMetabolism

class StartCatControler(robot:BotCatable) {

  private val slipnetBuilder = new RobocodeSlipnetBuilder();
  private var coderack: Coderack = new Coderack()
  private var slipnet = slipnetBuilder.buildSlipnet(robot.getChromosome())
  private var workspace = new RobocodeWorkspace(robot)

  private val coderackMetabolism = new StandardMetabolism(coderack);
  private val slipnetMetabolism = new StandardMetabolism(slipnet);
  private val workspaceMetabolism = new StandardMetabolism(workspace);

  coderack.setMetabolism(coderackMetabolism)
  slipnet.setMetabolism(slipnetMetabolism)
  workspace.setMetabolism(workspaceMetabolism)

  // note: you add to a component the listener that will be listening for
  // codelets from that component (i.e. read it as "add me as a source of
  // codelets to the
  // listener named in the argument)
  slipnet.addCodeletEventListener(coderack)
  coderack.addCodeletEventListener(workspace)
  workspace.addCodeletEventListener(slipnet)

// -----------------------------------------------------------------------------
// #region Public Members
// -----------------------------------------------------------------------------
   
   /**
    * Starts all the starcat components' threads
    */
   def start() {
      coderack.start();
      workspace.start();
      slipnet.start();
   }
   
   /**
    * Stops all the starcat componets' threads
    */
   def stop() {
      coderack.stop();
      workspace.stop();
      slipnet.stop();
   }
}