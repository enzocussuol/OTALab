#include <OTANetworkDevice.h>

OTANetworkDevice* dispositivo = new OTANetworkDevice();

void setup(){
    /* Fill up the fields below with your WiFi network name and password, so as your MQTT broker IP. 
    All devices on OTA Network must fill these fields with the same data */

    dispositivo->setWiFiNetworkName("");
    dispositivo->setWiFiNetworkPassword("");
    dispositivo->setBrokerIP("");

    dispositivo->setup();
}

void loop(){
    dispositivo->handle();
}