package com.filipkin.doordashhelperserver.web;

import ru.skornei.restserver.annotations.RestServer;
import ru.skornei.restserver.server.BaseRestServer;

@RestServer( port = MainServer.PORT,
        converter = JsonConverter.class,
        controllers = {UIController.class, OfferController.class} )
public class MainServer extends BaseRestServer {
    public static final int PORT = 8080;
}