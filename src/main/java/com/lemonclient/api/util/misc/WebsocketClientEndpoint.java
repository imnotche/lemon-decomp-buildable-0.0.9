// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.api.util.misc;

import javax.websocket.*;
import java.net.URI;
import java.io.IOException;

@ClientEndpoint
public class WebsocketClientEndpoint
{
    Session userSession;
    private MessageHandler messageHandler;
    
    public int getUserSession() {
        return (this.userSession != null) ? 1 : 0;
    }
    
    public void close() {
        try {
            if (this.userSession != null) {
                this.userSession.close();
            }
        }
        catch (final IOException | NullPointerException ex) {}
    }
    
    public WebsocketClientEndpoint(final URI endpointURI) {
        this.userSession = null;
        try {
            Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
            final WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.userSession = container.connectToServer(this, endpointURI);
        }
        catch (final Exception ex) {}
    }
    
    @OnOpen
    public void onOpen(final Session userSession) {
        System.out.println("opening websocket");
        this.userSession = userSession;
    }
    
    @OnClose
    public void onClose(final Session userSession, final CloseReason reason) {
        System.out.println("closing websocket");
        this.userSession = null;
    }
    
    @OnMessage
    public void onMessage(final String message) {
        if (this.messageHandler != null) {
            this.messageHandler.handleMessage(message);
        }
    }
    
    public void addMessageHandler(final MessageHandler msgHandler) {
        this.messageHandler = msgHandler;
    }
    
    public void sendMessage(final String message) {
        final RemoteEndpoint.Async remoteEndpoint = this.userSession.getAsyncRemote();
        remoteEndpoint.sendText(message);
    }
    
    public interface MessageHandler
    {
        void handleMessage(final String p0);
    }
}
