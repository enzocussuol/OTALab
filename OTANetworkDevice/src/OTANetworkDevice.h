#ifndef OTANETWORKDEVICE_H
    #define OTANETWORKDEVICE_H

    class OTANetworkDevice;

    #include <list>

    #include "OTA.h"
    #include "WebServer.h"
    #include "MQTT.h"
    #include "Sensor.h"

    class OTANetworkDevice{
        private:
            String WiFiNetworkName;
            String WiFiNetworkPassword;
            String brokerIP;

            String nome;
            String placa;
            IPAddress ip;
            std::list<Sensor*>* sensores;
        public:
            OTANetworkDevice();

            void setup();
            void handle();

            void setWiFiNetworkName(String);
            void setWiFiNetworkPassword(String);
            String getBrokerIP() const;
            void setBrokerIP(String);

            String getNome() const;
            String getPlaca() const;
            IPAddress getIp() const;
            void setIp(IPAddress);
            std::list<Sensor*>* getSensores() const;
    };
#endif