package org.finfrock.robocode_starcat

import robocode.BulletHitEvent
import robocode.BulletMissedEvent

/**
 * @author lancewf
 */
class BulletManager(robot:BotCatable) {
    // -------------------------------------------------------------------------
  // Private Data
  // -------------------------------------------------------------------------
  
  private var maximumBulletAge = 80L
  
  private var bulletHitEvents = List[BulletHitEvent]()
  
  private var bulletMissedEvents = List[BulletMissedEvent]()
  
  // -------------------------------------------------------------------------
  // Public Members
  // -------------------------------------------------------------------------
  
  def setMaximumBulletAge(maximumBulletAge:Long){
    this.maximumBulletAge = maximumBulletAge;
  }
  
  def getRecentBulletHitEvents():List[BulletHitEvent] ={
    update()
    
    bulletHitEvents
  }
  
  def getRecentBulletMissedEvents():List[BulletMissedEvent] ={
    update();
    bulletMissedEvents
  }
  
  def add(event:BulletHitEvent){
    bulletHitEvents ::= event
    
    update()
  }
  
  def add(event:BulletMissedEvent ){
    bulletMissedEvents ::= event
    
    update();
  }
  
  // -------------------------------------------------------------------------
  // Private Members
  // -------------------------------------------------------------------------
  
  private def update(){
    val currentTime = robot.getTime();
    
    var removedBulletHitEvents = List[BulletHitEvent]()
      for (bulletHitEvent <- bulletHitEvents) {
        val time = bulletHitEvent.getTime();

        val diff = currentTime - time;

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

        val diff = currentTime - time;

        if (diff > maximumBulletAge) {
          removedBulletMissedEvents ::= bulletMissedEvent
        }
    }
    
    for(bulletMissedEvent <- removedBulletMissedEvents){
      bulletMissedEvents = bulletMissedEvents.filterNot(_ == bulletMissedEvent)
    }
  }
}