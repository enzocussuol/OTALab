#line 1 "/home/enzo/ProjetoOTA/OTABlink/DispositivoOTAWS.h"
#ifndef DISPOSITIVOOTAWS_H
    #define DISPOSITIVOOTAWS_H

    #include <ESP8266WiFi.h>
    #include <ESP8266mDNS.h>
    #include <WiFiUdp.h>
    #include <ArduinoOTA.h>

    using namespace std;

    class Dispositivo{
        String nome;
        String placa;
        IPAddress ip;

        public:
            Dispositivo(int);
            String getNome() const;
            String getPlaca() const;
            IPAddress getIp() const;
            void setIp(IPAddress);
            void start();
            void handle();
    };
#endif
