package org.finfrock.robocode_starcat

import java.awt.Graphics2D

/**
 * @author lancewf
 */
class BotCatPainter(robot:BotCatable){
  def onPaint(g:Graphics2D) {
    g.setColor(java.awt.Color.RED)
  }
}