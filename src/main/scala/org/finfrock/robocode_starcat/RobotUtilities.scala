package org.finfrock.robocode_starcat

import java.util.Random

object RobotUtilities {
  def bearingToTank(tank: Tank, x1: Double, y1: Double): Double = {
    val radians = bearingToTankRadian(tank, x1, y1)
    return math.toDegrees(radians)
  }

  def distanceToTank(tank: Tank, x1: Double, y1: Double): Double = {
    val deltaX = tank.x - x1;
    val deltaY = tank.y - y1;
    return math.sqrt(deltaX * deltaX + deltaY * deltaY);
  }

  def getTurretHeadingFromFront(gunHeading:Double, bodyHeading:Double): Double = {
    var bearing = gunHeading - bodyHeading;

    if (bearing < 0) {
      bearing = 360 + bearing;
    }
    return bearing;
  }
  
  def findDistanceToWall(heading:Double, x:Double, y:Double, battleFieldWidth:Double, 
      battleFieldHeight:Double, degreesFromCurrentClockwise: Double): Double = {
    val degreesAfterTurn = turnClockwise(heading,
      degreesFromCurrentClockwise);

    // convert to radians
    val absoluteBearing = math.toRadians(degreesAfterTurn);

    var distance = Double.MaxValue

    if (degreesAfterTurn > 0 && degreesAfterTurn < 180) {
      // Test east wall
      // law of sin

      val nintyDegreesDistanceFromEastWall = battleFieldWidth - x;

      distance = (nintyDegreesDistanceFromEastWall) / (math.sin(absoluteBearing))
    }
    if (degreesAfterTurn > 90 && degreesAfterTurn < 270) {
      // Test south wall

      val oneEightyDegreesDistanceFromEastWall = y;

      val tempDistance = (oneEightyDegreesDistanceFromEastWall) / (math.sin(absoluteBearing - math.Pi / 2.0));

      if (tempDistance < distance) {
        distance = tempDistance;
      }
    }
    if (degreesAfterTurn > 180 && degreesAfterTurn < 359.999999) {
      // Test west wall

      val twoSeventyDegreesDistanceFromEastWall = x;

      var tempDistance = Double.MaxValue;

      if (degreesAfterTurn < 270) {
        tempDistance = (twoSeventyDegreesDistanceFromEastWall) / (math.sin(absoluteBearing - math.Pi));
      } else {
        tempDistance = (twoSeventyDegreesDistanceFromEastWall) / (math.sin((-1.0) * absoluteBearing));
      }

      if (tempDistance < distance) {
        distance = tempDistance;
      }
    }
    if ((degreesAfterTurn > 270 && degreesAfterTurn < 359.999999)
      || (degreesAfterTurn >= 0 && degreesAfterTurn < 90)) {
      // Test north wall

      val zeroDegreesDistanceFromNorthWall = battleFieldHeight - y;

      val tempDistance = (zeroDegreesDistanceFromNorthWall) / (math.sin(math.Pi / 2.0 - absoluteBearing));

      if (tempDistance < distance) {
        distance = tempDistance;
      }
    }

    return distance;
  }

  def findDistanceToOtherTanks(directionLooking: Double,
                               heading: Double, x:Double, y:Double, random: Random, 
                               tanks:List[Tank]): Double = {
    var modifiedHeadingToLook = directionLooking;

    val amountToAdd = random.nextInt(30);

    if (random.nextBoolean()) {
      modifiedHeadingToLook += amountToAdd;
    } else {
      modifiedHeadingToLook -= amountToAdd;
    }

    findDistanceOpponets(heading, x, y, tanks,
      modifiedHeadingToLook)
  }

  def findDistanceOpponets(heading: Double, x:Double, y:Double, tanks:List[Tank],
                           degreesFromCurrentClockwise: Double): Double = {
    var distance = Double.MaxValue

    val degreesAfterTurn = turnClockwise(heading,
      degreesFromCurrentClockwise);

    for (tank <- tanks) {
      val angleToTank = bearingToTank(tank, x, y);

      var tempDistance = distanceToTank(tank, x, y);

      val tankWidth = 100;
      val tankHalfWidth = tankWidth / 2.0;
      tempDistance -= tankHalfWidth;

      val widthRadians = math.atan2(tankHalfWidth, tempDistance);
      val widthDegrees = math.toDegrees(widthRadians);

      if (math.abs(degreesAfterTurn - angleToTank) < widthDegrees) {
        if (tempDistance < distance) {
          distance = tempDistance;
        }
      }
    }

    return distance;
  }

  // -------------------------------------------------------------------------
  // Private Members
  // -------------------------------------------------------------------------

  private def bearingToTankRadian(tank: Tank, x1: Double, y1: Double): Double = {
    val deltaX = tank.x - x1;
    val deltaY = tank.y - y1;
    val distanceAway = distanceToTank(tank, x1, y1);
    if (deltaX == 0)
      return 0;

    if (deltaX > 0) {
      if (deltaY > 0)
        return math.asin(deltaX / distanceAway);
      else
        return (math.Pi - math.asin(deltaX / distanceAway));
    } else { // deltaX < 0
      if (deltaY > 0)
        return ((2 * math.Pi) - math.asin(-deltaX / distanceAway));
      else
        return math.Pi + math.asin(-deltaX / distanceAway);
    }
  }
  private def turnClockwise(currentDirection: Double,
                            degreesFromCurrentClockwise: Double): Double = {
    var degreesAfterTurn = currentDirection
    +degreesFromCurrentClockwise;

    if (degreesAfterTurn > 360) {
      degreesAfterTurn -= 360;
    } else if (degreesAfterTurn < 0) {
      degreesAfterTurn = 360 + degreesAfterTurn;
    }
    return degreesAfterTurn;
  }
}