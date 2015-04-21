package org.finfrock.robocode_starcat

import java.awt.Graphics2D
import robocode.AdvancedRobot

/**
 * @author lancewf
 */
class BotCatPainter(robot:AdvancedRobot){
  def onPaint(g:Graphics2D) {
    g.setColor(java.awt.Color.RED)
  }
}