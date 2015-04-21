package org.finfrock.robocode_starcat

import java.io.File
import org.finfrock.robocode_starcat.genenticalgorithm.Individual
import org.finfrock.robocode_starcat.genenticalgorithm.BotcatChromosome
import org.finfrock.robocode_starcat.genenticalgorithm.fitnesstest.InfightingFitnessTest
import org.finfrock.robocode_starcat.genenticalgorithm.GenenticAlgorithm
import org.finfrock.robocode_starcat.genenticalgorithm.GenerationRunner
import org.finfrock.robocode_starcat.genenticalgorithm.GenenticUI

object Main {
  private val INITIAL_POPULATION_SIZE = 50;

  def main(args: Array[String]) {
    startGenenticAlgorithm(args)
  }
    

  private def startGenenticAlgorithm(args: Array[String]){
    if (args.length != 1) {
      System.err.println("args.length: " + args.length)
      return ;
    }

    val initalAgent = new File(args(0))

    if (!initalAgent.exists()) {
      System.err.println("inital agent not found: " + args(0))
      return ;
    }

    new PropertyUtilities().readInProperties(initalAgent) match {
      case Some(properties) => {
        val initialChromosome = new BotcatChromosome(properties)

        val initialIndividual = new Individual(initialChromosome)

        val listener = new BotCatListener()

        val test = new InfightingFitnessTest(listener)
        //      IFitnessTest test = new DummyFitnessTest();

        val genenticAlgorithm = new GenenticAlgorithm(
          INITIAL_POPULATION_SIZE, test, initialIndividual)

        val generationRunner = new GenerationRunner(
          genenticAlgorithm)

        val ui = new GenenticUI(generationRunner, test);

        genenticAlgorithm.setProgress(ui.getProgressBar())

        ui.setVisible(true);
      }
      case None => throw new Exception("property file not read in: " + args(0))
    }
  }
}