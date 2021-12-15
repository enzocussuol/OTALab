#include <OTANetworkDevice.h>

#include <ESP8266WiFi.h>
#include <DHT.h>

#define DHTPIN D1
#define DHTTYPE DHT11
#define INTERVALO_ENVIO_THINGSPEAK 5000

char enderecoAPIThingspeak[] = "api.thingspeak.com";
String channelAPIKey = "80MAN6MJ7CTQZ06S";
unsigned long tempoUltimoEnvio = 0;

WiFiClient client;

OTANetworkDevice* device = new OTANetworkDevice();
DHT dht(DHTPIN, DHTTYPE);

void setup(){
    device->setWiFiNetworkName("Claudio");
    device->setWiFiNetworkPassword("adgj1234");
    device->setBrokerIP("192.168.86.41");
    device->setName("esp8266_DHT11");
    device->setup();

    Serial.begin(115200);
    Serial.println("Comecando...");
    
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
      Serial.println("- Informações enviadas ao ThingSpeak!");
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
      
      sprintf(camposAEnviar, "field1=%.2f&field2=%.2f", temperatura, umidade);
      enviaDadosThingspeak(camposAEnviar);
    }
}
