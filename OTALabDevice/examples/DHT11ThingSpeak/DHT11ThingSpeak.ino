#include <OTALabDevice.h>

// Insira o id do dispositivo escolhido
String id = ""
OTALabDevice* device = new OTALabDevice();

#include <ESP8266WiFi.h>
#include <DHT.h>

// Selecione a pinagem cadastrada para o dispositivo escolhido no OTALab
#define DHTPIN D1

#define DHTTYPE DHT11
#define INTERVALO_ENVIO_THINGSPEAK 5000

// Insira os dados do seu canal ThingSpeak
char enderecoAPIThingspeak[] = "";
String channelAPIKey = "";

unsigned long tempoUltimoEnvio = 0;

WiFiClient client;

DHT dht(DHTPIN, DHTTYPE);

void setup(){
    device->setup(id);
    
    dht.begin();
}

void enviaDadosThingspeak(String dados){
  if(client.connect(enderecoAPIThingspeak, 80)){
      client.print("POST /update HTTP/1.1\n");
      client.print("Host: api.thingspeak.com\n");
      client.print("Connection: close\n");
      client.print("X-THINGSPEAKAPIKEY: " + channelAPIKey + "\n");
      client.print("Content-Type: application/x-www-form-urlencoded\n");
      client.print("Content-Length: ");
      client.print(dados.length());
      client.print("\n\n");
      client.print(dados);
        
      tempoUltimoEnvio = millis();
    }
}

void loop(){
    device->handle();

    char camposAEnviar[100] = {0};
    float temperatura = 0;
    float umidade = 0;

    if(millis() - tempoUltimoEnvio > INTERVALO_ENVIO_THINGSPEAK){
      temperatura = dht.readTemperature();
      umidade = dht.readHumidity();

      Serial.print("Temperatura: ");
      Serial.println(temperatura);

      Serial.print("Umidade: ");
      Serial.println(temperatura);

      sprintf(camposAEnviar, "field1=%.2f&field2=%.2f", temperatura, umidade);
      enviaDadosThingspeak(camposAEnviar);
    }
}
