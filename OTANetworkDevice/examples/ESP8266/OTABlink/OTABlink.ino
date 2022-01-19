#include <OTANetworkDevice.h>

OTANetworkDevice* device = new OTANetworkDevice();

void setup(){
    Serial.begin(115200);
    Serial.println("Comecando...");

    device->setWiFiNetworkName(WIFI_NETWORK_NAME);
    device->setWiFiNetworkPassword(WIFI_NETWORK_PASSWORD);
    device->setBrokerIP(BROKER_IP);
    device->setName(DEVICE_NAME);
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
