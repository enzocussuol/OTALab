#include <OTALabDevice.h>

// Insira o id do dispositivo escolhido
String id = "3";
OTALabDevice* device = new OTALabDevice();

void setup() {
    device->setup(id);

    pinMode(LED_BUILTIN, OUTPUT);
}

void loop() {
    device->handle();

    digitalWrite(LED_BUILTIN, HIGH);
    delay(1000);
    digitalWrite(LED_BUILTIN, LOW);
    delay(1000);
}