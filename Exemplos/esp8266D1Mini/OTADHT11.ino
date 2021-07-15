#include <Arduino.h>
#include <DHT.h>
#include <ThingSpeak.h>
#include "Dispositivo.h"

#define DHTPIN D3
#define DHTTYPE DHT11

Dispositivo* dispositivo = new Dispositivo(esp8266D1Mini);
DHT dht(DHTPIN, DHTTYPE);

unsigned long myChannelNumber = 1424234;
const char * myWriteAPIKey = "FYDWLNURF61W80RB";

WiFiClient client;

void setup() {
  // put your setup code here, to run once:
  Serial.begin(115200);
  Serial.println("Comecando teste do DHT...");

  dht.begin();

  ThingSpeak.begin(client);

  dispositivo->setupDispositivo();
}

void loop() {
  // put your main code here, to run repeatedly:
  dispositivo->handleDispositivo();

  float h = dht.readHumidity();
  float t = dht.readTemperature();

  if(isnan(t) || isnan(h)){
    Serial.println("Falha na leitura do DHT");
  }else{
    Serial.print("Umidade: ");
    Serial.print(h);
    Serial.print(" %t");
    Serial.print("Temperatura: ");
    Serial.print(t);
    Serial.println(" *C");
  }

  ThingSpeak.setField(1, t);
  ThingSpeak.setField(2, h);
  ThingSpeak.writeFields(myChannelNumber, myWriteAPIKey);

  delay(25000);
}