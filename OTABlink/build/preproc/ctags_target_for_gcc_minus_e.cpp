# 1 "/home/enzo/ProjetoOTA/OTABlink/OTABlink.ino"
# 2 "/home/enzo/ProjetoOTA/OTABlink/OTABlink.ino" 2

Dispositivo dispositivo(1);

void setup() {
  pinMode(2, 0x01);

  dispositivo.start();
}

void loop() {
  dispositivo.handle();

  digitalWrite(2, 0x0);
  delay(5000);
  digitalWrite(2, 0x1);
  delay(5000);
}
