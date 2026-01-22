package com.framework.api;

import com.framework.data.entity.PlayerEntity;
import com.framework.data.entity.PlayerItemEntity;
import com.framework.data.model.Location;
import com.framework.service.logic.ActionService;
import com.framework.service.logic.ActionState;
import com.framework.service.logic.InventoryService;
import com.framework.service.logic.LocationService;
import com.framework.service.logic.PlayerService;
import com.framework.service.repos.AgentStateRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket endpoint for real-time game communication.
 * Protocol: v1 - WebSocket (WSS/WS) on ws://localhost:8080/game/ws
 */
@ServerEndpoint("/game/ws")
@ApplicationScoped
public class GameWebSocket {
    
    // Map to store active sessions (playerId -> Session)
    private final Map<String, Session> sessions = new ConcurrentHashMap<>();
    
    @Inject
    PlayerService playerService;
    
    @Inject
    InventoryService inventoryService;
    
    @Inject
    ActionService actionService;
    
    @Inject
    LocationService locationService;
    
    @Inject
    AgentStateRepository agentStateRepository;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @OnOpen
    public void onOpen(Session session) {
        // Extract player ID from query parameters
        String playerId = getPlayerIdFromSession(session);
        if (playerId != null) {
            sessions.put(playerId, session);
            session.getUserProperties().put("playerId", playerId);
            
            // Get or create player on connection
            try {
                playerService.getOrCreatePlayer(playerId);
                
                // Send full state update (STATE_INIT)
                sendStateInit(session, playerId);
                
                System.out.println("Player connected: " + playerId);
            } catch (Exception e) {
                System.err.println("Error initializing player: " + e.getMessage());
                e.printStackTrace();
                sendError(session, "Failed to initialize player: " + e.getMessage());
            }
        } else {
            try {
                session.close();
            } catch (Exception e) {
                System.err.println("Error closing session: " + e.getMessage());
            }
        }
    }
    
    @OnClose
    public void onClose(Session session) {
        String playerId = (String) session.getUserProperties().get("playerId");
        if (playerId != null) {
            sessions.remove(playerId);
            // Stop any active actions
            actionService.stopAction(playerId);
            System.out.println("Player disconnected: " + playerId);
        }
    }
    
    @OnError
    public void onError(Session session, Throwable throwable) {
        System.err.println("WebSocket error for session: " + session.getId());
        throwable.printStackTrace();
        sendError(session, "Internal server error: " + throwable.getMessage());
    }
    
    @OnMessage
    public void onMessage(String message, Session session) {
        String playerId = (String) session.getUserProperties().get("playerId");
        if (playerId == null) {
            sendError(session, "Player ID not found");
            return;
        }
        
        try {
            // Parse JSON message
            ObjectNode json = (ObjectNode) objectMapper.readTree(message);
            String type = json.has("type") ? json.get("type").asText() : null;
            
            if (type == null) {
                sendError(session, "Missing 'type' field in message");
                return;
            }
            
            // Route to appropriate handler based on protocol
            switch (type) {
                case "START_ACTION":
                    handleStartAction(session, playerId, json);
                    break;
                case "MOVE_LOCATION":
                    handleMoveLocation(session, playerId, json);
                    break;
                case "ATTACK_TARGET":
                    handleAttackTarget(session, playerId, json);
                    break;
                case "EQUIP_ITEM":
                    handleEquipItem(session, playerId, json);
                    break;
                case "SELL_ITEM":
                    handleSellItem(session, playerId, json);
                    break;
                default:
                    sendError(session, "Unknown message type: " + type);
            }
        } catch (Exception e) {
            System.err.println("Error processing message: " + e.getMessage());
            e.printStackTrace();
            sendError(session, "Error processing message: " + e.getMessage());
        }
    }
    
    // ========== Server → Client Message Handlers ==========
    
