package com.filipkin.doordashhelperserver;

import static java.text.DateFormat.getTimeInstance;

import android.content.Context;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        boolean isIPv4 = sAddr.indexOf(':') < 0;

                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                return delim < 0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        } // for now eat exceptions
        return "";
    }

    public static List<String> regexMatch(String str, String pattern) {
        List<String> allMatches = new ArrayList<String>();
        Matcher m = Pattern.compile(pattern).matcher(str);
        m.find();
        for (int i = 0; i <= m.groupCount(); i++) {
            allMatches.add(m.group(i));
        }
        return allMatches;
    }

    public static List<CharSequence> getAllChildNodeText(AccessibilityNodeInfo info) {
        List<CharSequence> contents = new ArrayList<>();
        if (info == null)
            return contents;
        if (info.getContentDescription() != null) {
            contents.add(info.getContentDescription().toString().isEmpty() ? "unlabelled" : info.getContentDescription());
        } else if (info.getText() != null) {
            contents.add(info.getText().toString().isEmpty() ? "unlabelled" : info.getText());
        } else {
            getTextInChildren(info, contents);
        }
        if (info.isClickable()) {
            if (info.getClassName().toString().contains(Button.class.getSimpleName())) {
                if (contents.size() == 0) {
                    contents.add("Unlabelled button");
                } else {
                    contents.add("button");
                }
            }
            contents.add("Double tap to activate");
        }
        return contents;
    }

    public static void getTextInChildren(AccessibilityNodeInfo nodeInfo, List<CharSequence> contents) {
        if (nodeInfo == null)
            return;
        if (!nodeInfo.isScrollable()) {
            if (nodeInfo.getContentDescription() != null) {
                contents.add(nodeInfo.getContentDescription());
            } else if (nodeInfo.getText() != null) {
                contents.add(nodeInfo.getText());
            }
            if (nodeInfo.getChildCount() > 0) {
                for (int i = 0; i < nodeInfo.getChildCount(); i++) {
                    if (nodeInfo.getChild(i) != null) {
                        getTextInChildren(nodeInfo.getChild(i), contents);
                    }
                }
            }
        }
    }

    public static void appendLog(Context context, String text) {
        File logFile = new File(context.getFilesDir(), DATE_FORMAT.format(new Date())+"-log.txt");
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));

            buf.append("[" + TIME_FORMAT.format(new Date()) + "] " + text);
            buf.newLine();
            buf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void logError(Context context, Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        Utils.appendLog(context, sw.toString());
    }
}
