package com.filipkin.doordashhelperserver;

import android.app.Notification;
import android.content.Intent;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

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
        Notification n = sbn.getNotification();
        System.out.println(sbn.getPackageName());
        if (!(sbn.getPackageName().equals("com.paramobile") || sbn.getPackageName().equals("com.matusmak.fakenotifications"))) return;
        System.out.println(sbn.getPackageName());

        List<String> titleMatches = Utils.regexMatch(n.extras.getString("android.title"), TITLE_PATTERN);
        List<String> textMatches = Utils.regexMatch(n.extras.getString("android.text"), TEXT_PATTERN);
        Log.i("matches", titleMatches.toString());
        Log.i("matches", textMatches.toString());

        OfferController.lastOffer = new OfferEntity(
                Double.parseDouble(titleMatches.get(1)),
                Double.parseDouble(titleMatches.get(2)),
                (textMatches.get(1) == null) ? 0.0 : Double.parseDouble(textMatches.get(2)),
                Integer.parseInt(textMatches.get(3)),
                (titleMatches.get(3) != null),
                titleMatches.get(4)
        );
    }
}