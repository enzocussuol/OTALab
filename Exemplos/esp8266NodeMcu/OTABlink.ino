#include "Dispositivo.h"

Dispositivo* dispositivo = new Dispositivo(esp8266NodeMCU);

void setup() {
  Serial.begin(115200);
  Serial.println("Booting...");

  pinMode(LED_BUILTIN, OUTPUT);

  dispositivo->setupDispositivo();
}

void loop() {  
  dispositivo->handleDispositivo();
    
  digitalWrite(LED_BUILTIN, LOW);
  delay(5000);
  digitalWrite(LED_BUILTIN, HIGH);
  delay(5000);
}