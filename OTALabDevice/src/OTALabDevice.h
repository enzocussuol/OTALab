#ifndef OTALABDEVICE_H
    #define OTALABDEVICE_H

    #define WIFI_NETWORK_NAME ""
    #define WIFI_NETWORK_PASSWORD ""
    #define BROKER_IP ""
    #define DEVICE_NAME ""

    class OTALabDevice;

    #include "OTA.h"
    #include "MQTT.h"
    #include "WebServer.h"

    class OTALabDevice{
        private:
            const char* WiFiNetworkName;
            const char* WiFiNetworkPassword;
            String brokerIP;
            String name;
        public:
            void setup();
            void handle();
            
            void setWiFiNetworkName(const char*);
            void setWiFiNetworkPassword(const char*);
            void setBrokerIP(String);
            void setName(String);

            String getName() const;
    };
#endif