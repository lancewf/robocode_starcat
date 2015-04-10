package org.finfrock.robocode_starcat

import robocode.control.events.IBattleListener
import robocode.control.RobocodeEngine
import robocode.BattleResults
import robocode.control.BattleSpecification
import robocode.control.events.BattleStartedEvent
import robocode.control.events.BattleFinishedEvent
import robocode.control.events.BattleCompletedEvent
import robocode.control.events.BattlePausedEvent
import robocode.control.events.BattleResumedEvent
import robocode.control.events.RoundStartedEvent
import robocode.control.events.RoundEndedEvent
import robocode.control.events.TurnStartedEvent
import robocode.control.events.TurnEndedEvent
import robocode.control.events.BattleMessageEvent
import robocode.control.events.BattleErrorEvent
import org.finfrock.robocode_starcat.genenticalgorithm.fitnesstest.FitnessTest

class BotCatListener extends IBattleListener {
  // --------------------------------------------------------------------------
  // Private Data
  // --------------------------------------------------------------------------

  private var isBattleDone = true;
  private var engine:RobocodeEngine = null
  private var endResult:BattleResults = null;

  // --------------------------------------------------------------------------
  // Public Members
  // --------------------------------------------------------------------------

  def getRobotResults() = endResult

  def runBattle(battle:BattleSpecification) {
    if (isBattleDone) {
      endResult = null;
      isBattleDone = false
      engine.setVisible(true)
      engine.runBattle(battle)
      waitForTest()
    }
  }

  def setEngine(engine:RobocodeEngine) {
    this.engine = engine;
  }

  def dispose() {
    engine = null;
    endResult = null;
  }

  // --------------------------------------------------------------------------
  // Private Members
  // --------------------------------------------------------------------------

  private def waitForTest() {
    while (!isBattleDone) {
      try {
        Thread.sleep(100);
      } catch {
        case e:InterruptedException => {
        e.printStackTrace();
      }
      }
    }
  }

  // --------------------------------------------------------------------------
  // RobocodeListener Members
  // --------------------------------------------------------------------------

  @Override
  def onBattleStarted(event:BattleStartedEvent) {
    // TODO Auto-generated method stub

  }

  @Override
  def onBattleFinished(event:BattleFinishedEvent) {
    isBattleDone = true;
  }

  @Override
  def onBattleCompleted(event:BattleCompletedEvent) {

    endResult = null;
    for (result <- event.getSortedResults()) {
      if (result.getTeamLeaderName().equals(FitnessTest.BOTCAT_NAME + "*")) {
        endResult = result;
      }
    }
    isBattleDone = true;
  }

  @Override
  def onBattlePaused(event:BattlePausedEvent) {
    // TODO Auto-generated method stub

  }

  @Override
  def onBattleResumed(event:BattleResumedEvent) {
    // TODO Auto-generated method stub

  }

  @Override
  def onRoundStarted(event:RoundStartedEvent) {
    // TODO Auto-generated method stub

  }

  @Override
  def onRoundEnded(event:RoundEndedEvent) {
    // TODO Auto-generated method stub

  }

  @Override
  def onTurnStarted(event:TurnStartedEvent) {
    // TODO Auto-generated method stub

  }

  @Override
  def onTurnEnded(event:TurnEndedEvent) {
    // TODO Auto-generated method stub

  }

  @Override
  def onBattleMessage(event:BattleMessageEvent) {
    // TODO Auto-generated method stub

  }

  @Override
  def onBattleError(event:BattleErrorEvent) {
    // TODO Auto-generated method stub

  }
}