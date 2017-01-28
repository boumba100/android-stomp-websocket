package nbouma.com.wstompclient.implementation;


import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

import nbouma.com.wstompclient.model.Frame;
import nbouma.com.wstompclient.services.StompClientProvider;


public abstract class StompClient implements StompClientProvider {

    private static final Draft DEFAULT_DRAFT = new Draft_17();
    private WebSocketClient webSocketClient;
    private boolean isWebSocketConnected = false;

    protected StompClient(final String destination, final Frame frame, final Draft draft) {
        this.connectWebSocket(destination, frame, draft);
    }

    protected StompClient(final String destination) {
        this(destination, new Frame().setConnection(destination), DEFAULT_DRAFT);
    }

    protected StompClient(final String destination, final Frame frame) {
        this(destination, frame, DEFAULT_DRAFT);
    }


    protected abstract void onStompError(final String errorMessage);

    protected abstract void onConnection(final boolean connected);

    protected abstract void onDisconnection(final String reason);

    protected abstract void onStompMessage(final Frame frame);


    @Override
    public void disconnect() {
        this.transmitFrame(new Frame().setDisconnection());
        this.webSocketClient.close();
        this.setWebSocketConnected(false);
        onDisconnection("Device disconnected from the server");
    }

    @Override
    public void subscribe(final String destination, final String id) {
        this.transmitFrame(new Frame().setSubscribe(destination, id));
    }

    @Override
    public void subscribe(final String destination) {
        this.subscribe(destination, "sub-0");
    }

    @Override
    public void unSubscribe(final String destination, final String id) {
        this.transmitFrame(new Frame().setUnsubscribe(destination, id));
    }

    @Override
    public void unSubscribe(final String destination) {
        this.subscribe(destination, "sub-0");
    }

    @Override
    public void sendMessage(final String destination, final String body) {
        this.transmitFrame(new Frame().setSend(destination, body));
    }

    @Override
    public void sendFrame(final Frame frame) {
        this.transmitFrame(frame);
    }


    private void transmitFrame(final Frame frame) {
        if (this.webSocketClient != null && this.isWebSocketConnected) {
            this.webSocketClient.send(frame.toString());
        } else {
            onStompError("Device not connected to the server");
        }
    }

    private void connectStomp(final Frame connectionFrame) {
        if (this.webSocketClient != null && this.isWebSocketConnected) {
            this.webSocketClient.send(connectionFrame.toString());
        }
    }

    private void connectWebSocket(final String destination, final Frame frame, final Draft draft) {
        URI destinationURI;
        try {
            destinationURI = new URI(destination);
            this.webSocketClient = new WebSocketClient(destinationURI, draft) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    connectStomp(frame);
                    setWebSocketConnected(true);
                    onConnection(true);
                }

                @Override
                public void onMessage(String message) {
                    Frame frame = new Frame().parseFrame(message);
                    onStompMessage(frame);
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    onDisconnection(reason);
                }

                @Override
                public void onError(Exception ex) {
                    onStompError(ex.toString());
                }
            };
        } catch (URISyntaxException e) {
            e.printStackTrace();
            onStompError(e.toString());
        }
        this.webSocketClient.connect();
    }

    private void setWebSocketConnected(final boolean connected) {
        this.isWebSocketConnected = connected;
    }

}
