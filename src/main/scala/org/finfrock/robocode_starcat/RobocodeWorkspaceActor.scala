package org.finfrock.robocode_starcat

import akka.actor.ActorRef
import org.finfrock.starcat.workspace.WorkspaceActor
import akka.actor.Props

object RobocodeWorkspaceActor{
  object GetBotCatable
  def getProps(robot:BotCatable):Props = Props(classOf[RobocodeWorkspaceActor], robot)
}

class RobocodeWorkspaceActor(val robot:BotCatable) extends WorkspaceActor{
  override def receive = super.receive orElse {
    case RobocodeWorkspaceActor.GetBotCatable  => sender ! robot
    case message => log.error("message not handeld: " + message)
  }
}