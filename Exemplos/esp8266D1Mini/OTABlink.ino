#include "Dispositivo.h"

Dispositivo* dispositivo = new Dispositivo(esp8266D1Mini);

void setup() {
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