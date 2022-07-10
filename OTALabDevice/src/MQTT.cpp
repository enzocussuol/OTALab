#include "MQTT.h"

WiFiClient clienteWiFi;
PubSubClient clienteMQTT(clienteWiFi);
IPAddress broker;

void callback(char* topic, byte* payload, unsigned int length) {
    std::string mensagem = "";
    for(int i = 0; i < (int)length; i++) mensagem += (char)payload[i];

    if(!mensagem.compare("Are you alive?")){
        Serial.println("Recebi um sinal de vida!");

        String resposta = clienteWiFi.localIP().toString();
        resposta = resposta + " " + DEVICE_ID;

        Serial.println(resposta);

        int tamResposta = resposta.length();
        
        char respostaAsCharArray[tamResposta+1];
        resposta.toCharArray(respostaAsCharArray, tamResposta+1);

        clienteMQTT.publish("Inicializacao/inTopic", respostaAsCharArray);
    }
}

void reconnect() {
    // Loop until we're reconnected
    while (!clienteMQTT.connected()) {
        Serial.print("Attempting MQTT connection...");
        // Attempt to connect
        if (clienteMQTT.connect(WiFi.macAddress().c_str())) {
            Serial.println("connected");

            clienteMQTT.subscribe("Inicializacao/outTopic");
        } else {
            Serial.print("failed, rc=");
            Serial.print(clienteMQTT.state());
            Serial.println(" try again in 5 seconds");
            // Wait 5 seconds before retrying
            delay(5000);
        }
    }
}

void setupMQTT(){
    broker.fromString(BROKER_IP);
    clienteMQTT.setServer(broker, 1883);
    clienteMQTT.setCallback(callback);
}

void handleMQTT(){
    if(!clienteMQTT.connected()) reconnect();
    clienteMQTT.loop();
}