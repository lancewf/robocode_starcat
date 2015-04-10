package org.finfrock.robocode_starcat.genenticalgorithm

import org.finfrock.robocode_starcat.genenticalgorithm.fitnesstest.IFitnessTest
import java.util.Random
import scala.annotation.tailrec

trait IProgress{
  def setValue(value:Int)
}
class GenenticAlgorithm(populationSize:Int, fitnessTest:IFitnessTest,
         initialIndividual:Individual) {
  // --------------------------------------------------------------------------
   // Private Data
   // --------------------------------------------------------------------------

   private var population:List[Individual] = Nil

   private val random = new Random()

   private var bestIndividual:Individual = null

   private var totalPopulationScore = 0;

   private val topPercentPopulation = 0.06;

   private var progress:IProgress = null

   private var isPaused = true;
   
   generateInitialPopulation(populationSize, initialIndividual)

   // --------------------------------------------------------------------------
   // Public Members
   // --------------------------------------------------------------------------

   def setProgress(progress:IProgress){
      this.progress = progress;
   }

   /**
    * Get the best individual in the population
    */
   def getBestIndividual():Individual = bestIndividual

   /**
    * Get the entire population
    */
   def getPopulation() = population
   
   /**
    * Get the entire population
    */
   def setPopulation(population:List[Individual]){
      this.population = population
   }
   
   def pause(){
      isPaused = true;
   }
   
   def unPause(){
      isPaused = false;
   }

   /**
    * Perform the next generation
    */
   def nextGeneration()
   {
      isPaused = false;
      var newPopulation = List[Individual]()

      scoreFitnessOfPopulation(fitnessTest);
      findRelativeFitness()

      val topPercentOfPopulation = getTopPercentOfPopulation();
      val totalSize = population.size - getNumberOfEliteMembers()
      while (newPopulation.size < totalSize)
      {
         val mate1 = selectIndividualForMating();
         val mate2 = selectIndividualForMating(mate1);

         val children = mate1.crossover(mate2, random);

         newPopulation :::= children.toList
      }

      newPopulation = mutate(newPopulation)

      for (socialite <- topPercentOfPopulation){
         println("socialite added fitness = " + socialite.getFitnessScore())

         newPopulation ::= socialite
      }

      population = newPopulation;
   }

   // --------------------------------------------------------------------------
   // Protected Members
   // --------------------------------------------------------------------------

   /**
    * Find the fitness of each individual in the population
    */
   protected def scoreFitnessOfPopulation(fitnessTest:IFitnessTest)
   {
      var count = 0;
      progress.setValue(0);
      for (individual <- population)
      {
         checkForPause()
         
         fitnessTest.run(individual);
         count+=1
         progress.setValue((count.toDouble
               / population.size.toDouble * 100.0).toInt)
      }

      progress.setValue(100)
   }
   
   // --------------------------------------------------------------------------
   // Private Members
   // --------------------------------------------------------------------------
   
   private def checkForPause(){
      while (isPaused)
      {
         try
         {
            Thread.sleep(500);
         }
         catch 
         {
           case ex:InterruptedException =>{
            isPaused = false 
           }
         }
      }
   }

   /**
    * Find the relative fitness for each individual in the population
    * The relative fitness is the fitness of the individual in relation to the
    * other individuals in the population.
    */
   private def findRelativeFitness()
   {
      // sort so that the individual with the best fitness value is in the 
      // position highest or index; 
      population = population.sortWith((i1,i2) => i1.getFitnessScore() > i2.getFitnessScore())

      // the sum of all the ranks
      totalPopulationScore = (population.size * (population.size + 1)) / 2;

      for (rank <- 0 until population.size)
      {
         println("Rank = " + rank + " Fitness = " + population(rank).getFitnessScore())

         population(rank).setRelativeFitness(rank + 1);
      }
   }

   /**
    * Select an individual from the population for mating
    * 
    * An individual is selected from the population in roulette wheel selection.
    * The selection is based of the rank of the individual or relative fitness.
    * 
    * @return the individual mate randomly selected by roulette wheel selection
    */
   private def selectIndividualForMating():Individual = {
      val selectionPercent = random.nextInt(totalPopulationScore)
      
      selectIndividualForMating(population, 0.0, selectionPercent)
   }
   
   @tailrec
   private def selectIndividualForMating(population:List[Individual], currentPercent:Double, selectionPercent:Double):Individual = {
         if (currentPercent >= selectionPercent){
           population.head
         } else{
           selectIndividualForMating(population.tail, currentPercent + population.head.getRelativeFitness(), selectionPercent)
         }
   }

   /**
    * Select an individual from the population excluding the individual 
    * passed in
    * 
    * @param mate1 - the individual mate to exclude from the roulette wheel
    * selection. 
    */
   private def selectIndividualForMating(mate1:Individual):Individual ={
      val totalPopulationScoreMinsMate = totalPopulationScore - mate1.getRelativeFitness().toInt
      
      val selectionPercent = random.nextInt(totalPopulationScoreMinsMate);

      val selectionPopulation = population.filterNot(_ == mate1)

      selectIndividualForMating(selectionPopulation, 0.0, selectionPercent)
   }

   /**
    * Get the top percent of the population
    */
   private def getTopPercentOfPopulation():List[Individual] = {
      var topPercentOfPopulation = List[Individual]()

      population = population.sortWith((i1,i2) => i1.getFitnessScore() > i2.getFitnessScore())

      bestIndividual = population.last

      population.drop(getNumberOfEliteMembers())
   }

   /**
    * Generate an initial population from an initial chromosome object. 
    * 
    * The method takes the initial chromosome and mutates it to create an 
    * individual for the population. 
    * 
    */
   private def generateInitialPopulation(populationSize:Int, 
                                          initialIndividual:Individual){
      population = List[Individual]()

      for (count <- 0 until populationSize - 1)
      {
         val individual = initialIndividual.mutateAll(random);
         population ::= individual
      }
      population ::= initialIndividual

      bestIndividual = initialIndividual
   }

   /**
    * mutate the population passed in 
    */
   private def mutate(childrenPopulation:List[Individual]):List[Individual] = {
      var newPopulation = List[Individual]()

      for (individual <- childrenPopulation)
      {
         var mutateIndividual = individual.mutate(random);
         mutateIndividual = mutateIndividual.mutate(random);
         newPopulation ::= mutateIndividual
      }

      return newPopulation;
   }

   /**
    * Get the number of elite members
    * @return
    */
   private def getNumberOfEliteMembers():Int = {
      (population.size * topPercentPopulation).toInt
   }
}