#include <OTALabDevice.h>

String id = "";
OTALabDevice* device = new OTALabDevice();

void setup(){
    Serial.begin(115200);
    device->setup(id);
}

void loop(){
    device->handle();
}