package org.finfrock.robocode_starcat.genenticalgorithm.fitnesstest

import org.finfrock.robocode_starcat.BotCatListener
import org.finfrock.robocode_starcat.genenticalgorithm.Individual
import java.io.File
import org.finfrock.robocode_starcat.genenticalgorithm.BotcatChromosome
import org.finfrock.robocode_starcat.PropertyUtilities
import robocode.control.BattlefieldSpecification
import robocode.control.RobocodeEngine
import robocode.control.BattleSpecification
import robocode.control.RobotSpecification

class InfightingFitnessTest(listener:BotCatListener) extends IFitnessTest {
  // ----------------------------------------------------------------------
  // Private Data
  // ----------------------------------------------------------------------

  val BOTCAT_NAME = "org.robocode.BotCat";
  val BOTCAT_2_NAME = "org.robocode.BotCat2";
  private val fitnessTestProperties = new FitnessTestProperties()
  private lazy val engine:RobocodeEngine = new RobocodeEngine(listener)

  // ----------------------------------------------------------------------
  // Public Members
  // ----------------------------------------------------------------------

  def run(individual:Individual) {
    val individualFile = new File(
        fitnessTestProperties.getBotCatPropertiesPath());
    individual.save(individualFile);
    val bestIndividualFile = new File(
        fitnessTestProperties.getBotCat2PropertiesPath());
    getBestIndividual().save(bestIndividualFile)

    runBattle()

    val score = getScore()

    individual.setFitnessScore(score)
  }

  // ----------------------------------------------------------------------
  // Private Members
  // ----------------------------------------------------------------------

  private def getBestIndividual():Individual = {
    val bestBotFile = new File(
        "/home/lancewf/robocode/bestAgent.properties");
    
    val bestBotChromosome = new PropertyUtilities().readInProperties(bestBotFile) match{
      case Some(properties) => new BotcatChromosome(properties)
    }
     
    val bestBotIndividual = new Individual(bestBotChromosome);

    return bestBotIndividual;
  }
  
  private def getScore():Int = {
    val robotResults = listener.getRobotResults();

    if (robotResults != null) {
      val fitnessScore = robotResults.getScore();
      println("Fitness = " + fitnessScore + " Total Score = "
          + robotResults.getScore() + " Bullet = "
          + robotResults.getBulletDamage() + " Ram = "
          + robotResults.getRamDamage());

      return fitnessScore;
    } else {
      System.out.println("error in set Score the results was not found");
      return 0;
    }
  }

  private def runBattle() {
    val battleField = new BattlefieldSpecification(
        fitnessTestProperties.getBattlefieldWidth(),
        fitnessTestProperties.getBattlefieldHeight());

    listener.setEngine(engine);

    val robots = getRobots(engine, fitnessTestProperties);

    val battle = new BattleSpecification(
        fitnessTestProperties.getNumberOfRound(),
        fitnessTestProperties.getInactivityTime(), 0.1, battleField,
        robots);

    listener.runBattle(battle)

    System.gc();
  }



  private def getRobots(engine:RobocodeEngine,
      fitnessTestProperties:FitnessTestProperties):Array[RobotSpecification] = {
    val opponents = fitnessTestProperties.getOpponentsNames();

    var opponentsNames = "";

    for (opponentName <- opponents) {
      opponentsNames += opponentName + "*,";
    }

    opponentsNames += BOTCAT_NAME + "*," + BOTCAT_2_NAME + "*";

    return engine.getLocalRepository(opponentsNames);
  }
}