    /**
     * Sends a full state update (STATE_INIT) on connect.
     */
    private void sendStateInit(Session session, String playerId) {
        try {
            PlayerEntity player = playerService.getPlayer(playerId);
            if (player == null) {
                sendError(session, "Player not found");
                return;
            }
            
            List<PlayerItemEntity> inventory = inventoryService.getInventory(playerId);
            List<PlayerItemEntity> equipped = inventoryService.getEquippedItems(playerId);
            ActionState currentAction = actionService.getActiveAction(playerId);
            Location location = locationService.getCurrentLocation(playerId);
            
            ObjectNode response = objectMapper.createObjectNode();
            response.put("type", "STATE_INIT");
            response.set("player", objectMapper.valueToTree(player));
            response.set("inventory", objectMapper.valueToTree(inventory));
            response.set("equipped", objectMapper.valueToTree(equipped));
            
            // Current action details
            if (currentAction != null) {
                ObjectNode actionNode = objectMapper.createObjectNode();
                actionNode.put("actionId", currentAction.actionId);
                actionNode.put("instanceId", currentAction.instanceId != null ? currentAction.instanceId : "");
                long elapsed = System.currentTimeMillis() - currentAction.startTime;
                actionNode.put("progress", elapsed / 1000.0); // Progress in seconds
                response.set("currentAction", actionNode);
            } else {
                response.set("currentAction", objectMapper.nullNode());
            }
            
            // Location details
            if (location != null) {
                ObjectNode locationNode = objectMapper.createObjectNode();
                locationNode.put("id", location.getId());
                locationNode.put("name", location.getName());
                locationNode.put("description", location.getDescription());
                locationNode.set("availableActions", objectMapper.valueToTree(location.getAvailableActionIds()));
                response.set("location", locationNode);
            } else {
                response.set("location", objectMapper.nullNode());
            }
            
            sendMessage(session, objectMapper.writeValueAsString(response));
        } catch (Exception e) {
            System.err.println("Error sending STATE_INIT: " + e.getMessage());
            e.printStackTrace();
            sendError(session, "Failed to send initial state: " + e.getMessage());
        }
    }
    
