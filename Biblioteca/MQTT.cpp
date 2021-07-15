#include "MQTT.h"

WiFiClient clienteWiFi;
PubSubClient clienteMQTT(clienteWiFi);
IPAddress servidor(192, 168, 86, 41);

void callback(char* topic, byte* payload, unsigned int length) {
    Serial.print("Message arrived [");
    Serial.print(topic);
    Serial.print("] ");
    for (int i=0;i<length;i++) {
        Serial.print((char)payload[i]);
    }
    Serial.println();
}

void reconnect() {
    // Loop until we're reconnected
    while (!clienteMQTT.connected()) {
        Serial.print("Attempting MQTT connection...");
        // Attempt to connect
        if (clienteMQTT.connect("arduinoClient")) {
            Serial.println("connected");
            // Once connected, publish an announcement...
            clienteMQTT.publish("teste", "hello world");
            // ... and resubscribe
            clienteMQTT.subscribe("teste");
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