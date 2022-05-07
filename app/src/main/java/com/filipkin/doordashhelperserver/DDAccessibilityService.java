package com.filipkin.doordashhelperserver;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

public class DDAccessibilityService extends AccessibilityService {
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            AccessibilityNodeInfo source = event.getSource();
            if (source == null) {
                return;
            }

            // List all text for debug
            List<CharSequence> allText = Utils.getAllChildNodeText(source);
            String screen = String.valueOf(getScreenType(allText));
            Log.v("Screen", screen);
            for (int i = 0; i < allText.size(); i++) {
                Log.v("Child " + i, allText.get(i).toString());
            }
            long time = event.getEventTime()/1000;
            long seconds = time % 60;
            long minutes = time / 60;
            long hours = minutes / 60;
            minutes = minutes % 60;

            Utils.appendLog(getApplicationContext(), "[" + hours + ":" + minutes + ":" + seconds + "] Screen " + screen + " " + allText.toString());
        }
    }

    @Override
    public void onInterrupt() {

    }

    /**
     * Figures out what page you're on in the dasher app
     *
     * @return int representing screen type
     */
    private int getScreenType(List<CharSequence> allText) {
        if (allText.size() < 2) return 0;
        String text = allText.get(1).toString();
        if (text.equalsIgnoreCase("Before you begin")) return DoordashScreen.DASH_START;
        if (text.equalsIgnoreCase("Looking for Orders")) return DoordashScreen.LOOKING_FOR_ORDERS;
        if (text.equalsIgnoreCase("Decline")) return DoordashScreen.OFFER;
        if (text.equalsIgnoreCase("Are you sure you want to decline this order?"))
            return DoordashScreen.OFFER_DECLINE_CONFIRMATION;
        if (text.startsWith("Pick up by")) return DoordashScreen.PICKUP;
        if (text.equalsIgnoreCase("Current Dash")) return DoordashScreen.HELP_DURING_PICKUP;
        if (text.equalsIgnoreCase("Your completion rate will drop to:"))
            return DoordashScreen.ORDER_UNASSIGN_CONFIRMATION;
        if (text.startsWith("Demand is high for dashers"))
            return DoordashScreen.END_DASH_CONFIRMATION;
        if (text.equalsIgnoreCase("Dash")) return DoordashScreen.MAIN_MENU;
        if (text.equalsIgnoreCase("Dash Now")) return DoordashScreen.DASH_START_SELECT_ENDTIME;
        if (text.startsWith("Deliver by")) return DoordashScreen.DELIVERY;
        if (text.equalsIgnoreCase("From Camera")) return DoordashScreen.DELIVERY_PICTURE;
        if (text.startsWith("Drop-off")) return DoordashScreen.DELIVERY_DROPOFF;
        if (text.equalsIgnoreCase("Rate this delivery")) return DoordashScreen.DELIVERY_CONFIRM;
        return 0;
    }
}
