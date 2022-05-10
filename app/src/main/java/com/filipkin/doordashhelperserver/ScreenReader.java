package com.filipkin.doordashhelperserver;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ScreenReader extends AccessibilityService {

    private DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");

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

                double thisDash = getThisDashIncome(allText);

                if (screen == Screen.LOOKING_FOR_ORDERS) {
                    MainActivity.wsServer.send("{\"type\": \"looking\", \"thisdash\": "+thisDash+"}");
                } else if (screen == Screen.OFFER) {
                    int dotIndex = allText.indexOf("•");

                    double distance = -1.0;
                    if (allText.get(dotIndex + 1).toString().contains("mi")) {
                        distance = Double.parseDouble(allText.get(dotIndex + 1).toString().replace(" mi", ""));
                    }

                    int amountIndex = dotIndex + 1;
                    if (distance > -1) {
                        amountIndex++;
                    }
                    double amount = Double.parseDouble(allText.get(amountIndex).toString().replace("$", ""));

                    Date due = TIME_FORMAT.parse(allText.get(2).toString().replace("Deliver by ", ""));
                    long time = due.getTime() - (new Date()).getTime();

                    MainActivity.wsServer.send(new Offer(
                            amount,
                            distance,
                            (int) TimeUnit.MILLISECONDS.toMinutes(time),
                            Integer.parseInt(allText.get(dotIndex - 1).toString().replace(" item", "").replace("s", "")),
                            allText.get(3).toString()
                    ).toJson());
                } else if (screen == Screen.DELIVERY) {
                    int addy = allText.indexOf("Delivery for")+2;
                    if (!allText.get(addy).equals("Directions")) {
                        MainActivity.wsServer.send("{\"type\": \"address\", \"address\": \""+allText.get(addy)+"\", \"thisdash\": "+thisDash+"}");
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

    private double getThisDashIncome(List<CharSequence> allText) {
        int thisDashIndex = allText.indexOf("this dash");
        if (thisDashIndex > -1) {
            return Double.parseDouble(allText.get(thisDashIndex).toString().substring(1));
        } else {
            return -1;
        }
    }

    public class Offer {
        String type = "offer";
        double amount;
        double distance;
        int driveTime;
        double hourly;
        double perMile;
        int items;
        String store;

        public Offer(double amount, double distance, int driveTime, int items, String store) {
            this.amount = amount;
            this.distance = distance;
            this.driveTime = driveTime;
            this.hourly = Math.round((amount / (driveTime / 60.0))*100) / 100.0;
            if (distance == 0) {
                this.perMile = this.amount;
            } else if (distance < 0) {
                this.perMile = -1;
            } else {
                this.perMile = Math.round((amount / distance)*100) / 100.0;
            }
            this.items = items;
            this.store = store;
        }

        public String toJson() {
            Gson gson = new Gson();
            return gson.toJson(this);
        }
    }
}
