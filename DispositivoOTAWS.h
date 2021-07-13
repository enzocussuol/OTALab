#ifndef DISPOSITIVOOTAWS_H
    #define DISPOSITIVOOTAWS_H

    #include <ESP8266WiFi.h>
    #include <ESP8266mDNS.h>
    #include <WiFiUdp.h>
    #include <ArduinoOTA.h>
    #include <PubSubClient.h>
    #include <list>
    #include "Sensor.h"

    #define esp8266D1Mini 1
    #define esp8266NodeMCU 2

    class Dispositivo{
        private:
            String nome;
            String placa;
            IPAddress ip;
            std::list<Sensor*>* sensores;
        public:
            Dispositivo(int);
            String getNome() const;
            String getPlaca() const;
            IPAddress getIp() const;
            void setIp(IPAddress);
            std::list<Sensor*>* getSensores() const;
            void start();
            void handle();
    };
#endif