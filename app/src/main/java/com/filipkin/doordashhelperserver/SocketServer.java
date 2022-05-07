package com.filipkin.doordashhelperserver;

import android.content.Context;
import android.util.Log;

import com.filipkin.doordashhelperserver.Utils;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

import java.net.InetSocketAddress;

public class SocketServer extends org.java_websocket.server.WebSocketServer {

    private Context context;
    private WebSocket conn;

    public SocketServer(Context context, InetSocketAddress address) {
        super(address);
        this.context = context;
        Log.i("WSS", "Socket object created");
    }

    @Override
    public void onClose(WebSocket conn, int arg1, String arg2, boolean arg3) {
        this.conn = null;
    }

    @Override
    public void onError(WebSocket conn, Exception e) {
        Utils.logError(context, e);
    }

    @Override
    public void onStart() {
        Log.i("WSS", "Started WebSocket Server");
    }

    @Override
    public void onMessage(WebSocket arg0, String arg1) {
        // Handle message
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        this.conn = conn;
        Log.i("WSS", "New connection to " + conn.getRemoteSocketAddress());
    }

    public void sendMessage(String msg) {
        Log.i("WSS", msg);
        conn.send(msg);
    }
}
