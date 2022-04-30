package com.example.idatt2106_2022_05_backend.util;

import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@ServerEndpoint("/chat/{id}")
public class WebSocket {
    private javax.websocket.Session session;
    private static CopyOnWriteArraySet<WebSocket> webSockets = new CopyOnWriteArraySet<>();
    private static Map<String, javax.websocket.Session> sessionPool = new HashMap<String, javax.websocket.Session>();

    @OnOpen
    public void onOpen(javax.websocket.Session session, @PathParam(value="id")String id){
        this.session = session;
        webSockets.add(this);
        sessionPool.put(id, session);
    }

    @OnClose
    public void onClose() {
        webSockets.remove(this);
    }

    @OnMessage
    public void onMessage(String message) {
    }

    public void sendAllMessage(MessageObjectModel message) {
        for(WebSocket webSocket : webSockets) {
            try {
                webSocket.session.getAsyncRemote().sendText(message.getContent());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void sendOneMessage(String shopId, MessageObjectModel message) {
        javax.websocket.Session session = sessionPool.get(shopId);
        if (session != null) {
            try {
                session.getAsyncRemote().sendText(message.getContent());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
