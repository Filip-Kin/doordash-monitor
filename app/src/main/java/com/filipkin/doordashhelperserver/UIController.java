package com.filipkin.doordashhelperserver;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import ru.skornei.restserver.annotations.ExceptionHandler;
import ru.skornei.restserver.annotations.Produces;
import ru.skornei.restserver.annotations.RestController;
import ru.skornei.restserver.annotations.methods.GET;
import ru.skornei.restserver.server.dictionary.ContentType;
import ru.skornei.restserver.server.protocol.ResponseInfo;

@RestController("/")
public class UIController {

    private final String HOME = "<HTML>\n" +
            "    <head>\n" +
            "        <link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/materialize/1.0.0/css/materialize.min.css\">\n" +
            "        <style>\n" +
            "            td {\n" +
            "                font-size: 24px;\n" +
            "            }\n" +
            "        </style>\n" +
            "    </head>\n" +
            "    <body>\n" +
            "        <div class=\"container\" style=\"width: 90%\">\n" +
            "            <div class=\"row\">\n" +
            "                <a href=\"#\" class=\"btn\" id=\"fullscreen-btn\">Full screen</a>\n" +
            "            </div>\n" +
            "            <div class=\"row\">\n" +
            "                <div class=\"col s8\">\n" +
            "                    <table>\n" +
            "                        <tr>\n" +
            "                            <td>Amount</td>\n" +
            "                            <td id=\"offer-amount\" style=\"font-weight: 900; font-size: 36px\"></td>\n" +
            "                        </tr>\n" +
            "                        <tr>\n" +
            "                            <td>Tip</td>\n" +
            "                            <td id=\"offer-tip\"></td>\n" +
            "                        </tr>\n" +
            "                        <tr>\n" +
            "                            <td>Order</td>\n" +
            "                            <td id=\"offer-subtotal\"></td>\n" +
            "                        </tr>\n" +
            "                        <tr>\n" +
            "                            <td>Store</td>\n" +
            "                            <td id=\"offer-store\"></td>\n" +
            "                        </tr>\n" +
            "                        <tr>\n" +
            "                            <td>Drive Time</td>\n" +
            "                            <td id=\"offer-drivetime\"></td>\n" +
            "                        </tr>\n" +
            "                        <tr>\n" +
            "                            <td>Estimated Hourly</td>\n" +
            "                            <td id=\"offer-hourly\"></td>\n" +
            "                        </tr>\n" +
            "                    </table>\n" +
            "                </div>\n" +
            "            </div>\n" +
            "        </div>\n" +
            "        <script>\n" +
            "            function padDecimal(str) {\n" +
            "                let parts = str.toString().split('.');\n" +
            "                if (parts[1] == undefined) parts[1] = '00';\n" +
            "                return `${parts[0]}.${parts[1].padEnd(2, '0')}`;\n" +
            "            }\n" +
            "\n" +
            "            let fsbtn = document.getElementById('fullscreen-btn');\n" +
            "            fsbtn.addEventListener('click', () => {\n" +
            "                document.documentElement.webkitRequestFullScreen();\n" +
            "                fsbtn.style.display = 'none';\n" +
            "            });\n" +
            "\n" +
            "            function updateOfferUI(offer) {\n" +
            "                document.getElementById('offer-amount').innerHTML = `$${padDecimal(offer.amount)}`;\n" +
            "                document.getElementById('offer-tip').innerHTML = `$${padDecimal(offer.tip)} ${offer.confident ? '&#10024;' : '&#10067;'}`;\n" +
            "                document.getElementById('offer-store').innerHTML = offer.store;\n" +
            "                document.getElementById('offer-drivetime').innerHTML = `${offer.driveTime} Mins`;\n" +
            "                document.getElementById('offer-hourly').innerHTML = `$${padDecimal(offer.hourly)}/hr`;\n" +
            "                document.getElementById('offer-subtotal').innerHTML = `$${padDecimal(offer.subtotal)}`;\n" +
            "            }\n" +
            "\n" +
            "            setInterval(() => {\n" +
            "                fetch('/offer').then(res => res.json()).then(updateOfferUI);\n" +
            "            }, 1e3);\n" +
            "        </script>\n" +
            "    </body>\n" +
            "</HTML>";

    public UIController() throws IOException {
    }

    @GET
    public void ui(ResponseInfo res) {
        res.setType("text/html");
        res.setBody(HOME.getBytes(StandardCharsets.UTF_8));
    }

    private static String getStackTrace(Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        throwable.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }

    @ExceptionHandler
    @Produces(ContentType.TEXT_PLAIN)
    public void handleThrowable(Throwable throwable, ResponseInfo response) {
        String throwableStr = getStackTrace(throwable);
        response.setBody(throwableStr.getBytes());
    }
}