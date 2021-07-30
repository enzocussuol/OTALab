#include "MQTT.h"

WiFiClient clienteWiFi;
PubSubClient clienteMQTT(clienteWiFi);
IPAddress servidor(192, 168, 86, 41);

void callback(char* topic, byte* payload, unsigned int length) {
    std::string mensagem = "";
    for(int i = 0; i < (int)length; i++) mensagem += (char)payload[i];

    if(!mensagem.compare("Are you alive?")){
        String ipString = clienteWiFi.localIP().toString();
        int tamIpString = ipString.length();
        
        char ipCharArray[tamIpString];
        ipString.toCharArray(ipCharArray, tamIpString);

        clienteMQTT.publish("inTopic", ipCharArray);
    }
}

void reconnect() {
    // Loop until we're reconnected
    while (!clienteMQTT.connected()) {
        Serial.print("Attempting MQTT connection...");
        // Attempt to connect
        if (clienteMQTT.connect("arduinoClient")) {
            Serial.println("connected");
            // Once connected, publish an announcement...
            clienteMQTT.publish("inTopic", "Hello World!");
            // ... and resubscribe
            clienteMQTT.subscribe("outTopic");
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
    clienteMQTT.setServer(servidor, 1883);
    clienteMQTT.setCallback(callback);
}

void handleMQTT(){
    if(!clienteMQTT.connected()) reconnect();
    clienteMQTT.loop();
}