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
        String text = allText.get(1).toString();
        if (text.equals("Before you begin")) return DoordashScreen.DASH_START;
        if (text.equals("Looking for orders")) return DoordashScreen.LOOKING_FOR_ORDERS;
        if (text.equals("Decline")) return DoordashScreen.OFFER;
        if (text.equals("Are you sure you want to decline this order?"))
            return DoordashScreen.OFFER_DECLINE_CONFIRMATION;
        if (text.startsWith("Pick up by")) return DoordashScreen.PICKUP;
        if (text.equals("Current Dash")) return DoordashScreen.HELP_DURING_PICKUP;
        if (text.equals("Your completion rate will drop to:"))
            return DoordashScreen.ORDER_UNASSIGN_CONFIRMATION;
        if (text.startsWith("Demand is high for dashers"))
            return DoordashScreen.END_DASH_CONFIRMATION;
        if (text.equals("Dash")) return DoordashScreen.MAIN_MENU;
        if (text.equals("Dash Now")) return DoordashScreen.DASH_START_SELECT_ENDTIME;
        return 0;
    }
}
