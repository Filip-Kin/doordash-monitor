package com.filipkin.doordashhelperserver;

import static com.filipkin.doordashhelperserver.MainActivity.wsServer;

import android.app.Notification;
import android.content.Intent;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.google.gson.Gson;

import java.util.List;

public class NotificationMonitor extends NotificationListenerService {

    private final String TITLE_PATTERN = "^\\[DD\\] \\$(\\d+\\.\\d{2}) \\(tip: \\$(\\d+\\.\\d{2})( ✨)*\\) - (.+)$";
    private final String TEXT_PATTERN = "^(Subtotal: \\$(\\d+\\.\\d{2}))*.+\\nDrive time: (\\d+) mins$";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        try {
            Notification n = sbn.getNotification();
            if (!(sbn.getPackageName().equals("com.paramobile") || sbn.getPackageName().equals("com.matusmak.fakenotifications")))
                return;
            if (!n.extras.getString("android.title").startsWith("[DD]")) return;
            List<String> titleMatches = Utils.regexMatch(n.extras.getString("android.title"), TITLE_PATTERN);
            List<String> textMatches = Utils.regexMatch(n.extras.getString("android.text"), TEXT_PATTERN);
            Log.i("matches", titleMatches.toString());
            Log.i("matches", textMatches.toString());

            ParaOffer offer = new ParaOffer(
                    Double.parseDouble(titleMatches.get(1)),
                    Double.parseDouble(titleMatches.get(2)),
                    (textMatches.get(1) == null) ? 0.0 : Double.parseDouble(textMatches.get(2)),
                    Integer.parseInt(textMatches.get(3)),
                    (titleMatches.get(3) != null),
                    (titleMatches.get(4) == null ? "Unknown" : titleMatches.get(4))
            );
            wsServer.sendMessage(offer.toJson());
        } catch (Exception e) {
            e.printStackTrace();
            Utils.logError(getApplicationContext(), e);
        }
    }

    public class ParaOffer {
        String type = "offer";
        double amount;
        double tip;
        double subtotal;
        int driveTime;
        double hourly;
        boolean confident;
        String store;

        public ParaOffer(double amount, double tip, double subtotal, int driveTime, boolean confident, String store) {
            this.amount = amount;
            this.tip = tip;
            this.subtotal = subtotal;
            this.driveTime = driveTime;
            this.hourly = Math.round((amount / (driveTime / 60.0))*100) / 100.0;
            this.confident = confident;
            this.store = store;
        }

        public String toJson() {
            Gson gson = new Gson();
            return gson.toJson(this);
        }
    }
}
