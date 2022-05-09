package com.filipkin.doordashhelperserver;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

public class ScreenReader extends AccessibilityService {

    public class Screen {
        public static final int DASH_START = 1;
        public static final int LOOKING_FOR_ORDERS = 2;
        public static final int OFFER = 3;
        public static final int OFFER_DECLINE_CONFIRMATION = 4;
        public static final int PICKUP = 5;
        public static final int HELP_DURING_PICKUP = 6;
        public static final int ORDER_UNASSIGN_CONFIRMATION = 7;
        public static final int END_DASH_CONFIRMATION = 8;
        public static final int MAIN_MENU = 9;
        public static final int DASH_START_SELECT_ENDTIME = 10;
        public static final int DELIVERY = 11;
        public static final int DELIVERY_PICTURE = 12;
        public static final int DELIVERY_DROPOFF = 13;
        public static final int DELIVERY_CONFIRM = 14;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        try {
            if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
                AccessibilityNodeInfo source = event.getSource();
                if (source == null) {
                    return;
                }

                // List all text for debug
                List<CharSequence> allText = Utils.getAllChildNodeText(source);
                int screen = getScreenType(allText);
                Log.v("Screen", String.valueOf(screen));
                for (int i = 0; i < allText.size(); i++) {
                    Log.v("Child " + i, allText.get(i).toString());
                }

                Utils.appendLog(getApplicationContext(), "Screen " + screen + " " + allText.toString());
                if (screen == Screen.DELIVERY) {
                    int addy = allText.indexOf("Delivery for")+2;
                    if (!allText.get(addy).equals("Directions")) {
                        MainActivity.wsServer.send("{\"type\": \"address\", \"address\": \""+allText.get(addy)+"\"}");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Utils.logError(getApplicationContext(), e);
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
        if (text.equalsIgnoreCase("Before you begin")) return Screen.DASH_START;
        if (text.equalsIgnoreCase("Looking for Orders")) return Screen.LOOKING_FOR_ORDERS;
        if (text.equalsIgnoreCase("Decline")) return Screen.OFFER;
        if (text.equalsIgnoreCase("Are you sure you want to decline this order?"))
            return Screen.OFFER_DECLINE_CONFIRMATION;
        if (text.startsWith("Pick up by")) return Screen.PICKUP;
        if (text.equalsIgnoreCase("Current Dash")) return Screen.HELP_DURING_PICKUP;
        if (text.equalsIgnoreCase("Your completion rate will drop to:"))
            return Screen.ORDER_UNASSIGN_CONFIRMATION;
        if (text.startsWith("Demand is high for dashers"))
            return Screen.END_DASH_CONFIRMATION;
        if (text.equalsIgnoreCase("Dash")) return Screen.MAIN_MENU;
        if (text.equalsIgnoreCase("Dash Now")) return Screen.DASH_START_SELECT_ENDTIME;
        if (text.startsWith("Deliver by")) return Screen.DELIVERY;
        if (text.equalsIgnoreCase("From Camera")) return Screen.DELIVERY_PICTURE;
        if (text.startsWith("Drop-off")) return Screen.DELIVERY_DROPOFF;
        if (text.equalsIgnoreCase("Rate this delivery")) return Screen.DELIVERY_CONFIRM;
        return 0;
    }
}
