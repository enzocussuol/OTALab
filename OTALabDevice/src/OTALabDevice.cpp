#include "OTALabDevice.h"

long MY_DEVICE_ID = 0;

void OTALabDevice::setup(String id){
    WiFi.mode(WIFI_STA);
    WiFi.begin(WIFI_NETWORK_NAME, WIFI_NETWORK_PASSWORD);

    while (WiFi.status() != WL_CONNECTED) {
        Serial.print("Trying to connect to: ");
        Serial.println(WIFI_NETWORK_NAME);
        Serial.println("Connection Failed! Rebooting...");
        delay(5000);
    }

    Serial.println("");
    Serial.print("Connected to ");
    Serial.println(WIFI_NETWORK_NAME);
    Serial.print("IP address: ");
    Serial.println(WiFi.localIP());

    MY_DEVICE_ID = id.toInt();

    setupOTA();
    setupMQTT();
}

void OTALabDevice::handle(){
    handleOTA();
    handleMQTT();
}