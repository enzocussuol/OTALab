#ifndef WEBSERVER_H
    #define WEBSERVER_H

    #include <ESP8266WiFi.h>

    #include "Dispositivo.h"

    void setupWebServer();
    void handleWebServer(Dispositivo*);
#endif