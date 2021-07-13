#include "DispositivoOTAWS.h"

#ifndef STASSID
    #define STASSID "Claudio"
    #define STAPSK "adgj1234"
#endif

const char* ssid = STASSID;
const char* password = STAPSK;

WiFiServer server(80);

String header;

// Current time
unsigned long currentTime = millis();
// Previous time
unsigned long previousTime = 0;
// Define timeout time in milliseconds (example: 2000ms = 2s)
const long timeoutTime = 2000;

WiFiClient clienteWiFi;
PubSubClient clienteMQTT(clienteWiFi);
IPAddress servidor(192, 168, 86, 41);

Dispositivo::Dispositivo(int id){
    this->sensores = new std::list<Sensor*>;

    if(id == esp8266D1Mini){
        this->nome = "esp8266D1Mini";
        this->placa = "esp8266:esp8266:d1_mini";
    }else if(id == esp8266NodeMCU){
        this->nome = "esp8266NodeMCU";
        this->placa = "esp8266:esp8266:nodemcuv2";
    }else exit(EXIT_FAILURE);
}

String Dispositivo::getNome() const{
    return this->nome;
}

String Dispositivo::getPlaca() const{
    return this->placa;
}

IPAddress Dispositivo::getIp() const{
    return this->ip;
}

void Dispositivo::setIp(IPAddress ip){
    this->ip = ip;
}

std::list<Sensor*>* Dispositivo::getSensores() const{
    return this->sensores;
}

void inicializaWiFi(){
    WiFi.mode(WIFI_STA);
    WiFi.begin(ssid, password);

    while (WiFi.waitForConnectResult() != WL_CONNECTED) {
        Serial.println("Connection Failed! Rebooting...");
        delay(5000);
        ESP.restart();
    }

    Serial.println("");
    Serial.print("Connected to ");
    Serial.println(ssid);
    Serial.print("IP address: ");
    Serial.println(WiFi.localIP());
}

void inicializaOTA(){
    // Port defaults to 8266
    // ArduinoOTA.setPort(8266);

    // Hostname defaults to esp8266-[ChipID]
    // ArduinoOTA.setHostname("esp8266OTA");

    // No authentication by default
    // ArduinoOTA.setPassword("admin");

    // Password can be set with it's md5 value as well
    // MD5(admin) = 21232f297a57a5a743894a0e4a801fc3
    // ArduinoOTA.setPasswordHash("21232f297a57a5a743894a0e4a801fc3");

    ArduinoOTA.onStart([]() {
    String type;
    if (ArduinoOTA.getCommand() == U_FLASH) {
        type = "sketch";
    } else { // U_FS
        type = "filesystem";
    }

    // NOTE: if updating FS this would be the place to unmount FS using FS.end()
    Serial.println("Start updating " + type);
    });
    ArduinoOTA.onEnd([]() {
    Serial.println("\nEnd");
    });
    ArduinoOTA.onProgress([](unsigned int progress, unsigned int total) {
    Serial.printf("Progress: %u%%\r", (progress / (total / 100)));
    });
    ArduinoOTA.onError([](ota_error_t error) {
    Serial.printf("Error[%u]: ", error);
    if (error == OTA_AUTH_ERROR) {
        Serial.println("Auth Failed");
    } else if (error == OTA_BEGIN_ERROR) {
        Serial.println("Begin Failed");
    } else if (error == OTA_CONNECT_ERROR) {
        Serial.println("Connect Failed");
    } else if (error == OTA_RECEIVE_ERROR) {
        Serial.println("Receive Failed");
    } else if (error == OTA_END_ERROR) {
        Serial.println("End Failed");
    }
    });
    ArduinoOTA.begin();
}

void callback(char* topic, byte* payload, unsigned int length) {
  Serial.print("Message arrived [");
  Serial.print(topic);
  Serial.print("] ");
  for (int i=0;i<length;i++) {
    Serial.print((char)payload[i]);
  }
  Serial.println();
}

void Dispositivo::start(){
    Serial.begin(115200);
    Serial.println("Booting...");

    inicializaWiFi();

    this->setIp(WiFi.localIP());

    inicializaOTA();

    clienteMQTT.setServer(servidor, 1883);
    clienteMQTT.setCallback(callback);

    server.begin();
}

