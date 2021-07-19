#ifndef MQTT_H
    #define MQTT_H

    #include <PubSubClient.h>
    #include <ESP8266WiFi.h>

    #include "Conf.h"

    void setupMQTT();
    void handleMQTT();
#endif