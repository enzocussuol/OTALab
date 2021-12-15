#include <OTANetworkDevice.h>

OTANetworkDevice* device = new OTANetworkDevice();

void setup(){
    device->setWiFiNetworkName("Claudio");
    device->setWiFiNetworkPassword("adgj1234");
    device->setBrokerIP("192.168.86.41");
    device->setName("dispositivo1");
    device->setup();

    Serial.begin(115200);
    Serial.println("Comecando...");
    
    pinMode(LED_BUILTIN, OUTPUT);
}

void loop(){
    device->handle();

    digitalWrite(LED_BUILTIN, HIGH);
    delay(5000);
    digitalWrite(LED_BUILTIN, LOW);
    delay(5000);
}