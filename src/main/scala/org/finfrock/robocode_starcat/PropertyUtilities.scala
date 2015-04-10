package org.finfrock.robocode_starcat

import java.io.File
import java.util.Properties
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException

class PropertyUtilities {

  def readInProperties(file:File): Option[Properties] = {
    var fileInputStream: FileInputStream = null;
    try {
      val properties = new Properties()
      fileInputStream = new FileInputStream(file);
      properties.load(fileInputStream)
      fileInputStream.close()
      Some(properties)
    } catch {
      case e: FileNotFoundException => {
        e.printStackTrace()
        None
      }
      case e: IOException => {
        e.printStackTrace()
        None
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
  }
}