#include "ThingSpeak.h"
#include <OTANetworkDevice.h>

unsigned long myChannelNumber = 0;
const char * myWriteAPIKey = "";

#include <ESP8266WiFi.h>

int pin = D8;

unsigned long duration;
unsigned long starttime;
unsigned long sampletime_ms = 30000;
long lowpulseoccupancy = 0;

char ssid[] = "";   // your network SSID (name)
char pass[] = "";  // your network password
int keyIndex = 0;          // your network key index number (needed only for WEP)

WiFiClient  client;

OTANetworkDevice* device = new OTANetworkDevice();

void setup() {
    Serial.begin(115200);
    device->setWiFiNetworkName("Claudio");
    device->setWiFiNetworkPassword("adgj1234");
    device->setBrokerIP("192.168.86.41");
    device->setName("ESP8266_D1Mini_1");
    device->setup();

    delay(100);

    WiFi.mode(WIFI_STA);

    ThingSpeak.begin(client);
}

void loop() {
    device->handle();

    duration = pulseIn(pin, LOW);
    lowpulseoccupancy = lowpulseoccupancy+duration;

    // Connect or reconnect to WiFi
    if (WiFi.status() != WL_CONNECTED) {
        Serial.print("Attempting to connect to SSID: ");
        Serial.println(ssid);
        while (WiFi.status() != WL_CONNECTED) {
            WiFi.begin(ssid, pass); // Connect to WPA/WPA2 network. Change this line if using open or WEP network
            Serial.print(".");
            delay(5000);
        }
        Serial.println("\nConnected.");
    }

    // Measure Signal Strength (RSSI) of Wi-Fi connection
    //long rssi = WiFi.RSSI();

    if ((millis()-starttime) > sampletime_ms){
      // Write value to Field 1 of a ThingSpeak Channel
      int httpCode = ThingSpeak.writeField(myChannelNumber, 1, lowpulseoccupancy, myWriteAPIKey);

      if (httpCode == 200) {
        Serial.println("Channel write successful.");
      }
      else {
        Serial.println("Problem writing to channel. HTTP error code " + String(httpCode));
      }

      lowpulseoccupancy = 0;
      starttime = millis();
    }

    // Wait 20 seconds to uodate the channel again
    delay(20000);
}
