#ifndef MQTT_H
    #define MQTT_H

    #include <PubSubClient.h>
    #include <ESP8266WiFi.h>
    #include <string>

    #include "OTANetworkDevice.h"

    void setupMQTT(String);
    void handleMQTT(OTANetworkDevice*);
#endif