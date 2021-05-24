#include <Arduino.h>
#line 1 "/home/enzo/ProjetoOTA/OTABlink/OTABlink.ino"
#include "DispositivoOTAWS.h"

Dispositivo dispositivo(1);

#line 5 "/home/enzo/ProjetoOTA/OTABlink/OTABlink.ino"
void setup();
#line 11 "/home/enzo/ProjetoOTA/OTABlink/OTABlink.ino"
void loop();
#line 5 "/home/enzo/ProjetoOTA/OTABlink/OTABlink.ino"
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

