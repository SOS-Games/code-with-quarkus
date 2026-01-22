package com.framework.service.logic;

/**
 * Represents the state of an active action for a player.
 */
public class ActionState {
    public final String actionId;
    public final String instanceId;
    public final long startTime;
    
    public ActionState(String actionId, String instanceId) {
        this.actionId = actionId;
        this.instanceId = instanceId;
        this.startTime = System.currentTimeMillis();
    }
}
