#include "WebServer.h"

WiFiServer webServer(80);

String header;

// Current time
unsigned long currentTime = millis();
// Previous time
unsigned long previousTime = 0;
// Define timeout time in milliseconds (example: 2000ms = 2s)
const long timeoutTime = 2000;

void setupWebServer(){
    webServer.begin();
}

static void displayJSON(WiFiClient client, OTANetworkDevice* dispositivo){
    client.println("{");
                        
    client.print("\t\"nome\": \"");
    client.print(dispositivo->getNome());
    client.println("\",");

    client.print("\t\"placa\": \"");
    client.print(dispositivo->getPlaca());
    client.println("\",");

    client.print("\t\"ip\": \"");
    client.print(dispositivo->getIp());
    client.println("\",");

    unsigned int cont1 = 0, cont2 = 0;

    client.println("\t\"sensores\": [");
    
    for(auto it = dispositivo->getSensores()->begin(); it != dispositivo->getSensores()->end(); ++it){
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

        if(cont1 < dispositivo->getSensores()->size() - 1) client.println(",");
        else client.println("");
        cont1++;
    }
    
    client.println("\t]");
    client.println("}");
}

void handleWebServer(OTANetworkDevice* dispositivo){
    WiFiClient client = webServer.available();   // Listen for incoming clients

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
                        displayJSON(client, dispositivo);
                        
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
}