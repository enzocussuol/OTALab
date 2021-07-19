#include "Dispositivo.h"

const char* ssid = NOME_WIFI;
const char* password = SENHA_WIFI;

Dispositivo::Dispositivo(int id){
    this->sensores = new std::list<Sensor*>;

    if(id == esp8266D1Mini){
        this->nome = "esp8266D1Mini";
        this->placa = "esp8266:esp8266:d1_mini";
    }else if(id == esp8266NodeMCU){
        this->nome = "esp8266NodeMCU";
        this->placa = "esp8266:esp8266:nodemcuv2";
    }else exit(EXIT_FAILURE);
}

static void inicializaWiFi(){
    WiFi.mode(WIFI_STA);
    WiFi.begin(ssid, password);

    while (WiFi.waitForConnectResult() != WL_CONNECTED) {
        Serial.println("Connection Failed! Rebooting...");
        delay(5000);
        ESP.restart();
    }

    Serial.println("");
    Serial.print("Connected to ");
    Serial.println(ssid);
    Serial.print("IP address: ");
    Serial.println(WiFi.localIP());
}

void Dispositivo::setupDispositivo(){
    inicializaWiFi();

    this->setIp(WiFi.localIP());

    setupOTA();
    setupWebServer();
    // setupMQTT();
}

void Dispositivo::handleDispositivo(){
    handleOTA();
    handleWebServer(this);
    // handleMQTT();
}

String Dispositivo::getNome() const{
    return this->nome;
}

String Dispositivo::getPlaca() const{
    return this->placa;
}

IPAddress Dispositivo::getIp() const{
    return this->ip;
}

void Dispositivo::setIp(IPAddress ip){
    this->ip = ip;
}

std::list<Sensor*>* Dispositivo::getSensores() const{
    return this->sensores;
}