#include <OTALabDevice.h>

String id = "27";
OTALabDevice* device = new OTALabDevice();

void setup(){
    Serial.begin(115200);
    device->setup(id);
}

void loop(){
    device->handle();
}