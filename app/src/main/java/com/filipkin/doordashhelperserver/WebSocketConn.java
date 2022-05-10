package com.filipkin.doordashhelperserver;

import android.app.Activity;
import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.filipkin.doordashhelperserver.Utils;

import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.handshake.ServerHandshake;

import java.net.InetSocketAddress;
import java.net.URI;

public class WebSocketConn extends WebSocketClient {

    private String id;
    public static boolean wsServerRunning = false;

    public WebSocketConn(String id) {
        super(URI.create("wss://dd.filipkin.com:9008/"));
        this.id = id;
        Log.i("WS", "Socket object created");
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        wsServerRunning = true;
        this.send("ESTABLISH SERVER:"+this.id);
        Log.i("WS", "WS Connected");
    }

    @Override
    public void onMessage(String msg) {
        Log.i("WS", msg);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Log.i("WS", "WS Disconnected");
    }

    @Override
    public void onError(Exception e) {
        e.printStackTrace();
    }
}
