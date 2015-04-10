package org.finfrock.robocode_starcat.genenticalgorithm

import java.util.Properties
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.FileNotFoundException
import java.util.Random
import scala.collection.JavaConversions._
import java.io.OutputStream

abstract class SizableChromosome(val properties: Properties) extends Chromosome {
  // --------------------------------------------------------------------------
  // Private Static Data
  // --------------------------------------------------------------------------

  private val MUTATION_DIFF_VALUE = 25;
  private val EIGHT_MUTATION_DIFF_VALUE = MUTATION_DIFF_VALUE / 8;

  // --------------------------------------------------------------------------
  // Constructor
  // --------------------------------------------------------------------------

  def this() {
    this(new Properties())
  }

  def this(sizableChromosome: SizableChromosome) {
    this(sizableChromosome.properties.clone().asInstanceOf[Properties])
  }

  // --------------------------------------------------------------------------
  // Public Members
  // --------------------------------------------------------------------------

  def load(file: File): Properties = {
    val properties = new Properties();

    var fileInputStream: FileInputStream = null;
    try {
      fileInputStream = new FileInputStream(file);
      properties.load(fileInputStream)
      fileInputStream.close()
    } catch {
      case e: FileNotFoundException => {
        e.printStackTrace()
      }
      case e: IOException => {
        e.printStackTrace()
      }
    } finally {
      if (fileInputStream != null) {
        try {
          fileInputStream.close()
        } catch {
          case e: IOException => {
            e.printStackTrace()
          }
        }
      }
    }

    properties
  }

  def mutate(random: Random): Chromosome = {
    val mutatedChromosome = clone().asInstanceOf[SizableChromosome]

    val numberToMutate = (getSize() * 0.01).toInt
    for (count <- 0 until numberToMutate) {
      val mutatingIndex = random.nextInt(getSize());

      val key = getKey(mutatingIndex);

      val newValue = getMutateBase(random,
        mutatedChromosome.getValue(key));

      mutatedChromosome.setValue(key, newValue);
    }

    return mutatedChromosome;
  }

  def mutateAll(random: Random): SizableChromosome = {
    val mutatedChromosome = clone().asInstanceOf[SizableChromosome]

    for (index <- 0 until getSize()) {
      val key = getKey(index)

      val newValue = getMutateBase(random,
        mutatedChromosome.getValue(key), EIGHT_MUTATION_DIFF_VALUE);

      mutatedChromosome.setValue(key, newValue);
    }

    return mutatedChromosome;
  }

  def crossover(mate: Chromosome): Array[Chromosome] = {
    val random = new Random();

    return crossover(mate, random);
  }

  def crossover(mate: Chromosome, random: Random): Array[Chromosome] = {
    mate match {
      case sizableMate: SizableChromosome => {
        val child1 = sizableMate.clone().asInstanceOf[SizableChromosome]
        val child2 = sizableMate.clone().asInstanceOf[SizableChromosome]

        // strandBreakPoint every 10 percent of bases
        val percentageOfBasesToBreakOn = random.nextDouble();

        var numberOfBasesPerBreak = (getSize() * percentageOfBasesToBreakOn).toInt

        // So that it is not zero
        if (numberOfBasesPerBreak == 0) {
          numberOfBasesPerBreak += 1
        }

        var isMotherFirstChild = random.nextBoolean()
        var index = 0

        for {keyRaw <- properties.keySet()
            key = keyRaw.toString} {

          if (index % numberOfBasesPerBreak == 0) {
            isMotherFirstChild = !isMotherFirstChild;
          }

          if (isMotherFirstChild) {
            child1.setValue(key, getValue(key));
            child2.setValue(key, sizableMate.getValue(key));
          } else {
            child2.setValue(key, getValue(key));
            child1.setValue(key, sizableMate.getValue(key));
          }
          index += 1
        }

        Array[Chromosome](child1, child2)
      }
    }
  }

  /**
   * Save the Chromosome value to the output stream passed in
   *
   * @param outputStream
   */
  def save(outputStream: OutputStream) {
    try {
      properties.store(outputStream, null);
    } catch {
      case ex: FileNotFoundException => {
        ex.printStackTrace();
      }
      case ex: IOException => {
        ex.printStackTrace();
      }
    }
  }

  // --------------------------------------------------------------------------
  // Protected Members
  // --------------------------------------------------------------------------

  /**
   * Adjusts the value to the minimum and maximum value
   *
   * formula use: x = (max - min)/100 = 0.029 y = 0.1
   *
   * value * x + y = fire amount
   *
   * Maximum value: 100 * 0.029 + 0.1 = 3
   *
   * Minimum value: 0 * 0.029 + 0.1 = 0.1
   *
   * @param max
   *            - the maximum value to adjust the value
   * @param min
   *            - the minimum value to adjust the value
   * @param value
   *            - the value to adjust must be from 0 to 100
   *
   * @return the adjusted value
   */
  protected def adjustValue(max: Double, min: Double, value: Double): Double = {
    val x = (max - min) / 100;
    val y = min;

    value * x + y;
  }

  protected def getValue(tag: String): Int = {
    val value = properties.getProperty(tag)
    if (value == null) {
      setValue(tag, 100);
      return 100;
    }
    return Integer.parseInt(value)
  }

  def setValue(tag: String, value: Int) {
    properties.setProperty(tag, value + "");
  }

  protected def doesBaseExist(tag: String): Boolean = {
    val value = properties.getProperty(tag);

    if (value == null) {
      //      System.out.println("Tag not found " + tag);
      return false;
    } else {
      return true;
    }
  }

  // --------------------------------------------------------------------------
  // Private Member
  // --------------------------------------------------------------------------

  /**
   * Get a new value for the Chromosome base with a random value plus or minus
   * the current value with the muatationDiff being the maximum different in
   * either direction
   */
  private def getMutateBase(random: Random, currentValue: Int, mutationDiff: Int): Int = {
    var newValue = if (random.nextBoolean()) {
      currentValue + random.nextInt(mutationDiff)
    } else {
      currentValue - random.nextInt(mutationDiff)
    }

    if (newValue > 100) {
      newValue = 100;
    }
    if (newValue < 0) {
      newValue = 0;
    }

    return newValue;
  }

  private def getMutateBase(random: Random, currentValue: Int): Int = {
    return getMutateBase(random, currentValue, MUTATION_DIFF_VALUE);
  }

  private def getKey(index: Int): String = {
    properties.keySet().toArray()(index).toString()
  }

  private def getSize(): Int = {
    return properties.size();
  }
}