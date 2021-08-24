#ifndef OTANETWORKDEVICE_H
    #define OTANETWORKDEVICE_H

    class OTANetworkDevice;

    #include <list>

    #include "Conf.h"
    #include "OTA.h"
    #include "WebServer.h"
    #include "MQTT.h"
    #include "Sensor.h"

    #define esp8266D1Mini 1
    #define esp8266NodeMCU 2

    class OTANetworkDevice{
        private:
            String nome;
            String placa;
            IPAddress ip;
            std::list<Sensor*>* sensores;
        public:
            OTANetworkDevice(int);
            void setup();
            void handle();
            String getNome() const;
            String getPlaca() const;
            IPAddress getIp() const;
            void setIp(IPAddress);
            std::list<Sensor*>* getSensores() const;
    };
#endif