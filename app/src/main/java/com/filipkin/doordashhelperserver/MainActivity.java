package com.filipkin.doordashhelperserver;

import static com.filipkin.doordashhelperserver.Utils.getIPAddress;
import static com.filipkin.doordashhelperserver.Utils.logError;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    public static SocketServer wsServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            // Fetch or generate an ID for this device
            String id = getDeviceID();
            // Give IP to the mothership
            String ip = getIPAddress(true);
            Log.i("ID response", String.valueOf(sendIP(id, ip)));

            startService(new Intent(this, NotificationMonitor.class));
            startService(new Intent(this, ScreenReader.class));

            InetSocketAddress inetSockAddress = new InetSocketAddress("0.0.0.0", 8080);
            wsServer = new SocketServer(getApplicationContext(), inetSockAddress);
            wsServer.start();
        } catch (Exception e) {
            e.printStackTrace();
            Utils.logError(getApplicationContext(), e);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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

    private int sendIP(String id, String ip) {
        try {
            String urlString = "https://dd.filipkin.com/app/"+id+"/"+ip;
            Log.v("URL", urlString);
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            return conn.getResponseCode();
        } catch (Exception e) {
            e.printStackTrace();
            logError(getApplicationContext(), e);
            return 500;
        }
    }
}