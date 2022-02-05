#include <OTANetworkDevice.h>

OTANetworkDevice* device = new OTANetworkDevice();

void setup(){
    Serial.begin(115200);
    Serial.println("Comecando...");

    device->setWiFiNetworkName("Claudio");
    device->setWiFiNetworkPassword("adgj1234");
    device->setBrokerIP("192.168.86.41");
    device->setName("ESP8266_NodeMCU");
    device->setup();
    
    pinMode(2, OUTPUT);
}

void loop(){
    device->handle();

    digitalWrite(2, HIGH);
    delay(5000);
    digitalWrite(2, LOW);
    delay(5000);
}
