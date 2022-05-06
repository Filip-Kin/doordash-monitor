package com.filipkin.doordashhelperserver;

import static com.filipkin.doordashhelperserver.Utils.getIPAddress;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.filipkin.doordashhelperserver.web.MainServer;

import java.io.IOException;

import ru.skornei.restserver.RestServerManager;

public class MainActivity extends AppCompatActivity {

    private MainServer mainServer = new MainServer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((TextView) findViewById(R.id.ipAddress)).setText(getIPAddress(true));

        RestServerManager.initialize(this.getApplication());
        try {
            mainServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        startService(new Intent(this, NotificationMonitor.class));

        Log.i("APP", "test");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mainServer.stop();
    }
}