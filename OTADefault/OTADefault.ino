#include <OTANetworkDevice.h>

OTANetworkDevice* device = new OTANetworkDevice();

void setup(){
    device->setWiFiNetworkName("Claudio");
    device->setWiFiNetworkPassword("adgj1234");
    device->setBrokerIP("192.168.86.41");
    device->setName("ESP32_DevKit");

    device->setup();
}

void loop(){
    device->handle();
}