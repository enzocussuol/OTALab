/*
  ESP 32 Blink
  Turns on an LED on for one second, then off for one second, repeatedly.
  The ESP32 has an internal blue LED at D2 (GPIO 02)
*/

#include <OTANetworkDevice.h>

OTANetworkDevice* device = new OTANetworkDevice();

void setup() {
    Serial.begin(115200);
    Serial.println("Comecando...");

    device->setWiFiNetworkName(WIFI_NETWORK_NAME);
    device->setWiFiNetworkPassword(WIFI_NETWORK_PASSWORD);
    device->setBrokerIP(BROKER_IP);
    device->setName(DEVICE_NAME);
    device->setup();

    pinMode(LED_BUILTIN, OUTPUT);
}

void loop() {
    device->handle();

    digitalWrite(LED_BUILTIN, HIGH);   // turn the LED on (HIGH is the voltage level)
    delay(1000);                       // wait for a second
    digitalWrite(LED_BUILTIN, LOW);    // turn the LED off by making the voltage LOW
    delay(1000);                       // wait for a second
}