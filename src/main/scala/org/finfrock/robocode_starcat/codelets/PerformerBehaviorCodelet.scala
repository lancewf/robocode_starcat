package org.finfrock.robocode_starcat.codelets

import org.finfrock.starcat.codelets.FuzzyBehaviorCodelet
import org.finfrock.starcat.workspace.Workspace
import org.finfrock.robocode_starcat.RobocodeWorkspace
import org.finfrock.robocode_starcat.RobotUtilities
import org.finfrock.starcat.codelets.BehaviorCodelet
import org.finfrock.robocode_starcat.RobotActionType._
import java.util.UUID
import org.finfrock.starcat.codelets.SlipnetNodeActivationRecipient

class PerformerBehaviorCodelet(robotAction:RobotActionType) extends BehaviorCodelet {

    val name:String = UUID.randomUUID().toString()
    val numberToEmit:Int = 1
    val urgency:Double = 100
    
   val successActivationRecipients:List[SlipnetNodeActivationRecipient] = Nil
   val failureActivationRecipients:List[SlipnetNodeActivationRecipient] = Nil
   
   @Override
   def execute(workspace:Workspace){
     workspace match {
       case robocodeWorkspace:RobocodeWorkspace =>{
         robocodeWorkspace.robot.setMovement(robotAction)
       }
     }
   }
}