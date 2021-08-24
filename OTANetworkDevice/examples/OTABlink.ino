#include <OTANetworkDevice.h>

OTANetworkDevice* dispositivo = new OTANetworkDevice(esp8266D1Mini);

void setup(){
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