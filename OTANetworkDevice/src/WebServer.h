#ifndef WEBSERVER_H
    #define WEBSERVER_H

    #include <ESP8266WiFi.h>

    #include "OTANetworkDevice.h"

    void setupWebServer();
    void handleWebServer(OTANetworkDevice*);
#endif