#include "MQTT.h"

WiFiClient clienteWiFi;
PubSubClient clienteMQTT(clienteWiFi);
IPAddress broker;

void callback(char* topic, byte* payload, unsigned int length) {
    std::string mensagem = "";
    for(int i = 0; i < (int)length; i++) mensagem += (char)payload[i];

    if(!mensagem.compare("Are you alive?")){
        String ipString = clienteWiFi.localIP().toString();
        int tamIpString = ipString.length();
        
        char ipCharArray[tamIpString+1];
        ipString.toCharArray(ipCharArray, tamIpString+1);

        clienteMQTT.publish("Inicializacao/inTopic", ipCharArray);
    }
}

void reconnect() {
    // Loop until we're reconnected
    while (!clienteMQTT.connected()) {
        Serial.print("Attempting MQTT connection...");
        // Attempt to connect
        if (clienteMQTT.connect("arduinoClient")) {
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
    broker.fromString(IP_BROKER);
    clienteMQTT.setServer(broker, 1883);
    clienteMQTT.setCallback(callback);
}

void handleMQTT(){
    if(!clienteMQTT.connected()) reconnect();
    clienteMQTT.loop();
}