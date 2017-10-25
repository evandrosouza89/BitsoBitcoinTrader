package com.evandro.challenges.bitcointrader.controller.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.tyrus.client.ClientManager;
import org.glassfish.tyrus.client.ClientProperties;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;

@ClientEndpoint
public class WebSocketClient {

    private final Logger logger = LogManager.getLogger();

    private Session userSession = null;

    private MessageHandler messageHandler;

    public WebSocketClient(URI endpointURI) {
            ClientManager client = ClientManager.createClient();
            client.getProperties().put(ClientProperties.REDIRECT_ENABLED, true);
        try {
            client.connectToServer(this, endpointURI);
        } catch (DeploymentException | IOException e) {
            logger.warn(e);
        }
    }

    @OnOpen
    public void onOpen(Session userSession) {
        this.userSession = userSession;
    }

    @OnClose
    public void onClose(Session userSession, CloseReason reason) {
        this.userSession = null;
    }

    @OnMessage
    public void onMessage(String message) {
        if (this.messageHandler != null) {
            this.messageHandler.handleMessage(message);
        }
    }

    public void addMessageHandler(MessageHandler msgHandler) {
        this.messageHandler = msgHandler;
    }

    public void sendMessage(String message) {
        this.userSession.getAsyncRemote().sendText(message);
    }

    public interface MessageHandler {

        void handleMessage(String message);
    }
}