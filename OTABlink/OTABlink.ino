#include "DispositivoOTAWS.h"

Dispositivo dispositivo(1);

void setup() {
  pinMode(LED_BUILTIN, OUTPUT);
  
  dispositivo.start();
}

void loop() {  
  dispositivo.handle();
    
  digitalWrite(LED_BUILTIN, LOW);
  delay(5000);
  digitalWrite(LED_BUILTIN, HIGH);
  delay(5000);
}
