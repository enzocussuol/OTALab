#ifndef DISPOSITIVOOTAWS_H
    #define DISPOSITIVOOTAWS_H

    #include <ESP8266WiFi.h>
    #include <ESP8266mDNS.h>
    #include <WiFiUdp.h>
    #include <ArduinoOTA.h>

    using namespace std;

    class Dispositivo{
        private:
            String nome;
            String placa;
            IPAddress ip;

            String getNome() const;
            String getPlaca() const;
            IPAddress getIp() const;
            void setIp(IPAddress);
        public:
            Dispositivo(int);
            void start();
            void handle();
    };
#endif
