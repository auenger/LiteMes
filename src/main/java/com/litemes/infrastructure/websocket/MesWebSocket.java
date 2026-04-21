package com.litemes.infrastructure.websocket;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.server.ServerEndpoint;
import jakarta.websocket.Session;
import org.jboss.logging.Logger;

import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket endpoint skeleton for real-time MES data push.
 * Production implementation will push production line status, alarm notifications, etc.
 */
@ApplicationScoped
@ServerEndpoint("/ws")
public class MesWebSocket {

    private static final Logger LOG = Logger.getLogger(MesWebSocket.class);

    private static final ConcurrentHashMap<String, Session> SESSIONS = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session) {
        SESSIONS.put(session.getId(), session);
        LOG.infof("WebSocket connected: %s", session.getId());
    }

    @OnClose
    public void onClose(Session session) {
        SESSIONS.remove(session.getId());
        LOG.infof("WebSocket disconnected: %s", session.getId());
    }

    @OnError
    public void onError(Session session, Throwable error) {
        SESSIONS.remove(session.getId());
        LOG.errorf("WebSocket error [%s]: %s", session.getId(), error.getMessage());
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        LOG.debugf("WebSocket message from %s: %s", session.getId(), message);
        // Placeholder: route incoming messages to domain services
    }

    /**
     * Broadcast a message to all connected sessions.
     */
    public void broadcast(String message) {
        SESSIONS.values().forEach(session -> {
            if (session.isOpen()) {
                session.getAsyncRemote().sendText(message);
            }
        });
    }
}
