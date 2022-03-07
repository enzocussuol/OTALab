#include "OTALabDevice.h"

void OTALabDevice::setup(){
    WiFi.mode(WIFI_STA);
    WiFi.begin(this->WiFiNetworkName, this->WiFiNetworkPassword);

    while (WiFi.waitForConnectResult() != WL_CONNECTED) {
        Serial.println("Connection Failed! Rebooting...");
        delay(5000);
        ESP.restart();
    }

    Serial.println("");
    Serial.print("Connected to ");
    Serial.println(this->WiFiNetworkName);
    Serial.print("IP address: ");
    Serial.println(WiFi.localIP());

    setupOTA();
    setupWebServer();
    setupMQTT(this->brokerIP);
}

void OTALabDevice::handle(){
    handleOTA();
    handleWebServer(this);
    handleMQTT();
}

void OTALabDevice::setWiFiNetworkName(const char* WiFiNetworkName){
    this->WiFiNetworkName = WiFiNetworkName;
}

void OTALabDevice::setWiFiNetworkPassword(const char* WifiNetworkPassword){
    this->WiFiNetworkPassword = WifiNetworkPassword;
}

void OTALabDevice::setBrokerIP(String brokerIP){
    this->brokerIP = brokerIP;
}

void OTALabDevice::setName(String name){
    this->name = name;
}

String OTALabDevice::getName() const{
    return this->name;
}