void reconnect() {
  // Loop until we're reconnected
  while (!clienteMQTT.connected()) {
    Serial.print("Attempting MQTT connection...");
    // Attempt to connect
    if (clienteMQTT.connect("arduinoClient")) {
      Serial.println("connected");
      // Once connected, publish an announcement...
      clienteMQTT.publish("teste", "hello world");
      // ... and resubscribe
      clienteMQTT.subscribe("teste");
    } else {
      Serial.print("failed, rc=");
      Serial.print(clienteMQTT.state());
      Serial.println(" try again in 5 seconds");
      // Wait 5 seconds before retrying
      delay(5000);
    }
  }
}

void Dispositivo::handle(){
    ArduinoOTA.handle();

    WiFiClient client = server.available();   // Listen for incoming clients

    if (client) {                             // If a new client connects,
        Serial.println("New Client.");          // print a message out in the serial port
        String currentLine = "";                // make a String to hold incoming data from the client
        currentTime = millis();
        previousTime = currentTime;
        while (client.connected() && currentTime - previousTime <= timeoutTime) { // loop while the client's connected
            currentTime = millis();         
            if (client.available()) {             // if there's bytes to read from the client,
                char c = client.read();             // read a byte, then
                Serial.write(c);                    // print it out the serial monitor
                header += c;
                if (c == '\n') {                    // if the byte is a newline character
                    // if the current line is blank, you got two newline characters in a row.
                    // that's the end of the client HTTP request, so send a response:
                    if (currentLine.length() == 0) {
                        // HTTP headers always start with a response code (e.g. HTTP/1.1 200 OK)
                        // and a content-type so the client knows what's coming, then a blank line:
                        client.println("HTTP/1.1 200 OK");
                        client.println("Content-type:application/json");
                        client.println("Connection: close");
                        client.println();
                        
                        // Display the JSON web page
                        client.println("{");
                        
                        client.print("\t\"nome\": \"");
                        client.print(this->getNome());
                        client.println("\",");

                        client.print("\t\"placa\": \"");
                        client.print(this->getPlaca());
                        client.println("\",");

                        client.print("\t\"ip\": \"");
                        client.print(this->getIp());
                        client.println("\",");

                        unsigned int cont1 = 0, cont2 = 0;

                        client.println("\t\"sensores\": [");
                        
                        for(auto it = this->sensores->begin(); it != this->sensores->end(); ++it){
                            client.println("\t\t{");
                            client.print("\t\t\t\"nome\": \"");
                            client.print((*it)->getNome());
                            client.println("\",");

                            client.println("\t\t\t\"conexoes\": [");

                            for(auto it2 = (*it)->getConexoes()->begin(); it2 != (*it)->getConexoes()->end(); ++it2){
                                client.println("\t\t\t\t{");
                                client.print("\t\t\t\t\t\"");
                                client.print(it2->first);
                                client.print("\": \"");
                                client.print(it2->second);
                                client.println("\"");
                                client.print("\t\t\t\t}");

                                if(cont2 < (*it)->getConexoes()->size() - 1) client.println(",");
                                else client.println("");
                                cont2++;
                            }
                            cont2 = 0;

                            client.println("\t\t\t]");
                            client.print("\t\t}");

                            if(cont1 < this->sensores->size() - 1) client.println(",");
                            else client.println("");
                            cont1++;
                        }
                        
                        client.println("\t]");
                        client.println("}");
                        
                        // The HTTP response ends with another blank line
                        client.println();
                        // Break out of the while loop
                        break;
                    } else { // if you got a newline, then clear currentLine
                        currentLine = "";
                    }
                } else if (c != '\r') {  // if you got anything else but a carriage return character,
                    currentLine += c;      // add it to the end of the currentLine
                }
            }
        }
        // Clear the header variable
        header = "";
        // Close the connection
        client.stop();
        Serial.println("Client disconnected.");
        Serial.println("");
    }

    if(!clienteMQTT.connected()) reconnect();
    clienteMQTT.loop();
}