package org.finfrock.robocode_starcat

import robocode.ScannedRobotEvent
import robocode.RobotDeathEvent

/**
 * @author lancewf
 */
class TankBuilder {
def buildTank(scannedRobotEvent:ScannedRobotEvent,
      xWhenSighted:Double, yWhenSighted:Double, headingWhenSighted:Double):Tank = {

    val name = scannedRobotEvent.getName();
    val energy = scannedRobotEvent.getEnergy();
    val bearing = scannedRobotEvent.getBearing();
    val distance = scannedRobotEvent.getDistance();
    val heading = scannedRobotEvent.getHeading();
    val velocity = scannedRobotEvent.getVelocity();
    val time = scannedRobotEvent.getTime();

    return buildTank(name, energy, bearing, distance, heading, velocity,
        time, xWhenSighted, yWhenSighted, headingWhenSighted);
  }
  
  def buildTank(name:String, energy:Double, bearing:Double, 
      distance:Double, heading:Double, velocity:Double, time:Long, 
      xWhenSighted:Double, yWhenSighted:Double, headingWhenSighted:Double):Tank ={
    var absoluteBearing = (headingWhenSighted + bearing) % 360.0;

    if (absoluteBearing < 0) {
      absoluteBearing = 360.0 + absoluteBearing;
    }

    val distanceWhenSighted = distance;
    absoluteBearing = Math.toRadians(absoluteBearing); // convert to radians

    val x = xWhenSighted + math.sin(absoluteBearing) * distanceWhenSighted;
    val y = yWhenSighted + math.cos(absoluteBearing) * distanceWhenSighted;

    return Tank(name, energy, bearing, distance, heading, velocity,
        x, y, time)
  }

  def buildTank(event:RobotDeathEvent):Tank = {

    val name = event.getName();
    val time = event.getTime();

    return buildTank(name, 0.0, 0.0, 0.0, 0.0, 0.0,
        time, 0.0, 0.0, 0.0);
  }
}