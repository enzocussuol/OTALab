#ifndef OTALABDEVICE_H
    #define OTALABDEVICE_H

    #define WIFI_NETWORK_NAME ""
    #define WIFI_NETWORK_PASSWORD ""
    #define BROKER_IP ""
    #define DEVICE_ID ""

    class OTALabDevice;

    #include "OTA.h"
    #include "MQTT.h"

    class OTALabDevice{
        public:
            void setup();
            void handle();
    };
#endif