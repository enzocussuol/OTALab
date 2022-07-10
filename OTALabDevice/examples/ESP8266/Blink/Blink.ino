#include <OTALabDevice.h>

OTALabDevice* device = new OTALabDevice();

void setup(){
    device->setup();
    
    pinMode(2, OUTPUT);
}

void loop(){
    device->handle();

    digitalWrite(2, HIGH);
    delay(5000);
    digitalWrite(2, LOW);
    delay(5000);
}
