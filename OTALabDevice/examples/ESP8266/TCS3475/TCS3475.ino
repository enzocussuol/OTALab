#include <Wire.h>
#include "Adafruit_TCS34725.h"
#include <SPI.h>
#include <OTALabDevice.h>

OTALabDevice* device = new OTALabDevice();
 
/* Example code for the Adafruit TCS34725 breakout library */
 
/* Connect SCL to analog 5
Connect SDA to analog 4
Connect VDD to 3.3V DC
Connect GROUND to common ground */
 
/* Initialise with default values (int time = 2.4ms, gain = 1x) */
// Adafruit_TCS34725 tcs = Adafruit_TCS34725();
 
/* Initialise with specific int time and gain values */
Adafruit_TCS34725 tcs = Adafruit_TCS34725(TCS34725_INTEGRATIONTIME_600MS, TCS34725_GAIN_1X);
 
void setup(void) {
    Serial.begin(115200);
    
    if (tcs.begin()) {
        Serial.println("Found sensor");
    } else {
        Serial.println("No TCS34725 found ... check your connections");
        while (1);
    }

    device->setWiFiNetworkName(WIFI_NETWORK_NAME);
    device->setWiFiNetworkPassword(WIFI_NETWORK_PASSWORD);
    device->setBrokerIP(BROKER_IP);
    device->setName(DEVICE_NAME);
    device->setup();
    
    // Now we're ready to get readings!
}
 
void loop(void) {
    device->handle();

    uint16_t r, g, b, c, colorTemp, lux;
    
    tcs.getRawData(&r, &g, &b, &c);
    colorTemp = tcs.calculateColorTemperature(r, g, b);
    lux = tcs.calculateLux(r, g, b);
    
    Serial.print("Color Temp: "); Serial.print(colorTemp, DEC); Serial.print(" K - ");
    Serial.print("Lux: "); Serial.print(lux, DEC); Serial.print(" - ");
    Serial.print("R: "); Serial.print(r, DEC); Serial.print(" ");
    Serial.print("G: "); Serial.print(g, DEC); Serial.print(" ");
    Serial.print("B: "); Serial.print(b, DEC); Serial.print(" ");
    Serial.print("C: "); Serial.print(c, DEC); Serial.print(" ");
    Serial.println(" ");
}