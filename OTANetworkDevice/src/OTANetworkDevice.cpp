#include "OTANetworkDevice.h"

void OTANetworkDevice::setup(){
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

void OTANetworkDevice::handle(){
    handleOTA();
    handleWebServer(this);
    handleMQTT();
}

void OTANetworkDevice::setWiFiNetworkName(const char* WiFiNetworkName){
    this->WiFiNetworkName = WiFiNetworkName;
}

void OTANetworkDevice::setWiFiNetworkPassword(const char* WifiNetworkPassword){
    this->WiFiNetworkPassword = WifiNetworkPassword;
}

void OTANetworkDevice::setBrokerIP(String brokerIP){
    this->brokerIP = brokerIP;
}

void OTANetworkDevice::setName(String name){
    this->name = name;
}

String OTANetworkDevice::getName() const{
    return this->name;
}