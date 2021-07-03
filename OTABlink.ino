#include <Arduino.h>
#include "DispositivoOTAWS.h"

Dispositivo* dispositivo = new Dispositivo(esp8266D1Mini);

void setup() {
  pinMode(LED_BUILTIN, OUTPUT);

  dispositivo->start();
}

void loop() {  
  dispositivo->handle();
    
  digitalWrite(LED_BUILTIN, LOW);
  delay(1000);
  digitalWrite(LED_BUILTIN, HIGH);
  delay(1000);
}