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
 * e 1|            _-_
 * M  |          _-   -_   
 * B  |        _-       -_    
 * E 0|_______-____________-_________
 * R  |0     90    180     270    360  : value
 *          East   South   West
 */      
class SouthBodyOrientationObserverCodelet(
    val successActivationRecipients: List[SlipnetNodeActivationRecipient]) 
    extends FuzzyBehaviorCodelet(
    successOneValueX = 180, 
    successMinimumZeroValueX = 90, 
    successMaximumZeroValueX = 270, 
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
         val bearing = robocodeWorkspace.robot.getHeading()

         setCrispValue(bearing)
       }
     }
   }
}