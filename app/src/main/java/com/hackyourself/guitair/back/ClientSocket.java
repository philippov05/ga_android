package com.hackyourself.guitair.back;

import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.handshake.ServerHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Date;

/**
 * Created by don on 23.04.2016.
 */
public class ClientSocket extends WebSocketClient {

    private final String LOG_TAG = "client";

    private WebSocket client;

    public ClientSocket(URI serverURI) {
        super(serverURI);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        long time = new Date().getTime();
        Log.d(LOG_TAG, "Client os connected ...");
        try {
            this.send(new JSONObject()
                    .put("time", time)
                    .put("movement", Movement.START).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessage(String message) {

    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Log.d(LOG_TAG, "close with code " + code + ", with reason: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        Log.d(LOG_TAG, "ERROR : " + ex);
    }
}
