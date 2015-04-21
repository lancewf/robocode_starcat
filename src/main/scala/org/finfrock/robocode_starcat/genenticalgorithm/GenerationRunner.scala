package org.finfrock.robocode_starcat.genenticalgorithm

import java.io.File
import java.util.Properties
import java.io.FileOutputStream
import java.io.FileInputStream
import java.io.IOException
import java.io.FileNotFoundException
import org.finfrock.robocode_starcat.PropertyUtilities

class GenerationRunner(genenticAlgorithm: GenenticAlgorithm) {

  private val POPULATION_INFO_FILE_NAME = "populationInfo.properties";
  private val GENERATION_COUNT = "generations";

  // --------------------------------------------------------------------------
  // Private Data
  // --------------------------------------------------------------------------

  private var numberOfGenerationsRan = 0;

  // --------------------------------------------------------------------------
  // #region Public Members
  // --------------------------------------------------------------------------

  def run() {
    genenticAlgorithm.nextGeneration();

    numberOfGenerationsRan += 1

    val bestIndividual = genenticAlgorithm.getBestIndividual();

    bestIndividual.save(new File("bestAgent.properties"));
  }

  def pause() {
    genenticAlgorithm.pause();
  }

  def unPause() {
    genenticAlgorithm.unPause();
  }

  def getNumberOfGenerationsRan(): Int = {
    return numberOfGenerationsRan;
  }

  def saveAll(directory: File) {
    if (directory.isDirectory()) {
      val newDirectory = new File(directory.getPath() + "/"
        + numberOfGenerationsRan);

      newDirectory.mkdir();

      var index = 0;
      for (individual <- genenticAlgorithm.getPopulation()) {
        individual.save(new File(newDirectory.getPath() + "/Agent"
          + index + ".properties"));

        index += 1
      }

      createPopulationInformationFile(newDirectory);
    }
  }

  def load(file: File) {
    if (file.isDirectory()) {
      var population = List[Individual]()

      for (agentFile <- file.listFiles()) {
        if (agentFile.isFile()
          && agentFile.getName().endsWith(".properties")) {
          if (agentFile.getName().equals(POPULATION_INFO_FILE_NAME)) {
            val populationInformation = readProperty(agentFile);

            val generationString = populationInformation
              .getProperty(GENERATION_COUNT);

            numberOfGenerationsRan = Integer
              .parseInt(generationString);
          } else {
            new PropertyUtilities().readInProperties(agentFile) match {
              case Some(properties) => {
                val loadedChromosome = new BotcatChromosome(properties)
                val individual = new Individual(loadedChromosome)
                population ::= individual
              }
              case None => throw new Exception("property file not read in: " + agentFile.getAbsolutePath)
            }

          }
        }
      }

      genenticAlgorithm.setPopulation(population);
    }
  }

  // #endregion

  // --------------------------------------------------------------------------
  // #region Private Members
  // --------------------------------------------------------------------------

  private def createPopulationInformationFile(directory: File) {
    val populationInformationFile = new File(directory.getPath() + "/"
      + POPULATION_INFO_FILE_NAME);

    val populationInformation = new Properties();

    populationInformation.setProperty(GENERATION_COUNT,
      numberOfGenerationsRan + "");

    try {
      val outputStream = new FileOutputStream(
        populationInformationFile, false);

      populationInformation.store(outputStream, null);
    } catch {
      case ex: FileNotFoundException => {
        ex.printStackTrace()
      }
      case ex: IOException => {
        ex.printStackTrace()
      }
    }
  }

  private def readProperty(agentFile: File): Properties = {
    val properties = new Properties();

    var fileInputStream: FileInputStream = null;
    try {
      fileInputStream = new FileInputStream(agentFile);
      properties.load(fileInputStream);

      fileInputStream.close();
    } catch {
      case ex: FileNotFoundException => {
        ex.printStackTrace()
      }
      case ex: IOException => {
        ex.printStackTrace()
      }
    } finally {
      if (fileInputStream != null) {
        try {
          fileInputStream.close();
        } catch {
          case ex: IOException => {
            ex.printStackTrace()
          }
        }
      }
    }

    return properties;
  }
}