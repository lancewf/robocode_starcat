package org.finfrock.robocode_starcat.codelets

import akka.actor.ActorRef
import org.finfrock.robocode_starcat.BotCatable
import org.finfrock.robocode_starcat.RobocodeWorkspaceActor
import org.finfrock.starcat.codelets.BehaviorCodeletActor

trait BotCatableCodeletActor extends BehaviorCodeletActor {

  var botCatableOption:Option[BotCatable] = None
  
  override def receive = super.receive orElse {
    case botCatable:BotCatable=>{ 
      botCatableOption = Some(botCatable)
      preformUpdate(botCatable)
    }
  }
  
  override def preformExecuteWorkspace(workspace: ActorRef) {
    botCatableOption match{
      case Some(botCatable) => preformUpdate(botCatable)
      case None => workspace ! RobocodeWorkspaceActor.GetBotCatable
    }
    super.preformExecuteWorkspace(workspace)
  }
  
  protected def preformUpdate(botCatable:BotCatable)
}