    /**
     * Sends a delta update (STATE_DELTA) for game ticks.
     */
    public void sendStateDelta(String playerId, Map<String, Integer> xpDelta, 
                               Map<String, Integer> inventoryDelta, 
                               int health, String message) {
        Session session = sessions.get(playerId);
        if (session == null || !session.isOpen()) {
            return;
        }
        
        try {
            ObjectNode response = objectMapper.createObjectNode();
            response.put("type", "STATE_DELTA");
            response.set("xpDelta", objectMapper.valueToTree(xpDelta));
            response.set("inventoryDelta", objectMapper.valueToTree(inventoryDelta));
            response.put("health", health);
            if (message != null) {
                response.put("message", message);
            }
            
            sendMessage(session, objectMapper.writeValueAsString(response));
        } catch (Exception e) {
            System.err.println("Error sending STATE_DELTA: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Sends a combat start event.
     */
    public void sendCombatStart(String playerId, ObjectNode targetMob, String warningMessage) {
        Session session = sessions.get(playerId);
        if (session == null || !session.isOpen()) {
            return;
        }
        
        try {
            ObjectNode response = objectMapper.createObjectNode();
            response.put("type", "COMBAT_START");
            response.set("targetMob", targetMob);
            response.put("warningMessage", warningMessage);
            
            sendMessage(session, objectMapper.writeValueAsString(response));
        } catch (Exception e) {
            System.err.println("Error sending COMBAT_START: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Sends a combat end event.
     */
    public void sendCombatEnd(String playerId, String message) {
        Session session = sessions.get(playerId);
        if (session == null || !session.isOpen()) {
            return;
        }
        
        try {
            ObjectNode response = objectMapper.createObjectNode();
            response.put("type", "COMBAT_END");
            if (message != null) {
                response.put("message", message);
            }
            
            sendMessage(session, objectMapper.writeValueAsString(response));
        } catch (Exception e) {
            System.err.println("Error sending COMBAT_END: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Sends an alert message.
     */
    public void sendAlert(String playerId, String warningMessage) {
        Session session = sessions.get(playerId);
        if (session == null || !session.isOpen()) {
            return;
        }
        
        try {
            ObjectNode response = objectMapper.createObjectNode();
            response.put("type", "ALERT");
            response.put("warningMessage", warningMessage);
            
            sendMessage(session, objectMapper.writeValueAsString(response));
        } catch (Exception e) {
            System.err.println("Error sending ALERT: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // ========== Client → Server Message Handlers ==========
    
    /**
     * Handles START_ACTION command.
     */
    private void handleStartAction(Session session, String playerId, ObjectNode json) {
        try {
            String actionId = json.has("actionId") ? json.get("actionId").asText() : null;
            String instanceId = json.has("instanceId") ? json.get("instanceId").asText() : null;
            
            if (actionId == null) {
                sendError(session, "Missing 'actionId' field");
                return;
            }
            
            actionService.startAction(playerId, actionId, instanceId);
            
            // Send confirmation and updated state
            sendStateInit(session, playerId);
        } catch (Exception e) {
            sendError(session, "Failed to start action: " + e.getMessage());
        }
    }
    
    /**
     * Handles MOVE_LOCATION command.
     */
    private void handleMoveLocation(Session session, String playerId, ObjectNode json) {
        try {
            String targetLocationId = json.has("targetLocationId") ? json.get("targetLocationId").asText() : null;
            
            if (targetLocationId == null) {
                sendError(session, "Missing 'targetLocationId' field");
                return;
            }
            
            // Stop any active action
            actionService.stopAction(playerId);
            
            // Move to location
            locationService.moveToLocation(playerId, targetLocationId);
            
            // Send updated state
            sendStateInit(session, playerId);
        } catch (Exception e) {
            sendError(session, "Failed to move location: " + e.getMessage());
        }
    }
    
    /**
     * Handles ATTACK_TARGET command.
     */
    private void handleAttackTarget(Session session, String playerId, ObjectNode json) {
        try {
            String targetUnitId = json.has("targetUnitId") ? json.get("targetUnitId").asText() : null;
            
            if (targetUnitId == null) {
                sendError(session, "Missing 'targetUnitId' field");
                return;
            }
            
            // TODO: Implement combat logic
            // For now, just send an alert
            sendAlert(playerId, "Combat system not yet implemented");
        } catch (Exception e) {
            sendError(session, "Failed to attack target: " + e.getMessage());
        }
    }
    
    /**
     * Handles EQUIP_ITEM command.
     */
    private void handleEquipItem(Session session, String playerId, ObjectNode json) {
        try {
            String itemId = json.has("itemId") ? json.get("itemId").asText() : null;
            String slot = json.has("slot") ? json.get("slot").asText() : null;
            
            if (itemId == null || slot == null) {
                sendError(session, "Missing 'itemId' or 'slot' field");
                return;
            }
            
            // Find the item in inventory
            List<PlayerItemEntity> inventory = inventoryService.getInventory(playerId);
            PlayerItemEntity item = inventory.stream()
                .filter(i -> i.itemId.equals(itemId) && "INVENTORY".equals(i.slot))
                .findFirst()
                .orElse(null);
            
            if (item == null) {
                sendError(session, "Item not found in inventory");
                return;
            }
            
            inventoryService.equipItem(playerId, item.id, slot);
            
            // Send updated state
            sendStateInit(session, playerId);
        } catch (Exception e) {
            sendError(session, "Failed to equip item: " + e.getMessage());
        }
    }
    
    /**
     * Handles SELL_ITEM command.
     */
    private void handleSellItem(Session session, String playerId, ObjectNode json) {
        try {
            String itemId = json.has("itemId") ? json.get("itemId").asText() : null;
            String storeId = json.has("storeId") ? json.get("storeId").asText() : null;
            @SuppressWarnings("unused")
            int quantity = json.has("quantity") ? json.get("quantity").asInt() : 1;
            
            if (itemId == null || storeId == null) {
                sendError(session, "Missing 'itemId' or 'storeId' field");
                return;
            }
            
            // TODO: Implement store/selling logic
            // For now, just send an alert
            sendAlert(playerId, "Store system not yet implemented");
        } catch (Exception e) {
            sendError(session, "Failed to sell item: " + e.getMessage());
        }
    }
    
    // ========== Utility Methods ==========
    
    private void sendMessage(Session session, String message) {
        try {
            session.getBasicRemote().sendText(message);
        } catch (Exception e) {
            System.err.println("Error sending message: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void sendError(Session session, String errorMessage) {
        try {
            ObjectNode error = objectMapper.createObjectNode();
            error.put("type", "error");
            error.put("message", errorMessage);
            sendMessage(session, objectMapper.writeValueAsString(error));
        } catch (Exception e) {
            System.err.println("Error sending error message: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private String getPlayerIdFromSession(Session session) {
        // Try to get from query parameters
        String query = session.getQueryString();
        if (query != null && query.contains("playerId=")) {
            String[] params = query.split("&");
            for (String param : params) {
                if (param.startsWith("playerId=")) {
                    return param.substring("playerId=".length());
                }
            }
        }
        
        return null;
    }
}
