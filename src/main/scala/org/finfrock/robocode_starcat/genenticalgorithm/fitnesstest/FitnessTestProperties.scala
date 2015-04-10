package org.finfrock.robocode_starcat.genenticalgorithm.fitnesstest

import java.util.Properties
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException

class FitnessTestProperties {

  private val PROPERTIES_FILE_NAME = "fitnessTest.properties";
  private val NUMBER_OF_ROUNDS = "numberOfRounds";
  private val OPPONENTS = "opponents";
  private val BATTLEFIELD_WIDTH = "battlefieldWidth";
  private val BATTLEFIELD_HEIGHT = "battlefieldHeight";
  private val INACTIVITY_TIME = "inactivityTime";
  private val BOTCAT_PROPERTIES_PATH = "botcatPropertiesPath";
  private val BOTCAT_2_PROPERTIES_PATH = "botcat2PropertiesPath";

  private lazy val properties = loadProperties()

  private def loadProperties(): Properties = {
    val properties = new Properties()
    var fileInputStream: FileInputStream = null
    try {
      val file = new File(PROPERTIES_FILE_NAME)

      fileInputStream = new FileInputStream(file)

      properties.load(fileInputStream)
    } catch {
      case e: FileNotFoundException => {
        e.printStackTrace()
      } case e: IOException => {
        e.printStackTrace()
      }
    } finally {
      if (fileInputStream != null) {
        try {
          fileInputStream.close();
        } catch {
          case e: IOException => e.printStackTrace()
        }
      }
    }
    properties
  }
  def getNumberOfRound():Int = {
      val value = properties.getProperty(NUMBER_OF_ROUNDS);
      
      return Integer.parseInt(value);
   }
   
   def getOpponentsNames():List[String] = properties.getProperty(OPPONENTS).split(",").toList
   
   def getBattlefieldWidth():Int = {
      val value = properties.getProperty(BATTLEFIELD_WIDTH);
      
      return Integer.parseInt(value);
   }
   
   def getBattlefieldHeight():Int = {
      val value = properties.getProperty(BATTLEFIELD_HEIGHT);
      
      return Integer.parseInt(value);
   }
   
   def getInactivityTime():Int = {
      val value = properties.getProperty(INACTIVITY_TIME);
      
      return Integer.parseInt(value);
   }
   
   def getBotCatPropertiesPath() = properties.getProperty(BOTCAT_PROPERTIES_PATH)
   
   def getBotCat2PropertiesPath() = properties.getProperty(BOTCAT_2_PROPERTIES_PATH)
   
   def dispose(){ }
}