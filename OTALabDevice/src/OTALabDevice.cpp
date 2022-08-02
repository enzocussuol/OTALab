#include "OTALabDevice.h"

void OTALabDevice::setup(String id){
    WiFi.mode(WIFI_STA);
    WiFi.begin(WIFI_NETWORK_NAME, WIFI_NETWORK_PASSWORD);

    while (WiFi.waitForConnectResult() != WL_CONNECTED) {
        Serial.println("Connection Failed! Rebooting...");
        delay(5000);
        ESP.restart();
    }

    Serial.println("");
    Serial.print("Connected to ");
    Serial.println(WIFI_NETWORK_NAME);
    Serial.print("IP address: ");
    Serial.println(WiFi.localIP());

    DEVICE_ID = id.toInt();

    setupOTA();
    setupMQTT();
}

void OTALabDevice::handle(){
    handleOTA();
    handleMQTT();
}