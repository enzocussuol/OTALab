#ifndef MQTT_H
    #define MQTT_H

    #include <PubSubClient.h>
    #include <string>

    #include "OTALabDevice.h"

    void setupMQTT(String);
    void handleMQTT();
#endif