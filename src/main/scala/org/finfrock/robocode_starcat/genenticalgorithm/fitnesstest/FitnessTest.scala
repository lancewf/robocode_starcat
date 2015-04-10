package org.finfrock.robocode_starcat.genenticalgorithm.fitnesstest

import org.finfrock.robocode_starcat.BotCatListener
import java.io.File
import robocode.control.BattlefieldSpecification
import robocode.control.BattleSpecification
import robocode.control.RobocodeEngine
import robocode.control.RobotSpecification
import org.finfrock.robocode_starcat.genenticalgorithm.Individual

object FitnessTest{
  val BOTCAT_NAME = "org.robocode.BotCat"
}

class FitnessTest(listener:BotCatListener) extends IFitnessTest {

   private val fitnessTestProperties = new FitnessTestProperties()
   private lazy val engine = getEngine()
   
   // --------------------------------------------------------------------------
   //  Public Members
   // --------------------------------------------------------------------------

   def run(individual:Individual){
    val file = new File(fitnessTestProperties.getBotCatPropertiesPath())
      individual.save(file);

      runBattle()

      val score = getScore();
      
      individual.setFitnessScore(score)
   }
   
   // --------------------------------------------------------------------------
   // Private Members
   // --------------------------------------------------------------------------
   
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
      println("error in set Score the results was not found");
      return 0;
    }
  }
   
   private def runBattle()
   {      
      val battleField = 
         new BattlefieldSpecification(
            fitnessTestProperties.getBattlefieldWidth(),
            fitnessTestProperties.getBattlefieldHeight());
      
      val engine = getEngine();
      
      listener.setEngine(engine);

      val robots = getRobots(engine, fitnessTestProperties);

      val battle = new BattleSpecification(
         fitnessTestProperties.getNumberOfRound(), 
         fitnessTestProperties.getInactivityTime(),
         0.1, 
         battleField, 
         robots);

      listener.runBattle(battle)
   }
   
   private def getEngine() = new RobocodeEngine(listener)
   
   private def getRobots(engine:RobocodeEngine, 
       fitnessTestProperties:FitnessTestProperties):Array[RobotSpecification] =  {
      val opponents = fitnessTestProperties.getOpponentsNames();
      
      var opponentsNames = "";
      
      for(opponentName <- opponents){
        opponentsNames += opponentName + "*,";
      }
      
      opponentsNames += FitnessTest.BOTCAT_NAME + "*";
      
      return engine.getLocalRepository(opponentsNames);
   }
}