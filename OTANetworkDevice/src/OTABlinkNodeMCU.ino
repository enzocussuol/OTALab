#include <OTANetworkDevice.h>

OTANetworkDevice* dispositivo = new OTANetworkDevice(esp8266NodeMCU);

void setup(){
    Serial.begin(115200);
    Serial.println("Comecando...");
    
    pinMode(LED_BUILTIN, OUTPUT);

    dispositivo->setup();
}

void loop(){
    dispositivo->handle();

    digitalWrite(LED_BUILTIN, HIGH);
    delay(5000);
    digitalWrite(LED_BUILTIN, LOW);
    delay(5000);
}