package com.framework.api;

import com.framework.data.entity.PlayerEntity;
import com.framework.data.entity.PlayerItemEntity;
import com.framework.service.logic.ActionService;
import com.framework.service.logic.InventoryService;
import com.framework.service.logic.PlayerService;
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
 */
@ServerEndpoint("/game")
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
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @OnOpen
    public void onOpen(Session session) {
        // Extract player ID from query parameters or session attributes
        String playerId = getPlayerIdFromSession(session);
        if (playerId != null) {
            sessions.put(playerId, session);
            session.getUserProperties().put("playerId", playerId);
            
            // Get or create player on connection
            try {
                PlayerEntity player = playerService.getOrCreatePlayer(playerId);
                sendJsonResponse(session, "connected", Map.of("playerId", playerId, "playerName", player.name));
            } catch (Exception e) {
                sendError(session, "Failed to initialize player: " + e.getMessage());
            }
            
            System.out.println("Player connected: " + playerId);
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
            String action = json.has("action") ? json.get("action").asText() : null;
            
            if (action == null) {
                sendError(session, "Missing 'action' field in message");
                return;
            }
            
            // Route to appropriate handler
            switch (action) {
                case "startAction":
                    handleStartAction(session, playerId, json);
                    break;
                case "processActionTick":
                    handleProcessActionTick(session, playerId, json);
                    break;
                case "stopAction":
                    handleStopAction(session, playerId);
                    break;
                case "getInventory":
                    handleGetInventory(session, playerId);
                    break;
                case "equipItem":
                    handleEquipItem(session, playerId, json);
                    break;
                case "getPlayer":
                    handleGetPlayer(session, playerId);
                    break;
                default:
                    sendError(session, "Unknown action: " + action);
            }
        } catch (Exception e) {
            System.err.println("Error processing message: " + e.getMessage());
            e.printStackTrace();
            sendError(session, "Error processing message: " + e.getMessage());
        }
    }
    
    private void handleStartAction(Session session, String playerId, ObjectNode json) {
        try {
            String actionId = json.has("actionId") ? json.get("actionId").asText() : null;
            if (actionId == null) {
                sendError(session, "Missing 'actionId' field");
                return;
            }
            
            actionService.startAction(playerId, actionId);
            sendJsonResponse(session, "actionStarted", Map.of("actionId", actionId));
        } catch (Exception e) {
            sendError(session, "Failed to start action: " + e.getMessage());
        }
    }
    
    private void handleProcessActionTick(Session session, String playerId, ObjectNode json) {
        try {
            String actionId = json.has("actionId") ? json.get("actionId").asText() : null;
            double elapsedSeconds = json.has("elapsedSeconds") ? json.get("elapsedSeconds").asDouble() : 0.0;
            
            if (actionId == null) {
                sendError(session, "Missing 'actionId' field");
                return;
            }
            
            actionService.processActionTick(playerId, actionId, elapsedSeconds);
            sendJsonResponse(session, "actionTickProcessed", Map.of("actionId", actionId, "elapsedSeconds", elapsedSeconds));
        } catch (Exception e) {
            sendError(session, "Failed to process action tick: " + e.getMessage());
        }
    }
    
    private void handleStopAction(Session session, String playerId) {
        try {
            actionService.stopAction(playerId);
            sendJsonResponse(session, "actionStopped", Map.of());
        } catch (Exception e) {
            sendError(session, "Failed to stop action: " + e.getMessage());
        }
    }
    
    private void handleGetInventory(Session session, String playerId) {
        try {
            List<PlayerItemEntity> inventory = inventoryService.getInventory(playerId);
            sendJsonResponse(session, "inventory", Map.of("items", inventory));
        } catch (Exception e) {
            sendError(session, "Failed to get inventory: " + e.getMessage());
        }
    }
    
    private void handleEquipItem(Session session, String playerId, ObjectNode json) {
        try {
            Long playerItemId = json.has("playerItemId") ? json.get("playerItemId").asLong() : null;
            String slot = json.has("slot") ? json.get("slot").asText() : null;
            
            if (playerItemId == null || slot == null) {
                sendError(session, "Missing 'playerItemId' or 'slot' field");
                return;
            }
            
            PlayerItemEntity item = inventoryService.equipItem(playerId, playerItemId, slot);
            sendJsonResponse(session, "itemEquipped", Map.of("item", item));
        } catch (Exception e) {
            sendError(session, "Failed to equip item: " + e.getMessage());
        }
    }
    
    private void handleGetPlayer(Session session, String playerId) {
        try {
            PlayerEntity player = playerService.getPlayer(playerId);
            if (player != null) {
                sendJsonResponse(session, "player", Map.of("player", player));
            } else {
                sendError(session, "Player not found");
            }
        } catch (Exception e) {
            sendError(session, "Failed to get player: " + e.getMessage());
        }
    }
    
    /**
     * Sends a message to a specific player.
     * @param playerId The player ID
     * @param message The message to send
     */
    public void sendToPlayer(String playerId, String message) {
        Session session = sessions.get(playerId);
        if (session != null && session.isOpen()) {
            sendMessage(session, message);
        }
    }
    
    /**
     * Broadcasts a message to all connected players.
     * @param message The message to broadcast
     */
    public void broadcast(String message) {
        sessions.values().forEach(session -> {
            if (session.isOpen()) {
                sendMessage(session, message);
            }
        });
    }
    
    private void sendMessage(Session session, String message) {
        try {
            session.getBasicRemote().sendText(message);
        } catch (Exception e) {
            System.err.println("Error sending message: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void sendJsonResponse(Session session, String type, Map<String, Object> data) {
        try {
            ObjectNode response = objectMapper.createObjectNode();
            response.put("type", type);
            ObjectNode dataNode = objectMapper.valueToTree(data);
            response.set("data", dataNode);
            sendMessage(session, objectMapper.writeValueAsString(response));
        } catch (Exception e) {
            System.err.println("Error sending JSON response: " + e.getMessage());
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
        
        // Try to get from session user properties
        Object playerId = session.getUserProperties().get("playerId");
        if (playerId != null) {
            return playerId.toString();
        }
        
        return null;
    }
}
