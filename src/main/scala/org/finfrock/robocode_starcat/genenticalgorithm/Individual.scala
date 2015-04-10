package org.finfrock.robocode_starcat.genenticalgorithm

import java.util.Random
import java.io.File
import java.io.FileOutputStream
import java.io.FileNotFoundException
import java.io.IOException

class Individual(val chromosome:Chromosome) extends Comparable[Individual]
{
   // --------------------------------------------------------------------------
   // Private Data
   // --------------------------------------------------------------------------
   
   private var relativeFitness:Double = 0.0
   private var fitnessScore:Double = 0.0
   
   // --------------------------------------------------------------------------
   // Public Members
   // --------------------------------------------------------------------------
   
   def mutate(random:Random):Individual = {
      return new Individual(chromosome.mutate(random));
   }
   
   def mutateAll(random:Random):Individual = {
      val matatedChromosome = chromosome.mutateAll(random);
      
      return new Individual(matatedChromosome);
   }
   
   def crossover(mate:Individual, random:Random):Array[Individual] = {
      val chromosomes = chromosome.crossover(mate.chromosome, random);
      
      if(random.nextBoolean()){
         Array[Individual](new Individual(chromosomes(0)))
      }
      else{
         Array[Individual](new Individual(chromosomes(1)))
      }
   }
   
   def getFitnessScore() = fitnessScore
   
   def setFitnessScore(fitnessScore:Double)
   {
      this.fitnessScore = fitnessScore;
   }
   
   def getRelativeFitness() = relativeFitness

   def setRelativeFitness(relativeFitness:Double){
      this.relativeFitness = relativeFitness;
   }
   
   def save(file:File){  
      var outputStream:FileOutputStream = null;
      try
      {
         outputStream = new FileOutputStream(file, false);
         
         chromosome.save(outputStream);
      }
      catch{
        case e:FileNotFoundException =>{
         e.printStackTrace();
        }
      }
      finally
      {
         if(outputStream != null)
         {
            try
            {
               outputStream.close();
            }
            catch {
              case e:IOException =>{
               e.printStackTrace()
              }
            }
         }
      }
   }
   
   // --------------------------------------------------------------------------
   // Comparable<Individual> Members
   // --------------------------------------------------------------------------

   @Override
   def compareTo(compareToIndividual:Individual):Int = {
      if(getFitnessScore() > compareToIndividual.getFitnessScore())
      {
         return 1;
      }
      else if(getFitnessScore() < compareToIndividual.getFitnessScore())
      {
         return -1;
      }
      else
      {
         return 0;
      }
   }
}