#ifndef OTALABDEVICE_H
    #define OTALABDEVICE_H

    class OTALabDevice;

    #include <ESP8266WiFi.h>

    #include "OTA.h"
    #include "MQTT.h"
    #include "Config.h"

    class OTALabDevice{
        public:
            void setup(String id);
            void handle();
    };
#endif