#include "OTANetworkDevice.h"

OTANetworkDevice::OTANetworkDevice(){
    this->sensores = new std::list<Sensor*>;
}

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

    this->setIp(WiFi.localIP());

    setupOTA();
    setupWebServer();
    setupMQTT(this->brokerIP);
}

void OTANetworkDevice::handle(){
    handleOTA();
    handleWebServer(this);
    handleMQTT(this);
}

void OTANetworkDevice::setWiFiNetworkName(String WiFiNetworkName){
    this->WiFiNetworkName = WiFiNetworkName;
}

void OTANetworkDevice::setWiFiNetworkPassword(String WiFiNetworkPassword){
    this->WiFiNetworkPassword = WiFiNetworkPassword;
}

String OTANetworkDevice::getBrokerIP() const{
    return this->brokerIP;
}

void OTANetworkDevice::setBrokerIP(String brokerIP){
    this->brokerIP = brokerIP;
}

String OTANetworkDevice::getNome() const{
    return this->nome;
}

String OTANetworkDevice::getPlaca() const{
    return this->placa;
}

IPAddress OTANetworkDevice::getIp() const{
    return this->ip;
}

void OTANetworkDevice::setIp(IPAddress ip){
    this->ip = ip;
}

std::list<Sensor*>* OTANetworkDevice::getSensores() const{
    return this->sensores;
}