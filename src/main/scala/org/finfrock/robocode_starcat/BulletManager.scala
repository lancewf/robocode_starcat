package org.finfrock.robocode_starcat

import robocode.BulletHitEvent
import robocode.BulletMissedEvent

/**
 * @author lancewf
 */
class BulletManager(maximumBulletAge:Long) {
    // -------------------------------------------------------------------------
  // Private Data
  // -------------------------------------------------------------------------
  
  private var bulletHitEvents = List[BulletHitEvent]()
  
  private var bulletMissedEvents = List[BulletMissedEvent]()
  
  // -------------------------------------------------------------------------
  // Public Members
  // -------------------------------------------------------------------------
  
  def getRecentBulletHitEvents(robotTime:Long):List[BulletHitEvent] ={
    update(robotTime)
    
    bulletHitEvents
  }
  
  def getRecentBulletMissedEvents(robotTime:Long):List[BulletMissedEvent] ={
    update(robotTime);
    bulletMissedEvents
  }
  
  def add(event:BulletHitEvent, robotTime:Long){
    bulletHitEvents ::= event
    
    update(robotTime)
  }
  
  def add(event:BulletMissedEvent, robotTime:Long){
    bulletMissedEvents ::= event
    
    update(robotTime)
  }
  
  // -------------------------------------------------------------------------
  // Private Members
  // -------------------------------------------------------------------------
  
  private def update(robotTime:Long){
    var removedBulletHitEvents = List[BulletHitEvent]()
      for (bulletHitEvent <- bulletHitEvents) {
        val time = bulletHitEvent.getTime();

        val diff = robotTime - time;

        if (diff > maximumBulletAge) {
          removedBulletHitEvents ::= bulletHitEvent
        }
      }
    
    for(bulletHitEvent <- removedBulletHitEvents){
      bulletHitEvents = bulletHitEvents.filterNot(_ == bulletHitEvent)
    }
    
    var removedBulletMissedEvents = List[BulletMissedEvent]()
      for (bulletMissedEvent <- bulletMissedEvents) {
        val time = bulletMissedEvent.getTime();

        val diff = robotTime - time;

        if (diff > maximumBulletAge) {
          removedBulletMissedEvents ::= bulletMissedEvent
        }
    }
    
    for(bulletMissedEvent <- removedBulletMissedEvents){
      bulletMissedEvents = bulletMissedEvents.filterNot(_ == bulletMissedEvent)
    }
  }
}