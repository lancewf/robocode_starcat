package org.finfrock.robocode_starcat.codelets

import org.finfrock.starcat.codelets.FuzzyBehaviorCodelet
import org.finfrock.starcat.workspace.Workspace
import org.finfrock.robocode_starcat.RobocodeWorkspace
import org.finfrock.robocode_starcat.RobotUtilities
import org.finfrock.starcat.codelets.SlipnetNodeActivationRecipient
import java.util.UUID


/**
 * 
 * M  |      
 * e 1|     _-_
 * M  |   _-   -_ 
 * B  | _-       -_ 
 * E 0|-___________-_____________
 * R  |0    90    180   270    360  : value
 *    F     R      B     L      F
 *    
 * F - forward
 * R - right
 * B - backward
 * L - left
 */  
class RightTurretOrientationObserverCodelet(
    val successActivationRecipients: List[SlipnetNodeActivationRecipient]) 
     extends FuzzyBehaviorCodelet(
    successOneValueX = 90, 
    successMinimumZeroValueX = 0, 
    successMaximumZeroValueX = 180, 
    failureOneValueX = 135, 
    failureMinimumZeroValueX = Double.MinValue, 
    failureMaximumZeroValueX = Double.MaxValue) {
    
  val name: String = UUID.randomUUID().toString()
  val numberToEmit: Int = 1
  val urgency: Double = 1
  val failureActivationRecipients:List[SlipnetNodeActivationRecipient] = Nil
 
   @Override
   def execute(workspace:Workspace){
     workspace match {
       case robocodeWorkspace:RobocodeWorkspace =>{
         val bearing = RobotUtilities.getTurretHeadingFromFront(robocodeWorkspace.robot)

         setCrispValue(bearing)
       }
     }
   }
}