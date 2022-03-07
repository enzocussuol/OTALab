#include <Wire.h>
#include <Adafruit_Sensor.h>
#include <Adafruit_BME280.h>
#include <OTALabDevice.h>

OTALabDevice* device = new OTALabDevice();

/*#include <SPI.h>
#define BME_SCK 14
#define BME_MISO 12
#define BME_MOSI 13
#define BME_CS 15*/

#define SEALEVELPRESSURE_HPA (1013.25)

Adafruit_BME280 bme; // I2C
//Adafruit_BME280 bme(BME_CS); // hardware SPI
//Adafruit_BME280 bme(BME_CS, BME_MOSI, BME_MISO, BME_SCK); // software SPI


void setup() {
    Serial.begin(115200);
    Serial.println(F("BME280 test"));

    device->setWiFiNetworkName(WIFI_NETWORK_NAME);
    device->setWiFiNetworkPassword(WIFI_NETWORK_PASSWORD);
    device->setBrokerIP(BROKER_IP);
    device->setName(DEVICE_NAME);
    device->setup();

    bool status;

    // default settings
    // (you can also pass in a Wire library object like &Wire2)
    status = bme.begin(0x76);
    while(!status){
        Serial.println("Could not find a valid BME280 sensor, check wiring!");
        status = bme.begin(0x76);
        delay(5000);
    }

    Serial.println("-- Default Test --");
    Serial.println();
}

void loop() { 
    device->handle();
    
    printValues();
    delay(1000);
}

void printValues() {
    Serial.print("Temperature = ");
    Serial.print(bme.readTemperature());
    Serial.println(" *C");

    // Convert temperature to Fahrenheit
    /*Serial.print("Temperature = ");
    Serial.print(1.8 * bme.readTemperature() + 32);
    Serial.println(" *F");*/

    Serial.print("Pressure = ");
    Serial.print(bme.readPressure() / 100.0F);
    Serial.println(" hPa");

    Serial.print("Approx. Altitude = ");
    Serial.print(bme.readAltitude(SEALEVELPRESSURE_HPA));
    Serial.println(" m");

    Serial.print("Humidity = ");
    Serial.print(bme.readHumidity());
    Serial.println(" %");

    Serial.println();
}