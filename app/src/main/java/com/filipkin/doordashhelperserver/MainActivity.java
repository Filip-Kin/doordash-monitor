package com.filipkin.doordashhelperserver;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    public static WebSocketConn wsServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            startService(new Intent(this, NotificationMonitor.class));
            startService(new Intent(this, ScreenReader.class));

            String id = getDeviceID();
            wsServer = new WebSocketConn(getApplicationContext(), id);
            wsServer.connect();
        } catch (Exception e) {
            e.printStackTrace();
            Utils.logError(getApplicationContext(), e);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void startWSServer(View view) {
        if (WebSocketConn.wsServerRunning) return;
        wsServer.connect();
    }

    private String getDeviceID() {
        SharedPreferences prefs = getSharedPreferences("Share", Context.MODE_PRIVATE );
        String id = prefs.getString("id", UUID.randomUUID().toString().substring(0, 8)); // s2 is used as default if not found
        Log.i("id", id);
        // Save ID so it's persistent
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("id", id);
        editor.apply();
        setContentView(R.layout.activity_main);
        ((TextView) findViewById(R.id.ipAddress)).setText(id);
        return id;
    }
}