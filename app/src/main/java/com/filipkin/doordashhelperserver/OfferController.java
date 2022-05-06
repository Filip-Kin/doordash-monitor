package com.filipkin.doordashhelperserver;

import android.content.Context;

import java.io.PrintWriter;
import java.io.StringWriter;

import ru.skornei.restserver.annotations.ExceptionHandler;
import ru.skornei.restserver.annotations.Produces;
import ru.skornei.restserver.annotations.RestController;
import ru.skornei.restserver.annotations.methods.GET;
import ru.skornei.restserver.server.dictionary.ContentType;
import ru.skornei.restserver.server.protocol.RequestInfo;
import ru.skornei.restserver.server.protocol.ResponseInfo;

@RestController("/offer")
public class OfferController {

    public static OfferEntity lastOffer = new OfferEntity();

    @GET
    @Produces(ContentType.APPLICATION_JSON)
    public OfferEntity offer(Context context, RequestInfo request, ResponseInfo response) {
        return lastOffer;
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